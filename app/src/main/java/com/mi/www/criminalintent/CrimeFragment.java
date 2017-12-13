package com.mi.www.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.mi.www.criminalintent.bean.Crime;
import com.mi.www.criminalintent.bean.CrimeLab;

import java.util.UUID;

import static android.widget.CompoundButton.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private Crime mCrime;
    private UUID mCrimeId;
    private EditText mEtCrimeTitle;
    private Button mBtnCrimeDate;
    private CheckBox mCbCrimeSolved;

    public static CrimeFragment newInstance(UUID id){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID,id);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mEtCrimeTitle = view.findViewById(R.id.et_crime_title);
        mBtnCrimeDate = view.findViewById(R.id.btn_crime_date);
        mCbCrimeSolved = view.findViewById(R.id.cb_crime_solved);
        mBtnCrimeDate.setEnabled(false);
        mEtCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCbCrimeSolved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });

        mCrimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrimeById(mCrimeId);
        if(mCrime != null){
            mEtCrimeTitle.setText(mCrime.getTitle());
            mBtnCrimeDate.setText(mCrime.getDate());
            mCbCrimeSolved.setChecked(mCrime.isSolved());

        }
        return view;
    }

}
