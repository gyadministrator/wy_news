package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityPermissionBinding
import com.android.wy.news.databinding.LayoutPermissionItemBinding
import com.android.wy.news.entity.PermissionEntity
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.viewmodel.PermissionViewModel

@Route(path = RouteManager.PATH_ACTIVITY_PERMISSION)
class PermissionActivity : BaseActivity<ActivityPermissionBinding, PermissionViewModel>() {
    private lateinit var llContent: LinearLayout

    companion object {
        fun startPermissionActivity(context: Context) {
            val intent = Intent(context, PermissionActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun hideStatusBar(): Boolean {
        return false
    }

    override fun hideNavigationBar(): Boolean {
        return false
    }

    override fun isFollowNightMode(): Boolean {
        return true
    }

    override fun initView() {
        llContent = mBinding.llContent
    }

    override fun initData() {
        val permissionList = ArrayList<PermissionEntity>()
        val content = CommonTools.getAssertContent(this, "permission.json")
        val dataList = JsonUtil.parseJsonToList<PermissionEntity>(content)
        permissionList.addAll(dataList)
        addPermissionContent(permissionList)
    }

    private fun addPermissionContent(it: ArrayList<PermissionEntity>) {
        if (it.size > 0) {
            llContent.removeAllViews()
            for (i in 0 until it.size) {
                val permissionItemBinding = LayoutPermissionItemBinding.inflate(layoutInflater)
                val tvName = permissionItemBinding.tvName
                val tvPermissionName = permissionItemBinding.tvPermissionName
                val tvDesc = permissionItemBinding.tvDesc

                val permissionEntity = it[i]
                tvName.text = permissionEntity.title
                tvPermissionName.text = permissionEntity.name
                tvDesc.text = permissionEntity.desc

                val root = permissionItemBinding.root
                val parent = root.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(root)
                }
                llContent.addView(root)
            }
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): PermissionViewModel {
        return CommonTools.getViewModel(this, PermissionViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}