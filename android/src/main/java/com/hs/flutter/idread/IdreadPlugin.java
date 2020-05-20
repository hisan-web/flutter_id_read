package com.hs.flutter.idread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hs.flutter.idread.impl.StreamHandlerImpl;
import com.huashi.otg.sdk.GetImg;
import com.huashi.otg.sdk.HSIDCardInfo;
import com.huashi.otg.sdk.HandlerMsg;
import com.huashi.otg.sdk.HsOtgApi;
import com.huashi.otg.sdk.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** IdreadPlugin */
public class IdreadPlugin implements FlutterPlugin, MethodCallHandler {

  private static final String TAG = "IdReadPlugin";
  private static final String METHOD_CHANNEL = "com.hs.flutter.idRead/MethodChannel";
  private static final String EVENT_CHANNEL = "com.hs.flutter.idRead/EventChannel";

  private String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";

  private StreamHandlerImpl streamHandlerImpl;
  private Context context;

  private HsOtgApi api;
  private boolean apiInit = false;
  private boolean apiAuto = false;


  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      if (msg.what == HandlerMsg.READ_SUCCESS) {
        HSIDCardInfo ic = (HSIDCardInfo) msg.obj;
        Map<String, Object> output = new HashMap<>();
        output.put("certType", ic.getcertType());
        output.put("peopleName", ic.getPeopleName());
        output.put("idCard", ic.getIDCard());
        output.put("sex", ic.getSex());
        output.put("wltData", ic.getwltdata());
        byte[] bmpBuf = new byte[102 * 126 * 3 + 54 + 126 * 2]; // 照片头像bmp数据
        String bmpPath = "";
        int ret = api.unpack(ic.getwltdata(), bmpBuf, bmpPath);
        if (ret == 1) {//
//          Bitmap bitmap = BitmapFactory.decodeByteArray(bmpBuf, 0,bmpBuf.length);
//          Log.i(TAG, ""+bitmap);
//          output.put("bitMap", bitmap);
          output.put("wltData", ic.getwltdata());
        }
        streamHandlerImpl.eventSinkSuccess(output);
      }
    }
  };

  public static void registerWith(Registrar registrar) {
    final IdreadPlugin instance = new IdreadPlugin();
    instance.pluginRegister(registrar.messenger(), registrar.context());
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    pluginRegister(binding.getBinaryMessenger(), binding.getApplicationContext());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    pluginDestroy();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "init":
        initApi(result);
        break;
      case "startRead":
        startRead(result);
        break;
      case "stopRead":
        stopRead();
        break;
      case "unInit":
        pluginDestroy();
        break;
      default:
        result.notImplemented();
    }
  }

  // 注册插件
  private void pluginRegister(BinaryMessenger messenger, Context context) {
    final MethodChannel channel = new MethodChannel(messenger, METHOD_CHANNEL);
    channel.setMethodCallHandler(this);
    streamHandlerImpl = new StreamHandlerImpl(messenger, EVENT_CHANNEL);
    this.context = context;
  }

  // 插件销毁
  private void pluginDestroy() {
    if (api==null) {
      return;
    }
    api.unInit();
  }

  /**
   * 初始化api
   */
  private void initApi(Result result) {
    api = new HsOtgApi(handler,this.context);
    int ret = api.init();
    apiInit = ret == 1;
    result.success(apiInit);
  }

  /**
   * 开始读卡
   */
  private void startRead(Result result) {
    if (!apiInit) {
      Log.i(TAG, "请先初始化设备sdk");
    }
    // 卡认证
    apiAuto = true;
    new Thread(new CPUThread()).start();
    result.success(apiAuto);
  }

  /**
   * 停止读卡
   */
  private void stopRead() {
    apiAuto = false;
  }

  /**
   * 自动读卡
   */
  public class CPUThread extends Thread {
    public CPUThread() {
      super();
    }

    @Override
    public void run() {
      super.run();
      HSIDCardInfo ici;
      Message msg;
      while (apiAuto) {
        // ///////////////循环读卡，不拿开身份证
        if (api.NotAuthenticate(200, 200) != 1) {
          // ////////////////循环读卡，需要重新拿开身份证
          // if (api.Authenticate(200, 200) != 1) {
          // 卡认证失败
          msg = Message.obtain();
          msg.what = HandlerMsg.READ_ERROR;
          Log.i(TAG,""+HandlerMsg.READ_ERROR);
          handler.sendMessage(msg);
        } else {
          ici = new HSIDCardInfo();
          if (api.ReadCard(ici, 200, 1300) == 1) {
            //int readSuccess = HandlerMsg.READ_SUCCESS;
            Log.i(TAG, ici.toString());
            msg = Message.obtain();
            msg.obj = ici;
            msg.what = HandlerMsg.READ_SUCCESS;
            Log.i(TAG, msg.toString());
            handler.sendMessage(msg);
          }
        }
        SystemClock.sleep(300);
        msg = Message.obtain();
        msg.what = HandlerMsg.READ_ERROR;
        handler.sendMessage(msg);
        SystemClock.sleep(300);
      }
    }
  }
}
