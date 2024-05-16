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

        fun getServiceName(characteristicUuid: UUID): String {
            return when (characteristicUuid) {
                UUID.fromString("00001800-0000-1000-8000-00805F9B34FB") -> "GENERIC ACCESS"
                UUID.fromString("00001801-0000-1000-8000-00805F9B34FB") -> "GENERIC ATTRIBUTE"
                UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB") -> "DEVICE INFORMATION"
                else -> characteristicUuid.toString()
            }
        }

        fun getCharacteristicPurpose(characteristicUuid: UUID): String {
            return when (characteristicUuid) {
                // Device Information Service
                UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB") -> "Model Number String"
                UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB") -> "Serial Number String"
                UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB") -> "Firmware Revision String"
                UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB") -> "Hardware Revision String"
                UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB") -> "Software Revision String"
                UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB") -> "Manufacturer Name String"

                //Generic Access
                UUID.fromString("00002A00-0000-1000-8000-00805F9B34FB") -> "Device Name"
                UUID.fromString("00002A01-0000-1000-8000-00805F9B34FB") -> "Appearance"
                UUID.fromString("00002A04-0000-1000-8000-00805F9B34FB") -> "Peripheral Preferred Connection Parameters"
                else -> characteristicUuid.toString()
            }
        }

    }
}