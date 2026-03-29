import 'package:flutter/material.dart';

import '../../../../shared/widgets/auth_footer.dart'; // Đổi đường dẫn theo thực tế của bạn
// Tái sử dụng lại các Component đã tạo
import '../widgets/auth_input_field.dart';

class ForgotPasswordPage extends StatefulWidget {
  const ForgotPasswordPage({super.key});

  @override
  State<ForgotPasswordPage> createState() => _ForgotPasswordPageState();
}

class _ForgotPasswordPageState extends State<ForgotPasswordPage> {
  final _emailController = TextEditingController();

  @override
  void dispose() {
    _emailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        // Tái sử dụng nền Gradient giống trang Sign In
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFFF8EEFF), Colors.white],
            stops: [0.0, 0.7],
          ),
        ),
        child: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(
              horizontal: 24.0,
              vertical: 32.0,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                // Header (The Fluid Workspace)
                Row(
                  children: [
                    const Icon(
                      Icons.blur_on,
                      color: Color(0xFF814F85),
                      size: 28,
                    ), // Logo mẫu
                    const SizedBox(width: 8),
                    const Text(
                      'The Fluid Workspace',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF4A154B),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 40),

                // Card Form
                Container(
                  padding: const EdgeInsets.all(24.0),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.05),
                        blurRadius: 20,
                        offset: const Offset(0, 10),
                      ),
                    ],
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Reset Password',
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                      const SizedBox(height: 12),
                      const Text(
                        "Enter your email address and we'll send you instructions to reset your password.",
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey,
                          height: 1.4,
                        ),
                      ),
                      const SizedBox(height: 32),

                      // Tái sử dụng AuthInputField
                      AuthInputField(
                        label: 'EMAIL ADDRESS',
                        hintText: 'name@company.com',
                        controller: _emailController,
                      ),
                      const SizedBox(height: 32),

                      // Nút Send Reset Link
                      ElevatedButton(
                        onPressed: () {
                          // Logic gửi email reset password
                          debugPrint(
                            'Gửi link reset tới: ${_emailController.text}',
                          );
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF814F85), // Màu tím
                          minimumSize: const Size(double.infinity, 50),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          elevation: 0,
                        ),
                        child: const Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              'Send Reset Link',
                              style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                            SizedBox(width: 8),
                            Icon(
                              Icons.arrow_forward,
                              color: Colors.white,
                              size: 18,
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 32),

                      // Đường phân cách mờ ở giữa (tùy chọn theo design)
                      Center(
                        child: Container(
                          width: 40,
                          height: 2,
                          color: Colors.grey[200],
                        ),
                      ),
                      const SizedBox(height: 24),

                      // Nút Back to Sign In
                      Center(
                        child: GestureDetector(
                          onTap: () {
                            // Quay lại trang Sign In (Rút trang hiện tại ra khỏi Stack)
                            Navigator.pop(context);
                          },
                          child: const Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(
                                Icons.arrow_back,
                                color: Color(0xFF814F85),
                                size: 16,
                              ),
                              SizedBox(width: 8),
                              Text(
                                'Back to Sign In',
                                style: TextStyle(
                                  color: Color(0xFF814F85),
                                  fontWeight: FontWeight.bold,
                                  fontSize: 14,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),

                // Đẩy footer xuống dưới
                const SizedBox(height: 60),

                // Tái sử dụng Footer
                const AuthFooter(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
