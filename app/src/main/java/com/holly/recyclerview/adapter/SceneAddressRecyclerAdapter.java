package com.holly.recyclerview.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.holly.recyclerview.info.RealSceneLocationAreaInfo;
import com.holly.user.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongliang.xie on 2015/11/5.
 */
public class SceneAddressRecyclerAdapter extends RecyclerView.Adapter<SceneAddressRecyclerAdapter.ViewHolder> {

    private static final String TAG = SceneAddressRecyclerAdapter.class.getSimpleName();
    private OnSceneAddressChangedListener mAddressChangedListener;

    private List<RealSceneLocationAreaInfo> mAddressList;
    protected Context mCtx;

    private int mOldSelectedPosition = 0;
    private int mSelectedPosition = 0;
    private LinearLayoutManager mLinearLayoutManager;

    private int mOffset;
    private int mSpaceHolderSizeAtStart = 0;//列表头部的空白占位
    private int mSpaceHolderSizeAtEnd = 0;//列表尾部的空白占位
    private int mScreenWidth;

    private boolean hasMoreData = true;

    public static final int NO_CHANGE = 99;
    private int mMiddleItemPosition;

    public SceneAddressRecyclerAdapter(Context context) {
        mCtx = context;
        initLinearLayoutManager();
    }


    public void setHasMoreData(boolean moreData) {
        hasMoreData = moreData;
        if (!hasMoreData) {
            int startPostion = getItemCount() - 1;
            addAddressSpaceHolderAtEnd(getSpaceHolderSize());
            this.notifyItemRangeChanged(startPostion, getItemCount());
        }

    }

    public boolean hasMoreData() {
        return hasMoreData;
    }

    public void setList(@NonNull List<RealSceneLocationAreaInfo> list) {

        List<RealSceneLocationAreaInfo> addressList = addAddressSpaceHolderAtStart();

        addressList.addAll(list);
        mAddressList = addressList;

//        if (hasMoreData) {
//            if (list.size() < getSpaceHolderSize() + 1) {//地点少，无法填充满,当前地点无法据屏幕中央
//                addAddressSpaceHolderAtEnd((getSpaceHolderSize() + 1) - list.size());
//            }
//        } else {
        addAddressSpaceHolderAtEnd(getSpaceHolderSize());
//        }
    }

    @NonNull
    private List<RealSceneLocationAreaInfo> addAddressSpaceHolderAtStart() {
        List<RealSceneLocationAreaInfo> addressList = getAddressSpaceHolderList(mSpaceHolderSizeAtStart);
        return addressList;
    }

    public void addAddressSpaceHolderAtEnd(int spaceHolderSize) {

        List<RealSceneLocationAreaInfo> addressList = getAddressSpaceHolderList(spaceHolderSize);

        if (mAddressList != null) {
            mAddressList.addAll(addressList);
            mSpaceHolderSizeAtEnd = addressList.size();
        }

    }

    @NonNull
    private List<RealSceneLocationAreaInfo> getAddressSpaceHolderList(int spaceHolderSize) {
        List<RealSceneLocationAreaInfo> addressList = new ArrayList<>();
        for (int i = 0; i < spaceHolderSize; i++) {
            addressList.add(RealSceneLocationAreaInfo.getSceneAddressSpaceHolder());
        }
        return addressList;
    }

    public void addLoadingAreaInfo() {
        if (mAddressList != null) {
            mAddressList.get(getItemCount() - 1).setmViewType(RealSceneLocationAreaInfo.VIEW_TYPE_LOADING);
            this.notifyItemChanged(getItemCount() - 1);
            mDataState = DATA_STATE_LOADING;
        }
    }

