import Flutter
import UIKit

class CameraPlatformView: NSObject, FlutterPlatformView {
    private let cameraView: CameraPreviewView

    init(frame: CGRect) {
        self.cameraView = CameraPreviewView(frame: frame)
        super.init()
    }

    func view() -> UIView {
        return cameraView
    }
}
