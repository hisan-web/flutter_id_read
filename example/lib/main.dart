import 'package:flutter/material.dart';
import 'dart:async';

import 'package:idread/idread.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    init();
  }
  
  @override
  void dispose() {
    super.dispose();
    Idread.stopRead();
    unInit();
  }
  
  Future<void> unInit() async {
    await Idread.unInit();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> init() async {
    bool result = await Idread.init();
    if (result) {
      bool start = await Idread.startRead();
      if( start ) {
        Idread.dataStreamListen((data) {
          print(data.toString());
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
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }
}
