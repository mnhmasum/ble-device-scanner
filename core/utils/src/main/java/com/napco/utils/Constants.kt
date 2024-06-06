package com.napco.utils

import java.util.UUID

class Constants {

    enum class CharType(val type: String) {
        NOTIFY("Notify"),
        INDICATION("Indication"),
        READABLE("Readable"),
        WRITABLE("Writable"),
        WRITABLE_NO_RESPONSE("Writable Without Response"),
    }

    companion object {
        val DESCRIPTOR_PRE_CLIENT_CONFIG: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")
    }

}