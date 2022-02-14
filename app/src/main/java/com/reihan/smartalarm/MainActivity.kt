package com.reihan.smartalarm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reihan.smartalarm.adapter.AlarmAdapter
import com.reihan.smartalarm.data.Alarm
import com.reihan.smartalarm.data.local.AlarmDB
import com.reihan.smartalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmAdapter: AlarmAdapter? = null

    private val db by lazy { AlarmDB(this) }

    private var alarmReceiver: AlarmReceiver? = null


    override fun onResume() {
        super.onResume()

        db.alarmDao().getAlarm().observe(this){
            alarmAdapter?.setData(it)
            Log.i("GetAlarm", "setUpRecyclerVIew: with this data $it")
        }
        //Tidak Jadi karena ingin menggunakan Live Toom
//        CoroutineScope(Dispatchers.IO).launch {
//            val alarm = db.alarmDao().getAlarm()
//            withContext(Dispatchers.Main){
//                alarmAdapter?.setData(alarm)
//            }
//
//            Log.i("GetAlarm", "setUpRecyclerVIew: with this data $alarm")
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setUpRecyclerView()

        alarmReceiver = AlarmReceiver()
    }

    private fun setUpRecyclerView() {
        binding.apply {
            rvReminderAlarm.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = alarmAdapter
            }
            swipeToDelete(rvReminderAlarm)
        }
    }

    private fun initView() {
        binding.apply {
            alarmAdapter = AlarmAdapter()
            cvOneTimeAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, OneTimeAlarmActivity::class.java))
            }

            cvRepeatAlarm.setOnClickListener {
                startActivity(Intent(applicationContext, RepeatingAlarmActivity::class.java))
                //Context Bisa Menggunakan applicationContext atau this@MainActivity
            }
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView){
        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val deletedItem = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                    CoroutineScope(Dispatchers.IO).launch {
                        deletedItem?.let {db.alarmDao().deleteAlarm(it)}
                        Log.i("DeleteAlarm", "OnSwiped: Succed deleted alarm $deletedItem")
                    }
                    deletedItem?.let { alarmReceiver?.CancelAlarm(applicationContext, it.type) }

                }
            }
        ).attachToRecyclerView(recyclerView)
    }
}




