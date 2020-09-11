import 'package:json_annotation/json_annotation.dart';

part 'AppInfo.g.dart';

@JsonSerializable()
class AppInfo {
    AppInfo();

    bool isSystem;
    String name;
    String packageName;
    String packagePath;
    bool isRunning;
    num versionCode;
    String versionName;
    
    factory AppInfo.fromJson(Map<String,dynamic> json) => _$AppInfoFromJson(json);
    Map<String, dynamic> toJson() => _$AppInfoToJson(this);
}
