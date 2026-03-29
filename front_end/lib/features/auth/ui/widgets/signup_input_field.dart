import 'package:flutter/material.dart';

class SignupInputField extends StatefulWidget {
  final String label;
  final String hintText;
  final TextEditingController controller;
  final TextInputType keyboardType;
  final bool obscureText;
  final bool isPassword;

  // 1. THÊM BIẾN VALIDATOR VÀO ĐÂY
  final String? Function(String?)? validator;
  final String? errorText;

  final ValueChanged<String>? onChanged;
  const SignupInputField({
    super.key,
    required this.label,
    required this.hintText,
    required this.controller,
    this.keyboardType = TextInputType.text,
    this.obscureText = false,
    this.isPassword = false,
    this.validator, // 2. NHẬN BIẾN Ở CONSTRUCTOR
    this.errorText, // 3. NHẬN BIẾN Ở CONSTRUCTOR
    this.onChanged,
  });

  @override
  State<SignupInputField> createState() => _SignupInputFieldState();
}

class _SignupInputFieldState extends State<SignupInputField> {
  late bool _isObscured;

  @override
  void initState() {
    super.initState();
    _isObscured = widget.obscureText;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.label.toUpperCase(),
          style: const TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.bold,
            color: Colors.grey,
          ),
        ),
        const SizedBox(height: 8),
        Container(
          // Bỏ màu nền ở đây đi để TextFormField tự quản lý màu khi có lỗi
          decoration: BoxDecoration(borderRadius: BorderRadius.circular(12)),

          // 3. ĐỔI THÀNH TextFormField ĐỂ HỖ TRỢ BẮT LỖI
          child: TextFormField(
            controller: widget.controller,
            keyboardType: widget.keyboardType,
            obscureText: _isObscured,
            validator: widget.validator, // 4. TRUYỀN VALIDATOR VÀO
            autovalidateMode: AutovalidateMode
                .onUserInteraction, // Tự động báo lỗi khi user gõ

            decoration: InputDecoration(
              errorText: widget.errorText,
              hintText: widget.hintText,
              hintStyle: const TextStyle(color: Colors.grey, fontSize: 14),
              contentPadding: const EdgeInsets.all(16.0),

              // Thiết lập màu nền mặc định
              fillColor: const Color(0xFFF5F5F7),
              filled: true,

              // Border mặc định (Không viền)
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide.none,
              ),

              // 5. HIỆU ỨNG VIỀN ĐỎ KHI CÓ LỖI
              errorBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: const BorderSide(color: Colors.red, width: 1),
              ),
              focusedErrorBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: const BorderSide(color: Colors.red, width: 2),
              ),

              suffixIcon: widget.isPassword
                  ? IconButton(
                      icon: Icon(
                        _isObscured
                            ? Icons.visibility_outlined
                            : Icons.visibility_off_outlined,
                        color: Colors.grey,
                      ),
                      onPressed: () {
                        setState(() {
                          _isObscured = !_isObscured;
                        });
                      },
                    )
                  : null,
            ),
          ),
        ),
      ],
    );
  }
}
