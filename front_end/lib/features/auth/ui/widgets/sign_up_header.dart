// features/auth/ui/widgets/sign_up_header.dart
import 'package:flutter/material.dart';

class SignUpHeader extends StatelessWidget {
  const SignUpHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return const Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(Icons.widgets, color: Colors.brown, size: 24),
            SizedBox(width: 8),
            Text(
              'The Fluid Workspace',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
          ],
        ),
        SizedBox(height: 32),
        Text(
          'Create Account',
          style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
        ),
        SizedBox(height: 8),
        Text(
          'Join your team in the workspace',
          style: TextStyle(color: Colors.grey),
        ),
      ],
    );
  }
}
