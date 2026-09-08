package com.govassist.app.overlay

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.govassist.app.MainActivity

/**
 * Shows a small, persistent floating bubble ("GovAssist is here") pinned to the
 * top-left of the screen, visible even while the user has switched to another
 * app such as the myGov/Centrelink app. This is a visual skeleton only:
 * tapping it currently just shows a placeholder message — wiring it up to the
 * real Guided Navigation agent (reading on-screen content, telling the user
 * what to do next) is future work.
 *
 * The bubble is intentionally NOT draggable and stays fixed top-left, per the
 * product requirement that it always be found in the same place.
 */
class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var bubbleView: View? = null
    private val pulseAnimators = mutableListOf<ValueAnimator>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
        showBubble()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        pulseAnimators.forEach { it.cancel() }
        pulseAnimators.clear()
        bubbleView?.let { view ->
            runCatching { windowManager?.removeView(view) }
        }
        bubbleView = null
    }

    private fun showBubble() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val bubbleSizeDp = 56
        val bubbleSizePx = dpToPx(bubbleSizeDp)

        // Circular background using the app's accent colour.
        val circleBackground = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(ACCENT_ORANGE_HEX))
        }

        val micLabel = TextView(this).apply {
            text = "\uD83C\uDF99" // 🎙 microphone glyph, avoids needing a bundled icon asset
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val bubbleContainer = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(bubbleSizePx, bubbleSizePx)
            background = circleBackground
            addView(
                micLabel,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
            contentDescription =
                "GovAssist floating assistant. Double tap to open GovAssist. Long press to close."
            isClickable = true
            isFocusable = true
            elevation = dpToPx(4).toFloat()

            setOnClickListener {
                Toast.makeText(
                    context,
                    "Voice assistant coming soon.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            setOnLongClickListener {
                stopSelf()
                true
            }
        }

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            bubbleSizePx,
            bubbleSizePx,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dpToPx(16)
            y = dpToPx(48)
        }

        wm.addView(bubbleContainer, params)
        bubbleView = bubbleContainer

        // Gentle pulse to signal the assistant is present/listening, without
        // implying real speech recognition is happening yet.
        val pulseX = ObjectAnimator.ofFloat(bubbleContainer, "scaleX", 1f, 1.12f, 1f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
        }
        val pulseY = ObjectAnimator.ofFloat(bubbleContainer, "scaleY", 1f, 1.12f, 1f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
        }
        pulseAnimators.add(pulseX)
        pulseAnimators.add(pulseY)
        pulseX.start()
        pulseY.start()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun buildNotification(): android.app.Notification {
        val channelId = "govassist_overlay_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "GovAssist floating assistant",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shown while the GovAssist bubble is visible over other apps."
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, OverlayService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("GovAssist is active")
            .setContentText("Tap to return to GovAssist.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(contentIntent)
            .addAction(0, "Close bubble", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 4821
        private const val ACTION_STOP = "com.govassist.app.overlay.STOP"
        private const val ACCENT_ORANGE_HEX = "#B5501E"
    }
}
