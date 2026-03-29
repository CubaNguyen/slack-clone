// features/auth/ui/widgets/verify_email_header.dart
import 'package:flutter/material.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:go_router/go_router.dart';

class VerifyEmailHeader extends StatelessWidget {
  // 🔴 THÊM BIẾN EMAIL VÀO ĐÂY
  final String email;

  const VerifyEmailHeader({super.key, required this.email});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Phần Icon
        Container(
          width: 80,
          height: 80,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(20),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 20,
                offset: const Offset(0, 10),
              ),
            ],
          ),
          child: const Icon(
            Icons.mark_email_read_rounded,
            color: Color(0xFF814F85),
            size: 40,
          ),
        ),
        const SizedBox(height: 32),
        const Text(
          'Verify your email',
          style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 16),
        RichText(
          textAlign: TextAlign.center,
          text: TextSpan(
            style: const TextStyle(
              color: Colors.grey,
              fontSize: 14,
              height: 1.5,
            ),
            children: [
              const TextSpan(text: "Mã xác thực 8 số đã được gửi đến\n"),
              TextSpan(
                text: email, // 🔴 DÙNG TRỰC TIẾP BIẾN email
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.black87,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 8),
        GestureDetector(
          onTap: () async {
            // 1. User bảo sai rồi, dọn rác thôi!
            await getIt<StorageService>().deleteData(AppKeys.pendingUserId);
            await getIt<StorageService>().deleteData(AppKeys.pendingUserEmail);
            // 2. Đá về trang Sign Up để nhập lại từ đầu
            if (context.mounted) {
              context.go('/sign-up');
            }
          },
          child: const Text(
            'Sai email? Bấm vào đây để sửa',
            style: TextStyle(
              color: Colors.redAccent,
              fontSize: 12,
              decoration: TextDecoration.underline,
            ),
          ),
        ),
      ], // 🔴 ĐÓNG COLUMN
    );
  }
}
