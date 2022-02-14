package com.reihan.smartalarm.helper

import androidx.recyclerview.widget.DiffUtil
import com.reihan.smartalarm.data.Alarm

class AlarmDiffUtil(private val oldList: List<Alarm>, private  val newList: List<Alarm>):
 DiffUtil.Callback(){
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val OldList = oldList[oldItemPosition]
        val NewList = newList[newItemPosition]
        return OldList.id == NewList.id
                && OldList.date == NewList.date
                && OldList.time == NewList.time
                && OldList.note == OldList.note

    }
}