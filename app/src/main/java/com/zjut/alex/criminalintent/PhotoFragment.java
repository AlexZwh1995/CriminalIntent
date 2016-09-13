package com.zjut.alex.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import java.io.File;

/**
 * Created by Alex on 2016/7/14 0014.
 */
public class PhotoFragment extends DialogFragment {
	private static final String ARG_PHOTO = "photo";

	private ImageView mPhotoView;

	public static PhotoFragment newInstance(File photoFile) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_PHOTO, photoFile);

		PhotoFragment fragment = new PhotoFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		File photoFile = (File) getArguments().getSerializable(ARG_PHOTO);
		Bitmap image = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());

		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo, null);

		mPhotoView = (ImageView)v.findViewById(R.id.dialog_photo_view);
		mPhotoView.setImageBitmap(image);

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.create();
	}
}
