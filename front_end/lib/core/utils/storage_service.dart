// File: lib/core/utils/storage_service.dart

import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

class StorageService {
  final _secureStorage = const FlutterSecureStorage();
  late SharedPreferences _prefs;

  bool get _useSecureStorage {
    if (kIsWeb) return false;
    if (defaultTargetPlatform == TargetPlatform.android ||
        defaultTargetPlatform == TargetPlatform.iOS) {
      return true;
    }
    return false;
  }

  Future<void> init() async {
    _prefs = await SharedPreferences.getInstance();
  }

  // _____________________________________________________________________________
  // * 1. NHÓM BẢO MẬT (Dùng Secure Storage cho Token, Password)
  // _____________________________________________________________________________
  Future<void> writeSecureData(String key, String value) async {
    if (_useSecureStorage) {
      await _secureStorage.write(key: key, value: value);
    } else {
      await _prefs.setString(key, value);
    }
  }

  Future<String?> readSecureData(String key) async {
    if (_useSecureStorage) {
      return await _secureStorage.read(key: key);
    } else {
      return _prefs.getString(key);
    }
  }

  Future<void> deleteSecureData(String key) async {
    if (_useSecureStorage) {
      await _secureStorage.delete(key: key);
    } else {
      await _prefs.remove(key);
    }
  }

  // _____________________________________________________________________________
  // * NHÓM DỮ LIỆU THƯỜNG (Dùng SharedPreferences)
  // _____________________________________________________________________________

  Future<void> writeData(String key, dynamic value) async {
    if (value is String) {
      await _prefs.setString(key, value);
    } else if (value is bool) {
      await _prefs.setBool(key, value);
    } else if (value is int) {
      await _prefs.setInt(key, value);
    } else if (value is double) {
      await _prefs.setDouble(key, value);
    }
  }

  dynamic readData(String key) {
    return _prefs.get(key);
  }

  Future<void> deleteData(String key) async {
    await _prefs.remove(key);
  }

  // Quét sạch ổ cứng khi Logout
  Future<void> clearAllAuthData() async {
    if (_useSecureStorage) await _secureStorage.deleteAll();
    await _prefs.clear();
  }
}
