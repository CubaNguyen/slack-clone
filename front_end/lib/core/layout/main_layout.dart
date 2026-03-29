import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/features/chat/ui/pages/mentions_page.dart';
import 'package:front_end/features/profile/ui/pages/profile_widgets.dart';
import 'package:front_end/features/user/logic/user_cubit.dart';
// 1. IMPORT THƯ VIỆN TALKER VÀ BIẾN TALKER TOÀN CỤC VÀO ĐÂY
import 'package:talker_flutter/talker_flutter.dart';

import '../../features/chat/ui/pages/dms_page.dart';
import '../../features/workspace/ui/pages/workspace_page.dart';
import '../../shared/widgets/main_bottom_nav.dart';
import '../network/api_client.dart'; // Chú ý đường dẫn này xem có đúng chỗ ông để biến talker chưa nhé

class MainLayout extends StatefulWidget {
  const MainLayout({super.key});

  @override
  State<MainLayout> createState() => _MainLayoutState();
}

class _MainLayoutState extends State<MainLayout> {
  int _currentIndex = 0;

  late final List<Widget> _pages = [
    const WorkspacePage(), // Tab 0: Home
    const DMsPage(), // Tab 1: DMs
    const MentionsPage(), // Tab 2: Mentions
    const ProfilePage(), // Tab 3: You (Profile)
  ];
  @override
  void initState() {
    super.initState();
    // Vừa vào MainLayout là âm thầm gọi API lấy Profile liền
    context.read<UserCubit>().fetchUserProfile();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      bottomNavigationBar: MainBottomNav(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
      ),

      // 2. THÊM NÚT F12 (FLOATING ACTION BUTTON) VÀO ĐÂY
      floatingActionButton: FloatingActionButton(
        heroTag: 'fab_main',
        onPressed: () {
          // cách test nhanh các api

          // Khi bấm vào sẽ trượt ra màn hình Console
          Navigator.of(context).push(
            MaterialPageRoute(
              builder: (context) => TalkerScreen(talker: talker),
            ),
          );
        },
        backgroundColor: Colors.black87, // Màu đen ngầu lòi
        mini: true, // Cho nó nhỏ lại một chút để không che mất UI của app
        elevation: 4,
        child: const Icon(
          Icons.terminal,
          color: Colors.greenAccent,
        ), // Icon code xanh lá
      ),
    );
  }
}
