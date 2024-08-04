package com.example.hackathon_guru

import android.os.Parcel
import android.os.Parcelable

data class TravelGroup(
    var name: String,
    var dates: String,
    var members: List<String>,
    val isPast: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dates)
        parcel.writeStringList(members)
        parcel.writeByte(if (isPast) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TravelGroup> {
        override fun createFromParcel(parcel: Parcel): TravelGroup {
            return TravelGroup(parcel)
        }

        override fun newArray(size: Int): Array<TravelGroup?> {
            return arrayOfNulls(size)
        }
    }
}
