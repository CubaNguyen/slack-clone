// features/auth/ui/widgets/sign_up_form.dart
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../logic/auth_cubit.dart';
import '../../logic/auth_state.dart';
import 'legal_agreements_text.dart';
import 'signup_input_field.dart';

class SignUpForm extends StatefulWidget {
  final String? emailServerError;
  final Function(String?) onEmailChanged;

  const SignUpForm({
    super.key,
    this.emailServerError,
    required this.onEmailChanged,
  });

  @override
  State<SignUpForm> createState() => _SignUpFormState();
}

class _SignUpFormState extends State<SignUpForm> {
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
    return Form(
      key: _formKey,
      child: Column(
        children: [
          SignupInputField(
            label: 'EMAIL ADDRESS',
            hintText: 'user@gmail.com',
            controller: _emailController,
            errorText: widget.emailServerError,
            onChanged: (v) => widget.onEmailChanged(v),
            validator: (value) =>
                (value == null || value.isEmpty) ? 'Vui lòng nhập email' : null,
          ),
          const SizedBox(height: 16),
          SignupInputField(
            label: 'PASSWORD',
            hintText: 'password',
            controller: _passwordController,
            isPassword: true,
            obscureText: true,
            validator: (value) => (value == null || value.length < 8)
                ? 'Tối thiểu 8 ký tự'
                : null,
          ),
          const SizedBox(height: 16),
          const LegalAgreementsText(),
          const SizedBox(height: 32),
          _buildSubmitButton(),
        ],
      ),
    );
  }

  Widget _buildSubmitButton() {
    return BlocBuilder<AuthCubit, AuthState>(
      builder: (context, state) {
        return ElevatedButton(
          onPressed: state is AuthLoading
              ? null
              : () {
                  if (_formKey.currentState!.validate()) {
                    context.read<AuthCubit>().signUp(
                      _emailController.text.trim(),
                      _passwordController.text.trim(),
                    );
                  }
                },
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF814F85),
            minimumSize: const Size(double.infinity, 50),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
          ),
          child: state is AuthLoading
              ? const SizedBox(
                  height: 20,
                  width: 20,
                  child: CircularProgressIndicator(
                    color: Colors.white,
                    strokeWidth: 2,
                  ),
                )
              : const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      'Create Account',
                      style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    SizedBox(width: 8),
                    Icon(Icons.arrow_forward, color: Colors.white, size: 18),
                  ],
                ),
        );
      },
    );
  }
}
