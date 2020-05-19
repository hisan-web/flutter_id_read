import 'dart:async';

import 'package:flutter/services.dart';

class Idread {
  static const MethodChannel _channel =
      const MethodChannel('idread');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
