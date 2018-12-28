package com.horizon.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.horizon.R;
import com.horizon.dao.ConfigDao;

public class SplashScreen extends Activity {
	private Context context;
	private String paw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 全屏，隐藏通知栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_screen);

		context = this;
		ConfigDao configDao = new ConfigDao(context);
			new Handler().postDelayed(new Runnable() {
				public void run() {
					Intent intent = new Intent(SplashScreen.this,
							HomeT2Bookshelf.class);
					SplashScreen.this.startActivity(intent); // 启动Main界面
					SplashScreen.this.finish(); // 关闭自己这个开场屏
				}
			}, 2000);

			// Intent intent = new Intent(SplashScreen.this,HomeT2Bookshelf.class);
			// startActivity(intent);
//			finish();
	}
}
