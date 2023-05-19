package com.android.lyric;

import android.content.Context;
import android.util.Log;

import com.android.lyric.impl.LrcRow;

import java.util.ArrayList;
import java.util.List;

public class LrcRowUtil {
    public final static String TAG = "LrcRow";

    /**
     * 读取歌词的每一行内容，转换为LrcRow，加入到集合中
     */
    public static List<LrcRow> createRows(String standardLrcLine) {
        /*
         一行歌词只有一个时间的  例如：徐佳莹   《我好想你》
         [01:15.33]我好想你 好想你

         一行歌词有多个时间的  例如：草蜢 《失恋战线联盟》
         [02:34.14][01:07.00]当你我不小心又想起她
         [02:45.69][02:42.20][02:37.69][01:10.60]就在记忆里画一个叉
         **/
        try {
            if (standardLrcLine.indexOf("[") != 0 || standardLrcLine.indexOf("]") != 9) {
                return null;
            }
            //[02:34.14][01:07.00]当你我不小心又想起她
            //找到最后一个 ‘]’ 的位置
            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            //歌词内容就是 ‘]’ 的位置之后的文本   eg:   当你我不小心又想起她
            String content = standardLrcLine.substring(lastIndexOfRightBracket + 1);
            //歌词时间就是 ‘]’ 的位置之前的文本   eg:   [02:34.14][01:07.00]

            /*
             将时间格式转换一下  [mm:ss.SS][mm:ss.SS] 转换为  -mm:ss.SS--mm:ss.SS-
             即：[02:34.14][01:07.00]  转换为      -02:34.14--01:07.00-
             */
            String times = standardLrcLine.substring(0, lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-");
            //通过 ‘-’ 来拆分字符串
            String[] arrTimes = times.split("-");
            List<LrcRow> listTimes = new ArrayList<>();
            /* [02:34.14][01:07.00]当你我不小心又想起她
             *
             上面的歌词的就可以拆分为下面两句歌词了
             [02:34.14]当你我不小心又想起她
             [01:07.00]当你我不小心又想起她
             */
            for (String temp : arrTimes) {
                if (temp.trim().length() == 0) {
                    continue;
                }

                /* [02:34.14][01:07.00]当你我不小心又想起她
                 *
                 上面的歌词的就可以拆分为下面两句歌词了
                 [02:34.14]当你我不小心又想起她
                 [01:07.00]当你我不小心又想起她
                 */
                LrcRow lrcRow = new LrcRow();
                lrcRow.setContent(content);
                lrcRow.setStartTimeString(temp);
                long startTime = timeConvert(temp);
                lrcRow.setStartTime(startTime);
                listTimes.add(lrcRow);
            }
            return listTimes;
        } catch (Exception e) {
            Log.e(TAG, "createRows exception:" + Log.getStackTraceString(e));
            return null;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    public static int dp2px(Context context, float dpValue) {
        //获取当前手机的像素密度（1个dp对应几个px）
        float density = context.getResources().getDisplayMetrics().density;
        //四舍五入取整
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 将解析得到的表示时间的字符转化为Long型
     */
    private static long timeConvert(String timeString) {
        //因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        //将字符串 XX:XX.XX 转换为 XX:XX:XX
        timeString = timeString.replace('.', ':');
        //将字符串 XX:XX:XX 拆分
        String[] times = timeString.split(":");
        // mm:ss:SS
        return (long) Integer.parseInt(times[0]) * 60 * 1000L +//分
                Integer.parseInt(times[1]) * 1000L +//秒
                Integer.parseInt(times[2]);//毫秒
    }
}