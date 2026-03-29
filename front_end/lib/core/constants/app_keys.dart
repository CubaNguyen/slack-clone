// _____________________________________________________________________________
// * FILE: lib/core/constants/app_keys.dart
// * Chứa toàn bộ khóa (keys) dùng cho SharedPreferences hoặc SecureStorage.
// _____________________________________________________________________________

class AppKeys {
  // _____________________________________________________________________________
  // * AUTH KEYS
  // _____________________________________________________________________________
  static const String accessToken = 'access_token';
  static const String refreshToken = 'refresh_token';

  // _____________________________________________________________________________
  // ! CẢNH BÁO: Các key pending này chỉ dùng tạm trong luồng xác thực (OTP, đăng ký).
  // ! Nhớ clear ngay sau khi hoàn thành flow để tránh lỗi logic hoặc rò rỉ dữ liệu.
  // _____________________________________________________________________________
  static const String pendingUserId = 'pending_user_id';
  static const String pendingUserEmail = 'pending_user_email';

  // _____________________________________________________________________________
  // * USER KEYS
  // _____________________________________________________________________________
  static const String userId = 'user_id';
  static const String profileCompleted = 'profile_completed';
  static const String cachedUserData = 'cached_user_data';

  // _____________________________________________________________________________
  // * APP SETTINGS
  // _____________________________________________________________________________

  // // _____________________________________________________________________________
  // // TODO: Implement logic thay đổi theme Dark/Light mode, tạm thời define key trước.
  // // _____________________________________________________________________________
  // static const String isDarkMode = 'is_dark_mode';
}
