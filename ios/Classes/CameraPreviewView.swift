import UIKit
import AVFoundation

class CameraPreviewView: UIView {

    private let captureSession = AVCaptureSession()
    private var previewLayer: AVCaptureVideoPreviewLayer?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupCamera()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupCamera()
    }

    private var isSimulator: Bool {
        #if targetEnvironment(simulator)
        return true
        #else
        return false
        #endif
    }

    private func setupCamera() {
        if isSimulator {
            showSimulatorMessage()
            print("Camera not available in Simulator")
            return
        }

        captureSession.sessionPreset = .high

        let cameraTypes: [AVCaptureDevice.DeviceType] = [.builtInTrueDepthCamera, .builtInWideAngleCamera]

        var camera: AVCaptureDevice? = nil

        for type in cameraTypes {
            if let device = AVCaptureDevice.default(type, for: .video, position: .front) {
                camera = device
                break
            }
        }

        guard let selectedCamera = camera,
              let input = try? AVCaptureDeviceInput(device: selectedCamera)
        else {
            print("No camera available")
            return
        }

        if captureSession.canAddInput(input) {
            captureSession.addInput(input)
        }

        let layer = AVCaptureVideoPreviewLayer(session: captureSession)
        layer.videoGravity = .resizeAspectFill
        layer.frame = bounds

        self.layer.addSublayer(layer)
        previewLayer = layer

        captureSession.startRunning()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        previewLayer?.frame = bounds
    }

    deinit {
        captureSession.stopRunning()
    }

    private func showSimulatorMessage() {
        backgroundColor = .black
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        label.frame = bounds
        label.numberOfLines = 0
        label.text = "Camera not available in Simulator"
        label.textAlignment = .center
        label.textColor = .white
        label.translatesAutoresizingMaskIntoConstraints = false
        addSubview(label)

        NSLayoutConstraint.activate([
                                        label.centerXAnchor.constraint(equalTo: centerXAnchor),
                                        label.centerYAnchor.constraint(equalTo: centerYAnchor),
                                        label.leadingAnchor.constraint(greaterThanOrEqualTo: leadingAnchor, constant: 20),
                                        label.trailingAnchor.constraint(lessThanOrEqualTo: trailingAnchor, constant: -20)
                                    ])
    }
}
