import 'package:flutter/material.dart';

import '../widgets/chat_app_bar.dart';
import '../widgets/chat_input_area.dart';
import '../widgets/message_item.dart';

class ChatDetailPage extends StatelessWidget {
  final String title;
  final String subtitle;

  const ChatDetailPage({
    super.key,
    required this.title,
    required this.subtitle,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: ChatAppBar(title: title, subtitle: subtitle),
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: ListView(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 8,
                ),
                children: [
                  Center(
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.grey[200],
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: const Text(
                        'MONDAY, OCTOBER 16',
                        style: TextStyle(
                          fontSize: 10,
                          fontWeight: FontWeight.bold,
                          color: Colors.grey,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  const MessageItem(
                    name: 'Alex Johnson',
                    time: '10:24 AM',
                    message:
                        'Hey everyone! Just finished the first draft of the mobile navigation system...',
                    avatarUrl: 'https://i.pravatar.cc/150?u=alex',
                    hasAttachment: true,
                  ),
                  const MessageItem(
                    name: 'Sarah Lee',
                    time: '10:31 AM',
                    message:
                        'This looks so much cleaner, Alex! The glassmorphism on the FAB is a nice touch.',
                    avatarUrl: 'https://i.pravatar.cc/150?u=sarah',
                  ),
                ],
              ),
            ),
            ChatInputArea(channelName: title),
          ],
        ),
      ),
    );
  }
}
