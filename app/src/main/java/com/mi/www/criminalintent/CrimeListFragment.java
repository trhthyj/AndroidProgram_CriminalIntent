package com.mi.www.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
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
    private RecyclerView mRecyclerView;
    private CrimeLab mCrimeLab;
    private List<Crime> mCrimeList;
    private CrimeListAdapter mCrimeListAdapter;

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume----","updateUI");
        updateUI();
    }

    private void updateUI() {
        mCrimeLab = CrimeLab.getCrimeLab(getActivity());
        mCrimeList = mCrimeLab.getCrimeList();
        if(mCrimeListAdapter == null){
            mCrimeListAdapter = new CrimeListAdapter(mCrimeList);
            mRecyclerView.setAdapter(mCrimeListAdapter);
        }else{
            mCrimeListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mRecyclerView = view.findViewById(R.id.rcv_crime);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
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
