package com.android.wy.news.dialog

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.OperationAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutCommonOperationDialogBinding
import com.android.wy.news.entity.OperationItemEntity
import com.android.wy.news.util.JsonUtil


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/15 14:37
  * @Version:        1.0
  * @Description:    
 */
class CommonOperationDialogFragment(var itemAdapterListener: BaseNewsAdapter.OnItemAdapterListener<OperationItemEntity>) :
    BaseDialogFragment<LayoutCommonOperationDialogBinding>(),
    BaseNewsAdapter.OnItemAdapterListener<OperationItemEntity> {
    private var tvTitle: TextView? = null
    private var rvContent: RecyclerView? = null
    private var tvCancel: TextView? = null
    private var operationAdapter: OperationAdapter? = null
    private var dataList = ArrayList<OperationItemEntity>()

    companion object {
        const val OPERATION_LIST_KEY = "operation_list_key"
        const val OPERATION_TITLE = "operation_title"
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        tvCancel = mBinding.tvCancel
        rvContent = mBinding.rvContent
    }

    override fun initData() {
        operationAdapter = OperationAdapter(this)
        val arguments = arguments
        if (arguments != null) {
            val s = arguments.getString(OPERATION_LIST_KEY)
            dataList = JsonUtil.parseJsonToList(s)
            val s1 = arguments.getString(OPERATION_TITLE)
            tvTitle?.text = s1
        }
        rvContent?.layoutManager = GridLayoutManager(context, 3)
        rvContent?.adapter = operationAdapter
        operationAdapter?.refreshData(dataList)
    }

    override fun initEvent() {
        tvCancel?.setOnClickListener {
            dismiss()
        }
    }

    override fun getViewBinding(): LayoutCommonOperationDialogBinding {
        return LayoutCommonOperationDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {

    }

    override fun getLayoutHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun getLayoutWidth(): Int {
        return (CommonTools.getScreenWidth() * 0.9).toInt()
    }

    override fun setFragmentStyle() {

    }

    override fun getGravityLocation(): Int {
        return Gravity.BOTTOM
    }

    override fun isTouchDismiss(): Boolean {
        return false
    }

    override fun onItemClickListener(view: View, data: OperationItemEntity) {
        dismiss()
        itemAdapterListener.onItemClickListener(view, data)
    }

    override fun onItemLongClickListener(view: View, data: OperationItemEntity) {
        itemAdapterListener.onItemLongClickListener(view, data)
    }
}