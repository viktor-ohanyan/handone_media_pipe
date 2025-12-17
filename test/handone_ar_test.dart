import 'package:flutter_test/flutter_test.dart';
import 'package:handone_ar/handone_ar.dart';
import 'package:handone_ar/handone_ar_platform_interface.dart';
import 'package:handone_ar/handone_ar_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHandoneArPlatform
    with MockPlatformInterfaceMixin
    implements HandoneArPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HandoneArPlatform initialPlatform = HandoneArPlatform.instance;

  test('$MethodChannelHandoneAr is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHandoneAr>());
  });

  test('getPlatformVersion', () async {
    HandoneAr handoneArPlugin = HandoneAr();
    MockHandoneArPlatform fakePlatform = MockHandoneArPlatform();
    HandoneArPlatform.instance = fakePlatform;

    expect(await handoneArPlugin.getPlatformVersion(), '42');
  });
}
