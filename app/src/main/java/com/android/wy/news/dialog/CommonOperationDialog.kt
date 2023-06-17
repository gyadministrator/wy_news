package com.android.wy.news.dialog

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.entity.OperationItemEntity
import com.android.wy.news.util.JsonUtil
import java.lang.ref.WeakReference


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/15 14:37
  * @Version:        1.0
  * @Description:    
 */
object CommonOperationDialog {
    fun show(
        activity: FragmentActivity,
        title: String,
        dataList: ArrayList<OperationItemEntity>,
        itemAdapterListener: BaseNewsAdapter.OnItemAdapterListener<OperationItemEntity>
    ) {
        val commonOperationDialogFragment = CommonOperationDialogFragment(itemAdapterListener)
        val bundle = Bundle()
        bundle.putString(CommonOperationDialogFragment.OPERATION_TITLE, title)
        bundle.putString(
            CommonOperationDialogFragment.OPERATION_LIST_KEY,
            JsonUtil.parseObjectToJson(dataList)
        )
        commonOperationDialogFragment.arguments = bundle
        val supportFragmentManager = activity.supportFragmentManager
        if (!supportFragmentManager.isDestroyed) {
            commonOperationDialogFragment.show(
                supportFragmentManager,
                "common_operation_dialog"
            )
        }
    }
}