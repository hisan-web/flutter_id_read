# idread flutter身份证阅读器插件（基于华视CRV100U）

## 使用方法

### 使用注意

开启权限
```
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" tools:ignore="ProtectedPermissions" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

### 加载依赖
```
readqrcode:
    git:
        https://github.com/hisan-web/flutter_id_read.git
```

### 使用
```
    Idread.init()  // 初始化sdk
    Idread.startRead() // 开始读卡
    Idread.endRead() // 结束读卡
    Idread.unInit() // 销毁sdk

    // 监听读卡数据
    Idread.dataStreamListen((data) {
        print(data.toString());
    });
```

### 使用demo

```
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
```
