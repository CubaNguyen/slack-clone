import 'package:equatable/equatable.dart';
import 'package:front_end/features/user/data/model/user_model.dart';

abstract class UserState extends Equatable {
  const UserState();

  @override
  List<Object?> get props => [];
}

class UserInitial extends UserState {}

class UserLoading extends UserState {}

class UserLoadSuccess extends UserState {
  final UserModel user;

  const UserLoadSuccess(this.user);

  @override
  List<Object?> get props => [user];
}

class UserFailure extends UserState {
  final String message;

  const UserFailure(this.message);

  @override
  List<Object?> get props => [message];
}
