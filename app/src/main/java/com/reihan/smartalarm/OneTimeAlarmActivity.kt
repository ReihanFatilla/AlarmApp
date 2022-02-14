package com.reihan.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.reihan.smartalarm.data.Alarm
import com.reihan.smartalarm.data.local.AlarmDB
import com.reihan.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.reihan.smartalarm.fragment.DateDialogFragment
import com.reihan.smartalarm.fragment.TImeDialogFragment
import com.reihan.smartalarm.helper.dateFormatter
import com.reihan.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OneTimeAlarmActivity : AppCompatActivity(), DateDialogFragment.DialogDateSetListener,
    TImeDialogFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private val db by lazy { AlarmDB(this) }

    private var alarmService: AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        alarmService = AlarmReceiver()


    }

    private fun initView() {
        binding.apply {

            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DateDialogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val TimePickerFragment = TImeDialogFragment()
                TimePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnCancel.setOnClickListener {
                finish()
            }

            btnAdd.setOnClickListener {
                val date = tvOnceDate.text.toString()
                val time = tvOnceTime.text.toString()
                val note = edtNoteOneTime.text.toString()

                if (date == "Date" || time == "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_set_alarm),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    alarmService?.setOneTimeAlarm(applicationContext, AlarmReceiver.TYPE_ONE_TIME, date, time, note)

                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                note,
                                AlarmReceiver.TYPE_ONE_TIME
                            )
                        )
                        Log.i("AddAlarm", "Alarm Set On $date $time with note $note")
                        finish()
                    }
                }
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
//        val calender = Calendar.getInstance()
//        //calender.set berguna untuk mengubah time yg pada awalnya mengambil waktu sekarang menjadi
//        //waktu yang telah kita set
//        calender.set(year, month, dayOfMonth)
//        val dateFormatted = SimpleDateFormat("dd-MM-yy", Locale.getDefault())

        //Menggunakan fungsi
        binding.tvOnceDate.text = dateFormatter(year, month, dayOfMonth)
    }

    override fun onTimeSetListener(view: String?, hourOfDay: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hourOfDay, minute)
    }
}