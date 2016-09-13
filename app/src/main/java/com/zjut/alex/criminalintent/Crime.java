package com.zjut.alex.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Alex on 2016/6/22 0022.
 */
public class Crime {
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private String mSuspect;
	private long mContactId;

	public Crime() {
		//Generate unique identifier
		this(UUID.randomUUID());
	}

	public Crime(UUID id) {
		mId = id;
		mDate = new Date();
	}

	public UUID getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public String getSuspect() {
		return mSuspect;
	}

	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}

	public long getContactId() {
		return mContactId;
	}

	public void setContactId(long contactId) {
		mContactId = contactId;
	}

	//添加文件名获取方法
	public  String getPhotoFilename() {
		return "IMG_" + getId().toString() + ".jpg";
	}
}
