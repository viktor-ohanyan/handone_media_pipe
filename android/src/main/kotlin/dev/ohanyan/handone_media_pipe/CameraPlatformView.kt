package dev.ohanyan.handone_media_pipe

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import io.flutter.plugin.platform.PlatformView

class CameraPlatformView(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val factory: CameraPlatformViewFactory
) : PlatformView {

    private val cameraView: CameraPreviewView = CameraPreviewView(
        context,
        lifecycleOwner,
        factory.getActivity() ?: (lifecycleOwner as? Activity) ?: (context as? Activity),
        factory
    )

    override fun getView(): View = cameraView

    override fun dispose() {
        factory.unregisterCameraView(cameraView)
        // Camera will be cleaned up by lifecycle
    }
}

