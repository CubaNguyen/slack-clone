import 'package:flutter/material.dart';

class DMSection extends StatelessWidget {
  const DMSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        _buildSectionHeader('Direct Messages', Icons.person_add_alt_1),
        _buildDMItem(
          'Sarah Jenkins',
          'Online',
          Colors.green,
          'https://i.pravatar.cc/150?u=sarah',
        ),
        _buildDMItem(
          'Marcus Chen',
          'Away',
          Colors.orange,
          'https://i.pravatar.cc/150?u=marcus',
          badgeCount: 2,
        ),
        _buildDMItem(
          'David Wilson',
          'Offline',
          Colors.grey,
          'https://i.pravatar.cc/150?u=david',
        ),
      ],
    );
  }

  Widget _buildSectionHeader(String title, IconData icon) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              const Icon(Icons.arrow_drop_down, size: 20),
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
            ],
          ),
          Icon(icon, size: 20, color: Colors.grey),
        ],
      ),
    );
  }

  Widget _buildDMItem(
    String name,
    String status,
    Color statusColor,
    String avatarUrl, {
    int? badgeCount,
  }) {
    return ListTile(
      leading: Stack(
        children: [
          CircleAvatar(backgroundImage: NetworkImage(avatarUrl)),
          Positioned(
            right: 0,
            bottom: 0,
            child: Container(
              width: 12,
              height: 12,
              decoration: BoxDecoration(
                color: statusColor,
                shape: BoxShape.circle,
                border: Border.all(color: Colors.white, width: 2),
              ),
            ),
          ),
        ],
      ),
      title: Text(name, style: const TextStyle(fontWeight: FontWeight.w500)),
      subtitle: Text(status, style: const TextStyle(fontSize: 12)),
      trailing: badgeCount != null
          ? Container(
              padding: const EdgeInsets.all(6),
              decoration: const BoxDecoration(
                color: Colors.purple,
                shape: BoxShape.circle,
              ),
              child: Text(
                '$badgeCount',
                style: const TextStyle(color: Colors.white, fontSize: 10),
              ),
            )
          : null,
    );
  }
}
