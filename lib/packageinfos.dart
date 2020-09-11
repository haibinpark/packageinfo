import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';


class Packageinfos {
  static const MethodChannel _channel = const MethodChannel('packageinfos');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  //获取App的安装列表
  static Future<List<dynamic>> get appInfos async {
    final String appInfos = await _channel.invokeMethod('getAppInfos');
    List<dynamic> appInfoObj = json.decode(appInfos);
    return appInfoObj;
  }

  static Future<Null> updateApk(String downloadUrl) async {
    return await _channel.invokeMethod('updateApk', <String, dynamic>{
      "downloadUrl": downloadUrl
    });
//    final String appInfos = await _channel.invokeMethod('getAppInfos');
//    List<dynamic> appInfoObj = json.decode(appInfos);
//    return appInfoObj;
  }

  static Future<String> get getExternalStorageDirectory async {
    return await _channel.invokeMethod('getExternalStorageDirectory');
  }
}
