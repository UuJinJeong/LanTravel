package com.example.lantravel.navigation.model

import android.app.Notification
import android.icu.text.CaseMap

data class PushDTO (
    var to : String? = null,
    var notification : Notification = Notification()
){
    data class Notification(
        var body : String? = null,
        var title: String? = null
    )
}