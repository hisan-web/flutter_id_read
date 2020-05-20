import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:idread/idread.dart';
import 'package:idread/model/id_card_info_model.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Uint8List _uint8list;

  @override
  void initState() {
    super.initState();
    init();
  }
  
  @override
  void dispose() {
    super.dispose();
    unInit();
  }
  
  Future<void> unInit() async {
    await Idread.stopRead();
    await Idread.unInit();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> init() async {
    bool result = await Idread.init();
    if (result) {
      bool start = await Idread.startRead();
      if( start ) {
        Idread.dataStreamListen((data) {
          if (data.wltData != null && mounted) {
            setState(() {
              _uint8list = Base64Decoder().convert(data.base64Image);
            });

            print(data.toString());
            print(_uint8list);
            return;
          }
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: _uint8list == null ? Text('读取中') :Image.memory(_uint8list)
        ),
      ),
    );
  }
}
