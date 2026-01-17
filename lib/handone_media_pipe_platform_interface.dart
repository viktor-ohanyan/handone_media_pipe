import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'handone_media_pipe_method_channel.dart';

abstract class HandoneMediaPipePlatform extends PlatformInterface {
  /// Constructs a HandoneMediaPipePlatform.
  HandoneMediaPipePlatform() : super(token: _token);

  static final Object _token = Object();

  static HandoneMediaPipePlatform _instance = MethodChannelHandoneMediaPipe();

  /// The default instance of [HandoneMediaPipePlatform] to use.
  ///
  /// Defaults to [MethodChannelHandoneMediaPipe].
  static HandoneMediaPipePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HandoneMediaPipePlatform] when
  /// they register themselves.
  static set instance(HandoneMediaPipePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
