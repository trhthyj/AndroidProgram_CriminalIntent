package com.mi.www.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mi.www.criminalintent.bean.Crime;
import com.mi.www.criminalintent.bean.CrimeLab;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends Fragment {
    public static final String BUNDLE_SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mRecyclerView;
    private CrimeLab mCrimeLab;
    private List<Crime> mCrimeList;
    private CrimeListAdapter mCrimeListAdapter;
    private boolean mSubtitleVisible;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让FragmentManager 知道CrimeListFragment需接收选项菜单方法回调
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(BUNDLE_SAVED_SUBTITLE_VISIBLE);
        }
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mRecyclerView = view.findViewById(R.id.rcv_crime);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        //重建工具栏时会调用onCreateOptionsMenu（比如横竖屏切换）
        MenuItem subtitleMenuItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleMenuItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleMenuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_crime:
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();//重建menu
                updateSubTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * 横竖屏切换的时候会重新创建fragment，menu的状态会回到初始状态
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    /**
     * 添加crime返回时更新列表
     */
    private void updateUI() {
        mCrimeLab = CrimeLab.getCrimeLab(getActivity());
        mCrimeList = mCrimeLab.getCrimes();
        if(mCrimeListAdapter == null){
            mCrimeListAdapter = new CrimeListAdapter(mCrimeList);
            mRecyclerView.setAdapter(mCrimeListAdapter);
        }else{
            mCrimeListAdapter.setCrimes(mCrimeList);
            mCrimeListAdapter.notifyDataSetChanged();
        }
        //添加crime后刷新subtitle，因为onResume里会调用updateUI，因此在此方法调用updateSubTitle
        updateSubTitle();
    }

    /**
     * 更新subtitle,显示列表Crime的数量
     */
    private void updateSubTitle() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        //复数字符串资源,此方法在中文系统环境中不起作用
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural,crimeCount,crimeCount);
        if(!mSubtitleVisible){
            subtitle = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    class CrimeListAdapter extends RecyclerView.Adapter<CrimeListViewHolder>{
        private List<Crime> mCrimeList;

        public CrimeListAdapter(List<Crime> crimeList) {
            mCrimeList = crimeList;
        }

        @Override
        public CrimeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //todo  inflate的不同参数方法含义
            View view =layoutInflater.inflate(R.layout.item_list_crime,parent,false);
            return new CrimeListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeListViewHolder holder, int position) {
            holder.bind(mCrimeList.get(position));
        }

        @Override
        public int getItemCount() {
            return mCrimeList.size();
        }

        public void setCrimes(List<Crime> crimes){
            this.mCrimeList = crimes;
        }
    }

    class CrimeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTvCrimeTitle;
        private TextView mTvCrimeDate;
        private ImageView mIvResolved;
        private View mView;
        private Crime mCrime;
        public CrimeListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTvCrimeTitle = itemView.findViewById(R.id.tv_crime_title);
            mTvCrimeDate = itemView.findViewById(R.id.tv_crime_date);
            mIvResolved = itemView.findViewById(R.id.iv_resolved);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTvCrimeTitle.setText(crime.getTitle());
            String date = DateFormat.format("yyyy年MM月dd日",crime.getDate()).toString();
            mTvCrimeDate.setText(date);
            mIvResolved.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //这种调用方法可以避免startActivityForResult尴尬
//            Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getId());
//            startActivity(intent);
            Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
            startActivity(intent);
        }
    }

}
