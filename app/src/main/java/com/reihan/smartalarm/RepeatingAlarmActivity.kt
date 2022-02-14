package com.reihan.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.reihan.smartalarm.data.Alarm
import com.reihan.smartalarm.data.local.AlarmDB
import com.reihan.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.reihan.smartalarm.fragment.TImeDialogFragment
import com.reihan.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TImeDialogFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    val db by lazy { AlarmDB(this) }

    private var alarmService: AlarmReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()

        initView()

    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeating.setOnClickListener {
                TImeDialogFragment().show(supportFragmentManager, "TimePickweDialog")
            }

            btnCancel.setOnClickListener {
                finish()
            }

            btnAdd.setOnClickListener {
                val note = edtNote.text.toString()
                val time = tvOnceTime.text.toString()

                if (time == "date") {
                    Toast.makeText(applicationContext, "Please Set Time", Toast.LENGTH_SHORT).show()
                } else {
                    alarmService?.setRepeatingAlarm(applicationContext, AlarmReceiver.TYPE_REPEATING, time, note)

                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                note,
                                AlarmReceiver.TYPE_REPEATING
                            )
                        )
                        Log.i("AddAlarm", "Alarm Set On $time with note $note")
                        finish()

                    }
                }
            }
        }
    }


    override fun onTimeSetListener(view: String?, hourOfDay: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hourOfDay, minute)
    }


}