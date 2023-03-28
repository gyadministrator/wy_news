package com.android.wy.news.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.view.View
import java.io.File
import java.io.FileOutputStream


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/27 18:21
  * @Version:        1.0
  * @Description:    
 */
class ViewTools {
    companion object {
        /**
         * 将View保存到指定的文件中
         *
         * @param view 选定的view
         * @return 保存到文件地址
         */
        fun view2File(view: View?): File {
            val dir =
                Environment.getExternalStorageDirectory().absolutePath + File.separator + "wy_news"
            if (!File(dir).exists()) {
                File(dir).mkdirs()
            }
            val filePath = File(dir, "view_" + System.currentTimeMillis() + ".png").absolutePath
            return view2File(view, filePath)
        }

        /**
         * 将view生成图片保存在默认的路径中（磁盘的根目录下）
         *
         * @param view 选择的view
         * @return 最后保存的文件路径
         */
        private fun view2File(view: View?, dstPath: String): File {
            // 把一个View转换成图片
            val cacheBitmap: Bitmap? = view?.let { view2Bitmap(it) }
            var fos: FileOutputStream?=null
            try {
                // 判断手机设备是否有SD卡
                val isHasSDCard = Environment.getExternalStorageState() ==
                        Environment.MEDIA_MOUNTED
                if (isHasSDCard) {
                    try {
                        fos = FileOutputStream(dstPath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    throw Exception("创建文件失败!")
                }
                cacheBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos?.flush()
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return File(dstPath)
        }

        /**
         * 将view生成一个bitmap
         *
         * @param v 选定的view
         * @return 生成的bitmap
         */
        private fun view2Bitmap(v: View): Bitmap? {
            val w = v.width
            val h = v.height
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp)

            //不设置canvas颜色，则生成透明背景
            //c.drawColor(Color.WHITE);
            v.draw(c)
            return bmp
        }
    }
}