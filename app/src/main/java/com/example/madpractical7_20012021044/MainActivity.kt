package com.example.madpractical7_20012021044


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mili: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cancelAlarmCardView = findViewById<MaterialCardView>(R.id.cancel_alarm_card_view)
        val btnCreateAlarm = findViewById<Button>(R.id.btn_set_alarm)
        val setAlarmTime = findViewById<TextView>(R.id.set_alarm_time)
        val btnCancelAlarm = findViewById<Button>(R.id.btn_cancel_alarm)
        val clockTC = findViewById<TextClock>(R.id.show_time_tv)

        clockTC.format12Hour = "hh:mm:ss a "

        cancelAlarmCardView.visibility = View.GONE

        btnCreateAlarm.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            val hour :Int = cal.get(Calendar.HOUR_OF_DAY)
            val min :Int = cal.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this, { _, hourOfDay, minute ->
                val formattedTime: String = when {
                    hourOfDay == 0 -> {
                        if (minute < 10) {
                            "${hourOfDay + 12}:0${minute} am"
                        } else {
                            "${hourOfDay + 12}:${minute} am"
                        }
                    }
                    hourOfDay > 12 -> {
                        if (minute < 10) {
                            "0${hourOfDay - 12}:0${minute} pm"
                        } else {
                            "0${hourOfDay - 12}:${minute} pm"
                        }
                    }
                    hourOfDay == 12 -> {
                        if (minute < 10) {
                            "${hourOfDay}:0${minute} pm"
                        } else {
                            "${hourOfDay}:${minute} pm"
                        }
                    }
                    else -> {
                        if (minute < 10) {
                            "${hourOfDay}:${minute} am"
                        } else {
                            "${hourOfDay}:${minute} am"
                        }
                    }
                }
                val timeInMilli:Long = getMillis(hourOfDay,minute)
                Log.i("MainActivity","Time : $hourOfDay : $minute")
                Log.i("MainActivity","Milliseconds : $timeInMilli")
                setAlarm(timeInMilli,"Start")
                Log.i("MainActivity","Alarm is Set !!")
                cancelAlarmCardView.visibility = View.VISIBLE
                setAlarmTime.text = formattedTime
            },hour,min,false)

            tpd.show()
        }

        btnCancelAlarm.setOnClickListener {
            setAlarm(mili, "Stop")
            cancelAlarmCardView.visibility = View.GONE
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarm(millisTime: Long, str: String) {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.putExtra("Service1", str)
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, 234324243, intent, FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (str == "Start") {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                millisTime,
                pendingIntent
            )

        } else if (str == "Stop") {
            alarmManager.cancel(pendingIntent)
            sendBroadcast(intent)

        }
    }

    private fun getMillis(hour: Int, min: Int): Long {
        val setcalendar = Calendar.getInstance()
        setcalendar[Calendar.HOUR_OF_DAY] = hour
        setcalendar[Calendar.MINUTE] = min
        setcalendar[Calendar.SECOND] = 0


        return setcalendar.timeInMillis
    }
}