package com.mi.www.criminalintent.bean;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by wm on 2017/12/12.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimeList;

    private CrimeLab(Context context) {
        mCrimeList = new ArrayList<>();
        for(int i = 0;i < 100;i++){
            Crime crime =new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimeList.add(crime);
        }
    }

    public static CrimeLab getCrimeLab(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimeList(){
        return mCrimeList;
    }

    /*
    *  if(crime.getId() == id){
                return crime;
            }就不行？？
    * */
    //todo
    public Crime getCrimeById(UUID id){
        for(Crime crime : mCrimeList){
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }
}
