import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'handone_ar_platform_interface.dart';

/// An implementation of [HandoneArPlatform] that uses method channels.
class MethodChannelHandoneAr extends HandoneArPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('handone_ar');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
