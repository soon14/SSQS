package com.dading.ssqs.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.dading.ssqs.LocaleController
import com.dading.ssqs.R
import com.dading.ssqs.base.LayoutHelper
import com.dading.ssqs.fragment.guesstheball.EarlyFragment
import com.dading.ssqs.fragment.guesstheball.ScrollBallFragment
import com.dading.ssqs.fragment.guesstheball.ToDayMatchFragment
import com.dading.ssqs.utils.AndroidUtilities

/**
 * Created by mazhuang on 2017/11/30.
 */

class GuessTheBallFragment : Fragment() {

    private lateinit var scrollBallTextView: TextView
    private lateinit var toDayTextView: TextView
    private lateinit var earlyTextView: TextView

    private var scrollBallFragment: ScrollBallFragment? = null
    private var toDayMatchFragment: ToDayMatchFragment? = null
    private var earlyFragment: EarlyFragment? = null

    private var hasInit = false

    private var currType = GuessBallType.SCROLLBALL//当前的页面

    private var type = -1//足球还是篮球

    private enum class GuessBallType {
        SCROLLBALL, //滚球
        TODAY, //今日赛事
        Early//早盘
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView()
    }

    //构建页面布局
    private fun initView(): View {
        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL

        val titleLayout = LinearLayout(context)
        titleLayout.setBackgroundColor(resources.getColor(R.color.home_top_blue))
        titleLayout.gravity = Gravity.CENTER
        container.addView(titleLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48))

