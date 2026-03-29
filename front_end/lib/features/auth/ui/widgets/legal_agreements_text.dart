// legal_agreements_text.dart
import 'package:flutter/material.dart';

class LegalAgreementsText extends StatelessWidget {
  const LegalAgreementsText({super.key});

  @override
  Widget build(BuildContext context) {
    return RichText(
      text: const TextSpan(
        style: TextStyle(
          color: Colors.grey,
          fontSize: 13,
          height: 1.4,
          fontWeight: FontWeight.w400,
        ),
        children: [
          TextSpan(text: 'By signing up, you agree to our '),
          TextSpan(
            text: 'Terms',
            style: TextStyle(color: Colors.blue, fontWeight: FontWeight.bold),
          ), // Highlight
          TextSpan(text: ' and '),
          TextSpan(
            text: 'Privacy Policy',
            style: TextStyle(color: Colors.blue, fontWeight: FontWeight.bold),
          ), // Highlight
          TextSpan(text: '.'),
        ],
      ),
    );
  }
}
