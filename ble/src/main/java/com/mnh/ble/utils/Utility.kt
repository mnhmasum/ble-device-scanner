package com.mnh.ble.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Formatter
import java.util.Locale
import java.util.UUID


class Utility {

    companion object {
        fun encryptData(data: ByteArray, key: ByteArray?): ByteArray {
            val encryptedData = ByteArray(20)
            val paddedData = ByteArray(20)
            val encryptionKey = key ?: ByteArray(20)

            for (i in data.indices) {
                paddedData[i] = data[i]
            }

            for (i in data.size until 15) {
                paddedData[i] = 0x20.toByte()
            }

            for (i in 0 until 20) {
                encryptedData[i] = (paddedData[i] + encryptionKey[i]).toByte()
            }

            return encryptedData
        }

        fun passData(password: String): ByteArray {
            val badgeNo = "337800489559774425".toLong()
            val pktLen = 15
            val passData = ByteArray(pktLen)

            for (i in 0 until 8) {
                passData[i] = ((badgeNo shr (i * 8)) and 0xff).toByte()
            }

            return passData
        }


        private const val LOG_TAG = "MyLogger"
        private const val LOG_FILE_NAME = "my_log.txt"

        fun logToFile(context: Context, message: String) {
            try {
                // Check if external storage is available
                if (isExternalStorageAvailable()) {
                    // Get the external storage directory
                    //val externalStorageDir = context.getExternalFilesDir(null)

                    val downloadDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    // Create a subdirectory if it doesn't exist
                    val logDir = File(downloadDir, "Logs")
                    if (!logDir.exists()) {
                        logDir.mkdir()
                    }

                    // Create a file for the log
                    val logFile = File(logDir, LOG_FILE_NAME)

                    // Get the current timestamp
                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

                    // Append the timestamp to the log message
                    val logMessage = "$timeStamp: $message\n"

                    // Write the log message to the file
                    val outputStream = FileOutputStream(logFile, true)
                    val outputStreamWriter = OutputStreamWriter(outputStream)
                    outputStreamWriter.append(logMessage)
                    outputStreamWriter.close()

                    Log.d(LOG_TAG, "Log written to file: " + logFile.absolutePath)
                } else {
                    Log.e(LOG_TAG, "External storage not available.")
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error writing log to file: " + e.message)
            }
        }

        private fun isExternalStorageAvailable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

        fun bytesToHexString(bytes: ByteArray): String {
            val sb = java.lang.StringBuilder(bytes.size * 2)
            val formatter = Formatter(sb)
            for (b in bytes) formatter.format("%02x ", b)
            return sb.toString()
        }

        fun getServiceName(serviceUUID: UUID): String {
            return when (serviceUUID) {
                UUID.fromString("00001800-0000-1000-8000-00805F9B34FB") -> "GENERIC ACCESS"
                UUID.fromString("00001801-0000-1000-8000-00805F9B34FB") -> "GENERIC ATTRIBUTE"
                UUID.fromString("00001802-0000-1000-8000-00805F9B34FB") -> "IMMEDIATE ALERT"
                UUID.fromString("00001803-0000-1000-8000-00805F9B34FB") -> "LINK LOSS"
                UUID.fromString("00001804-0000-1000-8000-00805F9B34FB") -> "TX POWER"
                UUID.fromString("00001805-0000-1000-8000-00805F9B34FB") -> "CURRENT TIME SERVICE"
                UUID.fromString("00001806-0000-1000-8000-00805F9B34FB") -> "REFERENCE TIME UPDATE SERVICE"
                UUID.fromString("00001807-0000-1000-8000-00805F9B34FB") -> "NEXT DST CHANGE SERVICE"
                UUID.fromString("00001808-0000-1000-8000-00805F9B34FB") -> "GLUCOSE"
                UUID.fromString("00001809-0000-1000-8000-00805F9B34FB") -> "HEALTH THERMOMETER"
                UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB") -> "DEVICE INFORMATION"
                UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB") -> "HEART RATE"
                UUID.fromString("0000180E-0000-1000-8000-00805F9B34FB") -> "PHONE ALERT STATUS SERVICE"
                UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB") -> "BATTERY SERVICE"
                UUID.fromString("00001810-0000-1000-8000-00805F9B34FB") -> "BLOOD PRESSURE"
                UUID.fromString("00001811-0000-1000-8000-00805F9B34FB") -> "ALERT NOTIFICATION SERVICE"
                UUID.fromString("00001812-0000-1000-8000-00805F9B34FB") -> "HUMAN INTERFACE DEVICE"
                UUID.fromString("00001813-0000-1000-8000-00805F9B34FB") -> "SCAN PARAMETERS"
                UUID.fromString("00001814-0000-1000-8000-00805F9B34FB") -> "RUNNING SPEED AND CADENCE"
                UUID.fromString("00001816-0000-1000-8000-00805F9B34FB") -> "CYCLING SPEED AND CADENCE"
                UUID.fromString("00001818-0000-1000-8000-00805F9B34FB") -> "CYCLING POWER"
                UUID.fromString("00001819-0000-1000-8000-00805F9B34FB") -> "LOCATION AND NAVIGATION"
                UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB") -> "ENVIRONMENTAL SENSING"
                UUID.fromString("0000181B-0000-1000-8000-00805F9B34FB") -> "BODY COMPOSITION"
                UUID.fromString("0000181C-0000-1000-8000-00805F9B34FB") -> "USER DATA"
                UUID.fromString("0000181D-0000-1000-8000-00805F9B34FB") -> "WEIGHT SCALE"
                UUID.fromString("0000181E-0000-1000-8000-00805F9B34FB") -> "BOND MANAGEMENT SERVICE"
                UUID.fromString("0000181F-0000-1000-8000-00805F9B34FB") -> "CONTINUOUS GLUCOSE MONITORING"
                UUID.fromString("00001820-0000-1000-8000-00805F9B34FB") -> "INTERNET PROTOCOL SUPPORT SERVICE"
                UUID.fromString("00001821-0000-1000-8000-00805F9B34FB") -> "INDOOR POSITIONING"
                UUID.fromString("00001822-0000-1000-8000-00805F9B34FB") -> "PULSE OXIMETER SERVICE"
                UUID.fromString("00001823-0000-1000-8000-00805F9B34FB") -> "HTTP PROXY"
                UUID.fromString("00001824-0000-1000-8000-00805F9B34FB") -> "TRANSPORT DISCOVERY"
                UUID.fromString("00001825-0000-1000-8000-00805F9B34FB") -> "OBJECT TRANSFER SERVICE"
                UUID.fromString("00001826-0000-1000-8000-00805F9B34FB") -> "FITNESS MACHINE"
                UUID.fromString("00001827-0000-1000-8000-00805F9B34FB") -> "MESH PROVISIONING SERVICE"
                UUID.fromString("00001828-0000-1000-8000-00805F9B34FB") -> "MESH PROXY SERVICE"
                UUID.fromString("00001829-0000-1000-8000-00805F9B34FB") -> "RECONNECTION CONFIGURATION"
                UUID.fromString("0000183A-0000-1000-8000-00805F9B34FB") -> "INSULIN DELIVERY"

                else -> serviceUUID.toString()
            }
        }

        fun getCharacteristicPurpose(characteristicUuid: UUID): String {
            return when (characteristicUuid) {
                // Device Information Service
                UUID.fromString("00002A00-0000-1000-8000-00805F9B34FB") -> "DEVICE NAME"
                UUID.fromString("00002A01-0000-1000-8000-00805F9B34FB") -> "APPEARANCE"
                UUID.fromString("00002A02-0000-1000-8000-00805F9B34FB") -> "PERIPHERAL PRIVACY FLAG"
                UUID.fromString("00002A03-0000-1000-8000-00805F9B34FB") -> "RECONNECTION ADDRESS"
                UUID.fromString("00002A04-0000-1000-8000-00805F9B34FB") -> "PERIPHERAL PREFERRED CONNECTION PARAMETERS"
                UUID.fromString("00002A05-0000-1000-8000-00805F9B34FB") -> "SERVICE CHANGED"
                UUID.fromString("00002A06-0000-1000-8000-00805F9B34FB") -> "ALERT LEVEL"
                UUID.fromString("00002A07-0000-1000-8000-00805F9B34FB") -> "TX POWER LEVEL"
                UUID.fromString("00002A08-0000-1000-8000-00805F9B34FB") -> "DATE TIME"
                UUID.fromString("00002A09-0000-1000-8000-00805F9B34FB") -> "DAY OF WEEK"
                UUID.fromString("00002A0A-0000-1000-8000-00805F9B34FB") -> "DAY DATE TIME"
                UUID.fromString("00002A0C-0000-1000-8000-00805F9B34FB") -> "EXACT TIME 256"
                UUID.fromString("00002A0D-0000-1000-8000-00805F9B34FB") -> "DST OFFSET"
                UUID.fromString("00002A0E-0000-1000-8000-00805F9B34FB") -> "TIME ZONE"
                UUID.fromString("00002A0F-0000-1000-8000-00805F9B34FB") -> "LOCAL TIME INFORMATION"
                UUID.fromString("00002A11-0000-1000-8000-00805F9B34FB") -> "TIME WITH DST"
                UUID.fromString("00002A12-0000-1000-8000-00805F9B34FB") -> "TIME ACCURACY"
                UUID.fromString("00002A13-0000-1000-8000-00805F9B34FB") -> "TIME SOURCE"
                UUID.fromString("00002A14-0000-1000-8000-00805F9B34FB") -> "REFERENCE TIME INFORMATION"
                UUID.fromString("00002A16-0000-1000-8000-00805F9B34FB") -> "TIME UPDATE CONTROL POINT"
                UUID.fromString("00002A17-0000-1000-8000-00805F9B34FB") -> "TIME UPDATE STATE"
                UUID.fromString("00002A18-0000-1000-8000-00805F9B34FB") -> "GLUCOSE MEASUREMENT"
                UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB") -> "BATTERY LEVEL"
                UUID.fromString("00002A1C-0000-1000-8000-00805F9B34FB") -> "TEMPERATURE MEASUREMENT"
                UUID.fromString("00002A1D-0000-1000-8000-00805F9B34FB") -> "TEMPERATURE TYPE"
                UUID.fromString("00002A1E-0000-1000-8000-00805F9B34FB") -> "INTERMEDIATE TEMPERATURE"
                UUID.fromString("00002A21-0000-1000-8000-00805F9B34FB") -> "MEASUREMENT INTERVAL"
                UUID.fromString("00002A22-0000-1000-8000-00805F9B34FB") -> "BOOT KEYBOARD INPUT REPORT"
                UUID.fromString("00002A23-0000-1000-8000-00805F9B34FB") -> "SYSTEM ID"
                UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB") -> "MODEL NUMBER STRING"
                UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB") -> "SERIAL NUMBER STRING"
                UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB") -> "FIRMWARE REVISION STRING"
                UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB") -> "HARDWARE REVISION STRING"
                UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB") -> "SOFTWARE REVISION STRING"
                UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB") -> "MANUFACTURER NAME STRING"
                UUID.fromString("00002A2A-0000-1000-8000-00805F9B34FB") -> "IEEE 11073-20601 REGULATORY CERTIFICATION DATA LIST"
                UUID.fromString("00002A2B-0000-1000-8000-00805F9B34FB") -> "CURRENT TIME"
                UUID.fromString("00002A2C-0000-1000-8000-00805F9B34FB") -> "MAGNETIC DECLINATION"
                UUID.fromString("00002A31-0000-1000-8000-00805F9B34FB") -> "SCAN REFRESH"
                UUID.fromString("00002A32-0000-1000-8000-00805F9B34FB") -> "BOOT KEYBOARD OUTPUT REPORT"
                UUID.fromString("00002A33-0000-1000-8000-00805F9B34FB") -> "BOOT MOUSE INPUT REPORT"
                UUID.fromString("00002A34-0000-1000-8000-00805F9B34FB") -> "GLUCOSE MEASUREMENT CONTEXT"
                UUID.fromString("00002A35-0000-1000-8000-00805F9B34FB") -> "BLOOD PRESSURE MEASUREMENT"
                UUID.fromString("00002A36-0000-1000-8000-00805F9B34FB") -> "INTERMEDIATE CUFF PRESSURE"
                UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB") -> "HEART RATE MEASUREMENT"
                UUID.fromString("00002A38-0000-1000-8000-00805F9B34FB") -> "BODY SENSOR LOCATION"
                UUID.fromString("00002A39-0000-1000-8000-00805F9B34FB") -> "HEART RATE CONTROL POINT"
                UUID.fromString("00002A3F-0000-1000-8000-00805F9B34FB") -> "ALERT STATUS"
                UUID.fromString("00002A40-0000-1000-8000-00805F9B34FB") -> "RINGER CONTROL POINT"
                UUID.fromString("00002A41-0000-1000-8000-00805F9B34FB") -> "RINGER SETTING"
                UUID.fromString("00002A42-0000-1000-8000-00805F9B34FB") -> "ALERT CATEGORY ID BIT MASK"
                UUID.fromString("00002A43-0000-1000-8000-00805F9B34FB") -> "ALERT CATEGORY ID"
                UUID.fromString("00002A44-0000-1000-8000-00805F9B34FB") -> "ALERT NOTIFICATION CONTROL POINT"
                UUID.fromString("00002A45-0000-1000-8000-00805F9B34FB") -> "UNREAD ALERT STATUS"
                UUID.fromString("00002A46-0000-1000-8000-00805F9B34FB") -> "NEW ALERT"
                UUID.fromString("00002A47-0000-1000-8000-00805F9B34FB") -> "SUPPORTED NEW ALERT CATEGORY"
                UUID.fromString("00002A48-0000-1000-8000-00805F9B34FB") -> "SUPPORTED UNREAD ALERT CATEGORY"
                UUID.fromString("00002A49-0000-1000-8000-00805F9B34FB") -> "BLOOD PRESSURE FEATURE"
                UUID.fromString("00002A4A-0000-1000-8000-00805F9B34FB") -> "HID INFORMATION"
                UUID.fromString("00002A4B-0000-1000-8000-00805F9B34FB") -> "REPORT MAP"

                else -> characteristicUuid.toString()
            }
        }

    }
}