        val titleTextLayout = LinearLayout(context)
        titleTextLayout.orientation = LinearLayout.HORIZONTAL
        titleLayout.addView(titleTextLayout, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT))

        scrollBallTextView = TextView(context)
        scrollBallTextView.gravity = Gravity.CENTER_VERTICAL
        scrollBallTextView.setTextColor(Color.WHITE)
        scrollBallTextView.textSize = 18f
        scrollBallTextView.text = LocaleController.getString(R.string.scroll_ball)
        scrollBallTextView.setPadding(AndroidUtilities.dp(12f), 0, AndroidUtilities.dp(12f), 0)
        scrollBallTextView.setOnClickListener { changePageTextColor(GuessBallType.SCROLLBALL, false) }
        titleTextLayout.addView(scrollBallTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT))

        toDayTextView = TextView(context)
        toDayTextView.gravity = Gravity.CENTER_VERTICAL
        toDayTextView.setTextColor(-0xffbca2)
        toDayTextView.textSize = 18f
        toDayTextView.text = LocaleController.getString(R.string.today_match)
        toDayTextView.setPadding(AndroidUtilities.dp(12f), 0, AndroidUtilities.dp(12f), 0)
        toDayTextView.setOnClickListener { changePageTextColor(GuessBallType.TODAY, false) }
        titleTextLayout.addView(toDayTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT))

        earlyTextView = TextView(context)
        earlyTextView.gravity = Gravity.CENTER_VERTICAL
        earlyTextView.setTextColor(-0xffbca2)
        earlyTextView.textSize = 18f
        earlyTextView.text = LocaleController.getString(R.string.early)
        earlyTextView.setPadding(AndroidUtilities.dp(12f), 0, AndroidUtilities.dp(12f), 0)
        earlyTextView.setOnClickListener { changePageTextColor(GuessBallType.Early, false) }
        titleTextLayout.addView(earlyTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT))

        val parentLayout = LinearLayout(context)//用来替换fragment的布局
        parentLayout.id = R.id.guess_parent
        container.addView(parentLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT))

        return container
    }

    private fun init() {
        scrollBallFragment = ScrollBallFragment()

        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.add(R.id.guess_parent, scrollBallFragment)
        fragmentTransaction?.commit()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            if (!hasInit) {
                hasInit = true
                init()
            }
        }
    }

    //改变title文字颜色
    private fun changePageTextColor(type: GuessBallType, checkPage: Boolean) {
        if (this.currType != type) {
            this.currType = type

            clearTextColor()

            changePage(type)

            when (type) {
                GuessBallType.SCROLLBALL -> scrollBallTextView.setTextColor(Color.WHITE)
                GuessBallType.TODAY -> toDayTextView.setTextColor(Color.WHITE)
                GuessBallType.Early -> earlyTextView.setTextColor(Color.WHITE)
            }
        } else {
            if (checkPage) {
                changePage(type)
            }
        }
    }

    fun setType(type: Int, pageType: Int) {
        if (!hasInit) {
            hasInit = true
        }
        this.type = type

        when (pageType) {
            1 -> {
                changePageTextColor(GuessBallType.SCROLLBALL, true)
            }
            2 -> {
                changePageTextColor(GuessBallType.TODAY, true)
            }
            3 -> {
                changePageTextColor(GuessBallType.Early, true)
            }
        }
    }

    fun fragmentResume() {
        when (currType) {
            GuessBallType.SCROLLBALL -> scrollBallFragment?.fragmentResume()
            GuessBallType.TODAY -> toDayMatchFragment?.fragmentResume()
            GuessBallType.Early -> earlyFragment?.fragmentResume()
        }
    }

    override fun onPause() {
        super.onPause()
        fragmentPause()
    }

    override fun onResume() {
        super.onResume()
        fragmentResume()
    }

    fun fragmentPause() {
        when (currType) {
            GuessBallType.SCROLLBALL -> scrollBallFragment?.fragmentPause()
            GuessBallType.TODAY -> toDayMatchFragment?.fragmentPause()
            GuessBallType.Early -> earlyFragment?.fragmentPause()
        }
    }

    //fragment 试图切换
    private fun changePage(type: GuessBallType) {
        val fragmentTransaction = fragmentManager?.beginTransaction()

        if (scrollBallFragment != null) {
            scrollBallFragment!!.fragmentPause()
            fragmentTransaction?.hide(scrollBallFragment)
        }
        if (toDayMatchFragment != null) {
            toDayMatchFragment!!.fragmentPause()
            fragmentTransaction?.hide(toDayMatchFragment)
        }
        if (earlyFragment != null) {
            earlyFragment!!.fragmentPause()
            fragmentTransaction?.hide(earlyFragment)
        }

        if (type == GuessBallType.SCROLLBALL) {
            if (scrollBallFragment == null) {
                scrollBallFragment = ScrollBallFragment()
                scrollBallFragment!!.setBallType(this.type)
                fragmentTransaction?.add(R.id.guess_parent, scrollBallFragment)
            } else {
                scrollBallFragment!!.fragmentResume()
                fragmentTransaction?.show(scrollBallFragment)

                if (this.type == 1) scrollBallFragment!!.selectFootBall() else if (this.type == 2) scrollBallFragment!!.selectBasketBall()
            }
        } else if (type == GuessBallType.TODAY) {
            if (toDayMatchFragment == null) {
                toDayMatchFragment = ToDayMatchFragment()
                toDayMatchFragment!!.setBallType(this.type)
                fragmentTransaction?.add(R.id.guess_parent, toDayMatchFragment)
            } else {
                toDayMatchFragment?.fragmentResume()
                fragmentTransaction?.show(toDayMatchFragment)

                if (this.type == 1) toDayMatchFragment!!.selectFootBall() else if (this.type == 2) toDayMatchFragment!!.selectBasketBall()
            }
        } else if (type == GuessBallType.Early) {
            if (earlyFragment == null) {
                earlyFragment = EarlyFragment()
                earlyFragment!!.setBallType(this.type)
                fragmentTransaction?.add(R.id.guess_parent, earlyFragment)
            } else {
                earlyFragment?.fragmentResume()
                fragmentTransaction?.show(earlyFragment)

                if (this.type == 1) earlyFragment!!.selectFootBall() else if (this.type == 2) earlyFragment!!.selectBasketBall()
            }
        }
        fragmentTransaction?.commit()

        this.type = 0
    }

    //清除标题字体颜色
    private fun clearTextColor() {
        scrollBallTextView.setTextColor(-0xffbca2)
        toDayTextView.setTextColor(-0xffbca2)
        earlyTextView.setTextColor(-0xffbca2)
    }
}
