package com.mi.www.criminalintent;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerDialogFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "result_date";
    private DatePicker mDatePicker;

   /* public DatePickerDialogFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker_dialog, container, false);
    }*/

//   CrimeFragment向DatePickerDialogFragment传递数据
   public static DatePickerDialogFragment newInstance(Date date){
       Bundle bundle = new Bundle();
       bundle.putSerializable(ARG_DATE,date);
       DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
       datePickerDialogFragment.setArguments(bundle);
       return datePickerDialogFragment;
   }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_date_picker_dialog,null);
        mDatePicker = view.findViewById(R.id.date_picker);
        mDatePicker.init(year,month,day,null);
        /*
        * android.app.AlertDialog;  系统标准AlertDialog，在5.0之前的系统上显示很丑
        * android.support.v7.app.AlertDialog;   支持库的AlertDialog，可以保证在老系统上也是5.0之后的效果
        * */
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("请选择日期")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();
    }

    /**
     * 给CrimeFragment返回Date数据
     * @param resultCode
     * @param date
     */
    public void sendResult(int resultCode,Date date){
       if(getTargetFragment() == null){
           return;
       }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
