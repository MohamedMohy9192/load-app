package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var downloadManager: DownloadManager

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentBinding: ContentMainBinding

    private lateinit var downloadStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        contentBinding = binding.contentMainLayout

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        contentBinding.customButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("TAG", "onReceive")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                val downloadStatusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(downloadStatusIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadStatus = "Success"
                        Log.i("TAG", "Success")
                    } else {
                        downloadStatus = "Failed"
                        Log.i("TAG", "Failed")
                    }
                }
            }
        }
    }

    private fun download() {
        val selectedUrl = when (contentBinding.radioGroup.checkedRadioButtonId) {
            R.id.glide_radio_button -> GlIDE_URL
            R.id.load_app_radio_button -> LOAD_APP_URL
            R.id.retrofit_radio_button -> RETROFIT_URL
            else -> null
        }

        if (selectedUrl != null) {
            contentBinding.customButton.buttonState = ButtonState.Loading
            val request =
                DownloadManager.Request(Uri.parse(selectedUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            Toast.makeText(
                this,
                R.string.no_selection_toast_message,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    companion object {
        private const val GlIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
