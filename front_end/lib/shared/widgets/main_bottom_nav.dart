import 'package:flutter/material.dart';

class MainBottomNav extends StatelessWidget {
  final int currentIndex;
  // Thêm một hàm callback để báo cho component cha biết user vừa bấm tab nào
  final ValueChanged<int> onTap;

  const MainBottomNav({
    super.key,
    required this.currentIndex,
    required this.onTap, // Yêu cầu phải truyền hàm này vào
  });

  @override
  Widget build(BuildContext context) {
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      selectedItemColor: Colors.purple,
      unselectedItemColor: Colors.grey,
      currentIndex: currentIndex,
      onTap: onTap, // Gọi thẳng hàm callback ở đây
      items: const [
        BottomNavigationBarItem(icon: Icon(Icons.home_filled), label: 'Home'),
        BottomNavigationBarItem(
          icon: Icon(Icons.chat_bubble_outline),
          label: 'DMs',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.alternate_email),
          label: 'Mentions',
        ),
        BottomNavigationBarItem(icon: Icon(Icons.person_outline), label: 'You'),
      ],
    );
  }
}
