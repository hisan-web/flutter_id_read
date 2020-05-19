import 'dart:async';

import 'package:flutter/services.dart';
import 'model/id_card_info_model.dart';

class Idread {
  static const MethodChannel _methodChannel = const MethodChannel('com.hs.flutter.idRead/MethodChannel');
  static const EventChannel _eventChannel = const EventChannel('com.hs.flutter.idRead/EventChannel');

  /// 检测扫码结果数据
  static void dataStreamListen(dynamic success, {dynamic error}) {
    _eventChannel.receiveBroadcastStream().listen((data) {
      success(data);
    }, onError: error);
  }

  static Future<bool> init() async {
    final bool result = await _methodChannel.invokeMethod('init');
    return result;
  }

  static Future<void> unInit() async {
    try {
      await _methodChannel.invokeMethod('unInit');
    } catch (e) {
      print(e.toString());
    }
  }

  static Future<bool> startRead() async {
    bool result = await _methodChannel.invokeMethod('startRead');
    return result;
  }

  static Future<void> stopRead() async {
    await _methodChannel.invokeMethod('stopRead');
  }
}
