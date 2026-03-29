import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
// 🔴 Đảm bảo import 2 file này chuẩn với đường dẫn của bạn
import 'package:front_end/features/user/logic/user_cubit.dart';
import 'package:front_end/features/user/logic/user_state.dart';

class ProfileInfoCard extends StatelessWidget {
  const ProfileInfoCard({super.key});

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      color: Colors.grey[50],
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      // Bọc nội dung bằng BlocBuilder
      child: BlocBuilder<UserCubit, UserState>(
        builder: (context, state) {
          // 1. Đang tải
          if (state is UserLoading) {
            return const Padding(
              padding: EdgeInsets.all(32.0),
              child: Center(child: CircularProgressIndicator()),
            );
          }

          // 2. Có dữ liệu
          if (state is UserLoadSuccess) {
            final user = state.user;
            final profile = user.profile;

            // Xử lý TÊN: Nếu chưa có full_name thì lấy tạm phần chữ trước @ của email
            final displayName = profile.fullName.isNotEmpty
                ? profile.fullName
                : user.email.split('@')[0];

            // Xử lý AVATAR: Lấy chữ cái đầu tiên
            final firstLetter = displayName[0].toUpperCase();

            // Xử lý BIO (Tiểu sử): Nếu trống thì hiện chữ mặc định
            final bioText = profile.bio.isNotEmpty
                ? profile.bio
                : 'Chưa cập nhật tiểu sử';

            return Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                children: [
                  Stack(
                    children: [
                      // --- CỤC AVATAR ---
                      CircleAvatar(
                        radius: 36,
                        backgroundColor: const Color(0xFF007A5A), // Xanh lá
                        backgroundImage: profile.avatarUrl.isNotEmpty
                            ? NetworkImage(profile.avatarUrl)
                            : null,
                        child: profile.avatarUrl.isEmpty
                            ? Text(
                                firstLetter,
                                style: const TextStyle(
                                  fontSize: 28,
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                ),
                              )
                            : null,
                      ),
                      // Dấu chấm xanh online
                      Positioned(
                        right: 0,
                        bottom: 0,
                        child: Container(
                          width: 16,
                          height: 16,
                          decoration: BoxDecoration(
                            color: Colors.green,
                            shape: BoxShape.circle,
                            border: Border.all(color: Colors.white, width: 2),
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(width: 16),

                  // --- CỤC TEXT ---
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          displayName, // Tên "huy" hiển thị ở đây
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Text(
                          bioText, // Bio hiển thị ở đây
                          style: const TextStyle(
                            color: Colors.grey,
                            fontSize: 14,
                          ),
                        ),
                        const SizedBox(height: 8),

                        // Cục Status giữ nguyên
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.white,
                            borderRadius: BorderRadius.circular(12),
                            border: Border.all(color: Colors.grey[200]!),
                          ),
                          child: const Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(
                                Icons.house_siding,
                                size: 14,
                                color: Colors.brown,
                              ),
                              SizedBox(width: 4),
                              Text(
                                'Working remotely',
                                style: TextStyle(fontSize: 12),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            );
          }

          // 3. Lỗi hoặc chờ
          return const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text('Không tải được thông tin người dùng'),
          );
        },
      ),
    );
  }
}
