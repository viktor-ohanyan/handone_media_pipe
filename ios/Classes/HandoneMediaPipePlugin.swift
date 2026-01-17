import Flutter
import UIKit

public class HandoneMediaPipePlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(
            name: "handone_media_pipe",
            binaryMessenger: registrar.messenger()
        )
        let instance = HandoneMediaPipePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)

        let factory = CameraPlatformViewFactory(messenger: registrar.messenger())
        registrar.register(factory, withId: "camera_preview")
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "getPlatformVersion":
            result("iOS " + UIDevice.current.systemVersion)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
}
