import 'package:flutter/material.dart';
import 'package:front_end/features/auth/ui/pages/sign_in_page.dart';
// Nhớ import file Sign In vào đây nhé!
// import '../pages/sign_in_page.dart';

class ExistingAccountText extends StatelessWidget {
  const ExistingAccountText({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      // Bọc GestureDetector ở ngoài cùng để bắt sự kiện click
      child: GestureDetector(
        onTap: () {
          // Thay thế trang Đăng Ký bằng trang Đăng Nhập
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => const SignInPage()),
          );
        },
        child: RichText(
          text: const TextSpan(
            style: TextStyle(
              color: Colors.grey,
              fontSize: 14,
              fontWeight: FontWeight.w400,
            ),
            children: [
              TextSpan(text: 'Already have an account? '),
              TextSpan(
                text: 'Sign In',
                style: TextStyle(
                  color: Colors.blue,
                  fontWeight: FontWeight.bold,
                ),
              ), // Bạn có thể đổi màu xanh này thành màu tím cho đồng bộ
            ],
          ),
        ),
      ),
    );
  }
}
