package com.mnh.service.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter


class Utility {
    companion object {
        fun getCurrentTime(): String {
            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            return currentTime.format(formatter)
        }
    }

}