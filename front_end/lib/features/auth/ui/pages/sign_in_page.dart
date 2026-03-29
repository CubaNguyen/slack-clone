// features/auth/ui/pages/sign_in_page.dart
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import '../../../../shared/widgets/auth_footer.dart';
import '../../logic/auth_cubit.dart';
import '../../logic/auth_state.dart';
import '../widgets/auth_header.dart';
import '../widgets/sign_in_form.dart'; // Import mới
import '../widgets/social_auth_section.dart'; // Import mới

class SignInPage extends StatelessWidget {
  const SignInPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: BlocListener<AuthCubit, AuthState>(
        listener: (context, state) {
          if (state is AuthSuccess) {
            // Tắt dòng SnackBar này đi vì chuyển trang luôn rồi thì ko cần hiện thông báo nữa
            // ScaffoldMessenger.of(context).showSnackBar(...);

            // 1. Dùng go_router để chuyển trang và kiểm tra profile
            if (state.isProfileCompleted) {
              // context.go('/home');
              context.go('/gateway');
            } else {
              context.go('/setup-profile');
            }
          } else if (state is AuthFailure) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Row(
                  children: [
                    const Icon(Icons.error_outline, color: Colors.white),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        state.message, // Lời nhắn từ API
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ],
                ),
                backgroundColor: Colors.redAccent,
                behavior:
                    SnackBarBehavior.floating, // Nổi lên chứ không dính đáy
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10), // Bo góc
                ),
                margin: const EdgeInsets.all(20), // Cách lề ra cho đẹp
                duration: const Duration(seconds: 3), // 3 giây tự tắt
                elevation: 0, // Bỏ bóng mờ đi cho phẳng (Flat design)
              ),
            );
          }
        },
        child: _buildBody(),
      ),
    );
  }

  Widget _buildBody() {
    return Container(
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [Color(0xFFF8EEFF), Colors.white],
          stops: [0.0, 0.7],
        ),
      ),
      child: const SafeArea(
        child: SingleChildScrollView(
          padding: EdgeInsets.symmetric(horizontal: 24.0, vertical: 32.0),
          child: Column(
            children: [
              AuthHeader(),
              SizedBox(height: 40),
              SignInForm(), // Đã refactor
              SizedBox(height: 24),
              SocialAuthSection(), // Đã refactor
              SizedBox(height: 48),
              AuthFooter(),
            ],
          ),
        ),
      ),
    );
  }
}
