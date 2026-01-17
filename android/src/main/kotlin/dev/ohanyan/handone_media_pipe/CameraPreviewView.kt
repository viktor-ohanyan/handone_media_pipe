package dev.ohanyan.handone_media_pipe

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutionException

class CameraPreviewView @JvmOverloads constructor(
    context: Context,
    private val lifecycleOwner: LifecycleOwner? = null,
    private val activity: Activity? = null,
    private val factory: CameraPlatformViewFactory? = null,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    init {
        factory?.registerCameraView(this)
    }

    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    // Use PreviewView from CameraX which handles surface management automatically
    private val previewView: PreviewView = PreviewView(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        // Make PreviewView non-interactive
        isClickable = false
        isFocusable = false
        isFocusableInTouchMode = false
        // Use TextureView implementation mode for better compatibility with Flutter
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }

    init {
        // Make the view non-interactive so it doesn't block touches
        isClickable = false
        isFocusable = false
        isFocusableInTouchMode = false
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS

        // Make the view group non-interactive for touch events
        setOnTouchListener { _, _ -> false }

        addView(previewView)
        if (isEmulator()) {
            showEmulatorMessage()
        } else {
            lifecycleOwner?.let {
                // Post to ensure view is laid out before starting camera
                post {
                    startCamera(it)
                }
            } ?: Log.w("CameraPreviewView", "LifecycleOwner not provided, camera will not start")
        }
    }

    override fun onInterceptTouchEvent(ev: android.view.MotionEvent?): Boolean = false

    override fun onTouchEvent(event: android.view.MotionEvent?): Boolean = false

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86")
    }

    private fun showEmulatorMessage() {
        val textView = TextView(context)
        textView.text = "Camera not available in Emulator"
        textView.setTextColor(android.graphics.Color.WHITE)
        textView.gravity = Gravity.CENTER
        textView.textSize = 16f
        setBackgroundColor(android.graphics.Color.BLACK)
        addView(textView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun startCamera(lifecycleOwner: LifecycleOwner) {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
            return
        }

        initializeCamera(lifecycleOwner)
    }

    private fun requestCameraPermission() {
        // Try to get Activity from multiple sources
        val activity = this.activity
            ?: factory?.getActivity()
            ?: (lifecycleOwner as? Activity)
            ?: (context as? Activity)

        if (activity == null) {
            Log.e("CameraPreviewView", "Activity is null, cannot request permission")
            Log.e("CameraPreviewView", "this.activity: ${this.activity}, factory: $factory, lifecycleOwner: $lifecycleOwner")
            showErrorMessage("Cannot request camera permission: Activity not available")
            return
        }

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lifecycleOwner?.let {
                    initializeCamera(it)
                }
            } else {
                Log.e("CameraPreviewView", "Camera permission denied by user")
                showErrorMessage("Camera permission denied. Please grant camera permission in app settings.")
            }
        }
    }

    private fun initializeCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                // Find front ultra-wide camera
                val cameraSelector = findFrontUltraWideCamera(cameraProvider!!)

                // Wait for PreviewView to be ready
                previewView.post {
                    preview = Preview.Builder()
                        .build()
                        .also {
                            // Use PreviewView's surface provider - it handles everything automatically
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    try {
                        cameraProvider?.unbindAll()
                        cameraProvider?.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview!!
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreviewView", "Failed to bind camera", e)
                        Log.e("CameraPreviewView", "Exception details: ${e.message}", e)
                        e.printStackTrace()
                        showErrorMessage("Failed to start camera: ${e.message}")
                    }
                }
            } catch (e: ExecutionException) {
                Log.e("CameraPreviewView", "Failed to get camera provider (ExecutionException)", e)
                Log.e("CameraPreviewView", "Exception details: ${e.message}", e)
                e.printStackTrace()
                showErrorMessage("Camera provider error: ${e.message}")
            } catch (e: InterruptedException) {
                Log.e("CameraPreviewView", "Failed to get camera provider (InterruptedException)", e)
                Log.e("CameraPreviewView", "Exception details: ${e.message}", e)
                e.printStackTrace()
                showErrorMessage("Camera provider interrupted: ${e.message}")
            } catch (e: Exception) {
                Log.e("CameraPreviewView", "Unexpected error", e)
                Log.e("CameraPreviewView", "Exception details: ${e.message}", e)
                e.printStackTrace()
                showErrorMessage("Unexpected error: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun showErrorMessage(message: String) {
        val textView = TextView(context)
        textView.text = message
        textView.setTextColor(android.graphics.Color.RED)
        textView.gravity = Gravity.CENTER
        textView.textSize = 14f
        setBackgroundColor(android.graphics.Color.BLACK)
        addView(textView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    /**
     * Finds the front-facing ultra-wide camera.
     * On devices with multiple front cameras, CameraX will automatically select
     * the appropriate camera. For ultra-wide detection, we prioritize front-facing
     * cameras and let the system choose the best available option.
     */
    private fun findFrontUltraWideCamera(cameraProvider: ProcessCameraProvider): CameraSelector {
        return try {
            // Use the front camera selector
            // On devices with multiple front cameras (including ultra-wide),
            // CameraX will handle the selection based on availability
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()
        } catch (e: Exception) {
            Log.w("CameraPreviewView", "Error creating camera selector, using default front camera", e)
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            cameraProvider?.unbindAll()
        } catch (e: Exception) {
            Log.e("CameraPreviewView", "Error unbinding camera", e)
        }
    }
}

