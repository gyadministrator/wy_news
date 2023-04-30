package com.android.wy.news.music.lrc

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.util.Collections
import java.util.regex.Matcher
import java.util.regex.Pattern


class LrcHelper {
    companion object {
        private const val CHARSET = "utf-8"

        //[03:56.00][03:18.00][02:06.00][01:07.00]原谅我这一生不羁放纵爱自由
        private const val LINE_REGEX = "((\\[\\d{2}:\\d{2}\\.\\d{2}])+)(.*)"
        private const val TIME_REGEX = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})]"

        fun parseLrcFromAssets(context: Context, fileName: String?): List<Lrc?>? {
            try {
                return parseInputStream(context.resources.assets.open(fileName!!))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        fun parseLrcFromFile(file: File?): List<Lrc?>? {
            try {
                return parseInputStream(FileInputStream(file))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        private fun parseInputStream(inputStream: InputStream): List<Lrc?> {
            val lrcList= ArrayList<Lrc>()
            var isr: InputStreamReader? = null
            var br: BufferedReader? = null
            try {
                isr = InputStreamReader(inputStream, CHARSET)
                br = BufferedReader(isr)
                var line: String
                while (br.readLine().also { line = it } != null) {
                    val lrcList = parseLrc(line)
                    if (!lrcList.isNullOrEmpty()) {
                        lrcList.addAll(lrcList)
                    }
                }
                sortLrcList(lrcList)
                return lrcList
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    isr?.close()
                    br?.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
            return lrcList
        }

        private fun sortLrcList(lrcList: List<Lrc?>) {
            Collections.sort(
                lrcList
            ) { p0, p1 -> (p0?.time?.minus(p1?.time!!))?.toInt()!! }
        }

         fun parseLrc(lrcLine: String): ArrayList<Lrc>? {
            if (lrcLine.trim { it <= ' ' }.isEmpty()) {
                return null
            }
            val lrcList= ArrayList<Lrc>()
            val matcher: Matcher = Pattern.compile(LINE_REGEX).matcher(lrcLine)
            if (!matcher.matches()) {
                return null
            }
            val time = matcher.group(1)
            val content = matcher.group(3)
            val timeMatcher: Matcher? = time?.let { Pattern.compile(TIME_REGEX).matcher(it) }
            while (timeMatcher!!.find()) {
                val min = timeMatcher.group(1)
                val sec = timeMatcher.group(2)
                val mil = timeMatcher.group(3)
                if (content != null && content.isNotEmpty()) {
                    if (min != null) {
                        if (sec != null) {
                            val lrcTime =
                                min.toLong() * 60 * 1000 + sec.toLong() * 1000 + mil!!.toLong() * 10
                            val lrc = Lrc(lrcTime.toFloat(), content)
                            lrcList.add(lrc)
                        }
                    }
                }
            }
            return lrcList
        }

        fun formatTime(time: Float): String {
            val min = (time / 60000).toInt()
            val sec = (time / 1000 % 60).toInt()
            return adjustFormat(min) + ":" + adjustFormat(sec)
        }

        private fun adjustFormat(time: Int): String {
            return if (time < 10) {
                "0$time"
            } else time.toString() + ""
        }
    }
}