    /**
     * 移除loading的itemView
     */
    public void removeLoadingAreaInfo() {
        if (mAddressList != null) {
            mDataState = DATA_STATE_NORMAL;
            int position = getItemCount() - 1;
            mAddressList.get(position).setmViewType(RealSceneLocationAreaInfo.VIEW_TYPE_EMPTY);
            this.notifyItemChanged(position);

            scrollToMiddlePosition();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.real_scene_address_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view, this, mCtx);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mAddressList != null && position < mAddressList.size()) {
            RealSceneLocationAreaInfo address = mAddressList.get(position);
            holder.name.setText(address.getAreaName());

            if (mScrollState == RecyclerView.SCROLL_STATE_DRAGGING || mScrollState == RecyclerView.SCROLL_STATE_SETTLING || mDataState == DATA_STATE_LOADING) {
                if (address.getmViewType() == RealSceneLocationAreaInfo.VIEW_TYPE_EMPTY) {
                    holder.onEmpty();
                } else if (address.getmViewType() == RealSceneLocationAreaInfo.VIEW_TYPE_LOADING) {
                    holder.onLoading();
                } else {
                    holder.onSelected(false);
                }

            } else {
                if (address.getmViewType() == RealSceneLocationAreaInfo.VIEW_TYPE_EMPTY) {
                    holder.onEmpty();
                } else if (address.getmViewType() == RealSceneLocationAreaInfo.VIEW_TYPE_LOADING) {
                    holder.onLoading();
                } else {
                    holder.onSelected(position == mSelectedPosition);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return mAddressList == null ? 0 : mAddressList.size();
    }

    public RealSceneLocationAreaInfo getItem(int position) {
        return mAddressList == null || mAddressList.size() <= position ? null : mAddressList.get(position);
    }

    public int getPosition(RealSceneLocationAreaInfo address) {
        return mAddressList == null ? 0 : mAddressList.indexOf(address);
    }

    public int getPosition(String areaId) {
        //TODO xhl 性能优化  hashmap
        if (mAddressList == null) {
            return 0;
        }

        for (int i = 0; i < mAddressList.size(); i++) {
            RealSceneLocationAreaInfo info = mAddressList.get(i);
            if (info != null && TextUtils.equals(info.getAreaId(), areaId)) {
                return i;
            }
        }

        return 0;
    }

    /**
     * 设置当前项
     */
    public void setSelectedPosition(int position) {
        if (position < getItemCount()) {
            mSelectedPosition = position;

            notifyItemChanged(mSelectedPosition);
            notifyItemChanged(mOldSelectedPosition);

            mOldSelectedPosition = mSelectedPosition;
        }
    }

    public int getSelectedPosition() {
        return mOldSelectedPosition;
    }

    private void onItemClick(ViewHolder viewHolder) {
        if (viewHolder != null) {
            int position = viewHolder.getPosition();
            RealSceneLocationAreaInfo address = mAddressList.get(position);
            if (address != null && address.getmViewType() != RealSceneLocationAreaInfo.VIEW_TYPE_EMPTY && address.getmViewType() != RealSceneLocationAreaInfo.VIEW_TYPE_LOADING) {
                viewHolder.onSelected(true);
                mSelectedPosition = position;
                notifyItemChanged(mOldSelectedPosition);

                mOldSelectedPosition = mSelectedPosition;

                if (position < mAddressList.size() - getSpaceHolderSize()) {
                    scrollToPosition(position - mSpaceHolderSizeAtStart);
                } else {

                }
                if (mAddressChangedListener != null) {
                    mAddressChangedListener.onClick(address);
                }
            }
        }

    }

    public static final int DATA_STATE_NORMAL = 0x1001;
    public static final int DATA_STATE_LOADING = 0x1002;
    private int mDataState = DATA_STATE_NORMAL;

    public boolean isLoadingData() {
        return mDataState == DATA_STATE_LOADING;
    }

    public void setDataState(int state) {
        mDataState = state;
    }

    public int getDataState() {
        return mDataState;
    }

    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;

    public void setScrollState(int newState) {
        if (newState == mScrollState) {
            return;
        }
        mScrollState = newState;
        notifyItemChanged(mOldSelectedPosition);
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:

                break;
            case RecyclerView.SCROLL_STATE_IDLE:

                break;
            case RecyclerView.SCROLL_STATE_SETTLING:

                break;
        }
    }

    public int getItemWidth() {
        return mCtx.getResources().getDimensionPixelSize(R.dimen.real_scene_address_item_width);
//        return ResUtil.dipToPixel(mCtx, SCENE_ADDRESS_ITEM_WIDTH_IN_DIP);
    }

    public int initOffset(int spaceHolderSize) {
        int itemWidth = getItemWidth();
        mOffset = spaceHolderSize * itemWidth + itemWidth / 2 - mScreenWidth / 2;
        return mOffset;
    }

    public int getOffset() {
        return mOffset;
    }


    /**
     * 根据屏幕的宽度计算可以显示几个item
     */
    public int initSpaceHolderSize() {
        int itemWidth = getItemWidth();
        int spaceHolderWidth = mScreenWidth / 2 - itemWidth / 2;
        if (spaceHolderWidth % itemWidth == 0) {
            mSpaceHolderSizeAtStart = spaceHolderWidth / itemWidth;
        } else {
            mSpaceHolderSizeAtStart = spaceHolderWidth / itemWidth + 1;
        }
        return mSpaceHolderSizeAtStart;
    }

    public int getSpaceHolderSize() {
        return mSpaceHolderSizeAtStart;
    }

    public int getFristAviableItemPosition() {
        return mSpaceHolderSizeAtStart;
    }

    public int getLastAviableItemPosition() {
        return (getItemCount() - mSpaceHolderSizeAtEnd) - 1;
    }

    public void setScreenWidth(int screenWidth) {
        mScreenWidth = screenWidth;
    }

    public void scrollToPosition(int position) {
        mLinearLayoutManager.scrollToPositionWithOffset(position, -mOffset);
    }

    /**
     * 得到中间地点的position
     */
    public int getMiddleItemPosition() {
        int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();

        int firstCompletelyVisibleItemPosition = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisibleItemPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

        Log.i(TAG, "firstCompletelyVisibleItemPosition:" + firstCompletelyVisibleItemPosition
                + ",firstVisibleItemPosition:" + firstVisibleItemPosition
                + ",lastCompletelyVisibleItemPosition:" + lastCompletelyVisibleItemPosition
                + ",lastVisibleItemPosition:" + lastVisibleItemPosition);

        View firstCompletelyVisible = mLinearLayoutManager
                .findViewByPosition(firstCompletelyVisibleItemPosition);
        View firstVisible = mLinearLayoutManager.findViewByPosition(firstVisibleItemPosition);

        float firstCompletelyX = firstCompletelyVisible.getX();
        float firstVisibleX = firstVisible.getX();
        Log.i(TAG, "firstCompletelyX:" + firstCompletelyX + ",firstVisibleX:" + firstVisibleX);

        Rect firstVisibleRect = new Rect();
        firstVisible.getLocalVisibleRect(firstVisibleRect);

        int middleItemPosition = firstVisibleItemPosition
                + (lastVisibleItemPosition - firstVisibleItemPosition) / 2;
        boolean hasTwo = ((lastVisibleItemPosition - firstVisibleItemPosition) % 2 != 0);

        if (hasTwo) {
            View firstMiddleItem = mLinearLayoutManager.findViewByPosition(middleItemPosition);
            View secondMiddleItem = mLinearLayoutManager.findViewByPosition(middleItemPosition + 1);

            Rect firstMiddleRect = new Rect();
            firstMiddleItem.getGlobalVisibleRect(firstMiddleRect);
            Rect secondMiddleRect = new Rect();
            secondMiddleItem.getGlobalVisibleRect(secondMiddleRect);

            if (Math.abs(firstMiddleRect.left - mScreenWidth / 2) > Math.abs(secondMiddleRect.left
                    - mScreenWidth / 2)) {
                middleItemPosition = middleItemPosition + 1;
            }

            int lastAviliableItemPosition = getLastAviableItemPosition();
            int fristAviableItemPosition = getFristAviableItemPosition();
            if (middleItemPosition > lastAviliableItemPosition) {
                middleItemPosition = lastAviliableItemPosition;
            } else if (middleItemPosition < fristAviableItemPosition) {
                middleItemPosition = fristAviableItemPosition;
            }

        }
        return middleItemPosition;
    }

    /**
     * 滑动到中间的地点
     */
    public void scrollToMiddlePosition() {
        int middleItemPosition = getMiddleItemPosition();

//        int rightCout = getItemCount() - (middleItemPosition + 1);//屏幕中间右侧的item数量
//        if (rightCout < getSpaceHolderSize()) {
//            int endSpaceHolderSize = getSpaceHolderSize() - rightCout;
//            addAddressSpaceHolderAtEnd(endSpaceHolderSize);
//            this.notifyItemRangeChanged(middleItemPosition, getItemCount());
//        }

        scrollToPosition(middleItemPosition - getSpaceHolderSize());
        setSelectedPosition(middleItemPosition);

        if (mMiddleItemPosition == middleItemPosition) {
            if (getSceneAddressChangedListener() != null) {
                getSceneAddressChangedListener().onScroll(getItem(middleItemPosition),
                        NO_CHANGE);
            }
            return;
        }

        mMiddleItemPosition = middleItemPosition;

        if (getSceneAddressChangedListener() != null) {
            getSceneAddressChangedListener().onScroll(getItem(middleItemPosition),
                    RecyclerView.SCROLL_STATE_IDLE);
        }
    }

    public void loadDataMore() {
        if (mAddressChangedListener != null) {
            if (!hasMoreData()) {
                mAddressChangedListener.onDataNoMore();
            } else if (getItemCount() == mLinearLayoutManager.findLastVisibleItemPosition() + 1 && !isLoadingData()) {//滑倒最后一个item加载数据
                mAddressChangedListener.onDataLoading();
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public ImageView addressIcon;
        private SceneAddressRecyclerAdapter recyclerAdapter;
        private Context ctx;

        public ViewHolder(View itemView, SceneAddressRecyclerAdapter adapter, Context context) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.scene_address_name);
            addressIcon = (ImageView) itemView.findViewById(R.id.scene_address_icon);
            recyclerAdapter = adapter;
            ctx = context;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            recyclerAdapter.onItemClick(this);
        }

        public void onSelected(boolean selected) {
            addressIcon.clearAnimation();
            addressIcon.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            if (selected) {
                ((RelativeLayout.LayoutParams) name.getLayoutParams()).setMargins(0, dipToPixel(ctx, 3), 0, 0);
                name.setTextColor(0x0091ff);
                name.setTextSize(14);
                addressIcon.setImageResource(R.drawable.scene_address_selected);
            } else {
                ((RelativeLayout.LayoutParams) name.getLayoutParams()).setMargins(0, dipToPixel(ctx, 9), 0, 0);
                name.setTextColor(0x666666);
                name.setTextSize(12);
                addressIcon.setImageResource(R.drawable.scene_address_normal);
            }

        }


        private void onLoading() {
            addressIcon.setImageResource(R.drawable.loading);
            startRotateAnimation(addressIcon);
            addressIcon.setVisibility(View.VISIBLE);
            name.setVisibility(View.GONE);
        }

        public void onEmpty() {
            addressIcon.clearAnimation();
            addressIcon.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
        }


        public void startRotateAnimation(View v) {
            RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(1200);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            v.startAnimation(rotateAnimation);
        }

        private int dipToPixel(Context context, int dipValue) {
            if (context == null) {
                return dipValue; // 原值返回
            }
            try {
                float pixelFloat = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, dipValue, context
                                .getResources().getDisplayMetrics());
                return (int) pixelFloat;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dipValue;
        }

    }

    private void initLinearLayoutManager() {
        mLinearLayoutManager = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);
    }

    public LinearLayoutManager getLayoutManager() {
        return mLinearLayoutManager;
    }

    public void setSceneAddressChangedListener(OnSceneAddressChangedListener listener) {
        //TODO xhl 何时反注册？防止内存泄漏
        mAddressChangedListener = listener;
    }

    public OnSceneAddressChangedListener getSceneAddressChangedListener() {
        return mAddressChangedListener;
    }

    public interface OnSceneAddressChangedListener {
        void onClick(RealSceneLocationAreaInfo address);

        void onScroll(RealSceneLocationAreaInfo address, int state);

        void onDataLoading();

        void onDataNoMore();
    }


}

