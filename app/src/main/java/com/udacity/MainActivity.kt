package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
    private lateinit var fileName: String

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

        createChannel(
            CHANNEL_ID,
            getString(R.string.downloads_notification_channel_name)
        )
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
                sendNotification(fileName, downloadStatus)

            }
        }
    }

    private fun download() {
        val selectedUrl = when (contentBinding.radioGroup.checkedRadioButtonId) {
            R.id.glide_radio_button -> {
                fileName = contentBinding.glideRadioButton.text.toString()
                GlIDE_URL
            }
            R.id.load_app_radio_button -> {
                fileName = contentBinding.loadAppRadioButton.text.toString()
                LOAD_APP_URL
            }
            R.id.retrofit_radio_button -> {
                fileName = contentBinding.retrofitRadioButton.text.toString()
                RETROFIT_URL
            }
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

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Repository Downloads"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(fileName: String, downloadStatus: String) {
        notificationManager = getSystemService(
            NotificationManager::class.java
        ) as NotificationManager

        val detailActivityIntent = Intent(applicationContext, DetailActivity::class.java)
        detailActivityIntent.putExtra(FILE_NAME_EXTRA, fileName)
        detailActivityIntent.putExtra(DOWNLOAD_STATUS_EXTRA, downloadStatus)

        val detailActivityPendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            detailActivityIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                R.drawable.ic_baseline_cloud_done_24,
                getString(R.string.notification_button),
                detailActivityPendingIntent
            )
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val GlIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"

        const val FILE_NAME_EXTRA = "file_name"
        const val DOWNLOAD_STATUS_EXTRA = "download_status"
        const val REQUEST_CODE = 101
        const val DOWNLOAD_NOTIFICATION_ID = 202
    }

}
