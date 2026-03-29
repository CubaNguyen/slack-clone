import 'package:flutter/material.dart';

import '../../../../shared/widgets/auth_footer.dart';
import '../widgets/auth_input_field.dart';
import '../widgets/privacy_info_card.dart';

class ChangePasswordPage extends StatelessWidget {
  const ChangePasswordPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFAFAFA),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leadingWidth: 200,
        leading: GestureDetector(
          onTap: () => Navigator.pop(context),
          child: const Row(
            children: [
              SizedBox(width: 16),
              Icon(Icons.arrow_back, color: Color(0xFF4A154B), size: 20),
              SizedBox(width: 8),
              Text(
                'Lumina Workspace',
                style: TextStyle(
                  color: Color(0xFF4A154B),
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                ),
              ),
            ],
          ),
        ),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 20),
              const Text(
                'Security Portal',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.black,
                ),
              ),
              const SizedBox(height: 8),
              const Text(
                'Update your credentials to maintain the\nintegrity of your digital atrium.',
                style: TextStyle(fontSize: 14, color: Colors.grey, height: 1.5),
              ),
              const SizedBox(height: 32),

              // Box chứa các form nhập liệu
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(16),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.03),
                      blurRadius: 20,
                      offset: const Offset(0, 10),
                    ),
                  ],
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    AuthInputField(
                      label: 'CURRENT PASSWORD',
                      hintText: '••••••••',
                      controller: TextEditingController(),
                      isPassword: true,
                    ),
                    const SizedBox(height: 20),

                    AuthInputField(
                      label: 'NEW PASSWORD',
                      hintText: 'Min. 12 characters',
                      controller: TextEditingController(),
                      isPassword: true,
                    ),
                    const SizedBox(height: 12),

                    // Thanh đo độ mạnh mật khẩu
                    Row(
                      children: [
                        Expanded(
                          child: Container(
                            height: 4,
                            decoration: BoxDecoration(
                              color: Colors.green,
                              borderRadius: BorderRadius.circular(2),
                            ),
                          ),
                        ),
                        const SizedBox(width: 4),
                        Expanded(
                          child: Container(
                            height: 4,
                            decoration: BoxDecoration(
                              color: Colors.green,
                              borderRadius: BorderRadius.circular(2),
                            ),
                          ),
                        ),
                        const SizedBox(width: 4),
                        Expanded(
                          child: Container(
                            height: 4,
                            decoration: BoxDecoration(
                              color: Colors.grey[200],
                              borderRadius: BorderRadius.circular(2),
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    const Row(
                      children: [
                        Icon(Icons.check_circle, color: Colors.green, size: 14),
                        SizedBox(width: 6),
                        Text(
                          'Strong security score',
                          style: TextStyle(
                            fontSize: 11,
                            color: Colors.green,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 20),

                    AuthInputField(
                      label: 'CONFIRM NEW PASSWORD',
                      hintText: 'Repeat new password',
                      controller: TextEditingController(),
                      isPassword: true,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 32),

              // Nút Update
              ElevatedButton(
                onPressed: () {},
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF814F85),
                  minimumSize: const Size(double.infinity, 50),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  elevation: 0,
                ),
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.lock_outline, color: Colors.white, size: 18),
                    SizedBox(width: 8),
                    Text(
                      'Update Password',
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // Link Forgot
              Center(
                child: GestureDetector(
                  onTap: () {},
                  child: const Text(
                    'I forgot my current password',
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF814F85),
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 32),

              // Widget Privacy
              const PrivacyInfoCard(),

              const SizedBox(height: 40),
              const AuthFooter(),
            ],
          ),
        ),
      ),
    );
  }
}
