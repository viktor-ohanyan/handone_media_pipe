package dev.ohanyan.handone_media_pipe

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.platform.PlatformViewRegistry

/** HandoneMediaPipePlugin */
class HandoneMediaPipePlugin :
    FlutterPlugin,
    MethodCallHandler,
    ActivityAware {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var cameraFactory: CameraPlatformViewFactory? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "handone_media_pipe")
        channel.setMethodCallHandler(this)

        // Register platform view factory for camera preview
        // We'll update it with the Activity when it becomes available
        val registry: PlatformViewRegistry = flutterPluginBinding.platformViewRegistry
        cameraFactory = CameraPlatformViewFactory(flutterPluginBinding.applicationContext)
        registry.registerViewFactory(
            "camera_preview",
            cameraFactory!!
        )
    }

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        val lifecycleOwner = activity as? LifecycleOwner
        cameraFactory?.setActivity(lifecycleOwner)

        // Add permission result listener
        binding.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
            cameraFactory?.onRequestPermissionsResult(requestCode, permissions, grantResults)
            false // Return false to allow other plugins to handle it too
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        // Keep the activity reference during config changes
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        cameraFactory?.setActivity(activity as? LifecycleOwner)
    }

    override fun onDetachedFromActivity() {
        cameraFactory?.setActivity(null)
        activity = null
    }
}
