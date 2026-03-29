import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:front_end/features/auth/logic/auth_cubit.dart';
import 'package:front_end/features/auth/logic/auth_state.dart';
import 'package:front_end/features/auth/ui/widgets/lumina_app_bar.dart';
import 'package:front_end/features/auth/ui/widgets/otp_input_mock.dart';
import 'package:front_end/features/auth/ui/widgets/resend_code_section.dart';
import 'package:front_end/features/auth/ui/widgets/verify_email_header.dart';
import 'package:front_end/shared/widgets/auth_footer.dart';
import 'package:go_router/go_router.dart';

class VerifyEmailPage extends StatefulWidget {
  final String userId;
  final String email;

  const VerifyEmailPage({super.key, required this.userId, required this.email});

  @override
  State<VerifyEmailPage> createState() => _VerifyEmailPageState();
}

class _VerifyEmailPageState extends State<VerifyEmailPage> {
  final TextEditingController _otpController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return BlocListener<AuthCubit, AuthState>(
      listener: (context, state) async {
        if (state is AuthVerifySuccess) {
          await getIt<StorageService>().deleteData(AppKeys.pendingUserId);
          await getIt<StorageService>().deleteData(AppKeys.pendingUserEmail);
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Xác thực thành công! Vui lòng đăng nhập.'),

              backgroundColor: Colors.green,
            ),
          );

          context.go('/sign-in');
        }
        // 🔴 THÊM KHÚC NÀY ĐỂ BÁO CÁO SAU KHI GỬI LẠI MÃ THÀNH CÔNG
        else if (state is AuthResendSuccess) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Đã gửi lại mã mới. Ông check mail nhé!'),
              backgroundColor: Colors.green,
            ),
          );
        } else if (state is AuthFailure) {
          // 2. Báo lỗi nếu mã sai hoặc hết hạn

          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(state.message),

              backgroundColor: Colors.redAccent,
            ),
          );
        }
      },
      child: Scaffold(
        backgroundColor: const Color(0xFFFAFAFA),
        appBar: const LuminaAppBar(
          title: 'Lumina Workspace',
        ), // Có thể tách tiếp AppBar nếu dùng nhiều
        body: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 24.0),
            child: Column(
              children: [
                const SizedBox(height: 40),
                VerifyEmailHeader(email: widget.email), // Class mới
                const SizedBox(height: 32),
                OtpInputMock(
                  controller: _otpController,
                ), // Nhớ truyền controller vào
                const SizedBox(height: 32),
                _buildVerifyButton(), // Có thể giữ lại hàm này nếu nó quá ngắn
                const SizedBox(height: 24),
                ResendCodeSection(email: widget.email),
                const SizedBox(height: 80),
                const AuthFooter(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  // Nút bấm nên dùng BlocBuilder để handle Loading state
  Widget _buildVerifyButton() {
    return BlocBuilder<AuthCubit, AuthState>(
      builder: (context, state) {
        return ElevatedButton(
          onPressed: state is AuthLoading
              ? null
              : () {
                  // Lấy mã từ ô nhập (Ví dụ ông nhập RXZ0RXMQ vào ô OTP)
                  final code = _otpController.text.trim();

                  if (code.length == 8) {
                    context.read<AuthCubit>().verifyEmail(widget.userId, code);
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text(
                          'Mã xác thực phải đủ 8 ký tự ông giáo ạ!',
                        ),
                      ),
                    );
                  }
                },
          // ... style giữ nguyên ...
          child: state is AuthLoading
              ? const SizedBox(
                  height: 20,
                  width: 20,
                  child: CircularProgressIndicator(
                    color: Colors.white,
                    strokeWidth: 2,
                  ),
                )
              : const Text('Verify'),
        );
      },
    );
  }
}
