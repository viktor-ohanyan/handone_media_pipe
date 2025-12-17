import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'handone_ar_method_channel.dart';

abstract class HandoneArPlatform extends PlatformInterface {
  /// Constructs a HandoneArPlatform.
  HandoneArPlatform() : super(token: _token);

  static final Object _token = Object();

  static HandoneArPlatform _instance = MethodChannelHandoneAr();

  /// The default instance of [HandoneArPlatform] to use.
  ///
  /// Defaults to [MethodChannelHandoneAr].
  static HandoneArPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HandoneArPlatform] when
  /// they register themselves.
  static set instance(HandoneArPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
