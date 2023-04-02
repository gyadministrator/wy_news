package com.android.wy.news.common

import android.text.TextUtils
import com.xuexiang.xupdate.proxy.IFileEncryptor
import com.xuexiang.xupdate.utils.Md5Utils
import java.io.File

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/1 16:07
  * @Version:        1.0
  * @Description:    
 */
class DefaultFileEncryptor : IFileEncryptor {
    override fun encryptFile(file: File?): String {
        return Md5Utils.getFileMD5(file)
    }

    override fun isFileValid(encrypt: String?, file: File?): Boolean {
        return TextUtils.isEmpty(encrypt) || encrypt.equals(encryptFile(file), true)
    }
}