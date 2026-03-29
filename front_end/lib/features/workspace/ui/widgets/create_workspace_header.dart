import 'package:flutter/material.dart';

class CreateWorkspaceHeader extends StatelessWidget {
  const CreateWorkspaceHeader({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        IconButton(
          icon: const Icon(Icons.close, color: Color(0xFF5A3A65)),
          onPressed: () => Navigator.pop(context),
          padding: EdgeInsets.zero,
          constraints: const BoxConstraints(),
        ),
        const SizedBox(width: 16),
        const Text(
          'Create Workspace',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
            color: Color(0xFF5A3A65),
          ),
        ),
      ],
    );
  }
}
