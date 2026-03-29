import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/features/auth/logic/auth_cubit.dart';

class SignOutButton extends StatelessWidget {
  const SignOutButton({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: OutlinedButton(
        // 🔴 BƠM LOGIC VÀO ĐÂY 🔴
        onPressed: () async {
          context.read<AuthCubit>().logout();
        },
        style: OutlinedButton.styleFrom(
          foregroundColor: const Color(0xFFF06292),
          side: const BorderSide(color: Color(0xFFF06292)),
          minimumSize: const Size(double.infinity, 48),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        child: const Text(
          'Sign Out',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
    );
  }
}
