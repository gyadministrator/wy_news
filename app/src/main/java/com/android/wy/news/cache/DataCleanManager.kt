package com.android.wy.news.cache

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.math.BigDecimal


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/13 16:51
  * @Version:        1.0
  * @Description:    
 */
object DataCleanManager {
    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    private fun cleanInternalCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir)
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    @SuppressLint("SdCardPath")
    private fun cleanDatabases(context: Context) {
        deleteFilesByDirectory(
            File(
                "/data/data/"
                        + context.packageName + "/databases"
            )
        )
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    @SuppressLint("SdCardPath")
    fun cleanSharedPreference(context: Context) {
        deleteFilesByDirectory(
            File(
                ("/data/data/"
                        + context.packageName + "/shared_prefs")
            )
        )
    }

    /**
     * * 按名字清除本应用数据库 * *
     *
     * @param context
     * @param dbName
     */
    fun cleanDatabaseByName(context: Context, dbName: String?) {
        context.deleteDatabase(dbName)
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context
     */
    private fun cleanFiles(context: Context) {
        deleteFilesByDirectory(context.filesDir)
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    private fun cleanExternalCache(context: Context) {
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    /**
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
     *
     * @param filePath
     */
    private fun cleanCustomCache(filePath: String?) {
        deleteFilesByDirectory(filePath?.let { File(it) })
    }

    /**
     * * 清除本应用所有的数据 * *
     *
     * @param context
     * @param filepath
     */
    fun cleanApplicationData(context: Context, vararg filepath: String?) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        for (filePath: String? in filepath) {
            cleanCustomCache(filePath)
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     */
    private fun deleteFilesByDirectory(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (i in children.indices) {
                    val success = deleteFilesByDirectory(
                        File(
                            dir,
                            children[i]
                        )
                    )
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return dir!!.delete()
    }

    // 获取文件
    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
    // 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    private fun getFolderSize(file: File?): Long {
        var size: Long = 0
        try {
            val fileList = file?.listFiles()
            if (fileList != null) {
                for (i in fileList.indices) {

                    // 如果下面还有文件
                    size += if (fileList[i].isDirectory) {
                        getFolderSize(fileList[i])
                    } else {
                        fileList[i].length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size.toInt().toLong()
    }

    /**
     * 删除指定目录下文件及目录
     */
    private fun deleteFolderFile(filePath: String?, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = filePath?.let { File(it) }
                if (file != null) {
                    if (file.isDirectory) { // 如果下面还有文件
                        val files = file.listFiles()
                        if (files != null && files.isNotEmpty()) {
                            for (i in files.indices) {
                                deleteFolderFile(files[i].absolutePath, true)
                            }
                        }
                    }
                }
                if (deleteThisPath) {
                    if (file != null) {
                        if (!file.isDirectory) { // 如果是文件，删除
                            file.delete()
                        } else { // 目录
                            if (file.listFiles()!!.isEmpty()) { // 目录下没有文件或者目录，删除
                                file.delete()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取缓存大小,返回 String 型，在我们显示的地方调用就好
     *
     * @param context
     * @return
     */
    fun getCacheSize(context: Context): String {
        var tCacheSize = getFolderSize(context.cacheDir) // 内部缓存大小

        // 判断SD卡是否存在，并且是否具有读写权限
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            tCacheSize += getFolderSize(context.externalCacheDir) // 外部缓存大小
        }
        return getFormatSize(tCacheSize.toDouble())
    }

    /**
     * 获取缓存大小,返回 int 值，一般用于判断是否进行缓存清除，即为 0 时，不进行缓存清除
     *
     * @param context
     * @return
     */
    fun getCacheSizeInt(context: Context): Long {
        var tCacheSize = getFolderSize(context.cacheDir) // 内部缓存大小

        // 判断SD卡是否存在，并且是否具有读写权限
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            tCacheSize += getFolderSize(context.externalCacheDir) // 外部缓存大小
        }
        return tCacheSize
    }

    /**
     * 清除内外缓存
     */
    fun clearIntExtCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir) // 清除内部缓存
        if ((Environment.getExternalStorageState() ==
                    Environment.MEDIA_MOUNTED)
        ) {
            deleteFilesByDirectory(context.externalCacheDir) // 清除外部缓存
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return String
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "B"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB")
    }
}