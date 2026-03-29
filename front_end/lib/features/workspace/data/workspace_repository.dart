import 'package:dio/dio.dart';
import 'package:front_end/features/workspace/data/model/workspace_model.dart';

import '../../../core/network/api_client.dart';

// _____________________________________________________________________________
// * REPOSITORY: Quản lý các call API liên quan đến Workspace
// _____________________________________________________________________________
class WorkspaceRepository {
  final ApiClient apiClient;

  WorkspaceRepository(this.apiClient);

  // _____________________________________________________________________________
  // * TÍNH NĂNG: Tạo mới Workspace
  // _____________________________________________________________________________
  Future<WorkspaceModel> createWorkspace(String name, String slug) async {
    try {
      // _____________________________________________________________________________
      // TODO: Đổi URL chỗ này cho đúng với endpoint thực tế của Backend
      // _____________________________________________________________________________
      final response = await apiClient.dio.post(
        '/api/ws/workspaces',
        data: {'name': name, 'slug': slug},
      );

      if (response.data['success'] == true) {
        return WorkspaceModel.fromJson(response.data['data']);
      } else {
        throw Exception(response.data['message'] ?? 'Tạo Workspace thất bại');
      }
    } on DioException catch (e) {
      // _____________________________________________________________________________
      // ! CẢNH BÁO: Lưu ý cấu trúc trả về của Backend bắt buộc phải có format {"error": {"code": ...}} để logic này không bị crash
      // _____________________________________________________________________________
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

  // _____________________________________________________________________________
  // * TÍNH NĂNG: Lấy danh sách Workspace
  // _____________________________________________________________________________
  Future<List<WorkspaceModel>> getWorkspaces() async {
    try {
      // _____________________________________________________________________________
      // ? CẦN XEM LẠI: Hàm tạo dùng '/api/ws/workspace', nhưng hàm get list lại dùng '/api/workspaces'. Cần check lại với Backend xem có viết nhầm không.
      // _____________________________________________________________________________
      final response = await apiClient.dio.get('/api/ws/workspaces/mine');

      if (response.data['success'] == true) {
        final List<dynamic> dataList = response.data['data'] ?? [];
        talker.debug(
          'Raw workspace data from API: ${dataList.toString()}',
        ); // Log dữ liệu thô nhận được
        return dataList.map((json) => WorkspaceModel.fromJson(json)).toList();
      } else {
        throw Exception(
          response.data['message'] ?? 'Lấy danh sách Workspace thất bại',
        );
      }
    } on DioException catch (e) {
      // _____________________________________________________________________________
      // TODO: Cục try/catch bắt DioException đang bị duplicate code với hàm create. Cần viết 1 hàm HandleError dùng chung để code clean hơn.
      // _____________________________________________________________________________
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
