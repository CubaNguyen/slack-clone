// File: lib/features/auth/ui/pages/splash_page.dart

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import '../../logic/auth_cubit.dart';
import '../../logic/auth_state.dart';

// 1. ĐỔI THÀNH StatefulWidget ĐỂ DÙNG ĐƯỢC initState
class SplashPage extends StatefulWidget {
  const SplashPage({super.key});

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  @override
  void initState() {
    super.initState();
    // 2. 🔴 LỆNH CỨU MẠNG NẰM Ở ĐÂY 🔴
    // Vừa mở màn hình lên là đá đít thằng Cubit đi kiểm tra vé (Token) ngay!
    context.read<AuthCubit>().checkAuthStatus();
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthCubit, AuthState>(
      listener: (context, state) {
        // 1. Nếu là AuthSuccess -> Chỉ vào Home, ĐỪNG gọi userId ở đây
        if (state is AuthSuccess) {
          if (state.isProfileCompleted) {
            // context.go('/home');
            context.go('/gateway');
          } else {
            context.go(
              '/setup-profile',
            ); // Chưa có Profile -> Bắt ra phường làm căn cước
          }
        }
        // 2. Nếu là AuthSignUpSuccess -> Lúc này mới được gọi userId
        else if (state is AuthSignUpSuccess) {
          // 🔴 CHỖ NÀY MỚI CÓ userId NÈ 🔴
          context.go(
            '/verify-email',
            extra: {'userId': state.userId, 'email': state.email},
          );
        } else if (state is AuthInitial || state is AuthFailure) {
          context.go('/sign-in');
        }
      },
      child: const Scaffold(
        backgroundColor: Color(0xFF4A1A4A),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.grid_view_rounded, size: 80, color: Colors.white),
              SizedBox(height: 24),
              CircularProgressIndicator(color: Colors.white),
            ],
          ),
        ),
      ),
    );
  }
}
