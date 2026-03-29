import 'package:logger/logger.dart';

// Khởi tạo một biến toàn cục để dùng ở mọi nơi
var logger = Logger(
  printer: PrettyPrinter(
    methodCount: 0,
    errorMethodCount: 5,
    lineLength: 50,
    colors: true,
    printEmojis: true,
  ),
);
