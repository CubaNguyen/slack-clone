import 'package:flutter/material.dart';

class OtpInputMock extends StatelessWidget {
  // 1. Khai báo tham số controller để nhận từ trang cha
  final TextEditingController controller;
  final int length; // Để ông tùy chỉnh 6 hay 8 số

  const OtpInputMock({
    super.key,
    required this.controller,
    this.length = 8, // Mặc định 8 số như thiết kế của ông
  });

  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: Alignment.center,
      children: [
        // 1. LỚP HIỆN (Nằm dưới cùng)
        ListenableBuilder(
          listenable: controller,
          builder: (context, child) {
            return Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: List.generate(length, (index) {
                String char = "";
                if (controller.text.length > index) {
                  char = controller.text[index];
                }

                return Container(
                  width:
                      38, // Chỉnh nhỏ lại một xíu cho an toàn trên màn hình nhỏ
                  height: 48,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: controller.text.length == index
                          ? const Color(0xFF814F85)
                          : Colors.transparent,
                    ),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.05),
                        blurRadius: 10,
                        offset: const Offset(0, 4),
                      ),
                    ],
                  ),
                  alignment: Alignment.center,
                  child: Text(
                    char.isEmpty ? '•' : char,
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: char.isEmpty ? Colors.grey : Colors.black,
                    ),
                  ),
                );
              }),
            );
          },
        ),

        // 2. LỚP ẨN (Nằm đè lên trên cùng để hứng toàn bộ thao tác chạm)
        Positioned.fill(
          child: Opacity(
            opacity: 0, // Vẫn tàng hình
            child: TextField(
              controller: controller,
              // Sửa thành kiểu Text và ép viết hoa luôn cho xịn
              keyboardType: TextInputType.text,
              textCapitalization: TextCapitalization.characters,
              autofocus: true,
              maxLength: length,
              showCursor: false,
              decoration: const InputDecoration(
                counterText: '', // 🔴 Giấu cái chữ "0/8" đi để khỏi hỏng layout
                border: InputBorder.none,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
