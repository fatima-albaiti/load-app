package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit.git/archive/master.zip"
        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "Download"
        private const val NOTIFICATION_ID = 0
        const val DOWNLOAD_STATUS = "DOWNLOAD_STATUS"
        const val DOWNLOAD_NAME = "DOWNLOAD_NAME"
    }

    private var downloadID: Long = 0
    private var downloadStatus = "N/A"
    private var downloadName = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(CHANNEL_ID, CHANNEL_NAME)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val radioGroup = findViewById<RadioGroup>(R.id.download_radio)

        custom_button.setOnClickListener {
            when(radioGroup.checkedRadioButtonId){
                R.id.glide -> {
                    download(GLIDE_URL)
                    downloadName = getString(R.string.option_glide)
                }
                R.id.loadapp -> {
                    download(APP_URL)
                    downloadName = getString(R.string.option_load_app)
                }
                R.id.retrofit -> {
                    download(RETROFIT_URL)
                    downloadName = getString(R.string.option_retrofit)
                }
                else -> Toast.makeText(this, getString(R.string.select_an_option), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadID)
            val cursor = downloadManager.query(query)

            cursor.moveToFirst()
            downloadStatus = if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_SUCCESSFUL){
                "Success"
            } else {
                "Failed"
            }
            onDownloadComplete()
        }
    }

    private fun download(url: String) {
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun NotificationManager.sendNotification(message: String, applicationContext: Context) {
        val intent = Intent(applicationContext, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.putExtra(DOWNLOAD_STATUS, downloadStatus)
        intent.putExtra(DOWNLOAD_NAME, downloadName)
        pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        action = NotificationCompat.Action(R.drawable.ic_download_notif, getString(R.string.details), pendingIntent)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(getString(R.string.ttl_download_complete))
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_download_notif)
            .addAction(action)

        notify(NOTIFICATION_ID, builder.build())
    }

    private fun onDownloadComplete(){
        custom_button.buttonState = ButtonState.Completed
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(getString(R.string.download_complete), applicationContext)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }

            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download status"

            notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
