import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:handone_media_pipe/handone_media_pipe_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelHandoneMediaPipe platform = MethodChannelHandoneMediaPipe();
  const MethodChannel channel = MethodChannel('handone_media_pipe');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
          return '42';
        });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
