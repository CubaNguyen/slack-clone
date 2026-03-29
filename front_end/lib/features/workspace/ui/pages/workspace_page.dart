import 'package:flutter/material.dart';

import '../../../../core/di/injection.dart';
import '../../../../core/network/api_client.dart'; // Để lấy talker
import '../../../../core/utils/storage_service.dart'; // Để lấy storageService
import '../widgets/channel_list.dart';
import '../widgets/dm_list.dart';
import '../widgets/workspace_header.dart';
import '../widgets/workspace_selector.dart';

class WorkspacePage extends StatelessWidget {
  const WorkspacePage({super.key});
  final int count = 0;

  @override
  Widget build(BuildContext context) {
    // 2. LẤY CÁC SERVICE TỪ CỬA HÀNG GET_IT
    final storageService = getIt<StorageService>();
    final talker = getIt<ApiClient>().talker;

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            const WorkspaceHeader(),
            const WorkspaceSelector(),
            const SizedBox(height: 16),

            // Search Bar
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: TextField(
                decoration: InputDecoration(
                  hintText: 'Jump to channel or DM...',
                  prefixIcon: const Icon(Icons.manage_search),
                  filled: true,
                  fillColor: Colors.grey[50],
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide.none,
                  ),
                ),
              ),
            ),
            // Nội dung cuộn
            Expanded(
              child: ListView(children: const [ChannelSection(), DMSection()]),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        heroTag: 'fab_workspace',
        backgroundColor: const Color(0xFF7E57C2),
        onPressed: () {},
        child: const Icon(Icons.edit_square, color: Colors.white),
      ),
    );
  }
}
