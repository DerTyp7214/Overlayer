package de.dertyp7214.overlayer.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import de.dertyp7214.overlayer.R

class CenteredToolbar : Toolbar {
    private var centeredTitleTextView: TextView? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    override fun setTitle(@StringRes resId: Int) {
        val s = resources.getString(resId)
        title = s
    }

    override fun setTitle(title: CharSequence) {
        getCenteredTitleTextView().text = title
    }

    override fun getTitle(): CharSequence {
        return getCenteredTitleTextView().text.toString()
    }

    fun setTypeface(font: Typeface?) {
        getCenteredTitleTextView().typeface = font
    }

    override fun setTitleTextColor(color: Int) {
        super.setTitleTextColor(color)
        getCenteredTitleTextView().setTextColor(color)
    }

    override fun setTitleTextColor(color: ColorStateList) {
        super.setTitleTextColor(color)
        getCenteredTitleTextView().setTextColor(color)
    }

    private fun getCenteredTitleTextView(): TextView {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = TextView(context).apply {
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                gravity = Gravity.CENTER
                setTextAppearance(R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
                val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                lp.gravity = Gravity.CENTER
                layoutParams = lp
            }
            addView(centeredTitleTextView)
        }
        return centeredTitleTextView!!
    }
}