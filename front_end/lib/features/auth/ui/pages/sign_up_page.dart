// features/auth/ui/pages/sign_up_page.dart
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:go_router/go_router.dart';

import '../../logic/auth_cubit.dart';
import '../../logic/auth_state.dart';
import '../widgets/existing_account_text.dart';
import '../widgets/or_divider.dart';
import '../widgets/sign_up_form.dart';
import '../widgets/sign_up_header.dart';
import '../widgets/social_login_button.dart';

class SignUpPage extends StatefulWidget {
  const SignUpPage({super.key});

  @override
  State<SignUpPage> createState() => _SignUpPageState();
}

// Hàm hiện thông báo cho "Người quen" (Đang đăng ký dở)
void _showVerifyOption(BuildContext context, String id) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: const Text('Email này đang chờ xác thực!'),
      action: SnackBarAction(
        label: 'XÁC THỰC TIẾP',
        onPressed: () => context.push('/verify-email', extra: id),
      ),
    ),
  );
}

// Hàm hiện thông báo cho "Người lạ" (Acc đã có chủ)
void _showSignInOption(BuildContext context) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: const Text('Email này đã có người sử dụng. Ông có nhầm không?'),
      action: SnackBarAction(
        label: 'ĐĂNG NHẬP',
        onPressed: () => context.go('/sign-in'),
      ),
    ),
  );
}

class _SignUpPageState extends State<SignUpPage> {
  String? _emailServerError;

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthCubit, AuthState>(
      listener: (context, state) {
        if (state is AuthSignUpSuccess) {
          context.push(
            '/verify-email',
            extra: {'userId': state.userId, 'email': state.email},
          );
        } else if (state is AuthFailure) {
          if (state.message.contains('exists')) {
            // 1. Hiện báo đỏ dưới ô Email cho đẹp UI
            setState(() => _emailServerError = "Email này đã được đăng ký!");

            // 2. [TƯ DUY CAO THỦ]: Kiểm tra xem có ID chờ xác thực trong máy không
            final pendingId =
                getIt<StorageService>().readData(AppKeys.pendingUserId)
                    as String?;
            final pendingEmail =
                getIt<StorageService>().readData(AppKeys.pendingUserEmail)
                    as String?;
            if (pendingId != null) {
              // Nếu đúng là "người quen" đang làm dở, hiện thêm cái nút mời vào xác thực tiếp
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: const Text('Ông đang đăng ký dở đúng không?'),
                  backgroundColor: const Color(
                    0xFF4A154B,
                  ), // Màu tím Lumina cho đồng bộ
                  action: SnackBarAction(
                    label: 'XÁC THỰC NGAY',
                    textColor: Colors.yellowAccent,
                    onPressed: () {
                      // Nhảy thẳng sang màn Verify với cái ID cũ
                      context.push(
                        '/verify-email',
                        extra: {
                          'userId': pendingId,
                          'email': pendingEmail ?? "",
                        },
                      );
                    },
                  ),
                  duration: const Duration(seconds: 8),
                ),
              );
            }
          } else {
            // Các lỗi khác (sai định dạng, mất mạng...) hiện SnackBar bình thường
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Colors.redAccent,
              ),
            );
          }
        }
      },
      child: Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(
              horizontal: 24.0,
              vertical: 16.0,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SignUpHeader(),
                const SizedBox(height: 32),
                SignUpForm(
                  emailServerError: _emailServerError,
                  onEmailChanged: (_) {
                    if (_emailServerError != null)
                      setState(() => _emailServerError = null);
                  },
                ),
                const SizedBox(height: 32),
                const OrDivider(text: 'OR CONTINUE WITH'),
                const SizedBox(height: 24),
                const SocialLoginButtons(),
                const SizedBox(height: 32),
                const ExistingAccountText(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
