package com.holly.recyclerview.listener;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.holly.recyclerview.info.RealSceneLocationAreaInfo;
import com.holly.recyclerview.adapter.SceneAddressRecyclerAdapter;

/**
 * Created by hongliang.xie on 2015/11/11.
 */
public class RealSceneAddressOnScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = RealSceneAddressOnScrollListener.class.getSimpleName();
    private int dxSum;
    private SceneAddressRecyclerAdapter mSceneAddressAdapter;
    private int mScreenWidth;

    public RealSceneAddressOnScrollListener(SceneAddressRecyclerAdapter addressAdapter, int screenWidth) {
        mSceneAddressAdapter = addressAdapter;
        mScreenWidth = screenWidth;
    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        Log.i(TAG, "ScrollState:" + newState);
        mSceneAddressAdapter.setScrollState(newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                dxSum = 0;
                mSceneAddressAdapter.scrollToMiddlePosition();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                mSceneAddressAdapter.loadDataMore();
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:

                break;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.i(TAG, "Scrolled,dx:" + dx + ",dy:" + dy);
        dxSum = dxSum + dx;
        int size = Math.abs(dxSum) / mSceneAddressAdapter.getItemWidth();
        int remaning = Math.abs(dxSum) % mSceneAddressAdapter.getItemWidth();
        int targetPosition;
        if (dxSum < 0) {
            if (remaning < mSceneAddressAdapter.getItemWidth() / 2) {
                targetPosition = mSceneAddressAdapter.getSelectedPosition() - size;

            } else {
                targetPosition = mSceneAddressAdapter.getSelectedPosition() - size - 1;

            }
        } else {
            if (remaning < mSceneAddressAdapter.getItemWidth() / 2) {
                targetPosition = mSceneAddressAdapter.getSelectedPosition() + size;

            } else {
                targetPosition = mSceneAddressAdapter.getSelectedPosition() + size + 1;

            }

        }
        if (targetPosition < mSceneAddressAdapter.getFristAviableItemPosition()) {
            targetPosition = mSceneAddressAdapter.getFristAviableItemPosition();
        }
        if (targetPosition > mSceneAddressAdapter.getLastAviableItemPosition()) {
            targetPosition = mSceneAddressAdapter.getLastAviableItemPosition();
        }
        RealSceneLocationAreaInfo areaInfo = mSceneAddressAdapter.getItem(targetPosition);
//        Log.i(TAG, "targetPosition:" + targetPosition + "," + areaInfo.getAreaName());
        if (mSceneAddressAdapter.getSceneAddressChangedListener() != null) {
            mSceneAddressAdapter.getSceneAddressChangedListener().onScroll(areaInfo, RecyclerView.SCROLL_STATE_DRAGGING
                    | RecyclerView.SCROLL_STATE_SETTLING);
        }
    }

}
