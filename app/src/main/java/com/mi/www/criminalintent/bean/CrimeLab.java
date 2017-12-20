package com.mi.www.criminalintent.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mi.www.criminalintent.database.CrimeBaseHelper;
import com.mi.www.criminalintent.database.CrimeCursorWrapper;
import com.mi.www.criminalintent.bean.Crime;
import com.mi.www.criminalintent.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mi.www.criminalintent.database.CrimeDbSchema.*;


/**
 * Created by wm on 2017/12/12.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab getCrimeLab(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    /*
    *  if(crime.getId() == id){
                return crime;
            }就不行？？
    *
    //todo
    public Crime getCrimeById(UUID id){
        for(Crime crime : mCrimeList){
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }*/

    /**
     * 添加一个crime
     * @param crime
     */
    public  void addCrime(Crime crime){
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, contentValues);
    }

    /**
     * 更新记录
     * @param crime
     */
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + "= ?",
                new String[]{uuidString});
    }

    /**
     * 查找记录
     * @return
     */
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            //最后记得关闭
            cursor.close();
        }
        return crimes;
    }

    /**
     * 根据uuid查找crime
     * @param id
     * @return
     */
    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + "= ?",
                new String[]{id.toString()}
        );
        try {
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }

    public void deleteCrime(Crime crime){
        String uuidString = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + "= ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return contentValues;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
}
