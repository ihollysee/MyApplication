package com.holly.recyclerview.info;

import android.support.annotation.NonNull;

/**
 * 实景地点区域信息
 */
public class RealSceneLocationAreaInfo {
    private final static String AREA_NAME = "area_name";
    private final static String AREA_ID = "area_id";
    private final static String ADCODE = "adcode";

    private String mAreaName;
    private String mAreaId;
    private String mAdCode;

    private int mViewType = VIEW_TYPE_NORMAL;
    public static final int VIEW_TYPE_NORMAL = 0x1001;
    public static final int VIEW_TYPE_EMPTY = 0x1002;
    public static final int VIEW_TYPE_LOADING = 0x1003;


    public String getAreaName() {
        return mAreaName;
    }

    public String getAreaId() {
        return mAreaId;
    }

    public String getAdCode() {
        return mAdCode;
    }

    public int getmViewType() {
        return mViewType;
    }

    public void setmViewType(int mViewType) {
        this.mViewType = mViewType;
    }

    public void setmAreaName(String mAreaName) {
        this.mAreaName = mAreaName;
    }

    public void setmAreaId(String mAreaId) {
        this.mAreaId = mAreaId;
    }

    public void setmAdCode(String mAdCode) {
        this.mAdCode = mAdCode;
    }

    // TODO: 需要调整结构
    @NonNull
    public static RealSceneLocationAreaInfo getSceneAddressSpaceHolder() {
        RealSceneLocationAreaInfo address = new RealSceneLocationAreaInfo();
        address.mViewType = VIEW_TYPE_EMPTY;
        return address;
    }
}
