package com.reihan.smartalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val type = intent?.getIntExtra(EXTRA_TYPE, -1)
        val note = intent?.getStringExtra(EXTRA_MESSAGE)
        val title = if (type == TYPE_ONE_TIME) "One Time Alarm" else "Repeating Alarm"

        val notificationId = if (type == TYPE_ONE_TIME) 101 else 102

        if (note != null) context?.let { showALarmNotification(it, title, note, notificationId) }
    }

    private fun showALarmNotification(
        context: Context,
        title: String,
        note: String,
        notificationId: Int
    ){
        val channelId = "Channel_1"
        val channelName = "AlarmManager channel"

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(note)
            .setSound(alarmSound)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(notificationId, notification)
    }

    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, note: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        intent.putExtra(EXTRA_TYPE, type)

        Log.e("ErrorSetOneTimeAlarm", "setOneTimeAlarm: $date $time")

        // date yg di terima misalnya 10-12-2022
        // split berfungsi untuk menghilangkan strip(-) pada date
        val dateArray = date.split("-").toTypedArray()
        convertedStringInt(dateArray)
        val timeArray = time.split(":").toTypedArray()
        convertedStringInt(timeArray)

        val calendar = Calendar.getInstance()
        //DATE
        calendar.set(Calendar.DAY_OF_MONTH, convertedStringInt(dateArray)[0])
        calendar.set(Calendar.MONTH, convertedStringInt(dateArray)[1]-1)
        calendar.set(Calendar.YEAR, convertedStringInt(dateArray)[2])

        //TIME
        calendar.set(Calendar.HOUR, convertedStringInt(timeArray)[0])
        calendar.set(Calendar.MINUTE, convertedStringInt(timeArray)[1])

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Succes Set One Time Alarm", Toast.LENGTH_SHORT).show()
        Log.i("SetAlarmNotif","setOneTimeAlarm: Alarm Will ring on ${calendar.time}")
    }

    private fun convertedStringInt(array: Array<String>) : List<Int>{
        return array.map {
            it.toInt()
        }
    }

    fun setRepeatingAlarm(context: Context, type: Int, time: String, note: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

    fun CancelAlarm(context: Context, type: Int) {
        // Alarm Manager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // intent e alarm receiver
        val intent = Intent(context, AlarmReceiver::class.java)
        // ambil requestCode/ id alarm berdasarkan tipe alarm nya
        val requestCode = if(type == TYPE_ONE_TIME) ID_ONE_TIME else ID_REPEATING

        // Cancel Pending Intent
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_MUTABLE)
        pendingIntent.cancel()

        // Cancel Alarm Manager
        alarmManager.cancel(pendingIntent)
        if (type == TYPE_ONE_TIME) {
            Toast.makeText(context, "Succesfully Cancel One Time Alarm", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(context, "Succesfully Cancel Repeating Alarm", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val EXTRA_TYPE = "type"
        const val EXTRA_NOTE = "note"

        const val TYPE_ONE_TIME = 0
        const val TYPE_REPEATING = 1

        const val ID_ONE_TIME = 101
        const val ID_REPEATING = 102
    }


}