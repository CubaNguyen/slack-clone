import 'package:flutter/material.dart';

// Import component con vừa tách
import '../widgets/dm_list_item.dart';

class DMsPage extends StatelessWidget {
  const DMsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text(
          'Direct Messages',
          style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit_square, color: Colors.black),
            onPressed: () {},
          ),
        ],
      ),
      body: Column(
        children: [
          // Khung Search
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Find or start a conversation',
                prefixIcon: const Icon(Icons.search),
                filled: true,
                fillColor: Colors.grey[100],
                contentPadding: const EdgeInsets.symmetric(vertical: 0),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(10),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
          ),

          // Danh sách dùng Component đã tách
          Expanded(
            child: ListView(
              children: const [
                // <-- Có thể thêm chữ const vì các biến truyền vào là text cứng
                DmListItem(
                  name: 'Sarah Jenkins',
                  lastMessage: 'Sounds good, let\'s catch up tomorrow!',
                  time: '10:30 AM',
                  avatarUrl: 'https://i.pravatar.cc/150?u=sarah',
                  unreadCount: 2,
                  isOnline: true,
                ),
                DmListItem(
                  name: 'Marcus Chen',
                  lastMessage: 'Did you check the latest API documentation?',
                  time: 'Yesterday',
                  avatarUrl: 'https://i.pravatar.cc/150?u=marcus',
                  isOnline: false,
                ),
                DmListItem(
                  name: 'David Wilson',
                  lastMessage: 'Sent an image',
                  time: 'Monday',
                  avatarUrl: 'https://i.pravatar.cc/150?u=david',
                  isOnline: false,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
