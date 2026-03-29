import 'package:dio/dio.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/network/api_client.dart'; // Đổi lại đường dẫn cho đúng
import 'package:front_end/core/utils/storage_service.dart';

class ProfileRepository {
  final ApiClient apiClient;

  ProfileRepository(this.apiClient);

  Future<void> updateProfile({
    required String fullName,
    String avatarUrl = "",
    String bio = "",
  }) async {
    try {
      // 🔴 Ông thấy không? Đéo cần truyền Token gì ở đây cả, Dio tự lo!
      final response = await apiClient.dio.put(
        '/api/identity/secure/profile',
        data: {"full_name": fullName, "avatar_url": avatarUrl, "bio": bio},
      );

      if (response.data['success'] != true) {
        throw Exception(response.data['message'] ?? 'Lỗi cập nhật profile');
      }

      // 🔴 CẬP NHẬT THÀNH CÔNG -> ĐỔI CỜ "LÍNH MỚI" THÀNH "LÍNH CŨ"
      await getIt<StorageService>().writeData(AppKeys.profileCompleted, true);
    } on DioException catch (e) {
      final msg =
          e.response?.data['message'] ?? 'Lỗi hệ thống khi cập nhật Profile';
      throw Exception(msg);
    } catch (e) {
      throw Exception(e.toString());
    }
  }
}
