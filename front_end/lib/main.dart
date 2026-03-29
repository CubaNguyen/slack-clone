import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:front_end/core/constants/app_router.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/network/api_client.dart';
import 'package:front_end/core/utils/storage_service.dart';
import 'package:front_end/features/auth/logic/auth_cubit.dart';
import 'package:front_end/features/auth/logic/auth_state.dart'; // 🔴 QUAN TRỌNG: Thêm để dùng AuthState
import 'package:front_end/features/profile/logic/profile_cubit.dart';
import 'package:talker_flutter/talker_flutter.dart';
import 'package:window_manager/window_manager.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // 1. Load biến môi trường trước tiên
  await dotenv.load(fileName: ".env");

  // 2. Cài đặt GetIt (Đăng ký StorageService, ApiClient, AuthRepository...)
  await setupDependencies();

  // 3. BÂY GIỜ mới được gọi getIt ra để dùng
  await getIt<StorageService>().init();

  await windowManager.ensureInitialized();
  WindowOptions windowOptions = const WindowOptions(
    size: Size(400, 800),
    center: true,
    title: "Lumina Slack Clone",
  );

  windowManager.waitUntilReadyToShow(windowOptions, () async {
    await windowManager.show();
    await windowManager.focus();
  });

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return TalkerWrapper(
      talker: talker,
      child: MultiBlocProvider(
        providers: [
          // Trạm 1: Lo chuyện vé vào cửa (Login, Logout, Token)
          BlocProvider<AuthCubit>(create: (context) => getIt<AuthCubit>()),

          // Trạm 2: VÍ DỤ - Lo chuyện thông tin cá nhân (Tên, Ảnh)
          BlocProvider<ProfileCubit>(
            create: (context) => getIt<ProfileCubit>(),
          ),

          // BlocProvider<UserCubit>(create: (context) => getIt<UserCubit>()),
        ],

        child: MaterialApp.router(
          title: 'Lumina',
          debugShowCheckedModeBanner: false,
          theme: ThemeData(
            colorScheme: ColorScheme.fromSeed(
              seedColor: const Color(0xFF4A154B),
            ),
            useMaterial3: true,
            snackBarTheme: SnackBarThemeData(
              behavior: SnackBarBehavior.floating,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              insetPadding: const EdgeInsets.all(20),
              elevation: 4,
            ),
          ),
          routerConfig: appRouter,

          // 🔴 TRẠM GÁC TỔNG NẰM Ở ĐÂY 🔴
          // Nó sẽ bọc toàn bộ các trang của app lại
          builder: (context, child) {
            return BlocListener<AuthCubit, AuthState>(
              listener: (context, state) {
                // Hễ có lệnh đuổi khách (AuthInitial) là đá văng ra /sign-in bất chấp đang ở đâu
                if (state is AuthInitial) {
                  // [Suy luận] Dùng GoRouter để xóa sạch lịch sử điều hướng, không cho back lại
                  appRouter.go('/sign-in');
                }
              },
              child: child!, // Trả lại giao diện các trang bình thường
            );
          },
        ),
      ),
    );
  }
}
