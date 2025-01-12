package com.rakesh.chargingnorch.utils


import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.rakesh.chargingnorch.appFeature.OverlayChargingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class MyLifecycleOwner : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    val isInitialized: Boolean
        get() = true

    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }
}

class Window(
    private val context: Context,
    private val xOffset : Float,
    private val yOffset: Float,
    private val  radius : Float,
) {


    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var composeView = ComposeView(context)
    private var mParams: WindowManager.LayoutParams? = null

    private val mWindowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var lifecycleOwner = MyLifecycleOwner()
    private var isWindowOpen = false

    fun open() {
        if (!isWindowOpen) {
            updateWindowProperty()

            mParams?.gravity = Gravity.TOP or Gravity.START

            composeView = ComposeView(context).apply {
                setContent {
                    OverlayChargingScreen(
                        x = xOffset,
                        y = yOffset,
                        r = radius
                    )
                        Log.d("State", "State: Radius=$radius, X=${xOffset}, Y=${yOffset}")

                }
            }

            mWindowManager.addView(composeView, mParams)
            isWindowOpen = true

            lifecycleOwner = MyLifecycleOwner().apply {
                performRestore(null)
                handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }
            composeView.setViewTreeLifecycleOwner(lifecycleOwner)
            composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
    }

    private fun updateWindowProperty() {
        val (screenHeight, screenWidth) = getScreenSize()
        Log.d("Window Size", "Screen height: $screenHeight, Screen width: $screenWidth")

        mParams = WindowManager.LayoutParams(
            screenWidth,
            screenHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        mParams?.gravity = Gravity.TOP or Gravity.START

        if (isWindowOpen) {
            Log.d("Window", "Updating overlay window layout")
            mWindowManager.updateViewLayout(composeView, mParams)
        }
    }

    private fun getScreenSize(): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = mWindowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            bounds.height() to bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            mWindowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels to displayMetrics.widthPixels
        }
    }

    fun isOpen(): Boolean {
        return composeView.windowToken != null
    }

    fun closeOverlay() {
        try {
            if (composeView.isAttachedToWindow) {
                // Remove the view from the window manager
                (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(composeView)
                Log.d("Window", "Overlay closed successfully.")
            } else {
                Log.e("Window", "ComposeView is not attached to the window manager.")
            }

            // Attempt to invalidate the view if it's not already detached
            if (composeView.parent != null) {
                // Cast the parent safely, ensuring it's a ViewGroup
                (composeView.parent as? ViewGroup)?.removeAllViews()
            } else {
                Log.e("Window", "ComposeView has no parent or is already detached.")
            }

            // Handle lifecycle event for the overlay
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)


            Log.d("Window", "Overlay closed successfully.")
        } catch (e: Exception) {
            Log.e("Window", "Error closing overlay: ${e.localizedMessage}")
        } finally {
            // Final cleanup
            mParams = null
            composeView.invalidate()
            composeView = ComposeView(context) // Reinitialize the composeView
            System.gc() // Force garbage collection if needed
            isWindowOpen = false
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}