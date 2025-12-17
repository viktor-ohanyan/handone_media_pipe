import 'handone_ar_platform_interface.dart';

class HandoneAr {
  Future<String?> getPlatformVersion() {
    return HandoneArPlatform.instance.getPlatformVersion();
  }
}
