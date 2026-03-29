import 'package:front_end/core/network/api_client.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:front_end/features/auth/data/auth_repository.dart';
import 'package:front_end/features/auth/logic/auth_cubit.dart';
import 'package:front_end/features/channels/data/channel_repository.dart';
import 'package:front_end/features/channels/logic/channel_cubit.dart';
import 'package:front_end/features/profile/data/profile_repository.dart';
import 'package:front_end/features/profile/logic/profile_cubit.dart';
import 'package:front_end/features/user/data/user_repository.dart';
import 'package:front_end/features/user/logic/user_cubit.dart';
import 'package:front_end/features/workspace/data/workspace_repository.dart';
import 'package:front_end/features/workspace/logic/workspace_cubit.dart';
import 'package:get_it/get_it.dart';
import 'package:talker_flutter/talker_flutter.dart';

// // CODE CŨ: Đã xóa bớt mấy dòng import bị comment thừa ở đây cho sạch file

final getIt = GetIt.instance;

Future<void> setupDependencies() async {
  // _____________________________________________________________________________
  // * 1. CORE SERVICES (Dùng chung toàn app - Khởi tạo 1 lần xài mãi)
  // _____________________________________________________________________________

  // Đăng ký StorageService (Quản lý SharedPreferences/Token)
  getIt.registerLazySingleton<StorageService>(() => StorageService());

  getIt.registerSingleton<Talker>(Talker());
  getIt.registerLazySingleton<ApiClient>(
    () => ApiClient(getIt<StorageService>()),
  );

  // _____________________________________________________________________________
  // * 2. REPOSITORIES (Nơi chứa logic gọi API trực tiếp)
  // _____________________________________________________________________________

  getIt.registerLazySingleton<AuthRepository>(
    () => AuthRepository(getIt<ApiClient>(), getIt<StorageService>()),
  );

  getIt.registerLazySingleton<ProfileRepository>(
    () => ProfileRepository(getIt<ApiClient>()),
  );

  getIt.registerLazySingleton<UserRepository>(
    () => UserRepository(getIt<ApiClient>()),
  );

  getIt.registerLazySingleton<WorkspaceRepository>(
    () => WorkspaceRepository(getIt<ApiClient>()),
  );
  getIt.registerLazySingleton<ChannelRepository>(
    () => ChannelRepository(getIt<ApiClient>()),
  );

  // _____________________________________________________________________________
  // TODO: Thêm các Repository khác ở đây sau này (ChatRepository, WorkspaceRepository...)
  // _____________________________________________________________________________

  // _____________________________________________________________________________
  // * 3. CUBITS / BLOCS (Quản lý State Giao diện)
  // _____________________________________________________________________________

  getIt.registerLazySingleton<AuthCubit>(
    () => AuthCubit(getIt<AuthRepository>()),
  );

  getIt.registerLazySingleton<UserCubit>(
    () => UserCubit(getIt<UserRepository>()),
  );
  getIt.registerFactory<ChannelCubit>(
    () => ChannelCubit(getIt<ChannelRepository>()),
  );
  getIt.registerFactory<WorkspaceCubit>(
    () => WorkspaceCubit(getIt<WorkspaceRepository>()),
  );

  // ProfileCubit: Ví dụ dùng để xem trang cá nhân của người khác -> Dùng Factory.
  // ! CẢNH BÁO: Dùng Factory nghĩa là mỗi lần vào màn hình Profile nó tạo 1 Cubit mới, ra khỏi màn hình thì xóa đi để giải phóng RAM.
  getIt.registerFactory(() => ProfileCubit(getIt<ProfileRepository>()));
}
