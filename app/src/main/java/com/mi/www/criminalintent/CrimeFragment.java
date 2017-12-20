package com.mi.www.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextUtils;
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
    private static final int REQUEST_CONTACT = 1;
    private Crime mCrime;
    private UUID mCrimeId;
    private EditText mEtCrimeTitle;
    private Button mBtnCrimeDate;
    private CheckBox mCbCrimeSolved;
    private Button mBtnDelete;
    private Button mBtnChooseSuspect;
    private Button mBtnReportCrime;

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
        mBtnChooseSuspect = view.findViewById(R.id.btn_choose_suspect);
        mBtnReportCrime = view.findViewById(R.id.btn_report_crime);
        mBtnDelete = view.findViewById(R.id.btn_crime_delete);
        mBtnCrimeDate.setOnClickListener(this);
        mBtnChooseSuspect.setOnClickListener(this);
        mBtnReportCrime.setOnClickListener(this);
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
            if(!TextUtils.isEmpty(mCrime.getSuspect())){
                mBtnChooseSuspect.setText(mCrime.getSuspect());
            }
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
            case R.id.btn_choose_suspect:
                pickContact();
                break;
            case R.id.btn_report_crime:
                sendReport();
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
        }else if(requestCode == REQUEST_CONTACT && data != null){
            //todo 不太清楚处理联系人数据什么含义
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                if(cursor.getCount() == 0){
                    return;
                }
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setSuspect(suspect);
                mBtnChooseSuspect.setText(suspect);
                CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
            }finally {
                cursor.close();
            }
        }
    }

    /**
     * 发送report
     */
    private void sendReport() {
        //下面两种方式都可以

       /* Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/palin");
        intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
        //只要候选项多于1个时，就展示程序选择列表
        intent = Intent.createChooser(intent, getString(R.string.send_report));
        startActivity(intent);*/

        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
        intentBuilder.setType("text/palin")
                .setText(getCrimeReport())
                .setSubject(getString(R.string.crime_report_subject))
                .setChooserTitle(R.string.send_report)
                .startChooser();
    }

    /**
     * 获取联系人
     */
    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        /*
        配上createChooser也可以防止崩溃
        intent.addCategory(Intent.CATEGORY_HOME);
        intent = Intent.createChooser(intent, "请选择：");*/
        //判断是否有符合条件的应用，如果找不到匹配，应用会崩溃
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null){
            return;
        }
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    /**
     * 获取报告的内容
     * @return
     */
    private String getCrimeReport() {
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(TextUtils.isEmpty(suspect)){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString,
                solvedString, suspect);
        return report;
    }

}
