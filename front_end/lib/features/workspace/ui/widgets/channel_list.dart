import 'package:flutter/material.dart';
import 'package:front_end/features/chat/ui/pages/chat_detail_page.dart';
// Đừng quên import trang chi tiết chat vào nhé!
// [Suy luận] Đường dẫn này có thể khác tùy thuộc vào cách bạn đặt file

class ChannelSection extends StatelessWidget {
  const ChannelSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        _buildSectionHeader('Channels', Icons.add),
        // 1. TRUYỀN THÊM biến 'context' VÀO ĐÂY 👇
        _buildChannelItem(context, 'general', isSelected: true),
        _buildChannelItem(context, 'design-team', badgeCount: 3),
        _buildChannelItem(context, 'marketing-ops'),
        _buildChannelItem(context, 'engineering', badgeCount: 12),
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

  // 2. THÊM BuildContext context VÀO KHAI BÁO HÀM NÀY 👇
  Widget _buildChannelItem(
    BuildContext context,
    String name, {
    bool isSelected = false,
    int? badgeCount,
  }) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: isSelected ? Colors.grey[100] : Colors.transparent,
        borderRadius: BorderRadius.circular(8),
      ),
      // 3. BỌC InkWell Ở ĐÂY ĐỂ BẮT SỰ KIỆN CLICK 👇
      child: InkWell(
        borderRadius: BorderRadius.circular(8), // Hiệu ứng sóng nước bo góc
        onTap: () {
          // LỆNH CHUYỂN TRANG
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => ChatDetailPage(
                title: '#$name', // Tên channel động
                subtitle: '12 members online', // Bạn có thể sửa sau
              ),
            ),
          );
        },
        child: ListTile(
          dense: true,
          leading: const Text(
            '#',
            style: TextStyle(fontSize: 20, color: Colors.grey),
          ),
          title: Text(
            name,
            style: TextStyle(
              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
            ),
          ),
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
        ),
      ),
    );
  }
}
