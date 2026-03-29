// File: lib/features/auth/logic/auth_cubit.dart
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/network/api_client.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

import '../../../core/constants/app_keys.dart';
import '../data/auth_repository.dart';
import 'auth_state.dart';

class AuthCubit extends Cubit<AuthState> {
  final AuthRepository _authRepository;

  AuthCubit(this._authRepository) : super(AuthInitial());

  // Lưu ý: Đổi tên hàm thành login cho giống với file UI mình gọi lúc nãy,
  // hoặc bên UI bạn phải gọi là signIn nhé.
  Future<void> login(String email, String password) async {
    emit(AuthLoading());
    try {
      // Bây giờ Repo trả về true/false (isCompleted) chứ không phải String nữa
      final isCompleted = await _authRepository.login(email, password);

      // Bắn trạng thái Thành Công kèm theo cờ isCompleted
      emit(AuthSuccess(isProfileCompleted: isCompleted));
    } catch (e) {
      // Bắt lỗi và chỉ hiển thị nội dung lỗi (bỏ chữ 'Exception: ' đi cho đẹp)
      emit(AuthFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }

  Future<void> checkAuthStatus() async {
    emit(AuthLoading()); // Bật spinner (Splash Screen)

    try {
      // Delay nhẹ cho mượt hiệu ứng Splash
      await Future.delayed(const Duration(milliseconds: 500));

      final storage = getIt<StorageService>();
      final accessToken = await storage.readSecureData(AppKeys.accessToken);
      // --- TẦNG 1: ĐÃ CÓ TOKEN TRONG MÁY ---
      if (accessToken != null && accessToken.isNotEmpty) {
        // Kiểm tra hạn sử dụng của Access Token
        if (JwtDecoder.isExpired(accessToken)) {
          talker.warning('🚨 Access Token thiu! Đang cố hồi sinh từ Splash...');

          // GỌI HÀM TỪ REPOSITORY
          final isRefreshed = await _authRepository.refreshToken();

          if (isRefreshed) {
            talker.info('✅ Hồi sinh thành công! Vào nhà thôi.');
            final isCompleted = await storage.readData(
              AppKeys.profileCompleted,
            );
            emit(AuthSuccess(isProfileCompleted: isCompleted));
            return;
          } else {
            talker.error('❌ Refresh Token cũng thiu nốt. Sút ra Login!');
            await storage.clearAllAuthData();
            emit(AuthInitial());
            return;
          }
        }

        // Nếu Token vẫn còn hạn sử dụng bình thường
        final isCompleted = await storage.readData(AppKeys.profileCompleted);
        emit(AuthSuccess(isProfileCompleted: isCompleted));
        return;
      }

      // --- TẦNG 2: ĐANG ĐĂNG KÝ DỞ (CHỜ OTP) ---
      final pendingUserId = await storage.readData(AppKeys.pendingUserId);
      final pendingEmail = await storage.readData(AppKeys.pendingUserEmail);
      if (pendingUserId != null && pendingUserId.isNotEmpty) {
        talker.info('📌 Phát hiện đăng ký dở, quay lại trang OTP...');
        emit(AuthSignUpSuccess(pendingUserId, pendingEmail ?? ''));
        return;
      }

      // --- TẦNG 3: NGƯỜI MỚI HOÀN TOÀN ---
      talker.info('👋 Người mới hoặc đã logout. Chào mừng tới Login!');
      emit(AuthInitial());
    } catch (e) {
      talker.error("🚨 LỖI CRITICAL TẠI SPLASH: $e");
      emit(AuthInitial());
    }
  }

  Future<void> signUp(String email, String password) async {
    emit(AuthLoading());
    try {
      final userId = await _authRepository.signUp(email, password);
      // Hét lên: Đăng ký xong rồi, trả userId cho giao diện chuyển trang!

      final storage = getIt<StorageService>();
      await storage.writeData(AppKeys.pendingUserId, userId);
      await storage.writeData(AppKeys.pendingUserEmail, email);

      emit(AuthSignUpSuccess(userId, email));
    } catch (e) {
      emit(AuthFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }

  // CÔNG TẮC 2: Bấm nút Xác nhận mã OTP
  Future<void> verifyEmail(String userId, String code) async {
    emit(AuthLoading());
    try {
      await _authRepository.verifyEmail(userId, code);
      // Hét lên: Xác thực xong rồi!
      emit(AuthVerifySuccess());
    } catch (e) {
      emit(AuthFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }

  Future<void> resendVerification(String email) async {
    emit(AuthLoading()); // Vẫn hiện vòng quay loading
    try {
      await _authRepository.resendVerification(email);
      emit(AuthResendSuccess()); // Báo thành công
    } catch (e) {
      emit(AuthFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }

  Future<void> logout() async {
    talker.warning('Bắt đầu quá trình Logout...');

    await _authRepository.logout();

    final storage = getIt<StorageService>();
    await storage.clearAllAuthData();

    talker.info('Đã dọn dẹp xong Storage. Phát lệnh AuthInitial.');

    // 3. Đổi trạng thái để văng ra Login
    emit(AuthInitial());
  }
}
