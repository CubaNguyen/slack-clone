// File: lib/features/auth/ui/widgets/resend_code_section.dart

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../logic/auth_cubit.dart';

class ResendCodeSection extends StatefulWidget {
  final String email; // 🔴 Cần nhận email từ trang cha để biết gửi cho ai

  const ResendCodeSection({super.key, required this.email});

  @override
  State<ResendCodeSection> createState() => _ResendCodeSectionState();
}

class _ResendCodeSectionState extends State<ResendCodeSection> {
  Timer? _timer;
  int _secondsRemaining = 60; // Bắt đầu từ 60 giây
  bool _canResend = false;

  @override
  void initState() {
    super.initState();
    _startTimer(); // Vừa mở màn hình lên là bắt đầu đếm luôn
  }

  void _startTimer() {
    setState(() {
      _secondsRemaining = 60;
      _canResend = false;
    });

    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (_secondsRemaining > 0) {
        setState(() {
          _secondsRemaining--;
        });
      } else {
        setState(() {
          _canResend = true; // Hết giờ thì cho phép bấm
        });
        _timer?.cancel();
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel(); // 🧹 Hủy timer khi rời khỏi trang để chống tràn bộ nhớ
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Text(
          "DIDN'T RECEIVE THE CODE?",
          style: TextStyle(
            fontSize: 10,
            fontWeight: FontWeight.bold,
            color: Colors.grey,
          ),
        ),
        const SizedBox(height: 8),
        GestureDetector(
          onTap: _canResend
              ? () {
                  // 1. Gọi API gửi lại mã
                  context.read<AuthCubit>().resendVerification(widget.email);
                  // 2. Quay ngược đồng hồ lại 60s
                  _startTimer();
                }
              : null, // Nếu chưa hết giờ thì bấm vào không có tác dụng
          child: Text(
            _canResend
                ? 'Resend Code' // Nút sáng lên khi được bấm
                : 'Resend Code in 00:${_secondsRemaining.toString().padLeft(2, '0')}', // Hiện số đếm ngược
            style: TextStyle(
              color: _canResend ? const Color(0xFF814F85) : Colors.grey,
              fontWeight: FontWeight.bold,
              decoration: _canResend
                  ? TextDecoration.underline
                  : TextDecoration.none,
            ),
          ),
        ),
      ],
    );
  }
}
