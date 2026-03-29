import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/core/network/api_client.dart';

import '../data/workspace_repository.dart';
import 'workspace_state.dart';

class WorkspaceCubit extends Cubit<WorkspaceState> {
  final WorkspaceRepository _workspaceRepository;

  WorkspaceCubit(this._workspaceRepository) : super(WorkspaceInitial());

  Future<void> createWorkspace(String name, String slug) async {
    emit(WorkspaceLoading()); // Bật spinner
    try {
      final newWorkspace = await _workspaceRepository.createWorkspace(
        name,
        slug,
      );

      // Thành công thì ném cái workspace ra cho UI biết
      emit(WorkspaceCreateSuccess(newWorkspace));
    } catch (e) {
      // Cắt bỏ chữ Exception xấu xí
      emit(WorkspaceFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }

  Future<void> fetchMyWorkspaces() async {
    emit(WorkspaceLoading());
    try {
      final workspaces = await _workspaceRepository.getWorkspaces();
      talker.debug(
        'Fetched workspaces: ${workspaces.toString()}',
      ); // Log số lượng workspace nhận được
      emit(WorkspaceListLoadSuccess(workspaces));
    } catch (e) {
      emit(WorkspaceFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }
}
