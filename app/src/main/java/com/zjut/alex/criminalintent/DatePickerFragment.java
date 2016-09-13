package com.zjut.alex.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
	private static final String ARG_DATE = "date";

	public static final String EXTRA_DATE = "com.zjut.alex.criminalintent.date";

	private DatePicker mDatePicker;

	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_DATE, date);

		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//获取Date对象并初始化DatePicker
		Date date = (Date) getArguments().getSerializable(ARG_DATE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_date_picker, null);

		mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
		mDatePicker.init(year, month, day, null);

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.date_picker_title)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						int year = mDatePicker.getYear();
						int month = mDatePicker.getMonth();
						int day = mDatePicker.getDayOfMonth();
						Date date1 = new GregorianCalendar(year, month, day).getTime();
						sendResult(Activity.RESULT_OK, date1);
					}
				})
				.create();
	}

//	@Nullable
//	@Override
//	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
//			savedInstanceState) {
//		//获取Date对象并初始化DatePicker
//		Date date = (Date) getArguments().getSerializable(ARG_DATE);
//
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
//		int year = calendar.get(Calendar.YEAR);
//		int month = calendar.get(Calendar.MONTH);
//		int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_date_picker, null);
//
//		mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
//		mDatePicker.init(year, month, day, null);
//
//		Button mOKButton = (Button) v.findViewById(R.id.dialog_date_ok_button);
//		mOKButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				int year = mDatePicker.getYear();
//				int month = mDatePicker.getMonth();
//				int day = mDatePicker.getDayOfMonth();
//				Date date1 = new GregorianCalendar(year, month, day).getTime();
//				sendResult(Activity.RESULT_OK, date1);
//				dismiss();
//			}
//		});
//
//		return v;
//	}

	private void sendResult(int resultCode, Date date) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, date);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}
}
