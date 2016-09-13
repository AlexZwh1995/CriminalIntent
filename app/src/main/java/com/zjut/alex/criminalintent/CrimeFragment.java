package com.zjut.alex.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Alex on 2016/6/22 0022.
 */
public class CrimeFragment extends Fragment {
	private static final String ARG_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "DialogDate";
	private static final String DIALOG_PHOTO = "DialogPhoto";

	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_CONTACT = 1;
	private static final int REQUEST_PHOTO = 2;

	private Crime mCrime;
	private File mPhotoFile;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private Button mReportButton;
	private Button mSuspectButton;
	private Button mCallButton;
	private ImageView mPhotoView;
	private ImageButton mPhotoButton;
	private Callbacks mCallbacks;

	/*
	Required interface for hosting activities.
	 */
	public interface Callbacks {
		void onCrimeUpdated(Crime crime);
		void onCrimeRemoved();
	}

	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_CRIME_ID, crimeId);

		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mCallbacks = (Callbacks) context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

		setHasOptionsMenu(true);
	}

	@Override
	public void onPause() {
		super.onPause();

		CrimeLab.get(getActivity()).updateCrime(mCrime);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);

		//Crime title
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				//This space intentionally left blank
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				mCrime.setTitle(charSequence.toString());
				updateCrime();
			}

			@Override
			public void afterTextChanged(Editable editable) {
				//This one too
			}
		});

		//Date button
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fragmentManager, DIALOG_DATE);
			}
		});

		//Solved check button
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				//set the crime's solved property
				mCrime.setSolved(b);
				updateCrime();
			}
		});

		//Report button
		mReportButton = (Button) v.findViewById(R.id.crime_report);
		mReportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = ShareCompat.IntentBuilder.from(getActivity())
						.setType("text/plain")
						.setText(getCrimeReport())
						.setSubject(getString(R.string.crime_report_subject))
						.setChooserTitle(getString(R.string.send_report))
						.createChooserIntent();
				startActivity(intent);
			}
		});

		//Suspect button
		final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts
				.CONTENT_URI);
		mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(pickContact, REQUEST_CONTACT);
			}
		});
		if (mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
		}

		//Call button
		mCallButton = (Button) v.findViewById(R.id.call_suspect);
		if (mCrime.getSuspect() == null) {
			mCallButton.setEnabled(false);
		}
		mCallButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
				String selectClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

				String[] queryFields = {ContactsContract.CommonDataKinds.Phone.NUMBER};
				String[] selectParams = {Long.toString(mCrime.getContactId())};

				Cursor cursor = getActivity().getContentResolver().query(contentUri, queryFields,
						selectClause, selectParams, null);

				if (cursor != null && cursor.getCount() > 0) {
					try {
						cursor.moveToFirst();
						String number = cursor.getString(0);
						Uri phoneNumber = Uri.parse("tel:" + number);

						Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
						startActivity(intent);
					} finally {
						cursor.close();
					}
				}
			}
		});

		//检查是否存在联系人应用
		PackageManager packageManager = getActivity().getPackageManager();
		if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) ==
				null) {
			mSuspectButton.setEnabled(false);
		}

		//设置照相按钮
		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
		final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager)
				!= null;
		mPhotoButton.setEnabled(canTakePhoto);

		if (canTakePhoto) {
			Uri uri = Uri.fromFile(mPhotoFile);
			captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}

		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(captureImage, REQUEST_PHOTO);
			}
		});

		//设置照片缩略图
		mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
		updatePhotoView();
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPhotoFile != null && mPhotoFile.exists()) {
					FragmentManager fragmentManager = getFragmentManager();
					PhotoFragment dialog = PhotoFragment.newInstance(mPhotoFile);
					dialog.show(fragmentManager, DIALOG_PHOTO);
				}
			}
		});

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_remove_crime:
				CrimeLab.get(getActivity()).removeCrime(mCrime);
				mCallbacks.onCrimeRemoved();
				updateCrime();
				Toast.makeText(getActivity(), "Crime has been removed!", Toast.LENGTH_SHORT)
						.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateCrime();
			updateDate();
		} else if (requestCode == REQUEST_CONTACT && data != null) {
			Uri contactUri = data.getData();
			//Specify which fields you want your query to return values for.
			String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME,
					ContactsContract.Contacts._ID};
			//Perform your query - the contactUri is like a "where" clause here
			Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields,
					null, null, null);

			try {
				//Double-check that you actually got results
				if (cursor.getCount() == 0) {
					return;
				}

				//Pull out the first column of the first row of data that is your suspect's name
				// and id.
				cursor.moveToFirst();
				String suspect = cursor.getString(0);
				long contactId = cursor.getLong(1);
				mCrime.setSuspect(suspect);
				mCrime.setContactId(contactId);
				updateCrime();
				mSuspectButton.setText(suspect);
				mCallButton.setEnabled(true);
			} finally {
				cursor.close();
			}
		} else if (requestCode == REQUEST_PHOTO) {
			updateCrime();
			updatePhotoView();
		}
	}

	private void updateCrime() {
		CrimeLab.get(getActivity()).updateCrime(mCrime);
		mCallbacks.onCrimeUpdated(mCrime);
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	private String getCrimeReport() {
		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		}

		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}

		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString,
				solvedString, suspect);

		return report;
	}

	private void updatePhotoView() {
		if (mPhotoFile == null || !mPhotoFile.exists()) {
			mPhotoView.setImageDrawable(null);
		} else {
			Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
			mPhotoView.setImageBitmap(bitmap);
		}
	}
}
