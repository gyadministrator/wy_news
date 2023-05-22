package com.android.wy.news.lrc

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.wy.news.lrc.impl.DefaultLrcBuilder
import com.android.wy.news.lrc.impl.LrcRow
import com.android.wy.news.lrc.listener.ILrcView
import com.android.wy.news.lrc.listener.ILrcViewListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Timer
import java.util.TimerTask

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/22 13:15
  * @Version:        1.0
  * @Description:    
 */
class TestLrcActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "gy"
        private const val fileName = "test.lrc"
    }

    /**
     * 自定义LrcView，用来展示歌词
     */
    var mLrcView: ILrcView? = null

    /**
     * 更新歌词的频率，每100ms更新一次
     */
    private val mPlayerTimerDuration = 100

    /**
     * 更新歌词的定时器
     */
    private var mTimer: Timer? = null

    /**
     * 更新歌词的定时任务
     */
    private var mTask: TimerTask? = null

    /**
     * 播放器
     */
    private var mPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取自定义的LrcView
        setContentView(R.layout.activity_test_lrc)
        mLrcView = findViewById(R.id.lrc_view)

        //从assets目录下读取歌词文件内容
        val lrc = getFromAssets()
        Log.e(TAG, "onCreate: lrc=$lrc")
        //解析歌词构造器
        val builder = DefaultLrcBuilder()
        //解析歌词返回LrcRow集合
        val rows = builder.getLrcRows(lrc)
        Log.e(TAG, "onCreate: rows=$rows")
        //将得到的歌词集合传给mLrcView用来展示
        mLrcView?.setLrc(rows)

        //开始播放歌曲并同步展示歌词
        beginLrcPlay()

        //设置自定义的LrcView上下拖动歌词时监听
        //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
        mLrcView?.setLrcViewListener(object : ILrcViewListener {
            override fun onLrcDrag(newPosition: Int, row: LrcRow) {
                Log.e(TAG, "onLrcDrag: newPosition=$newPosition row=$row")
                if (mPlayer != null) {
                    Log.d(TAG, "onLrcDrag:" + row.startTime)
                    mPlayer?.seekTo(row.startTime.toInt())
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.stop()
    }

    /**
     * 从assets目录下读取歌词文件内容
     * @return 文件内容
     */
    private fun getFromAssets(): String {
        try {
            val inputReader = InputStreamReader(
                resources.assets.open(fileName)
            )
            val bufReader = BufferedReader(inputReader)
            var line: String
            val result = StringBuilder()
            while (bufReader.readLine().also { line = it } != null) {
                if (line.trim { it <= ' ' } == "") continue
                result.append(line).append("\r\n")
            }
            return result.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 开始播放歌曲并同步展示歌词
     */
    private fun beginLrcPlay() {
        mPlayer = MediaPlayer()
        try {
            val fd = assets.openFd("test.mp3")
            mPlayer?.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            //准备播放歌曲监听
            mPlayer?.setOnPreparedListener { mp ->

                //准备完毕
                Log.e(TAG, "onPrepared: ")
                mp.start()
                if (mTimer == null) {
                    mTimer = Timer()
                    mTask = LrcTask(this)
                    mTimer?.scheduleAtFixedRate(mTask, 0, mPlayerTimerDuration.toLong())
                }
            }
            //歌曲播放完毕监听
            mPlayer?.setOnCompletionListener {
                Log.e(TAG, "onCompletion: ")
                stopLrcPlay()
            }
            mPlayer?.setOnErrorListener { _, _, _ ->
                Log.e(TAG, "onError: ")
                false
            }
            //准备播放歌曲
            mPlayer?.prepare()
            //开始播放歌曲
            mPlayer?.start()
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "beginLrcPlay: " + e.message)
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "beginLrcPlay: " + e.message)
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e(TAG, "beginLrcPlay: " + e.message)
            e.printStackTrace()
        }
    }

    /**
     * 停止展示歌曲
     */
    private fun stopLrcPlay() {
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer = null
        }
    }

    /**
     * 展示歌曲的定时任务
     */
    internal class LrcTask(testLrcActivity: TestLrcActivity) : TimerTask() {
        private var testLrcActivity: TestLrcActivity? = null

        init {
            this.testLrcActivity = testLrcActivity
        }

        override fun run() {
            //获取歌曲播放的位置
            val timePassed: Long? = this.testLrcActivity?.mPlayer?.currentPosition?.toLong()
            this.testLrcActivity?.runOnUiThread {
                Log.e(TAG, "run: $timePassed")
                //滚动歌词
                timePassed?.let { this.testLrcActivity?.mLrcView?.seekLrcToTime(it) }
            }
        }
    }
}