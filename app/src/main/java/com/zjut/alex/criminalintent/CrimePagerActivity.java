package com.zjut.alex.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {
	private static final String EXTRA_CRIME_ID = "com.zjut.alex.criminalintent.crime_id";

	private ViewPager mViewPager;
	private List<Crime> mCrimes;
	private boolean mSubtitleVisible;

	@Nullable
	@Override
	public Intent getParentActivityIntent() {
		return CrimeListActivity.newIntent(CrimePagerActivity.this, mSubtitleVisible);
	}

	public static Intent newIntent(Context packageContext, UUID crimeId, boolean showSubtitle) {
		Intent intent = new Intent(packageContext, CrimePagerActivity.class);
		intent.putExtra(EXTRA_CRIME_ID, crimeId);
		intent.putExtra(CrimeListFragment.SAVED_SUBTITLE_VISIBLE, showSubtitle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crime_pager);

		UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		mSubtitleVisible = getIntent().getBooleanExtra(CrimeListFragment
				.SAVED_SUBTITLE_VISIBLE, false);

		mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

		mCrimes = CrimeLab.get(this).getCrimes();

		FragmentManager fragmentManager = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
			@Override
			public Fragment getItem(int position) {
				Crime crime = mCrimes.get(position);
				return CrimeFragment.newInstance(crime.getId());
			}

			@Override
			public int getCount() {
				return mCrimes.size();
			}
		});

		for (int i = 0; i < mCrimes.size(); i++) {
			if (mCrimes.get(i).getId().equals(crimeId)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}
	}

	@Override
	public void onCrimeUpdated(Crime crime) {

	}

	@Override
	public void onCrimeRemoved() {
		finish();
	}
}