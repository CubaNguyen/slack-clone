import 'package:flutter/material.dart';

class AuthFooter extends StatelessWidget {
  const AuthFooter({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Text(
          '© 2024 THE FLUID WORKSPACE. EDITORIAL DIGITAL ATRIUM.',
          textAlign: TextAlign.center,
          style: TextStyle(fontSize: 10, color: Colors.grey, height: 1.5),
        ),
        const SizedBox(height: 16),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            _buildFooterLink('PRIVACY'),
            const SizedBox(width: 24),
            _buildFooterLink('TERMS'),
            const SizedBox(width: 24),
            _buildFooterLink('CONTACT'),
          ],
        ),
      ],
    );
  }

  Widget _buildFooterLink(String text) {
    return Text(
      text,
      style: const TextStyle(
        fontSize: 10,
        fontWeight: FontWeight.bold,
        color: Colors.grey,
      ),
    );
  }
}
