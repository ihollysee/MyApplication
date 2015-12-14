package com.holly.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.holly.recyclerview.adapter.SceneAddressRecyclerAdapter;
import com.holly.recyclerview.info.RealSceneLocationAreaInfo;
import com.holly.recyclerview.listener.RealSceneAddressOnScrollListener;
import com.holly.user.activity.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SceneAddressRecyclerAdapter mSceneAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAlarm();
        initSceneAddressAdapter();
        initSceneAddressRecyclerView();
        initData();
        mSceneAddressAdapter.setSelectedPosition(mSceneAddressAdapter.getSpaceHolderSize());
    }

    private void initData() {
        mSceneAddressAdapter.setList(getTestData());
        mSceneAddressAdapter.notifyDataSetChanged();
    }

    private List<RealSceneLocationAreaInfo> getTestData() {
        List<RealSceneLocationAreaInfo> areaInfoList = new ArrayList<>();

        for (int j = 0; j < 10; j++) {
            RealSceneLocationAreaInfo info = new RealSceneLocationAreaInfo();
            info.setmViewType(RealSceneLocationAreaInfo.VIEW_TYPE_NORMAL);
            info.setmAreaName("No." + j);
            areaInfoList.add(info);
        }


        return areaInfoList;
    }


    private void initSceneAddressAdapter() {
        mSceneAddressAdapter = new SceneAddressRecyclerAdapter(getApplicationContext());
        mSceneAddressAdapter.setScreenWidth(getScreenWidth());
        int mSpaceHolderSize = mSceneAddressAdapter.initSpaceHolderSize();
        mSceneAddressAdapter.initOffset(mSpaceHolderSize);
        mSceneAddressAdapter.setSceneAddressChangedListener(getSceneAddressChangedListener());
    }

    private void initSceneAddressRecyclerView() {
        View rootView = findViewById(R.id.root_view);
        if (rootView != null) {
            RecyclerView mSceneAddressRecyclerView = (RecyclerView) rootView.findViewById(R.id.scene_address_list);
            mSceneAddressRecyclerView.setLayoutManager(mSceneAddressAdapter.getLayoutManager());
            mSceneAddressRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mSceneAddressRecyclerView.setHasFixedSize(true);
            mSceneAddressRecyclerView.setAdapter(mSceneAddressAdapter);
            RealSceneAddressOnScrollListener scrollListener = new RealSceneAddressOnScrollListener(
                    mSceneAddressAdapter, getScreenWidth());
            mSceneAddressRecyclerView.setOnScrollListener(scrollListener);
            mSceneAddressAdapter.scrollToPosition(0);
        }
    }


    private SceneAddressRecyclerAdapter.OnSceneAddressChangedListener getSceneAddressChangedListener() {

        return new SceneAddressRecyclerAdapter.OnSceneAddressChangedListener() {
            @Override
            public void onClick(RealSceneLocationAreaInfo address) {

            }

            @Override
            public void onScroll(RealSceneLocationAreaInfo address, int state) {

            }

            @Override
            public void onDataLoading() {// 触发数据加载

            }

            @Override
            public void onDataNoMore() {
                Toast.makeText(MainActivity.this, "has no more data", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private int getScreenWidth() {
        ScreenSize size = getScreenSize(this);
        return Math.min(size.width, size.height);
    }

    private ScreenSize getScreenSize(Activity context) {

        DisplayMetrics dm = new DisplayMetrics();

        context.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;

        int screenHeigh = dm.heightPixels;

        ScreenSize mScreenSize = new ScreenSize();
        mScreenSize.width = screenWidth;
        mScreenSize.height = screenHeigh;
        return mScreenSize;

    }

    public static class ScreenSize {
        public int width;
        public int height;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent("FOOFOOFOO");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long firstTime = SystemClock.elapsedRealtime() + 1000;
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10 * 1000, pendingIntent);
    }

}
