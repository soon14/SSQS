package com.dading.ssqs.controllar.scores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.dading.ssqs.R;
import com.dading.ssqs.SSQSApplication;
import com.dading.ssqs.adapter.ScoreMatchAdapterSc;
import com.dading.ssqs.apis.CcApiClient;
import com.dading.ssqs.apis.CcApiResult;
import com.dading.ssqs.base.BaseScoreControllar;
import com.dading.ssqs.bean.Constent;
import com.dading.ssqs.bean.ScoreBean;
import com.dading.ssqs.utils.DateUtils;
import com.dading.ssqs.utils.Logger;
import com.dading.ssqs.utils.PopUtil;
import com.dading.ssqs.utils.ToastUtils;
import com.dading.ssqs.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dading.ssqs.components.pulltorefresh.PullToRefreshBase;
import com.dading.ssqs.components.pulltorefresh.PullToRefreshListView;

/**
 * 创建者     ZCL
 * 创建时间   2016/7/5 16:45
 * 描述	      ${TODO}
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class SCScoreControllar extends BaseScoreControllar implements View.OnClickListener {

    private static final String TAG = "SCScoreControllar";
    private PullToRefreshListView mScList;
    private ScoreMatchAdapterSc mAdapter;
    private String mFormatData1;
    private String mFormatData2;
    private int mcount = 10;
    private int mPage = 1;
    private List<ScoreBean> mItems = new ArrayList<>();
    private SimpleDateFormat mSdf1;
    private SimpleDateFormat mSdf2;
    private Calendar mCalendar;
    private String mWeek;
    private ArrayList<String> mListData1;
    private ArrayList<String> mListData2;
    private PopupWindow mPopupWindow;
    public String mDate;
    public SCRecevice mRecevice;
    private int mTotalCount;
    private RelativeLayout mEmpty;
    private LinearLayout mLoadingAnimal;
    private Runnable mTask;
    private Runnable mTaskMore;
    private boolean isGetData = false;


    @Override
    public View initContentView(Context context) {
        View view = View.inflate(mContent, R.layout.scorepager_sc, null);

        mScList = (PullToRefreshListView) view.findViewById(R.id.score_vp_sc);
        mEmpty = (RelativeLayout) view.findViewById(R.id.data_empty);
        mLoadingAnimal = (LinearLayout) view.findViewById(R.id.loading_anim);
        mScList.getRefreshableView().setEmptyView(mEmpty);

        mAdapter = new ScoreMatchAdapterSc(mContent, 2);
        mScList.setAdapter(mAdapter);
        return view;
    }

    private boolean hasInit = false;

    public void init() {
        if (!hasInit) {
            hasInit = true;

            if (!isGetData) {
                isGetData = true;
                getNetDataWork(mFormatData1, "0", 0, "0", mPage, true);
            }
        }
    }

    @Override
    public void initData() {
        mRecevice = new SCRecevice();
        UIUtils.ReRecevice(mRecevice, Constent.SC_RECEVICE);
        UIUtils.ReRecevice(mRecevice, Constent.SC_RECEVICE_CB);
        UIUtils.ReRecevice(mRecevice, Constent.LOADING_FOOTBALL_SCORE);
        UIUtils.ReRecevice(mRecevice, Constent.JS_SG_SC_FITTER);
        /***
         * 传递过去token
         */

        mCalendar = Calendar.getInstance();
        mSdf1 = new SimpleDateFormat("yyyyMMddHH:mm:ss", Locale.CHINA);
        mSdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        mCalendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = mCalendar.getTime();
        mFormatData1 = mSdf1.format(date);
        mFormatData2 = mSdf2.format(date);

        mDate = mFormatData2.replaceAll("-", "");
        UIUtils.getSputils().putString(Constent.SC_TIME, mFormatData1);
        Logger.INSTANCE.d(TAG, "赛程日期:" + mFormatData1);
        mWeek = AppendData(mFormatData2);
        mScoreWeekData.setText(mWeek);

        mListData1 = new ArrayList<>();
        mListData2 = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, i);
            Date dates = calendar.getTime();
            String s1 = mSdf1.format(dates);
            String s2 = mSdf2.format(dates);
            String sData = AppendData(s2);
            mListData1.add(s1);
            mListData2.add(sData);
        }

        for (int i = 0; i < mListRB.size(); i++) {
            mListRB.get(i).setText(mListData2.get(i));
        }
    }

    @Override
    public void checkInitListner() {
        mItems.clear();
        mPage = 1;
        switch (mCalendarPostion) {
            case 0:
                day = 0;
                break;
            case 1:
                day = 1;
                break;
            case 2:
                day = 2;
                break;
            case 3:
                day = 3;
                break;
            case 4:
                day = 4;
                break;
            case 5:
                day = 5;
                break;
            case 6:
                day = 6;
                break;
            default:
                break;
        }
        CalendarClick((day + 1));
        String s1 = mListData1.get(mCalendarPostion);
        mFormatData1 = s1;
        calendarVolley(UIUtils.getSputils().getString(Constent.SC_TIME, ""));
        if (mPopupWindow == null) {
            return;
        } else {
            mPopupWindow.dismiss();
        }
    }

    private void calendarData(int num) {
        mPage = 1;
        mItems.clear();
        switch (num) {
            case 0:
                mRg.check(R.id.pop_calendar_rb1);
                break;
            case 1:
                mRg.check(R.id.pop_calendar_rb2);
                break;
            case 2:
                mRg.check(R.id.pop_calendar_rb3);
                break;
            case 3:
                mRg.check(R.id.pop_calendar_rb4);
                break;
            case 4:
                mRg.check(R.id.pop_calendar_rb5);
                break;
            case 5:
                mRg.check(R.id.pop_calendar_rb6);
                break;
            case 6:
                mRg.check(R.id.pop_calendar_rb7);
                break;
            default:
                break;
        }
    }

    private void CalendarClick(int num) {
        mCalendar.setTime(new Date());
        mCalendar.add(Calendar.DAY_OF_YEAR, num);
        Date date = mCalendar.getTime();
        String s = mSdf2.format(date);
        String data = AppendData(s);
        mDate = s.replaceAll("-", "");
        UIUtils.getSputils().putString(Constent.SC_TIME, mSdf1.format(date));
        mScoreWeekData.setText(data);
        switch (num) {
            case 7:
                mScoreWeekLeft.setImageResource(R.mipmap.arrows_checked_2);
                mScoreWeekLeft.setClickable(true);
                mScoreWeekRight.setImageResource(R.mipmap.arrows_2);
                mScoreWeekRight.setClickable(false);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                mScoreWeekLeft.setImageResource(R.mipmap.arrows_checked_2);
                mScoreWeekLeft.setClickable(true);
                mScoreWeekRight.setImageResource(R.mipmap.arrows_checked);
                mScoreWeekRight.setClickable(true);
                break;
            case 1:
                mScoreWeekLeft.setImageResource(R.mipmap.arrows);
                mScoreWeekLeft.setClickable(false);
                mScoreWeekRight.setImageResource(R.mipmap.arrows_checked);
                mScoreWeekRight.setClickable(true);
                break;
            default:
                break;
        }
    }

    private String AppendData(String formatData2) {
        String week = DateUtils.getweekdayBystr(formatData2);
        String dataAll = "(" + week + ")" + formatData2;
        return dataAll;
    }

    @Override
    public void setUnDe() {
        super.setUnDe();
        UIUtils.UnReRecevice(mRecevice);
        UIUtils.removeTask(mTask);
        UIUtils.removeTask(mTaskMore);
    }

    @Override
    public void initScorecalendar() {
        mScoreWeekLayout.setVisibility(View.VISIBLE);
        mScoreWeekLeft.setImageResource(R.mipmap.arrows);
        mScoreWeekLeft.setClickable(false);
        mScoreWeekRight.setImageResource(R.mipmap.arrows_checked);
        mScoreWeekRight.setClickable(true);
    }

    private void getNetDataWork(String date, String subType, int stype, String leagueIds, int page, final boolean isRefresh) {
        boolean b = UIUtils.getSputils().getBoolean(Constent.IS_FOOTBALL, true);

        SSQSApplication.apiClient(0).getMatchBallOrTypeList(b, 4, date, subType, stype, leagueIds, page, mcount, new CcApiClient.OnCcListener() {
            @Override
            public void onResponse(CcApiResult result) {
                mScList.onRefreshComplete();
                mLoadingAnimal.setVisibility(View.GONE);

                if (result.isOk()) {
                    CcApiResult.ResultScorePage page = (CcApiResult.ResultScorePage) result.getData();

                    if (page != null) {
                        mTotalCount = page.getTotalCount();

                        if (page.getItems() != null) {
                            if (isRefresh) {
                                mItems = page.getItems();

                                mAdapter.setData(mItems);
                            } else {
                                mItems.addAll(page.getItems());
                                mcount = mItems.size();

                                mAdapter.addData(mItems);
                            }
                        }
                    }
                    isGetData = false;
                } else {
                    ToastUtils.midToast(mContent, result.getMessage(), 0);
                }
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mScList.setMode(PullToRefreshBase.Mode.BOTH);
        mScList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isGetData) {
                    isGetData = true;

                    //关闭下拉刷新的效果
                    mTask = new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.getSputils().putString(Constent.SUBTYPE, "0");
                            UIUtils.getSputils().putString(Constent.LEAGUEIDS, "0");

                            mPage = 1;
                            getNetDataWork(UIUtils.getSputils().getString(Constent.SC_TIME, "20000101"), "0", 0, "0", mPage, true);

                        }
                    };
                    UIUtils.postTaskDelay(mTask, 500);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                //关闭上啦加载的效果
                mTaskMore = new Runnable() {
                    @Override
                    public void run() {
                        mPage++;
                        if (mPage > mTotalCount) {
                            mPage--;
                            ToastUtils.midToast(UIUtils.getContext(), "已全部加载,无新数据!", 0);
                            mScList.onRefreshComplete();
                        } else {
                            if (!isGetData) {
                                isGetData = true;
                                getNetDataWork(UIUtils.getSputils().getString(Constent.SC_TIME, "20000101"), UIUtils.getSputils().getString(Constent.SUBTYPE, "0"), 0, UIUtils.getSputils().getString(Constent.LEAGUEIDS, "0"), mPage, false);
                            }
                        }
                    }
                };
                UIUtils.postTaskDelay(mTaskMore, 500);
            }
        });
        mScoreWeekLeft.setOnClickListener(this);
        mScoreWeekRight.setOnClickListener(this);
        mScoreWeekCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow = PopUtil.popuMake(mPopView);
                mPopupWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
            }
        });
        mPopLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        mPopClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        mItems.clear();
        switch (v.getId()) {
            case R.id.score_week_left:
                timeOperation(true);
                break;
            case R.id.score_week_right:
                timeOperation(false);
                break;
            default:
                break;
        }
    }

    private int day = 0;

    private void timeOperation(boolean isLeft) {
        if (day == 0 && isLeft || day >= 6 && !isLeft) {
            return;
        }

        mScoreWeekLeft.setImageResource(R.mipmap.arrows_checked_2);
        mScoreWeekLeft.setClickable(true);
        mScoreWeekRight.setImageResource(R.mipmap.arrows_checked);
        mScoreWeekRight.setClickable(true);

        if (isLeft) {
            day--;

            mCalendar.add(Calendar.DAY_OF_YEAR, -1);

            if (day == 0) {
                mScoreWeekLeft.setImageResource(R.mipmap.arrows);
                mScoreWeekLeft.setClickable(false);
                mScoreWeekRight.setImageResource(R.mipmap.arrows_checked);
                mScoreWeekRight.setClickable(true);
            }
        } else {
            day++;

            mCalendar.add(Calendar.DAY_OF_YEAR, 1);

            if (day == 6) {
                mScoreWeekLeft.setImageResource(R.mipmap.arrows_checked_2);
                mScoreWeekLeft.setClickable(true);
                mScoreWeekRight.setImageResource(R.mipmap.arrows_2);
                mScoreWeekRight.setClickable(false);
            }
        }
        Date date = mCalendar.getTime();
        String s = mSdf2.format(date);
        String data = AppendData(s);
        mScoreWeekData.setText(data);

        mDate = s.replaceAll("-", "");
        String netWorkDate=mSdf1.format(date);
        UIUtils.getSputils().putString(Constent.SC_TIME, netWorkDate);

        calendarVolley(netWorkDate);

        calendarData(day);
    }

    private void calendarVolley(String rightLeft) {
        if (!isGetData) {
            isGetData = true;

            mPage = 1;

            getNetDataWork(rightLeft, "0", 0, "0", mPage, true);
        }
    }

    private class SCRecevice extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.INSTANCE.d("GBSS", "接到广播SC------------------------------:");
            String action = intent.getAction();

            String s = UIUtils.getSputils().getString(Constent.LEAGUEIDS, "0");

            if (!isGetData) {
                isGetData = true;

                mPage = 1;
                getNetDataWork(UIUtils.getSputils().getString(Constent.SC_TIME, "20000101"), UIUtils.getSputils().getString(Constent.SUBTYPE, "0"), 0, (action.equals(Constent.JS_SG_SC_FITTER) ? s : "0"), mPage, true);
            }
        }
    }
}
