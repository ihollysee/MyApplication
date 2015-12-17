package com.holly.inject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.holly.inject.ContentView;
import com.holly.inject.ContentWidget;
import com.holly.inject.util.InjectUtils;
import com.holly.user.activity.R;

/**
 * 
 * @author hongliang.xie Android使用自定义注释来初始化控件
 *         http://blog.csdn.net/lee_duke/article/details/39505631
 */
@ContentView(R.layout.main_activity)
public class InjectActivity extends Activity {

	@ContentWidget(R.id.hello)
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main_activity);
		InjectUtils.injectObject(this, this);

		textView.setText("自定义注释");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
