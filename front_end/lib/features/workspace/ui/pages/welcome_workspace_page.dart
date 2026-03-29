import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/features/workspace/logic/workspace_cubit.dart';
// 🔴 BẮT BUỘC IMPORT FILE STATE NÀY VÀO
import 'package:front_end/features/workspace/logic/workspace_state.dart';
import 'package:go_router/go_router.dart'; // Gọi GoRouter để chuyển trang

class WelcomeWorkspacePage extends StatefulWidget {
  const WelcomeWorkspacePage({super.key});

  @override
  State<WelcomeWorkspacePage> createState() => _WelcomeWorkspacePageState();
}

class _WelcomeWorkspacePageState extends State<WelcomeWorkspacePage> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _slugController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _nameController.addListener(_generateSlug);
  }

  void _generateSlug() {
    String name = _nameController.text;
    String slug = name.toLowerCase().replaceAll(RegExp(r'[^a-z0-9]+'), '-');
    if (slug.startsWith('-')) slug = slug.substring(1);
    if (slug.endsWith('-')) slug = slug.substring(0, slug.length - 1);
    _slugController.text = slug;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _slugController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // 🔴 1. BỌC TOÀN BỘ SCAFFOLD BẰNG BLOC-LISTENER
    return BlocListener<WorkspaceCubit, WorkspaceState>(
      listener: (context, state) {
        // Nếu tạo thành công -> Bắn thông báo và chuyển trang
        if (state is WorkspaceCreateSuccess) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Tạo Workspace thành công!'),
              backgroundColor: Colors.green,
            ),
          );

          // Chuyển sang màn hình MainLayout (đổi '/home' thành tên route thực tế của bạn)
          context.go('/home');
        }

        // Nếu thất bại -> Báo lỗi báo đỏ
        if (state is WorkspaceFailure) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Lỗi: ${state.message}'),
              backgroundColor: Colors.red,
            ),
          );
        }
      },
      child: Scaffold(
        backgroundColor: const Color(0xFFF5F5F7), // Nền xám nhạt
        body: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  // Icon trang trí
                  Container(
                    padding: const EdgeInsets.all(20),
                    decoration: BoxDecoration(
                      color: const Color(0xFFE8E4D9),
                      borderRadius: BorderRadius.circular(24),
                    ),
                    child: const Icon(
                      Icons.hub_outlined,
                      size: 64,
                      color: Color(0xFF4A154B),
                    ),
                  ),
                  const SizedBox(height: 32),

                  // Tiêu đề chào mừng
                  const Text(
                    'Chào mừng đến với Lumina!',
                    style: TextStyle(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF2D2D2D),
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  const Text(
                    'Bạn chưa tham gia không gian làm việc nào.\nHãy tạo một cái mới để bắt đầu cùng team nhé.',
                    style: TextStyle(
                      fontSize: 16,
                      height: 1.5,
                      color: Color(0xFF6E6E6E),
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 40),

                  // Form nhập liệu
                  Container(
                    padding: const EdgeInsets.all(24),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(20),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.05),
                          blurRadius: 15,
                          offset: const Offset(0, 5),
                        ),
                      ],
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        _buildTextField(
                          label: 'Tên Workspace',
                          hintText: 'VD: K14 Software Team',
                          controller: _nameController,
                        ),
                        const SizedBox(height: 20),
                        _buildTextField(
                          label: 'Đường dẫn (Slug)',
                          hintText: 'k14-software-team',
                          controller: _slugController,
                          prefixText: 'lumina.app/',
                        ),
                        const SizedBox(height: 32),

                        // 🔴 2. BỌC NÚT BẤM BẰNG BLOC-BUILDER ĐỂ HIỆN LOADING
                        BlocBuilder<WorkspaceCubit, WorkspaceState>(
                          builder: (context, state) {
                            final isLoading = state is WorkspaceLoading;

                            return ElevatedButton(
                              // Nếu đang load thì khóa nút (tránh user bấm 2 lần liên tục)
                              onPressed: isLoading
                                  ? null
                                  : () {
                                      final name = _nameController.text.trim();
                                      final slug = _slugController.text.trim();

                                      if (name.isNotEmpty && slug.isNotEmpty) {
                                        context
                                            .read<WorkspaceCubit>()
                                            .createWorkspace(name, slug);
                                      } else {
                                        ScaffoldMessenger.of(
                                          context,
                                        ).showSnackBar(
                                          const SnackBar(
                                            content: Text(
                                              'Vui lòng nhập đầy đủ thông tin!',
                                            ),
                                          ),
                                        );
                                      }
                                    },
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(
                                  0xFF4A154B,
                                ), // Tím đậm
                                disabledBackgroundColor: const Color(
                                  0xFF4A154B,
                                ).withOpacity(0.6), // Tím mờ khi đang load
                                padding: const EdgeInsets.symmetric(
                                  vertical: 16,
                                ),
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                elevation: 0,
                              ),
                              // Nếu đang load thì hiện vòng xoay, không thì hiện Text
                              child: isLoading
                                  ? const SizedBox(
                                      height: 20,
                                      width: 20,
                                      child: CircularProgressIndicator(
                                        color: Colors.white,
                                        strokeWidth: 2.5,
                                      ),
                                    )
                                  : const Text(
                                      'Tạo Workspace',
                                      style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                        color: Colors.white,
                                      ),
                                    ),
                            );
                          },
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  // Hàm build TextField tái sử dụng
  Widget _buildTextField({
    required String label,
    required String hintText,
    required TextEditingController controller,
    String? prefixText,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.bold,
            color: Color(0xFF333333),
          ),
        ),
        const SizedBox(height: 8),
        TextField(
          controller: controller,
          decoration: InputDecoration(
            hintText: hintText,
            prefixText: prefixText,
            prefixStyle: const TextStyle(
              color: Color(0xFF888888),
              fontSize: 15,
            ),
            filled: true,
            fillColor: const Color(0xFFF3F3F5),
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 16,
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: BorderSide.none,
            ),
          ),
        ),
      ],
    );
  }
}
