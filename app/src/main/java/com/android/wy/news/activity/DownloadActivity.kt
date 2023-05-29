package com.android.wy.news.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.DownloadAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityDownloadBinding
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.DownloadViewModel
import java.io.File

@Route(path = RouteManager.PATH_ACTIVITY_DOWNLOAD)
class DownloadActivity : BaseActivity<ActivityDownloadBinding, DownloadViewModel>(),
    BaseNewsAdapter.OnItemAdapterListener<File> {
    private var rvContent: RecyclerView? = null
    private var downloadAdapter: DownloadAdapter? = null

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
        rvContent = mBinding.rvContent
    }

    override fun initData() {
        downloadAdapter = DownloadAdapter(this)
        rvContent?.layoutManager = LinearLayoutManager(this)
        rvContent?.adapter = downloadAdapter
        getDownloadFile()
    }

    private fun getDownloadFile() {
        val file = App.app.externalCacheDir
        val path = file?.absolutePath + File.separator + "downloads" + File.separator
        Logger.i("getDownloadFile downloadPath--->>>$path")
        val content = File(path)
        if (content.exists()) {
            val listFiles = content.listFiles()
            val dataList = ArrayList<File>()
            for (i in listFiles!!.indices) {
                val itemFile = listFiles[i]
                dataList.add(itemFile)
                Logger.i("initEvent--->>>${itemFile.name}")
            }
            downloadAdapter?.refreshData(dataList)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityDownloadBinding {
        return ActivityDownloadBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): DownloadViewModel {
        return CommonTools.getViewModel(this, DownloadViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

    override fun onItemClickListener(view: View, data: File) {

    }

    override fun onItemLongClickListener(view: View, data: File) {

    }

}