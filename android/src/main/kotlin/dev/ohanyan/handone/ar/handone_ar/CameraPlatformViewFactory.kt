package dev.ohanyan.handone.ar.handone_ar

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import io.flutter.plugin.common.StandardMessageCodec

class CameraPlatformViewFactory(
    private val context: Context
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    @Volatile
    private var lifecycleOwner: LifecycleOwner? = null

    @Volatile
    private var activity: Activity? = null

    private val cameraViews = mutableListOf<CameraPreviewView>()

    fun setActivity(activity: LifecycleOwner?) {
        lifecycleOwner = activity
        this.activity = activity as? Activity
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        cameraViews.forEach { view ->
            view.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        // viewId and args are not used but required by PlatformViewFactory interface
        // Try to get LifecycleOwner from stored value first
        val owner = lifecycleOwner ?: when (val ctx = context) {
            is LifecycleOwner -> ctx
            is Activity -> ctx as? LifecycleOwner
            else -> {
                // Try to get Activity from context
                var currentContext = context
                var foundActivity: Activity? = null
                while (currentContext is android.content.ContextWrapper) {
                    if (currentContext is Activity) {
                        foundActivity = currentContext
                        break
                    }
                    currentContext = currentContext.baseContext
                }
                foundActivity as? LifecycleOwner
            }
        }

        if (owner == null) {
            Log.e("CameraPlatformViewFactory", "Failed to get LifecycleOwner")
            throw IllegalStateException("Activity (LifecycleOwner) is not available. Make sure the plugin is properly initialized.")
        }

        return CameraPlatformView(context, owner, this)
    }

    fun registerCameraView(view: CameraPreviewView) {
        cameraViews.add(view)
    }

    fun unregisterCameraView(view: CameraPreviewView) {
        cameraViews.remove(view)
    }

    fun getActivity(): Activity? = activity
}

