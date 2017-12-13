package com.mi.www.criminalintent.bean;

import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by wm on 2017/12/11.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private String mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        //            yyyy年MM月dd日,kk:mm-------2014年09月30日,11:23
        //            "MMM dd, yyyy h:mmaa" -> "Nov 3, 1987 11:23am"
        mDate = DateFormat.format("yyyy年MM月dd日",new Date()).toString();

    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
