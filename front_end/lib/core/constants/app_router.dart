import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/core/di/injection.dart';
import 'package:front_end/core/layout/main_layout.dart';
import 'package:front_end/features/auth/ui/pages/splash_page.dart';
import 'package:front_end/features/auth/ui/pages/verify_email_page.dart';
import 'package:front_end/features/profile/ui/pages/setup_profile_page.dart';
import 'package:front_end/features/user/logic/user_cubit.dart';
import 'package:front_end/features/workspace/logic/workspace_cubit.dart';
import 'package:front_end/features/workspace/ui/pages/welcome_workspace_page.dart';
import 'package:front_end/features/workspace/ui/pages/workspace_gateway_page.dart';
import 'package:go_router/go_router.dart';
import 'package:talker_flutter/talker_flutter.dart';

import '../../../features/auth/ui/pages/reset_password_page.dart';
import '../../../features/auth/ui/pages/sign_in_page.dart';
import '../../../features/auth/ui/pages/sign_up_page.dart';
import '../network/api_client.dart';

final appRouter = GoRouter(
  // _____________________________________________________________________________
  // * Cấu Hình Khởi Tạo Router
  // _____________________________________________________________________________
  initialLocation: '/', // Mở app sẽ vào SplashPage (Trạm kiểm soát)
  observers: [
    TalkerRouteObserver(talker),
  ], // Tích hợp hệ thống soi Log khi chuyển trang

  routes: [
    // _____________________________________________________________________________
    // * Trạm Kiểm Soát (Splash)
    // _____________________________________________________________________________
    GoRoute(path: '/', builder: (context, state) => const SplashPage()),

    // _____________________________________________________________________________
    // * 1. Cụm Các Trang Auth
    // _____________________________________________________________________________
    GoRoute(path: '/sign-in', builder: (context, state) => const SignInPage()),
    GoRoute(path: '/sign-up', builder: (context, state) => const SignUpPage()),
    GoRoute(
      path: '/reset-password',
      builder: (context, state) => const ResetPasswordPage(),
    ),
    GoRoute(
      path: '/verify-email',
      builder: (context, state) {
        if (state.extra is Map<String, dynamic>) {
          final data = state.extra as Map<String, dynamic>;
          return VerifyEmailPage(
            userId: data['userId'] ?? '',
            email: data['email'] ?? 'Email không xác định',
          );
        }

        return VerifyEmailPage(
          userId: state.extra.toString(),
          email: 'Email đang cập nhật...',
        );
      },
    ),

    // _____________________________________________________________________________
    // * 2. Trang Chính (Sau khi đăng nhập)
    // _____________________________________________________________________________
    GoRoute(
      path: '/gateway',
      builder: (context, state) {
        // 🔴 CHỖ NÀY QUAN TRỌNG: Phải có BlocProvider bọc bên ngoài
        return BlocProvider<WorkspaceCubit>(
          create: (context) => getIt<WorkspaceCubit>(),
          child:
              const WorkspaceGatewayPage(), // Thằng con nằm bên trong mới gọi context.read() được
        );
      },
    ),
    GoRoute(
      path: '/welcome-workspace',
      builder: (context, state) {
        return MultiBlocProvider(
          providers: [
            BlocProvider<UserCubit>(create: (context) => getIt<UserCubit>()),
            BlocProvider<WorkspaceCubit>(
              create: (context) => getIt<WorkspaceCubit>(),
            ),
          ],
          child: const WelcomeWorkspacePage(),
        );
      },
    ),

    GoRoute(
      path: '/home',
      builder: (context, state) {
        return MultiBlocProvider(
          providers: [
            BlocProvider<UserCubit>(create: (context) => getIt<UserCubit>()),
            BlocProvider<WorkspaceCubit>(
              create: (context) => getIt<WorkspaceCubit>(),
            ),
          ],
          child: const MainLayout(),
        );
      },
    ),

    // _____________________________________________________________________________
    // * 3. Trang Thiết Lập Hồ Sơ (Nếu profile chưa hoàn thành)
    // _____________________________________________________________________________
    GoRoute(
      path: '/setup-profile',
      builder: (context, state) => const SetupProfilePage(),
    ),
  ],
);
