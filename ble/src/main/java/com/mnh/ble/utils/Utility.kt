package com.mnh.ble.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Formatter
import java.util.Locale


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

        fun getCurrentTime(): String {
            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
            return currentTime.format(formatter)
        }

        fun generateNoteOnSD(context: Context?, sFileName: String?, sBody: String?) {
            try {
                val root = File(Environment.getExternalStorageDirectory(), "log")
                if (!root.exists()) {
                    root.mkdirs()
                }
                val gpxfile = File(root, sFileName)
                val writer = FileWriter(gpxfile)
                writer.append(sBody)
                writer.flush()
                writer.close()
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

    }
}