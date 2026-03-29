import 'package:flutter/material.dart';

class MentionsPage extends StatelessWidget {
  const MentionsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text(
          'Mentions & Reactions',
          style: TextStyle(
            color: Colors.black,
            fontWeight: FontWeight.bold,
            fontSize: 18,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list, color: Colors.black),
            onPressed: () {},
          ),
        ],
      ),
      body: ListView(
        children: [
          _buildMentionItem(
            name: 'Sarah Lee',
            channel: '#design-team',
            time: '10:45 AM',
            avatarUrl: 'https://i.pravatar.cc/150?u=sarah',
            isUnread: true,
            content: RichText(
              text: const TextSpan(
                style: TextStyle(
                  color: Colors.black87,
                  fontSize: 14,
                  height: 1.4,
                ),
                children: [
                  TextSpan(text: 'I think '),
                  // Highlight chữ @Alex
                  TextSpan(
                    text: '@Alex Rivera',
                    style: TextStyle(
                      color: Colors.blue,
                      fontWeight: FontWeight.bold,
                      backgroundColor: Color(0xFFE3F2FD),
                    ),
                  ),
                  TextSpan(
                    text:
                        ' should review the latest Figma file before we hand it off to engineering.',
                  ),
                ],
              ),
            ),
          ),

          Divider(height: 1, color: Colors.grey[200]),

          _buildMentionItem(
            name: 'Marcus Chen',
            channel: '#general',
            time: 'Yesterday',
            avatarUrl: 'https://i.pravatar.cc/150?u=marcus',
            isUnread: false,
            content: RichText(
              text: const TextSpan(
                style: TextStyle(
                  color: Colors.black87,
                  fontSize: 14,
                  height: 1.4,
                ),
                children: [
                  TextSpan(text: 'Welcome to the team '),
                  TextSpan(
                    text: '@Alex Rivera',
                    style: TextStyle(
                      color: Colors.blue,
                      fontWeight: FontWeight.bold,
                      backgroundColor: Color(0xFFE3F2FD),
                    ),
                  ),
                  TextSpan(text: '! Glad to have you here. 🎉'),
                ],
              ),
            ),
          ),

          Divider(height: 1, color: Colors.grey[200]),

          // Ví dụ về Reaction (Thả tim/like)
          _buildReactionItem(
            name: 'David Wilson',
            channel: '#engineering',
            time: 'Monday',
            avatarUrl: 'https://i.pravatar.cc/150?u=david',
            reaction: '🚀',
            reactedTo:
                'Deployed the new microservices architecture to staging.',
          ),
        ],
      ),
    );
  }

  // Widget hiển thị người ta nhắc tên mình
  Widget _buildMentionItem({
    required String name,
    required String channel,
    required String time,
    required String avatarUrl,
    required bool isUnread,
    required Widget content,
  }) {
    return Container(
      color: isUnread
          ? const Color(0xFFFFF9E6)
          : Colors.white, // Màu nền vàng nhạt nếu chưa đọc
      padding: const EdgeInsets.all(16),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(backgroundImage: NetworkImage(avatarUrl), radius: 20),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Text(
                          name,
                          style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.grey[200],
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            channel,
                            style: TextStyle(
                              fontSize: 10,
                              color: Colors.grey[700],
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ),
                    Text(
                      time,
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                content, // Đoạn text đã được bọc RichText truyền từ trên xuống
              ],
            ),
          ),
        ],
      ),
    );
  }

  // Widget hiển thị người ta thả reaction
  Widget _buildReactionItem({
    required String name,
    required String channel,
    required String time,
    required String avatarUrl,
    required String reaction,
    required String reactedTo,
  }) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Stack(
            children: [
              CircleAvatar(
                backgroundImage: NetworkImage(avatarUrl),
                radius: 20,
              ),
              Positioned(
                right: -2,
                bottom: -2,
                child: Container(
                  padding: const EdgeInsets.all(2),
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    shape: BoxShape.circle,
                  ),
                  child: Text(reaction, style: const TextStyle(fontSize: 12)),
                ),
              ),
            ],
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Text(
                          name,
                          style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                          ),
                        ),
                        const Text(
                          ' reacted in ',
                          style: TextStyle(fontSize: 14, color: Colors.grey),
                        ),
                        Text(
                          channel,
                          style: const TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    Text(
                      time,
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Container(
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey[300]!),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    reactedTo,
                    style: const TextStyle(
                      color: Colors.grey,
                      fontStyle: FontStyle.italic,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
