package com.android.wy.news.locationselect.model

import androidx.annotation.IntDef
import java.lang.annotation.RetentionPolicy


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:18
  * @Version:        1.0
  * @Description:    
 */
class LocateState {
    companion object {
        const val LOCATING = 123
        const val SUCCESS = 132
        const val FAILURE = 321
    }

    @IntDef(SUCCESS, FAILURE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State
}