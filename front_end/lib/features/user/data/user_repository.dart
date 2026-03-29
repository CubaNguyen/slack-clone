import 'package:dio/dio.dart';
import 'package:front_end/features/user/data/model/user_model.dart';

import '../../../core/network/api_client.dart';

class UserRepository {
  final ApiClient apiClient;

  UserRepository(this.apiClient);

  Future<UserModel> getUserProfile() async {
    try {
      final response = await apiClient.dio.get('/api/identity/secure/users/me');

      if (response.data['success'] == true) {
        return UserModel.fromJson(response.data['data']);
      } else {
        throw Exception(
          response.data['message'] ?? 'Không thể tải thông tin user',
        );
      }
    } on DioException catch (e) {
      if (e.response != null && e.response?.data != null) {
        throw Exception(e.response?.data['message'] ?? 'Lỗi máy chủ');
      }
      throw Exception('Lỗi kết nối: ${e.message}');
    } catch (e) {
      throw Exception(e.toString());
    }
  }
}
