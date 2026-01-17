import 'handone_media_pipe_platform_interface.dart';

class HandoneMediaPipe {
  Future<String?> getPlatformVersion() {
    return HandoneMediaPipePlatform.instance.getPlatformVersion();
  }
}
