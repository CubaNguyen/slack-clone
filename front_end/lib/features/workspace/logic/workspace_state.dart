import 'package:equatable/equatable.dart';
import 'package:front_end/features/workspace/data/model/workspace_model.dart';

// _____________________________________________________________________________
// * BASE STATE
// _____________________________________________________________________________
abstract class WorkspaceState extends Equatable {
  const WorkspaceState();

  @override
  List<Object?> get props => [];
}

// _____________________________________________________________________________
// * COMMON STATES (Khởi tạo & Đang tải)
// _____________________________________________________________________________
class WorkspaceInitial extends WorkspaceState {}

class WorkspaceLoading extends WorkspaceState {}

// _____________________________________________________________________________
// * SUCCESS STATES (Xử lý thành công)
// _____________________________________________________________________________

// _____________________________________________________________________________
// ! LƯU Ý: Trạng thái khi TẠO THÀNH CÔNG
// _____________________________________________________________________________
class WorkspaceCreateSuccess extends WorkspaceState {
  final WorkspaceModel workspace;

  const WorkspaceCreateSuccess(this.workspace);

  @override
  List<Object?> get props => [workspace];
}

class WorkspaceListLoadSuccess extends WorkspaceState {
  final List<WorkspaceModel> workspaces;

  const WorkspaceListLoadSuccess(this.workspaces);

  @override
  List<Object?> get props => [workspaces];
}

// _____________________________________________________________________________
// * FAILURE STATE (Xử lý lỗi)
// _____________________________________________________________________________
class WorkspaceFailure extends WorkspaceState {
  final String message;

  const WorkspaceFailure(this.message);

  @override
  List<Object?> get props => [message];
}
