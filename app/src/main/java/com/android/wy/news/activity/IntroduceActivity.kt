package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityIntroduceBinding
import com.android.wy.news.databinding.LayoutPermissionItemBinding
import com.android.wy.news.entity.UpdateEntity
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.IntroduceViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Route(path = RouteManager.PATH_ACTIVITY_INTRODUCE)
class IntroduceActivity : BaseActivity<ActivityIntroduceBinding, IntroduceViewModel>() {
    private lateinit var llContent: LinearLayout

    companion object {
        fun startIntroduceActivity(context: Context) {
            val intent = Intent(context, IntroduceActivity::class.java)
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
        return false
    }

    override fun initView() {
        llContent = mBinding.llContent
    }

    override fun initData() {
        val updateList = ArrayList<UpdateEntity>()
        val content = CommonTools.getAssertContent(this, "updateInfo.json")
        val gson = Gson()
        val dataList = gson.fromJson<ArrayList<UpdateEntity>>(
            content, object : TypeToken<ArrayList<UpdateEntity>>() {}.type
        )
        updateList.addAll(dataList)
        addUpdateContent(updateList)
    }

    private fun addUpdateContent(updateList: ArrayList<UpdateEntity>) {
        if (updateList.size > 0) {
            llContent.removeAllViews()
            for (i in 0 until updateList.size) {
                val permissionItemBinding = LayoutPermissionItemBinding.inflate(layoutInflater)
                val tvName = permissionItemBinding.tvName
                val tvPermissionName = permissionItemBinding.tvPermissionName
                val tvDesc = permissionItemBinding.tvDesc

                val updateEntity = updateList[i]
                tvName.text = updateEntity.title
                tvPermissionName.text = updateEntity.content
                tvDesc.text = updateEntity.time

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

    override fun getViewBinding(): ActivityIntroduceBinding {
        return ActivityIntroduceBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): IntroduceViewModel {
        return CommonTools.getViewModel(this, IntroduceViewModel::class.java)
    }

    override fun onClear() {
    }

    override fun onNotifyDataChanged() {
    }

}