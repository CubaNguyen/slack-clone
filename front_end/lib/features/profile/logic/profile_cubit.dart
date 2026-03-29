import 'package:flutter_bloc/flutter_bloc.dart';

import '../data/profile_repository.dart';
import 'profile_state.dart';

class ProfileCubit extends Cubit<ProfileState> {
  final ProfileRepository _profileRepository;

  ProfileCubit(this._profileRepository) : super(ProfileInitial());

  Future<void> setupProfile({
    required String fullName,
    String avatarUrl = "",
    String bio = "",
  }) async {
    // 1. Validate sương sương ở Client
    if (fullName.trim().isEmpty) {
      emit(
        const ProfileFailure("Tên hiển thị không được để trống ông giáo ạ!"),
      );
      return;
    }

    // 2. Bật loading
    emit(ProfileLoading());

    try {
      // 3. Gọi API (Cái cờ profile_completed = true đã được xử lý ngầm trong Repo rồi)
      await _profileRepository.updateProfile(
        fullName: fullName.trim(),
        avatarUrl: avatarUrl.trim(),
        bio: bio.trim(),
      );

      // 4. Báo thành công
      emit(ProfileSuccess());
    } catch (e) {
      // 5. Báo lỗi
      emit(ProfileFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }
}
