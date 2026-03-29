import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:front_end/features/auth/data/auth_repository.dart';
import 'package:front_end/features/auth/logic/auth_cubit.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:talker/talker.dart';
import 'package:talker_dio_logger/talker_dio_logger.dart';

final talker = Talker();

class ApiClient {
  late Dio dio;
  final Talker talker = Talker();
  final StorageService _storageService;

  ApiClient(this._storageService) {
    // _____________________________________________________________________________
    // * 1. KHỞI TẠO DIO & CẤU HÌNH MẶC ĐỊNH
    // _____________________________________________________________________________
    dio = Dio(
      BaseOptions(
        baseUrl: dotenv.get('API_BASE_URL', fallback: 'http://localhost:8080'),
        connectTimeout: const Duration(seconds: 5),
      ),
    );

    // _____________________________________________________________________________
    // * 2. CÀI ĐẶT TRẠM KIỂM SOÁT (INTERCEPTOR)
    // _____________________________________________________________________________
    dio.interceptors.add(
      InterceptorsWrapper(
        // * A. KIỂM TRA TRƯỚC KHI GỬI (ON REQUEST)
        onRequest: (options, handler) async {
          // ? CẦN XEM LẠI: API logout của Backend có yêu cầu Token ở Header không?
          // Nếu có, phải xóa dòng if này đi để nó nhét Token vào. Nếu không, giữ nguyên.
          if (options.path.contains('logout')) {
            return handler.next(options);
          }

          // Lấy token từ local storage
          String? token = await _storageService.readSecureData(
            AppKeys.accessToken,
          );

          if (token != null) {
            // ! CẢNH BÁO: Kiểm tra hạn Token ngay tại máy, nếu hết hạn thì chặn luôn không gọi API
            if (JwtDecoder.isExpired(token)) {
              talker.error('Chặn request! Token hết hạn ở local.');
              getIt<AuthCubit>().logout();

              // Chủ động ném lỗi để hủy request hiện tại
              return handler.reject(
                DioException(
                  requestOptions: options,
                  error: 'Token đã hết hạn ở Local',
                  type: DioExceptionType.cancel,
                ),
              );
            }

            // Nếu vé còn hạn, nhét vào Header và cho đi tiếp
            options.headers['Authorization'] = 'Bearer $token';
          }
          return handler.next(options);
        },

        // * B. XỬ LÝ KHI BỊ LỖI (ON ERROR) - CƠ CHẾ SILENT REFRESH
        onError: (DioException err, ErrorInterceptorHandler handler) async {
          // Nếu Backend trả về lỗi 401 (Unauthorized / Sai hoặc hết hạn Token)
          if (err.response?.statusCode == 401) {
            // ! CẢNH BÁO: Chặn vòng lặp vô tận. Nếu đã thử refresh 1 lần mà vẫn 401 thì kick luôn
            if (err.requestOptions.extra['isRetry'] == true) {
              getIt<AuthCubit>().logout();
              return handler.next(err);
            }

            talker.warning('Bị 401! Đang gọi API Refresh Token...');

            // TODO: Đảm bảo AuthRepository đã viết chuẩn hàm refreshToken()
            final isSuccess = await getIt<AuthRepository>().refreshToken();

            if (isSuccess) {
              talker.info(
                'Refresh thành công! Đang tự động gọi lại request cũ...',
              );

              // Lấy token MỚI vừa được lưu
              final newToken = await _storageService.readSecureData(
                AppKeys.accessToken,
              );

              // Clone lại cấu hình của request bị lỗi ban nãy
              final options = err.requestOptions;
              options.headers['Authorization'] = 'Bearer $newToken';
              options.extra['isRetry'] = true; // Đánh dấu là đã thử cứu 1 lần

              // Gọi lại API đó với token mới
              final cloneReq = await dio.fetch(options);
              return handler.resolve(cloneReq);
            }

            // ! CẢNH BÁO: API Refresh Token cũng thất bại -> Bắt buộc đuổi khách
            talker.error('Refresh Token thất bại! Đăng xuất User.');
            getIt<AuthCubit>().logout();
          }
          return handler.next(err);
        },
      ),
    );

    // _____________________________________________________________________________
    // * 3. GẮN MÁY QUAY PHIM (TALKER LOGGER)
    // _____________________________________________________________________________
    dio.interceptors.add(
      TalkerDioLogger(
        talker: talker,
        settings: const TalkerDioLoggerSettings(
          printRequestHeaders: true,
          printResponseHeaders: false,
          printResponseData: true,
          printRequestData: true,
        ),
      ),
    );
  }
}
