// File: lib/features/auth/logic/auth_state.dart
import 'package:equatable/equatable.dart';

abstract class AuthState extends Equatable {
  const AuthState();

  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

class AuthResendSuccess extends AuthState {}

class AuthSuccess extends AuthState {
  // Thêm biến này vào để quyết định chuyển đi trang nào
  final bool isProfileCompleted;

  const AuthSuccess({required this.isProfileCompleted});

  @override
  List<Object?> get props => [isProfileCompleted];
}

// Trạng thái: Đăng ký thành công (Chờ nhập OTP)
class AuthSignUpSuccess extends AuthState {
  final String userId;
  final String email; // 🔴 Thêm dòng này

  const AuthSignUpSuccess(this.userId, this.email);

  @override
  List<Object?> get props => [userId, email];
}

class AuthVerifySuccess extends AuthState {}

class AuthFailure extends AuthState {
  final String message;
  const AuthFailure(this.message);

  @override
  List<Object?> get props => [message];
}
