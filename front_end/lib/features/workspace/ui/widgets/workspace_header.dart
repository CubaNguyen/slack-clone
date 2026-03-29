import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:front_end/features/user/logic/user_cubit.dart';
import 'package:front_end/features/user/logic/user_state.dart';

class WorkspaceHeader extends StatelessWidget {
  const WorkspaceHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: const Color(0xFFE8E4D9), // Màu beige nhạt như ảnh
              borderRadius: BorderRadius.circular(8),
            ),
            child: const Icon(Icons.blur_on, color: Colors.brown, size: 24),
          ),
          const SizedBox(width: 12),
          const Text(
            'The Atrium',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Color(0xFF4A154B),
            ),
          ),
          const Spacer(),
          IconButton(onPressed: () {}, icon: const Icon(Icons.search)),
          BlocBuilder<UserCubit, UserState>(
            builder: (context, state) {
              if (state is UserLoadSuccess) {
                final user = state.user;
                final profile = user.profile;

                // Lấy chữ cái đầu của tên (hoặc email nếu chưa có tên)
                final String firstLetter = profile.fullName.isNotEmpty
                    ? profile.fullName[0].toUpperCase()
                    : user.email[0].toUpperCase();

                return CircleAvatar(
                  radius: 18,
                  backgroundColor: const Color(
                    0xFF007A5A,
                  ), // Xanh lá phong cách Slack
                  backgroundImage: profile.avatarUrl.isNotEmpty
                      ? NetworkImage(profile.avatarUrl)
                      : null, // Bỏ hình cũ đi, thay bằng ảnh thật
                  child: profile.avatarUrl.isEmpty
                      ? Text(
                          firstLetter,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        )
                      : null,
                );
              }

              // Nếu đang Loading hoặc Lỗi thì hiển thị một cục xám quay vòng vòng cho ngầu
              return CircleAvatar(
                radius: 18,
                backgroundColor: Colors.grey.shade300,
                child: const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(
                    strokeWidth: 2,
                    color: Colors.grey,
                  ),
                ),
              );
            },
          ),
        ],
      ),
    );
  }
}
