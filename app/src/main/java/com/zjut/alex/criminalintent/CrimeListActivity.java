package com.zjut.alex.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Alex on 2016/6/23 0023.
 */
public class CrimeListActivity extends SingleFragmentActivity
		implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
	@Override
	protected Fragment createFragment() {
		return CrimeListFragment.newInstance(getIntent().getBooleanExtra(CrimeListFragment
				.SAVED_SUBTITLE_VISIBLE, false));
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}

	public static Intent newIntent(Context packageContext, boolean showSubtitle) {
		Intent intent = new Intent(packageContext, CrimeListActivity.class);
		intent.putExtra(CrimeListFragment.SAVED_SUBTITLE_VISIBLE, showSubtitle);
		return intent;
	}

	@Override
	public void onCrimeSelected(Crime crime, boolean showSubtitle) {
		if (findViewById(R.id.detail_fragment_container) == null) {
			Intent intent = CrimePagerActivity.newIntent(this, crime.getId(), showSubtitle);
			startActivity(intent);
		} else {
			Fragment newDetail = CrimeFragment.newInstance(crime.getId());

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.detail_fragment_container, newDetail)
					.commit();
		}
	}

	@Override
	public void onCrimeUpdated(Crime crime) {
		CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container);
		listFragment.updateUI();
	}

	@Override
	public void onCrimeRemoved() {
		if (findViewById(R.id.detail_fragment_container) == null) {

		} else {
			FragmentManager fragmentManager = getSupportFragmentManager();
			CrimeFragment detail = (CrimeFragment) fragmentManager.findFragmentById(R.id
					.detail_fragment_container);
			fragmentManager.beginTransaction()
					.remove(detail)
					.commit();
		}
	}
}
