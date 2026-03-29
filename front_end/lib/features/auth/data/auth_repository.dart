// File: lib/features/auth/data/auth_repository.dart
import 'package:dio/dio.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/utils/storage_service.dart';

import '../../../core/network/api_client.dart';

class AuthRepository {
  final ApiClient apiClient;
  final StorageService storageService; // Thêm bạn phu khuân vác vào đây
  AuthRepository(this.apiClient, this.storageService);

  // Lưu ý: Đổi kiểu trả về thành Future<bool> để báo cho UI biết profile đã hoàn thành chưa
  Future<bool> login(String email, String password) async {
    try {
      final response = await apiClient.dio.post(
        '/api/identity/public/signin',
        data: {'email': email, 'password': password},
      );

      if (response.data['success'] == true) {
        final data = response.data['data'];

        // 1. Bóc tách dữ liệu từ JSON
        final accessToken = data['access_token'];
        final refreshToken = data['refresh_token'];

        final userId = data['user']['id'];
        final isCompleted = data['user']['profile_completed'] ?? false;

        if (userId != null) {
          await storageService.writeData(AppKeys.userId, userId.toString());
        }
        await storageService.writeData(AppKeys.profileCompleted, isCompleted);
        // Lưu Token (Dữ liệu nhạy cảm -> dùng Secure)
        await storageService.writeSecureData(AppKeys.accessToken, accessToken);
        await storageService.writeSecureData(
          AppKeys.refreshToken,
          refreshToken,
        );
        talker.info('🔑 Login thành công! Đã lưu UserId: $userId');
        return isCompleted;
      } else {
        throw Exception(response.data['message'] ?? 'Đăng nhập thất bại');
      }
    } on DioException catch (e) {
      // 🔴 ĐÂY LÀ KHÚC "MÓC LÚP" THẰNG DIO 🔴
      if (e.response != null && e.response?.data != null) {
        // Moi cái chữ "Email hoặc password không đúng" ra đây
        final serverMessage = e.response?.data['message'] ?? 'Lỗi xác thực';
        throw Exception(serverMessage);
      }
      // Lỗi khi mất mạng hoặc server sập hẳn không trả về response
      throw Exception('Lỗi kết nối: ${e.message}');
    } catch (e) {
      // Bắt các lỗi vớ vẩn khác
      throw Exception(e.toString());
    }
  }

  // 1. HÀM ĐĂNG KÝ (Trả về user_id để mang sang bước xác thực)
  Future<String> signUp(String email, String password) async {
    try {
      final response = await apiClient.dio.post(
        '/api/identity/public/signup',
        data: {'email': email, 'password': password},
      );

      if (response.data['success'] == true) {
        return response.data['data'] as String;
      } else {
        // Trường hợp success: false nhưng status vẫn là 200 (hiếm gặp)
        throw Exception(response.data['message'] ?? 'Đăng ký thất bại');
      }
    } on DioException catch (e) {
      // 🔴 ĐÂY LÀ CHỖ QUAN TRỌNG NHẤT 🔴
      // Khi server trả về 400, 409... Dio sẽ ném ra DioException.
      // Mình phải chọc vào e.response để lấy cái "message" mà server gửi về.
      final serverMessage = e.response?.data['message'] ?? 'Đã có lỗi xảy ra';
      throw Exception(serverMessage);
    } catch (e) {
      // Lỗi vớ vẩn khác như mất mạng, crash code...
      throw Exception('Lỗi hệ thống: $e');
    }
  }

  // 2. HÀM XÁC THỰC EMAIL (OTP)
  Future<void> verifyEmail(String userId, String code) async {
    try {
      final response = await apiClient.dio.post(
        '/api/identity/public/verify-email', // Check lại route này trên Swagger của ông nhé
        data: {
          'user_id': userId,
          'code': code, // Chính là cái mã RXZ0RXMQ ông vừa gửi
        },
      );

      if (response.data['success'] != true) {
        throw Exception(response.data['message'] ?? 'Mã xác thực không hợp lệ');
      }
    } on DioException catch (e) {
      final msg = e.response?.data['message'] ?? 'Xác thực thất bại';
      throw Exception(msg);
    } catch (e) {
      throw Exception('Lỗi hệ thống: $e');
    }
  }

  // 3. HÀM GÜI LAI MẫU XÁC THỰC
  Future<void> resendVerification(String email) async {
    try {
      final response = await apiClient.dio.post(
        '/api/identity/public/resend-verification',
        data: {'email': email},
      );

      if (response.data['success'] != true) {
        throw Exception(response.data['message'] ?? 'Lỗi gửi lại mã');
      }
    } on DioException catch (e) {
      final msg = e.response?.data['message'] ?? 'Gửi lại mã thất bại';
      throw Exception(msg);
    }
  }

  // Thêm hàm này vào trong class AuthRepository
  Future<bool> refreshToken() async {
    try {
      final storage = getIt<StorageService>();
      final rToken = await storage.readSecureData(AppKeys.refreshToken);

      if (rToken == null || rToken.isEmpty) {
        talker.error('Refresh Token trống rỗng, không cứu được!');
        return false;
      }
      final refreshDio = Dio(
        BaseOptions(baseUrl: apiClient.dio.options.baseUrl),
      );

      final response = await refreshDio.post(
        '/api/identity/public/refresh-token',
        queryParameters: {'refresh_token': rToken}, // Theo Swagger của ông
        data: {'refresh_token': rToken}, // Theo thực tế Log đòi
      );

      if (response.data['success'] == true) {
        talker.info('data', response.data['data']);
        final data = response.data['data'];
        final newAccessToken = data['access_token'];
        final newRefreshToken = data['refresh_token'];

        if (newAccessToken != null) {
          // Lưu hàng mới vào kho
          await storage.writeSecureData(AppKeys.accessToken, newAccessToken);
          if (newRefreshToken != null) {
            await storage.writeSecureData(
              AppKeys.refreshToken,
              newRefreshToken,
            );
          }

          talker.info('✅ AuthRepository: Refresh Token thành công!');
          return true;
        }
      }
      return false;
    } catch (e) {
      talker.error('❌ AuthRepository: Lỗi khi refresh token: $e');
      return false;
    }
  }

  // File: lib/features/auth/data/auth_repository.dart

  Future<void> logout() async {
    try {
      final storage = getIt<StorageService>();
      final refreshToken = await storage.readSecureData(AppKeys.refreshToken);

      if (refreshToken != null && refreshToken.isNotEmpty) {
        // 🔴 DÙNG CHÍNH apiClient.dio để nó tự gắn Access Token vào Header
        // Backend của ông CẦN Access Token ở Header để vượt qua JwtAuthGuard
        await apiClient.dio.post(
          '/api/identity/public/logout', // Kiểm tra lại path: /logout hay public/logout?
          data: {
            'refresh_token': refreshToken, // CHỈ gửi mỗi cái này vào Body thôi
          },
        );
        talker.info('✅ Đã báo Server đốt Refresh Token thành công!');
      }
    } catch (e) {
      // Nếu nó báo 401 ở đây (do Access Token hết hạn), kệ nó!
      // Vì máy mình vẫn sẽ được dọn sạch ở bước sau trong Cubit.
      talker.error('❌ Server không cho logout hoặc Token hết hạn: $e');
    }
  }
}
