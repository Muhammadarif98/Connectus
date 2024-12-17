package com.example.connectus.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName

data class Users(
    @PropertyName("userId") val userId: String? = "",
    @PropertyName("name") val userName: String? = "",
    @PropertyName("lastName") val userLastName: String? = "",
    @PropertyName("phone") val userPhone: String? = "",
    @PropertyName("email") val userEmail: String? = "",
    @PropertyName("password") val userPassword: String? = "",
    @PropertyName("confirmPassword") val userConfirmPassword: String? = "",
    @PropertyName("image") val userImageUrl: String? = "",
    @PropertyName("status") val status: String? = "",
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
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(userLastName)
        parcel.writeString(userPhone)
        parcel.writeString(userEmail)
        parcel.writeString(userPassword)
        parcel.writeString(userConfirmPassword)
        parcel.writeString(userImageUrl)
        parcel.writeString(status)
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
