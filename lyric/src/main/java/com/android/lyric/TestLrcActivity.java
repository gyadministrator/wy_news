package com.android.lyric;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;

import com.android.lyric.impl.DefaultLrcBuilder;
import com.android.lyric.impl.LrcRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestLrcActivity extends Activity {

    public final static String TAG = "gy";

    /**
     * 自定义LrcView，用来展示歌词
     */
    ILrcView mLrcView;
    /**
     * 更新歌词的频率，每100ms更新一次
     */
    private final int mPlayerTimerDuration = 100;
    /**
     * 更新歌词的定时器
     */
    private Timer mTimer;
    /**
     * 更新歌词的定时任务
     */
    private TimerTask mTask;
    /**
     * 播放器
     */
    private MediaPlayer mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取自定义的LrcView
        setContentView(R.layout.activity_test_lrc);
        mLrcView = findViewById(R.id.lrc_view);

        //从assets目录下读取歌词文件内容
        String lrc = getFromAssets("test.lrc");
        Log.e(TAG, "onCreate: lrc=" + lrc);
        //解析歌词构造器
        ILrcBuilder builder = new DefaultLrcBuilder();
        //解析歌词返回LrcRow集合
        List<LrcRow> rows = builder.getLrcRows(lrc);
        Log.e(TAG, "onCreate: rows=" + rows);
        //将得到的歌词集合传给mLrcView用来展示
        mLrcView.setLrc(rows);

        //开始播放歌曲并同步展示歌词
        beginLrcPlay();

        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setLrcViewListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSought(int newPosition, LrcRow row) {
                Log.e(TAG, "onLrcSought: newPosition=" + newPosition + " row=" + row);
                if (mPlayer != null) {
                    Log.d(TAG, "onLrcSought:" + row.startTime);
                    mPlayer.seekTo((int) row.startTime);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    /**
     * 从assets目录下读取歌词文件内容
     *
     * @param fileName fileName
     * @return String
     */
    public String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result.append(line).append("\r\n");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 开始播放歌曲并同步展示歌词
     */
    public void beginLrcPlay() {
        mPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor fd = getAssets().openFd("test.mp3");
            mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            //准备播放歌曲监听
            mPlayer.setOnPreparedListener(new OnPreparedListener() {
                //准备完毕
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG, "onPrepared: ");
                    mp.start();
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new LrcTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPlayerTimerDuration);
                    }
                }
            });
            //歌曲播放完毕监听
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG, "onCompletion: ");
                    stopLrcPlay();
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.e(TAG, "onError: ");
                    return false;
                }
            });
            //准备播放歌曲
            mPlayer.prepare();
            //开始播放歌曲
            mPlayer.start();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Log.e(TAG, "beginLrcPlay: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 停止展示歌曲
     */
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = mPlayer.getCurrentPosition();
            TestLrcActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.e(TAG, "run: " + timePassed);
                    //滚动歌词
                    mLrcView.seekLrcToTime(timePassed);
                }
            });
        }
    }
}