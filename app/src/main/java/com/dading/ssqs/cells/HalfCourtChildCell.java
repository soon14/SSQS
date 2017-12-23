package com.dading.ssqs.cells;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dading.ssqs.base.LayoutHelper;
import com.dading.ssqs.bean.ScrollBallFootBallBoDanBean;
import com.dading.ssqs.bean.ScrollBallFootBallHalfCourtBean;
import com.dading.ssqs.fragment.guesstheball.scrollball.ScrollBallHalfCourtFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazhuang on 2017/12/2.
 */

public class HalfCourtChildCell extends LinearLayout {

    private TextView timeTextView;
    private TextView titleTextView;
    private LinearLayout tableLayout;

    private Context mContext;

    private String[] oneArray = new String[]{"球队", "胜/胜", "胜/平", "胜/负", "平/胜", "平/平", "平/负", "负/胜", "负/平", "负/负"};

    private View view;

    private List<HalfCourtItemCell> cells = new ArrayList<>();

    public HalfCourtChildCell(Context context) {
        super(context);
        mContext = context;

        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        RelativeLayout topLayout = new RelativeLayout(context);
        topLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(topLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 40));

        timeTextView = new TextView(context);
        timeTextView.setTextSize(10);
        timeTextView.setTextColor(0xFFBDBDBD);
        topLayout.addView(timeTextView, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 17, 0, 0, 0, RelativeLayout.CENTER_VERTICAL));

        titleTextView = new TextView(context);
        titleTextView.setTextSize(14);
        titleTextView.setTextColor(0xFF626262);
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        topLayout.addView(titleTextView, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, RelativeLayout.CENTER_IN_PARENT));

        tableLayout = new LinearLayout(context);
        tableLayout.setOrientation(LinearLayout.VERTICAL);
        addView(tableLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout titleLayout = new LinearLayout(mContext);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        tableLayout.addView(titleLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 30));

        for (int i = 0; i < oneArray.length; i++) {
            RelativeLayout layout = new RelativeLayout(mContext);

            float weight;
            if (i == 0) {
                weight = 2;
            } else {
                weight = 1;
            }

            titleLayout.addView(layout, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, weight));

            TextView textView = new TextView(mContext);
            textView.setBackgroundColor(0xFF009BDB);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setText(oneArray[i]);
            layout.addView(textView, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            if (i != oneArray.length - 1) {
                View view = new View(mContext);
                view.setBackgroundColor(0xFFE7E7E7);
                layout.addView(view, LayoutHelper.createRelative(1, LayoutHelper.MATCH_PARENT, RelativeLayout.ALIGN_PARENT_RIGHT));
            }
        }

        LinearLayout valueLayout = new LinearLayout(mContext);
        valueLayout.setOrientation(LinearLayout.HORIZONTAL);
        tableLayout.addView(valueLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 35));


        for (int i = 0; i < 10; i++) {
            HalfCourtItemCell childCell = new HalfCourtItemCell(mContext);

            float weight;
            if (i == 0) {
                weight = 2;
            } else {
                weight = 1;
            }

            valueLayout.addView(childCell, LayoutHelper.createLinear(0, LayoutHelper.MATCH_PARENT, weight));
            cells.add(childCell);

            if (i != 4) {
                childCell.isShowLine(true);
            }
        }

        view = new View(context);
        view.setBackgroundColor(0xFFE4E4E4);
        addView(view, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 8));
    }

    public void setTime(String time) {
        timeTextView.setText(time);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setData(ScrollBallFootBallHalfCourtBean.ScrollBallFootBallHalfCourtItems bean, List<ScrollBallHalfCourtFragment.MergeBean> focusList, HalfCourtItemCell.OnItemClickListener listener) {
        List<ScrollBallFootBallHalfCourtBean.ScrollBallFootBallHalfCourtItem> list = bean.getItemList();

        for (int i = 0; i < list.size(); i++) {
            cells.get(i).setData(bean, list.get(i), i, focusList);
            cells.get(i).setListener(listener);
        }
    }
}