// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'AppInfo.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AppInfo _$AppInfoFromJson(Map<String, dynamic> json) {
  return AppInfo()
    ..isSystem = json['isSystem'] as bool
    ..name = json['name'] as String
    ..packageName = json['packageName'] as String
    ..packagePath = json['packagePath'] as String
    ..isRunning = json['isRunning'] as bool
    ..versionCode = json['versionCode'] as num
    ..versionName = json['versionName'] as String;
}

Map<String, dynamic> _$AppInfoToJson(AppInfo instance) => <String, dynamic>{
      'isSystem': instance.isSystem,
      'name': instance.name,
      'packageName': instance.packageName,
      'packagePath': instance.packagePath,
      'isRunning': instance.isRunning,
      'versionCode': instance.versionCode,
      'versionName': instance.versionName
    };
