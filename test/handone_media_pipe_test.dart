import 'package:flutter_test/flutter_test.dart';
import 'package:handone_media_pipe/handone_media_pipe.dart';
import 'package:handone_media_pipe/handone_media_pipe_platform_interface.dart';
import 'package:handone_media_pipe/handone_media_pipe_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHandoneMediaPipePlatform
    with MockPlatformInterfaceMixin
    implements HandoneMediaPipePlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HandoneMediaPipePlatform initialPlatform =
      HandoneMediaPipePlatform.instance;

  test('$MethodChannelHandoneMediaPipe is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHandoneMediaPipe>());
  });

  test('getPlatformVersion', () async {
    HandoneMediaPipe handoneMediaPipePlugin = HandoneMediaPipe();
    MockHandoneMediaPipePlatform fakePlatform = MockHandoneMediaPipePlatform();
    HandoneMediaPipePlatform.instance = fakePlatform;

    expect(await handoneMediaPipePlugin.getPlatformVersion(), '42');
  });
}
