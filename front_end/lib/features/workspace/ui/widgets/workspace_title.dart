import 'package:flutter/material.dart';

class WorkspaceTitle extends StatelessWidget {
  const WorkspaceTitle({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
            color: const Color(0xFFFFD6F4),
            borderRadius: BorderRadius.circular(20),
          ),
          child: const Text(
            'NEW ENVIRONMENT',
            style: TextStyle(
              fontSize: 10,
              fontWeight: FontWeight.bold,
              letterSpacing: 1.2,
              color: Color(0xFFC73BA8),
            ),
          ),
        ),
        const SizedBox(height: 16),
        const Text(
          'Set up a new space\nfor your team to\ncollaborate.',
          style: TextStyle(
            fontSize: 32,
            height: 1.2,
            fontWeight: FontWeight.bold,
            color: Color(0xFF2D2D2D),
          ),
        ),
        const SizedBox(height: 12),
        const Text(
          "Your workspace is where communication happens. It's the digital atrium for your projects, files, and people.",
          style: TextStyle(fontSize: 15, height: 1.5, color: Color(0xFF6E6E6E)),
        ),
      ],
    );
  }
}
