package com.example.connectus.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName

data class Users(
    @PropertyName("userId") val userId: String? = "",
    @PropertyName("name") val name: String? = "",
    @PropertyName("lastName") val lastName: String? = "",
    @PropertyName("phone") val phone: String? = "",
    @PropertyName("email") val email: String? = "",
    @PropertyName("password") val password: String? = "",
    @PropertyName("confirmPassword") val confirmPassword: String? = "",
    @PropertyName("image") val imageUrl: String? = "",
    @PropertyName("status") val status: String? = "",
    @PropertyName("adress") val adress: String? = "",
    @PropertyName("age") val age: String? = "",
    @PropertyName("employee") val employee: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(lastName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(confirmPassword)
        parcel.writeString(imageUrl)
        parcel.writeString(status)
        parcel.writeString(adress)
        parcel.writeString(age)
        parcel.writeString(employee)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}