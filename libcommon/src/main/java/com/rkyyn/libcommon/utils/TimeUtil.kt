package com.rkyyn.libcommon.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.StringUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object TimeUtil {
    interface PickTimeListener {
        fun onTimeSet(time: String?)
    }

    /**
     * 日期时间选择
     */
    fun showDatePick(mContext: Context?, defaultDate: Date?=null, timeListener: PickTimeListener) {
        try {
            val time = StringBuffer()

            //获取Calendar对象，用于获取当前时间
            val calendar = Calendar.getInstance()
            if(defaultDate != null){
                calendar.time = defaultDate
            }
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            //实例化DatePickerDialog对象
            val datePickerDialog = DatePickerDialog(
                mContext!!,
                { view, year, monthOfYear, dayOfMonth ->
                    //选择完日期后会调用该回调函数
                    //因为monthOfYear会比实际月份少一月所以这边要加1
                    time.append(
                        year.toString() + "-" + String.format(
                            "%02d",
                            monthOfYear + 1
                        ) + "-" + String.format("%02d", dayOfMonth)
                    )
                    //                    timeText.setText(time);
                    timeListener.onTimeSet(time.toString())
                }, year, month, day
            )
            //弹出选择日期对话框
            datePickerDialog.show()
        } catch (e: Exception) {
            Log.e("showDialogPick", e.message!!)
        }
    }

    /**
     * 日期时间选择
     */
    fun showDateTimePick(mContext: Context?, timeListener: PickTimeListener) {
        try {
            val time = StringBuffer()
            //获取Calendar对象，用于获取当前时间
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            //实例化TimePickerDialog对象
            val timePickerDialog = TimePickerDialog(mContext,
                { view, hourOfDay, minute ->

                    //选择完时间后会调用该回调函数
                    time.append(
                        " " + String.format("%02d", hourOfDay) + ":" + String.format(
                            "%02d",
                            minute
                        )
                    )
                    //设置TextView显示最终选择的时间
                    timeListener.onTimeSet(time.toString())
                }, hour, minute, true
            )
            //实例化DatePickerDialog对象
            val datePickerDialog = DatePickerDialog(
                mContext!!,
                { view, year, monthOfYear, dayOfMonth ->
                    //选择完日期后会调用该回调函数
                    //因为monthOfYear会比实际月份少一月所以这边要加1
                    time.append(
                        year.toString() + "-" + String.format(
                            "%02d",
                            monthOfYear + 1
                        ) + "-" + String.format("%02d", dayOfMonth)
                    )
                    //选择完日期后弹出选择时间对话框
                    timePickerDialog.show()
                }, year, month, day
            )
            //弹出选择日期对话框
            datePickerDialog.show()
        } catch (e: Exception) {
            Log.e("showDialogPick", e.message!!)
        }
    }


    private fun getDefaultFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    fun string2Date(time: String): Date? {
        return string2Date(time, getDefaultFormat())
    }

    fun string2Date(time: String, format: DateFormat): Date? {
        try {
            var sTime = time
            if (time.length == 10) {
                sTime += " 00:00:00"
            }
            return format.parse(sTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取当前月的开始时间
     * @return
     */
    fun getCurrentMonthBegin(): String? {
        val cal_1 = Calendar.getInstance() //获取当前日期
        cal_1.add(Calendar.MONTH, 0)
        cal_1[Calendar.DAY_OF_MONTH] = 1 //设置为1号,当前日期既为本月第一天
        cal_1[Calendar.HOUR] = 0
        cal_1[Calendar.MINUTE] = 0
        cal_1[Calendar.SECOND] = 0
        return getDefaultFormat().format(cal_1.time)
    }

    /**
     * 获取当前月的结束时间
     * @return
     */
    fun getCurrentMonthEnd(): String? {
        val ca = Calendar.getInstance()
        ca[Calendar.DAY_OF_MONTH] = ca.getActualMaximum(Calendar.DAY_OF_MONTH)
        ca[Calendar.HOUR] = 23
        ca[Calendar.MINUTE] = 59
        ca[Calendar.SECOND] = 59
        return getDefaultFormat().format(ca.time)
    }

    fun getMonthEnd(date: Date?): String? {
        val ca = Calendar.getInstance()
        ca.time = date
        ca[Calendar.DAY_OF_MONTH] = ca.getActualMaximum(Calendar.DAY_OF_MONTH)
        ca[Calendar.HOUR] = 23
        ca[Calendar.MINUTE] = 59
        ca[Calendar.SECOND] = 59
        return getDefaultFormat().format(ca.time)
    }

    fun getMonthEnd(date: Date?, format: String?): String? {
        val ca = Calendar.getInstance()
        ca.time = date
        ca[Calendar.DAY_OF_MONTH] = ca.getActualMaximum(Calendar.DAY_OF_MONTH)
        ca[Calendar.HOUR] = 23
        ca[Calendar.MINUTE] = 59
        ca[Calendar.SECOND] = 59
        val sdf: SimpleDateFormat
        sdf = if (StringUtils.isEmpty(format)) {
            getDefaultFormat()
        } else {
            SimpleDateFormat(format)
        }
        return sdf.format(ca.time)
    }
}