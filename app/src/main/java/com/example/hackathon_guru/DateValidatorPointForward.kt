package com.example.hackathon_guru

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.*

class DateValidatorPointForward(private val point: Long) : CalendarConstraints.DateValidator {

    override fun isValid(date: Long): Boolean {
        return date >= point
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(point)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DateValidatorPointForward> {
        override fun createFromParcel(parcel: Parcel): DateValidatorPointForward {
            return DateValidatorPointForward(parcel.readLong())
        }

        override fun newArray(size: Int): Array<DateValidatorPointForward?> {
            return arrayOfNulls(size)
        }

        fun now(): DateValidatorPointForward {
            return DateValidatorPointForward(System.currentTimeMillis())
        }
    }
}
