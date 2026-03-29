import 'package:flutter/material.dart';
import 'package:front_end/features/workspace/ui/widgets/action_buttons.dart';
import 'package:front_end/features/workspace/ui/widgets/custom_input_field.dart';
import 'package:front_end/features/workspace/ui/widgets/terms_footer.dart';
import 'package:front_end/features/workspace/ui/widgets/workspace_header.dart';
import 'package:front_end/features/workspace/ui/widgets/workspace_title.dart';

class CreateWorkspaceScreen extends StatefulWidget {
  const CreateWorkspaceScreen({Key? key}) : super(key: key);

  @override
  State<CreateWorkspaceScreen> createState() => _CreateWorkspaceScreenState();
}

class _CreateWorkspaceScreenState extends State<CreateWorkspaceScreen> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _slugController = TextEditingController();

  @override
  void initState() {
    super.initState();
    // Lắng nghe thay đổi từ trường Name để tự động tạo Slug
    _nameController.addListener(_generateSlug);
  }

  void _generateSlug() {
    String name = _nameController.text;
    String slug = name.toLowerCase().replaceAll(RegExp(r'[^a-z0-9]+'), '-');
    // Xóa dấu gạch ngang ở đầu và cuối nếu có
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
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F7), // Màu nền sáng của page
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const WorkspaceHeader(),
              const SizedBox(height: 24),
              const WorkspaceTitle(),
              const SizedBox(height: 32),

              // Khối Form nền trắng chứa các input và nút
              Container(
                padding: const EdgeInsets.all(24.0),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(24.0),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.03),
                      blurRadius: 10,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Column(
                  children: [
                    CustomInputField(
                      label: 'Workspace Name',
                      hintText: 'e.g. K14 Software Team',
                      controller: _nameController,
                      helperText:
                          'This name will be visible to everyone in the workspace.',
                    ),
                    const SizedBox(height: 24),
                    CustomInputField(
                      label: 'Workspace URL (Slug)',
                      hintText: 'your-team-slug',
                      controller: _slugController,
                      prefixText: 'atrium.app/',
                      helperText: 'Your unique address for quick access.',
                    ),
                    const SizedBox(height: 32),
                    const ActionButtons(),
                    const SizedBox(height: 24),
                    const TermsFooter(),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
