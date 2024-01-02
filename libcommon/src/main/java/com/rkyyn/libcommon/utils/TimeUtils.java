package com.rkyyn.libcommon.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * author : hepeng
 * time   : 2022/09/05
 * desc   :
 */
public class TimeUtils {
    /**
     * 日期时间选择
     */
    public static void showDatePick(Context mContext, final TextView timeText) {
        try {
            final StringBuffer time = new StringBuffer();
            //获取Calendar对象，用于获取当前时间
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            //实例化DatePickerDialog对象
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                //选择完日期后会调用该回调函数
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //因为monthOfYear会比实际月份少一月所以这边要加1
                    time.append(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                    timeText.setText(time);
                }
            }, year, month, day);
            //弹出选择日期对话框
            datePickerDialog.show();
        }catch (Exception e){
            Log.e("showDialogPick",e.getMessage());
        }
    }
    /**
     * 日期时间选择
     */
    public static void showDateTimePick(Context mContext,final TextView timeText) {
        try {
            final StringBuffer time = new StringBuffer();
            //获取Calendar对象，用于获取当前时间
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            //实例化TimePickerDialog对象
            final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

                //选择完时间后会调用该回调函数
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time.append(" " + String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    //设置TextView显示最终选择的时间
                    timeText.setText(time);
                }
            }, hour, minute, true);
            //实例化DatePickerDialog对象
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                //选择完日期后会调用该回调函数
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //因为monthOfYear会比实际月份少一月所以这边要加1
                    time.append(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));
                    //选择完日期后弹出选择时间对话框
                    timePickerDialog.show();
                }
            }, year, month, day);
            //弹出选择日期对话框
            datePickerDialog.show();
        }catch (Exception e){
            Log.e("showDialogPick",e.getMessage());
        }
    }


    private static SimpleDateFormat getDefaultFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    public static Date string2Date(final String time) {
        return string2Date(time,getDefaultFormat());
    }
    public static Date string2Date(final String time, @NonNull final DateFormat format) {
        try {
            String sTime = time;
            if(time.length()==10){
                sTime +=" 00:00:00";
            }
            return format.parse(sTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前月的开始时间
     * @return
     */
    public static String getCurrentMonthBegin(){
        Calendar cal_1=Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, 0);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        cal_1.set(Calendar.HOUR,0);
        cal_1.set(Calendar.MINUTE,0);
        cal_1.set(Calendar.SECOND,0);
        return getDefaultFormat().format(cal_1.getTime());
    }

    /**
     * 获取当前月的结束时间
     * @return
     */
    public static String getCurrentMonthEnd(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        ca.set(Calendar.HOUR,23);
        ca.set(Calendar.MINUTE,59);
        ca.set(Calendar.SECOND,59);
        return getDefaultFormat().format(ca.getTime());
    }
    public static String getMonthEnd(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        ca.set(Calendar.HOUR,23);
        ca.set(Calendar.MINUTE,59);
        ca.set(Calendar.SECOND,59);
        return getDefaultFormat().format(ca.getTime());
    }
    public static String getMonthEnd(Date date,String format){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        ca.set(Calendar.HOUR,23);
        ca.set(Calendar.MINUTE,59);
        ca.set(Calendar.SECOND,59);
        SimpleDateFormat sdf ;
        if(StringUtils.isEmpty(format)){
            sdf = getDefaultFormat();
        }else{
            sdf = new SimpleDateFormat(format);
        }
        return sdf.format(ca.getTime());
    }
}
