package com.example.ecommerceproject.Model

import android.accounts.AuthenticatorDescription
import android.adservices.adid.AdId
import android.media.Rating
import android.telephony.mbms.StreamingServiceInfo
import androidx.compose.ui.text.LinkAnnotation
import java.io.Serializable
import kotlin.collections.ArrayList

data class ItemsModel(
    var title:String="",
    var description: String="",
    var picUrl: ArrayList<String> = ArrayList(),
    var model: ArrayList<String> = ArrayList(),
    var price: Double=0.0,
    var rating: Double=0.0,
    var numberInCart: Int=0,
    var showRecommnded: Boolean = false,
    var categoryId: String = ""
) : Serializable
