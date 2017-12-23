package cundong.recyclerview;

import android.view.View;

/**
 * Created by cundong on 2015/10/9.
 * RecyclerView/ListView/GridView 滑动加载下一页时的回调接口
 */
interface OnListLoadNextPageListener {

    /**
     * 开始加载下一页
     *
     * @param view 当前RecyclerView/ListView/GridView
     */
    void onLoadNextPage(View view);
}