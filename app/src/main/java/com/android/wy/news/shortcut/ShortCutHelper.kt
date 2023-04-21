package com.android.wy.news.shortcut

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.android.wy.news.R
import com.android.wy.news.activity.HomeActivity
import com.android.wy.news.activity.SearchActivity
import com.android.wy.news.activity.SettingActivity
import com.android.wy.news.activity.SkinActivity
import java.util.Collections

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/21 13:14
  * @Version:        1.0
  * @Description:    
 */
class ShortCutHelper {
    companion object {
        private const val SHORT_CUT_ID = "wy_news_short_cut"
        private var count = 0
        private val shortCutList = ArrayList<ShortcutInfoCompat>()

        private fun getShortCutId(): String {
            count++
            return SHORT_CUT_ID + "_$count"
        }

        fun initShortCut(context: Context) {
            shortCutList.clear()
            //动态方式添加一
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortCutSearch = ShortcutInfoCompat.Builder(context, getShortCutId())//唯一标识id
                    .setShortLabel("搜索")//短标签
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.cut_search))//图标
                    //跳转的目标，定义Activity
                    .setIntent(
                        Intent(
                            Intent.ACTION_MAIN,
                            null,
                            context,
                            SearchActivity::class.java
                        )
                    )
                    .build()
                val shortCutSkin = ShortcutInfoCompat.Builder(context, getShortCutId())//唯一标识id
                    .setShortLabel("主题")//短标签
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.cut_skin))//图标
                    //跳转的目标，定义Activity
                    .setIntent(Intent(Intent.ACTION_MAIN, null, context, SkinActivity::class.java))
                    .build()
                val shortCutSetting = ShortcutInfoCompat.Builder(context, getShortCutId())//唯一标识id
                    .setShortLabel("设置")//短标签
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.cut_setting))//图标
                    //跳转的目标，定义Activity
                    .setIntent(
                        Intent(
                            Intent.ACTION_MAIN,
                            null,
                            context,
                            SettingActivity::class.java
                        )
                    )
                    .build()
                shortCutList.add(shortCutSearch)
                shortCutList.add(shortCutSkin)
                shortCutList.add(shortCutSetting)
                //执行添加操作
                ShortcutManagerCompat.addDynamicShortcuts(context, shortCutList)
            }
        }

        fun addShortCut(context: Context, label: String) {
            //动态方式添加一
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortScan = ShortcutInfoCompat.Builder(context, getShortCutId())//唯一标识id
                    .setShortLabel(label)//短标签
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))//图标
                    //跳转的目标，定义Activity
                    .setIntent(Intent(Intent.ACTION_MAIN, null, context, HomeActivity::class.java))
                    .build()
                shortCutList.add(shortScan)
                //执行添加操作
                ShortcutManagerCompat.addDynamicShortcuts(context, shortCutList)
            }
        }

        fun updateShortCut(context: Context, list: ArrayList<ShortcutInfoCompat>) {
            ShortcutManagerCompat.updateShortcuts(context, list)
        }

        fun deleteShortCut(context: Context, shortCutId: String) {
            //动态移除方式一
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutManagerCompat.removeDynamicShortcuts(
                    context, Collections.singletonList(shortCutId)//唯一标识id
                )
            }
        }
    }
}