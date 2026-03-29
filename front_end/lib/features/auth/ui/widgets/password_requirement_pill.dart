import 'package:flutter/material.dart';

class PasswordRequirementPill extends StatelessWidget {
  final String text;
  final bool
  isMet; // Biến này kiểm soát trạng thái Đạt (Xanh) hay Chưa đạt (Xám)

  const PasswordRequirementPill({
    super.key,
    required this.text,
    this.isMet = false,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: isMet
            ? const Color(0xFFE8F5E9)
            : const Color(0xFFF5F5F7), // Xanh nhạt hoặc Xám nhạt
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            isMet ? Icons.check_circle : Icons.circle,
            color: isMet ? Colors.green : Colors.grey[400],
            size: 14,
          ),
          const SizedBox(width: 6),
          Text(
            text,
            style: TextStyle(
              fontSize: 11,
              fontWeight: FontWeight.bold,
              color: isMet ? Colors.green[800] : Colors.grey[600],
            ),
          ),
        ],
      ),
    );
  }
}
