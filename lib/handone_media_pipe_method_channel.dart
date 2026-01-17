import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'handone_media_pipe_platform_interface.dart';

/// An implementation of [HandoneMediaPipePlatform] that uses method channels.
class MethodChannelHandoneMediaPipe extends HandoneMediaPipePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('handone_media_pipe');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }
}
