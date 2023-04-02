package com.android.wy.news.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.android.wy.news.common.IBaseCommon
import com.android.wy.news.listener.IBackPressedListener
import com.android.wy.news.viewmodel.BaseViewModel

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 15:07
  * @Version:        1.0
  * @Description:    
 */
abstract class BaseFragment<V : ViewBinding, M : BaseViewModel> : Fragment(), IBaseCommon<V, M>,
    IBackPressedListener {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: M
    protected lateinit var mActivity: Activity

    private var mRoot: View? = null

    /**
     * 是否执行了lazyLoad方法
     */
    private var isLoaded = false

    /**
     * 是否创建了View
     */
    private var isCreateView = false

    /**
     * 当从另一个activity回到fragment所在的activity
     * 当fragment回调onResume方法的时候，可以通过这个变量判断fragment是否可见，来决定是否要刷新数据
     */
    var isFragmentVisible = false

    /*
    * 此方法在viewpager嵌套fragment时会回调
    * 查看FragmentPagerAdapter源码中instantiateItem和setPrimaryItem会调用此方法
    * 在所有生命周期方法前调用
    * 这个基类适用于在viewpager嵌套少量的fragment页面
    * 该方法是第一个回调，可以将数据放在这里处理（viewpager默认会预加载一个页面）
    * 只在fragment可见时加载数据，加快响应速度
    * */
    @Deprecated("Deprecated in Java")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    protected open fun onVisible() {
        isFragmentVisible = true
        if (isLoaded) {
            refreshLoad()
        }
        if (!isLoaded && isCreateView && userVisibleHint) {
            isLoaded = true
            lazyLoad()
        }
    }

    /**
     * fragment第一次可见的时候回调此方法
     */
    protected open fun lazyLoad() {
        mViewModel = getViewModel()
        initData()
        initEvent()
        onNotifyDataChanged()
    }

    /**
     * 在Fragment第一次可见加载以后，每次Fragment滑动可见的时候会回调这个方法，
     * 子类可以重写这个方法做数据刷新操作
     */
    protected open fun refreshLoad() {}

    protected open fun onInvisible() {
        isFragmentVisible = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (mRoot == null) {
            mActivity = requireActivity()
            mBinding = getViewBinding()
            mRoot = mBinding.root
            isCreateView = true
            initView()
            onVisible()
        }
        return mRoot
    }

    override fun onDestroy() {
        super.onDestroy()
        onClear()
    }

    override fun handleBackPressed(): Boolean {
        //默认不响应
        return false
    }
}