import 'package:dio/dio.dart';
import 'package:front_end/features/channels/data/model/channel_model.dart';

import '../../../core/network/api_client.dart';

class ChannelRepository {
  final ApiClient apiClient;

  ChannelRepository(this.apiClient);

  Future<List<ChannelModel>> getChannels(String workspaceId) async {
    try {
      final response = await apiClient.dio.get(
        '/api/workspaces/$workspaceId/channels',
      );

      if (response.data['success'] == true) {
        // Lấy mảng data ra
        final List<dynamic> dataList = response.data['data'] ?? [];

        // Map từng cục JSON thành ChannelModel
        return dataList.map((json) => ChannelModel.fromJson(json)).toList();
      } else {
        throw Exception(
          response.data['message'] ?? 'Không tải được danh sách kênh',
        );
      }
    } on DioException catch (e) {
      if (e.response != null && e.response?.data != null) {
        final errorObj = e.response?.data['error'];
        final serverMessage = errorObj != null
            ? errorObj['code']
            : 'Lỗi Server';
        throw Exception(serverMessage);
      }
      throw Exception('Lỗi kết nối: ${e.message}');
    } catch (e) {
      throw Exception(e.toString());
    }
  }
}
