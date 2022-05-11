package com.ssk.ncmusic.utils

import android.text.TextUtils
import android.text.format.DateUtils
import com.ssk.ncmusic.model.LyricResult
import com.ssk.ncmusic.viewmodel.playmusic.LyricModel
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by ssk on 2022/5/11.
 */
object LyricUtil {
    private val PATTERN_LINE = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d{2,3}\\])+)(.+)")
    private val PATTERN_TIME = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d{2,3})\\]")

    fun parse(lyricResult: LyricResult): List<LyricModel> {
        val originLrcTexts = lyricResult.lrc?.lyric ?: ""
        val originTLyricTexts = lyricResult.tlyric?.lyric ?: ""
        val lyricModelList = parseLyrics(originLrcTexts)
        val tLyricModelList = parseTlyrics(originTLyricTexts)
        lyricModelList.forEach outer@{ lyricModel ->
            tLyricModelList.forEach { tLyricModel ->
                if (lyricModel.time == tLyricModel.time) {
                    lyricModel.tLyric = tLyricModel.tLyric
                }
            }
        }

        return lyricModelList
    }

    /**
     * 从文本解析歌词
     */
    private fun parseLyrics(lyric: String): List<LyricModel> {
        var lrcText = lyric
        val entryList = ArrayList<LyricModel>()

        if (!TextUtils.isEmpty(lrcText)) {
            if (lrcText.startsWith("\uFEFF")) {
                lrcText = lrcText.replace("\uFEFF", "")
            }
            val array = lrcText.split("\\n".toRegex()).toTypedArray()
            for (line in array) {
                val list = parseLyricsLine(line)
                if (list != null && list.isNotEmpty()) {
                    entryList.addAll(list)
                }
            }
        }
        return entryList
    }

    /**
     * 从文本解析歌词
     */
    private fun parseTlyrics(tLyric: String): List<LyricModel> {
        var tlyric = tLyric
        val entryList = ArrayList<LyricModel>()

        if (!TextUtils.isEmpty(tlyric)) {
            if (tlyric.startsWith("\uFEFF")) {
                tlyric = tlyric.replace("\uFEFF", "")
            }
            val array = tlyric.split("\\n".toRegex()).toTypedArray()
            for (line in array) {
                val list = parseTLyricsLine(line)
                if (list != null && list.isNotEmpty()) {
                    entryList.addAll(list)
                }
            }
        }
        return entryList
    }

    /**
     * 解析一行歌词
     */
    private fun parseLyricsLine(line: String): List<LyricModel>? {
        var line = line
        if (TextUtils.isEmpty(line)) {
            return null
        }
        line = line.trim { it <= ' ' }
        // [00:17.65]让我掉下眼泪的
        val lineMatcher: Matcher = PATTERN_LINE.matcher(line)
        if (!lineMatcher.matches()) {
            return null
        }
        val times = lineMatcher.group(1)
        val text = lineMatcher.group(3)
        val entryList: MutableList<LyricModel> = ArrayList<LyricModel>()

        // [00:17.65]
        val timeMatcher: Matcher = PATTERN_TIME.matcher(times)
        while (timeMatcher.find()) {
            val min = timeMatcher.group(1)!!.toLong()
            val sec = timeMatcher.group(2)!!.toLong()
            val milString = timeMatcher.group(3)
            var mil = milString.toLong()
            // 如果毫秒是两位数，需要乘以10
            if (milString.length == 2) {
                mil *= 10
            }
            val time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil
            entryList.add(LyricModel(time, text))
        }
        return entryList
    }

    /**
     * 解析一行歌词
     */
    private fun parseTLyricsLine(line: String): List<LyricModel>? {
        var line = line
        if (TextUtils.isEmpty(line)) {
            return null
        }
        line = line.trim { it <= ' ' }
        // [00:17.65]让我掉下眼泪的
        val lineMatcher: Matcher = PATTERN_LINE.matcher(line)
        if (!lineMatcher.matches()) {
            return null
        }
        val times = lineMatcher.group(1)
        val text = lineMatcher.group(3)
        val entryList: MutableList<LyricModel> = ArrayList<LyricModel>()

        // [00:17.65]
        val timeMatcher: Matcher = PATTERN_TIME.matcher(times)
        while (timeMatcher.find()) {
            val min = timeMatcher.group(1)!!.toLong()
            val sec = timeMatcher.group(2)!!.toLong()
            val milString = timeMatcher.group(3)
            var mil = milString.toLong()
            // 如果毫秒是两位数，需要乘以10
            if (milString.length == 2) {
                mil *= 10
            }
            val time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil
            entryList.add(LyricModel(time, tLyric = text))
        }
        return entryList
    }

    /**
     * 转为[分:秒]
     */
    fun formatTime(milli: Long): String? {
        val m = (milli / DateUtils.MINUTE_IN_MILLIS).toInt()
        val s = (milli / DateUtils.SECOND_IN_MILLIS % 60).toInt()
        val mm = String.format(Locale.getDefault(), "%02d", m)
        val ss = String.format(Locale.getDefault(), "%02d", s)
        return "$mm:$ss"
    }
}