/*
 * Copyright (C) 2024 ankio(ankio@ankio.net)
 * Licensed under the Apache License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-3.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.ankio.auto.ui.api

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.google.android.material.elevation.SurfaceColors
import net.ankio.auto.App
import net.ankio.auto.storage.Logger
import net.ankio.auto.ui.activity.MainActivity
import net.ankio.auto.ui.componets.StatusPage
import net.ankio.auto.ui.models.ToolbarMenuItem


/**
 * 基础的Fragment
 */
abstract class BaseFragment : Fragment() {
    /**
     * 菜单列表
     */
    open val menuList: ArrayList<ToolbarMenuItem> = arrayListOf()

    override fun toString(): String {
        return this.javaClass.simpleName
    }


    private var scrollView: View? = null
    private var maxScrollViewLength = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Logger.d("view:$view")
        getScrollView(view as ViewGroup)
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity
        if (mainActivity !is MainActivity) return

        mainActivity.binding.toolbar.visibility = View.VISIBLE
        // 重置顶部导航栏图标
        mainActivity.binding.toolbar.menu.clear()
        // 添加菜单
        menuList.forEach {
            addMenuItem(it, mainActivity)
        }

        val mStatusBarColor = App.getThemeAttrColor(android.R.attr.colorBackground)
        val mStatusBarColor2 = SurfaceColors.SURFACE_4.getColor(requireActivity())
        var last = mStatusBarColor
        mainActivity.toolbarLayout?.setBackgroundColor(mStatusBarColor)
        if (scrollView != null) {
            var animatorStart = false
            // 滚动页面调整toolbar颜色
            scrollView!!.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                var scrollYs = scrollY // 获取宽度
                if (scrollView is RecyclerView) {
                    // RecyclerView获取真实高度
                    scrollYs = (scrollView as RecyclerView).computeVerticalScrollOffset()
                }

                if (animatorStart) return@setOnScrollChangeListener

                if (scrollYs.toFloat() > 0) {
                    if (last != mStatusBarColor2) {
                        animatorStart = true
                        viewBackgroundGradientAnimation(
                            mainActivity.toolbarLayout!!,
                            mStatusBarColor,
                            mStatusBarColor2,
                        )
                        last = mStatusBarColor2
                    }
                } else {
                    if (last != mStatusBarColor) {
                        animatorStart = true
                        viewBackgroundGradientAnimation(
                            mainActivity.toolbarLayout!!,
                            mStatusBarColor2,
                            mStatusBarColor,
                        )
                        last = mStatusBarColor
                    }
                }
                animatorStart = false
            }


        }
    }



    private fun findScrollView(view: View) {
        val scrollViewInner = when (view) {
            is ScrollView, is RecyclerView -> view
            is StatusPage -> view.contentView!!
            else -> null
        }
        scrollViewInner?.let {
            val length = it.height
            if (length >= maxScrollViewLength) {
                scrollView = it
                maxScrollViewLength = length
            }
        }
    }

    private fun getScrollView(view: ViewGroup, depth: Int = 0) {
        if (depth > 10) return
        findScrollView(view)
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            if (child is ViewGroup) {
                getScrollView(child, depth + 1)
            } else {
                findScrollView(child)
            }
        }
    }


    protected var searchData = ""

    /**
     * 添加菜单
     */
    private fun addMenuItem(menuItemObject: ToolbarMenuItem, mainActivity: MainActivity) {
        val menu = mainActivity.binding.toolbar.menu
        val menuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, getString(menuItemObject.title))
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        val icon = AppCompatResources.getDrawable(requireActivity(), menuItemObject.drawable)
        if (icon != null) {
            menuItem.setIcon(icon)
            DrawableCompat.setTint(
                icon,
                App.getThemeAttrColor(R.attr.colorOnBackground),
            )
        }

        if (menuItemObject.search) {
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)

            val searchView = SearchView(requireContext())
            menuItem.setActionView(searchView)

            searchView.queryHint = getString(menuItemObject.title)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchData = query.toString()
                    menuItemObject.callback.invoke()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchData = newText.toString()
                    menuItemObject.callback.invoke()
                    return false
                }
            })

        } else {
            menuItem.setOnMenuItemClickListener {
                menuItemObject.callback.invoke()
                true
            }
        }


    }


    /**
     * toolbar颜色渐变动画
     */
    private fun viewBackgroundGradientAnimation(
        view: View,
        fromColor: Int,
        toColor: Int,
        duration: Long = 600,
    ) {
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimator.addUpdateListener { animation ->
            val color = animation.animatedValue as Int // 之后就可以得到动画的颜色了
            view.setBackgroundColor(color) // 设置一下, 就可以看到效果.
        }
        colorAnimator.duration = duration
        colorAnimator.start()
    }


    override fun onStop() {
        super.onStop()
        App.pageStopOrDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.pageStopOrDestroy()
    }

}
