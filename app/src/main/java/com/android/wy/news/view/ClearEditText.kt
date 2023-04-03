package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.android.wy.news.R

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 16:55
  * @Version:        1.0
  * @Description:    
 */
class ClearEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
) : AppCompatEditText(context, attributeSet) {

    init {
        // 添加文本改变监听器
        addTextChangedListener {
            // 当内容为空后，就不要再显示图标了
            if (text.toString().isEmpty()) {
                hideClearIcon()
                editTextListener?.onEditTextClear()
            } else {
                // 就算有内容，也要获取到焦点后才能显示
                if (isFocused) {
                    showClearIcon(context)
                }
            }
        }
        // 设置点击关闭按钮
        setOnTouchListener { v, event ->
            performClick()
            var completed = false
            if (v is EditText && event.x >= width - v.totalPaddingRight
                && event.action == MotionEvent.ACTION_UP
            ) {
                setText("")
                completed = true
            }
            completed
        }
        // 设置监听焦点状态改变事件
        setOnFocusChangeListener { _, b ->
            if (text != null && text.toString().isNotEmpty()) {
                if (b) {
                    showClearIcon(context)
                    val len = text.toString().length
                    if (len > 0) {
                        setSelection(len)
                    }
                } else {
                    hideClearIcon()
                }
            }
        }
    }

    /**
     * 显示清除图标
     */
    private fun showClearIcon(context: Context) {
        val end = ResourcesCompat.getDrawable(resources, R.mipmap.et_clear, null)
        end?.setBounds(0, 0, px2dip(context, 20.0f), px2dip(context, 20.0f))
        setCompoundDrawables(null, null, end, null)
        // 使用原来的内边距
        setPadding(paddingLeft, 0, px2dip(context, 10.0f), 0);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }


    /**
     * 隐藏清除图标
     */
    private fun hideClearIcon() {
        setCompoundDrawables(null, null, null, null)
    }

    private var editTextListener: OnEditTextListener? = null

    fun addListener(onEditTextListener: OnEditTextListener) {
        this.editTextListener = onEditTextListener
    }

    interface OnEditTextListener {
        fun onEditTextClear()
    }
}