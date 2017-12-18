package com.mi.www.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.mi.www.criminalintent.bean.Crime;
import com.mi.www.criminalintent.bean.CrimeLab;

import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment implements OnClickListener{
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String TAG_DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private Crime mCrime;
    private UUID mCrimeId;
    private EditText mEtCrimeTitle;
    private Button mBtnCrimeDate;
    private CheckBox mCbCrimeSolved;
    private Button mBtnDelete;

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
        mBtnDelete = view.findViewById(R.id.btn_crime_delete);
        mBtnCrimeDate.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mCbCrimeSolved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });

        mCrimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(mCrimeId);
        if(mCrime != null){
            mEtCrimeTitle.setText(mCrime.getTitle());
            //            yyyy年MM月dd日,kk:mm-------2014年09月30日,11:23
            //            "MMM dd, yyyy h:mmaa" -> "Nov 3, 1987 11:23am"
            String date = DateFormat.format("yyyy年MM月dd日",mCrime.getDate()).toString();
            mBtnCrimeDate.setText(date);
            mCbCrimeSolved.setChecked(mCrime.isSolved());

        }

        mEtCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString().trim());
                CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCbCrimeSolved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
                CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_crime_date:
                //在CrimeFragment之上弹出一个DialogFragment
                FragmentManager manager = getFragmentManager();
                DatePickerDialogFragment datePickerDialogFragment =DatePickerDialogFragment.newInstance(mCrime.getDate());
                //设置DatePickerDialogFragment的目标是CrimeFragment，以便给CrimeFragment返回数据
                datePickerDialogFragment.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                datePickerDialogFragment.show(manager,TAG_DIALOG_DATE);
                break;
            case R.id.btn_crime_delete:
                CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerDialogFragment.EXTRA_DATE);
            mCrime.setDate(date);
            CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
            String dateString = DateFormat.format("yyyy年MM月dd日",mCrime.getDate()).toString();
            mBtnCrimeDate.setText(dateString);
        }
    }

}
