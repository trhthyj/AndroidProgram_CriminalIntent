package com.mi.www.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mi.www.criminalintent.bean.Crime;

import java.util.Date;
import java.util.UUID;

import static com.mi.www.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by wm on 2017/12/18.
 * 创建可复用的
 专用Cursor子类。创建Cursor子类最简单的方式是使用CursorWrapper。用
 CursorWrapper封装Cursor的对象，然后再添加有用的扩展方法。
 */

public class CrimeCursorWrapper extends CursorWrapper{

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        Crime crime =new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved == 1);
        crime.setSuspect(suspect);
        return crime;
    }
}
