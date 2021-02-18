package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

var duration: Long = 0
var filename: String = ""
var status: String = ""

class MainActivity : AppCompatActivity() {

    private var selectBool: Boolean = false
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            if (!selectBool) {
                // Show toast message if no radio buttons are selected
                Toast.makeText(applicationContext, getString(R.string.prompt_select), Toast.LENGTH_SHORT).show()
            } else {
                custom_button.buttonState = ButtonState.Clicked
                download()
            }

        }
    }

    fun onRadioButtonClick(view: View){
        if (view is RadioButton){
            val checked = view.isChecked
            selectBool = true

            when (view.id) {
                R.id.radioButton1 ->
                    if (checked) {
                        URL = "https://github.com/bumptech/glide/archive/master.zip"
                        filename = getString(R.string.glide_rb1)
                        duration = 9450
                    }
                R.id.radioButton2 ->
                    if (checked) {
                        URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                        filename = getString(R.string.loadapp_rb2)
                        duration = 1600
                    }

                R.id.radioButton3 ->
                    if (checked) {
                        URL = "https://github.com/square/retrofit/archive/master.zip"
                        filename = getString(R.string.retrofit_rb3)
                        duration = 1120
                    }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id){

                if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                    // Show Notification
                    val notificationManager = this@MainActivity.getSystemService(NotificationManager::class.java)
                    notificationManager.sendNotification(getString(R.string.notif_title), getString(R.string.notif_body), applicationContext)

                    // Set Button
                    custom_button.buttonState = ButtonState.Completed

                    // Get download status
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))

                    val manager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst() && cursor.count > 0) {
                        val statusId = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                        if (statusId == DownloadManager.STATUS_SUCCESSFUL){
                            status = getString(R.string.success)
                        } else {
                            status = getString(R.string.fail)
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private var URL = ""
    }


    private fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    getString(R.string.notification_channel_id),
                    getString(R.string.notification_channel_name),
                    // Change importance
                    NotificationManager.IMPORTANCE_HIGH
            )// Disable badges for this channel
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            notificationManager = this.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

}
