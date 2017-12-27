package com.mi.www.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.mi.www.criminalintent.bean.Crime;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.CallBacks,CrimeFragment.CallBacks{

    @Override
    protected Fragment createFragment() {
        Fragment fragment = new CrimeListFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    //CrimeListFragment点击时的回调
    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this,crime.getId());
            startActivity(intent);
        }else{
            Fragment fragmentDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragmentDetail)
                    .commit();
        }
    }

    //CrimeFragment新建crime时的回调
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
