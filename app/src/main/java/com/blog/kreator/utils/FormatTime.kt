package com.blog.kreator.utils

import android.util.Log
import org.threeten.bp.temporal.ChronoField
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object FormatTime {
    fun getFormattedTime(dateTime:String) : String{
        val date = dateTime.substring(0,10)
        val time = dateTime.substring(11,19)
        val year = dateTime.substring(0,4).toInt()
        val month = dateTime.substring(5,7).toInt()
        val day = dateTime.substring(8,10).toInt()
        val hour = dateTime.substring(11,13)
        val minute = dateTime.substring(14,16)
        val seconds = dateTime.substring(17,19)

        val outputFormat: DateFormat = SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH)
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val inputText: String = date
        var getDate: Date? = null
        try {
            getDate = inputFormat.parse(inputText)
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("Title", "------" + e.message.toString() + " = " + inputText)
        }
        var output = ""

        /** DateTime Functionality */
        if (date.equals(org.threeten.bp.LocalDate.now().toString(),true)){
            output = if (time.equals(org.threeten.bp.LocalTime.now().toString(),true)){
                "Just now"
            }else if (hour.equals(org.threeten.bp.LocalTime.now().hour.toString(),true)){
                if (minute.equals(org.threeten.bp.LocalTime.now().minute.toString(),true)){
                    "${org.threeten.bp.LocalTime.now().second.minus(seconds.toInt())} seconds ago"
                }else{
                    "${org.threeten.bp.LocalTime.now().minute.minus(minute.toInt())} minutes ago"
                }
            }else{
                "${org.threeten.bp.LocalTime.now().hour.minus(hour.toInt())} hours ago"
            }
        }else if(date.equals(org.threeten.bp.LocalDate.now().minusDays(1).toString(),true)){
            output = "Yesterday"
        }else if(org.threeten.bp.LocalDate.of(year, month, day).get(ChronoField.MONTH_OF_YEAR) == org.threeten.bp.LocalDate.now().get(
                ChronoField.MONTH_OF_YEAR)) {
            output = if (org.threeten.bp.LocalDate.of(year, month, day).get(ChronoField.ALIGNED_WEEK_OF_MONTH) == org.threeten.bp.LocalDate.now().get(
                    ChronoField.ALIGNED_WEEK_OF_MONTH)) {
//                "This Week"
                (7 - org.threeten.bp.LocalDate.now().dayOfWeek.value).toString() + " days ago"
            } else {
                outputFormat.format(getDate!!)
            }
        } else {
            output = outputFormat.format(getDate!!)
        }

        return output
    }
}