import 'dart:convert';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/core/constants/app_keys.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/network/api_client.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:front_end/features/user/data/model/user_model.dart';

import '../data/user_repository.dart';
import 'user_state.dart';

class UserCubit extends Cubit<UserState> {
  final UserRepository _userRepository;
  final StorageService _storageService = getIt<StorageService>();

  UserCubit(this._userRepository) : super(UserInitial());

  Future<void> fetchUserProfile() async {
    try {
      final cachedString =
          _storageService.readData(AppKeys.cachedUserData) as String?;

      if (cachedString != null && cachedString.isNotEmpty) {
        final cachedJson = jsonDecode(cachedString);
        final cachedUser = UserModel.fromJson(cachedJson);

        // talker.info('Đã load User từ Cache siêu tốc!');
        emit(UserLoadSuccess(cachedUser));
      } else {
        emit(UserLoading());
      }
    } catch (e) {
      talker.warning('Lỗi lúc đọc cache (kệ nó, gọi API bù): $e');
      if (state is! UserLoadSuccess) emit(UserLoading());
    }
    try {
      final user = await _userRepository.getUserProfile();
      // talker.info(
      //   'Toàn bộ cục data trả về từ API: \n${jsonEncode(user.toJson())}',
      // );

      final userJsonString = jsonEncode(user.toJson());
      await _storageService.writeData(AppKeys.cachedUserData, userJsonString);

      emit(UserLoadSuccess(user));
    } catch (e) {
      if (state is UserLoadSuccess) {
        talker.error('Rớt mạng lúc gọi API, đang dùng tạm Cache: $e');
      } else {
        emit(UserFailure(e.toString().replaceAll('Exception: ', '')));
      }
    }
  }
}
