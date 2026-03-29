import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/features/workspace/logic/workspace_cubit.dart';
import 'package:front_end/features/workspace/logic/workspace_state.dart';
import 'package:go_router/go_router.dart';

class WorkspaceGatewayPage extends StatefulWidget {
  const WorkspaceGatewayPage({super.key});

  @override
  State<WorkspaceGatewayPage> createState() => _WorkspaceGatewayPageState();
}

class _WorkspaceGatewayPageState extends State<WorkspaceGatewayPage> {
  @override
  void initState() {
    super.initState();
    // Vừa vào phòng chờ là gọi API check danh sách workspace ngay
    context.read<WorkspaceCubit>().fetchMyWorkspaces();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: BlocListener<WorkspaceCubit, WorkspaceState>(
        listener: (context, state) {
          if (state is WorkspaceListLoadSuccess) {
            if (state.workspaces.isEmpty) {
              context.go('/welcome-workspace');
            } else {
              context.go('/home');
            }
          } else if (state is WorkspaceFailure) {
            // Xử lý lỗi nếu rớt mạng (ví dụ hiện popup báo lỗi)
          }
        },
        child: const Center(
          // UI của trạm trung chuyển chỉ là cái vòng xoay
          child: CircularProgressIndicator(),
        ),
      ),
    );
  }
}
