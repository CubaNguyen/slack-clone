import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class LuminaAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;

  const LuminaAppBar({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    return AppBar(
      backgroundColor: Colors.transparent,
      elevation: 0,
      leadingWidth: 200,
      leading: GestureDetector(
        onTap: () async {
          // 1. Hiện một cái xác nhận nhẹ nhàng (Optional nhưng nên có)
          bool? confirm = await showDialog(
            context: context,
            builder: (context) => AlertDialog(
              title: const Text('Quay lại Đăng ký?'),
              content: const Text(
                'Mã xác thực đã được gửi. Nếu quay lại, ông sẽ phải nhập thông tin đăng ký từ đầu đấy nhé!',
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(context, false),
                  child: const Text('Ở lại'),
                ),
                TextButton(
                  onPressed: () => Navigator.pop(context, true),
                  child: const Text('Quay lại'),
                ),
              ],
            ),
          );

          if (confirm == true) {
            // 3. Đá về trang Đăng ký (Dùng .go để reset stack, không lo đen màn hình)
            if (context.mounted) {
              context.go('/sign-up');
            }
          }
        },
        child: Row(
          children: [
            const SizedBox(width: 16),
            const Icon(Icons.arrow_back, color: Color(0xFF4A154B), size: 20),
            const SizedBox(width: 8),
            Text(
              title,
              style: const TextStyle(
                color: Color(0xFF4A154B),
                fontWeight: FontWeight.bold,
                fontSize: 16,
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
