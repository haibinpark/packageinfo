import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:packageinfos/packageinfos.dart';

void main() {
  const MethodChannel channel = MethodChannel('packageinfos');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Packageinfos.platformVersion, '42');
  });
}
