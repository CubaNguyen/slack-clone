// File: lib/features/auth/ui/widgets/sign_in_form.dart

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/features/auth/logic/auth_cubit.dart';
import 'package:front_end/features/auth/logic/auth_state.dart';
import 'package:front_end/features/auth/ui/widgets/auth_input_field.dart';
// import các file của ông...

class SignInForm extends StatefulWidget {
  // 👈 Đổi thành StatefulWidget
  const SignInForm({super.key});

  @override
  State<SignInForm> createState() => _SignInFormState();
}

class _SignInFormState extends State<SignInForm> {
  // 1. TẠO CHÌA KHÓA FORM Ở ĐÂY
  final _formKey = GlobalKey<FormState>();

  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // 2. BỌC BẰNG WIDGET FORM VÀ TRUYỀN KEY VÀO
    return Form(
      key: _formKey,
      child: Container(
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
            // ... (Phần text Welcome Back của ông)

            // 3. THÊM LUẬT VALIDATOR CHO EMAIL
            AuthInputField(
              label: 'EMAIL ADDRESS',
              hintText: 'name@company.com',
              controller: _emailController,
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Email không được để trống';
                }
                if (!RegExp(
                  r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$',
                ).hasMatch(value)) {
                  return 'Email không đúng định dạng';
                }
                return null;
              },
            ),

            const SizedBox(height: 24),

            // 4. THÊM LUẬT VALIDATOR CHO PASSWORD
            AuthInputField(
              label: 'PASSWORD',
              hintText: '••••••••',
              controller: _passwordController,
              isPassword: true,
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Vui lòng nhập mật khẩu';
                }
                if (value.length < 6) {
                  return 'Mật khẩu phải có ít nhất 6 ký tự';
                }
                return null;
              },
            ),

            const SizedBox(height: 32),

            // 5. CẬP NHẬT NÚT BẤM ĐỂ KIỂM TRA FORM
            BlocBuilder<AuthCubit, AuthState>(
              builder: (context, state) {
                return ElevatedButton(
                  onPressed: state is AuthLoading
                      ? null
                      : () {
                          // KIỂM TRA FORM TRƯỚC KHI GỌI API
                          if (_formKey.currentState!.validate()) {
                            context.read<AuthCubit>().login(
                              _emailController.text.trim(),
                              _passwordController.text.trim(),
                            );
                          }
                        },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF4A1A4A),
                    minimumSize: const Size(double.infinity, 50),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: state is AuthLoading
                      ? const CircularProgressIndicator(color: Colors.white)
                      : const Text(
                          'Sign In',
                          style: TextStyle(color: Colors.white),
                        ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
