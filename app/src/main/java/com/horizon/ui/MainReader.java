package com.horizon.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.R;
import com.horizon.dao.BookInfoDao;
import com.horizon.dao.ConfigDao;
import com.horizon.model.Book;

import java.io.IOException;
import java.text.SimpleDateFormat;
//import static com.horizon.ui.MarkManager.single;

public class MainReader extends Activity {
	/** Called when the activity is first created. */
	private PageWidget mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	boolean is_Horizontal;
	
	Book mBook;
	BookInfoDao mBookInfo;
	ConfigDao mConfig;
	//菜单

	private int setTitle;
//	private int tmpe = -1;
//	private boolean isSelect = true;    
	
    private static final int REQUEST = 0;
	private static final int RESULT = 0;


	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mPageWidget = new PageWidget(this);
		setContentView(mPageWidget); 
		
		mConfig = new ConfigDao(MainReader.this);
		mBookInfo= new BookInfoDao(MainReader.this);
		
		int id = this.getIntent().getExtras().getInt("id");
		mBook = mBookInfo.getABook(id);

		DisplayMetrics mDM = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(mDM);
	    int W = mDM.widthPixels;
	    int H = mDM.heightPixels;
	    
	    Log.i("", "W is " + Integer.toString(W));
	    Log.i("", "H is " + Integer.toString(H));
	    
	    mPageWidget.setScreen(W, H);
	    
		// 若是要修改分辨率的话， 请自己手动该 480 800 两个值。
		mCurPageBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
		//
		// 两画布
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		pagefactory = new BookPageFactory(MainReader.this, W, H);
		// 设置一张背景图片
		//pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg));
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			is_Horizontal = true;
		} else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			is_Horizontal = false;
		}
//		pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg), W, H, is_Horizontal);
		
//		pagefactory.setBgBitmap(W, H, is_Horizontal);
		
		try {
			pagefactory.openbook(mBook);// 打开文件 获取到一个缓存
//			pagefactory.openbook(bookfillPath);
			pagefactory.onDraw(mCurPageCanvas);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Toast.makeText(this, "电子书不存在,请将《test.txt》放在SD卡根目录下", Toast.LENGTH_SHORT).show();
		}

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
		mPageWidget.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub

				boolean ret = false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(e.getX(), e.getY());

						pagefactory.onDraw(mCurPageCanvas);
						if (mPageWidget.DragToRight()) {// 右边点击的时候为false; 前一页
							try {
								pagefactory.prePage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pagefactory.isfirstPage())
								return false;
							pagefactory.onDraw(mNextPageCanvas);
						} else {
							try {
								pagefactory.nextPage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pagefactory.islastPage())
								return false;
							pagefactory.onDraw(mNextPageCanvas);
						}
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}

					ret = mPageWidget.doTouchEvent(e);
					return ret;
				}
				return false;
			}

		});


	}

	
	private void changeReadMode() {
		if(mConfig.getInNight()) {
			//yejian
			mConfig.setBackColour(0xff000000);
			mConfig.setTxtColour(0xff444644);
			mConfig.setInNight(false);
		} else {
			//rijian
			mConfig.setBackColour(0xfffed189);
			mConfig.setTxtColour(0xff000000);
			mConfig.setInNight(true);
		}
		pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
		try {
			pagefactory.nextPage();
			pagefactory.onDraw(mCurPageCanvas);
			pagefactory.onDraw(mNextPageCanvas);
			mPageWidget.invalidate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void changeTextSize() {
		AlertDialog.Builder ad = new AlertDialog.Builder(MainReader.this);
		ad.setTitle("修改字体大小").setMessage("请选择操作:");
		ad.setPositiveButton("放大", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int size = pagefactory.getM_fontSize();
				int newsize = size + 2;
				mConfig.setTxtSize(newsize);
				pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
				try {
					pagefactory.clearM_lines();
					pagefactory.onDraw(mCurPageCanvas);
					pagefactory.onDraw(mNextPageCanvas);
					mPageWidget.invalidate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).setNegativeButton("缩小", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int size1 = pagefactory.getM_fontSize();
				int newsize1 = size1 - 2;
				mConfig.setTxtSize(newsize1);
				
				pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
				try {
					pagefactory.clearM_lines();
					pagefactory.onDraw(mCurPageCanvas);
					pagefactory.onDraw(mNextPageCanvas);
					mPageWidget.invalidate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).show();
	}

	
	
	// 改变横竖屏时
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

		Log.i("Test", "this is onConfigurationChanged");

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		// 横屏时
		Log.i("Test", "this is ORIENTATION_LANDSCAPE");
			is_Horizontal = true;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏时
			Log.i("Test", "this is ORIENTATION_PORTRAIT");
			is_Horizontal = false;
		}

		// 检测实体键盘的状态：推出或者合上
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			// 实体键盘处于推出状态，在此处添加额外的处理代码
			Log.i("Test", "this is HARDKEYBOARDHIDDEN_NO");			
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			// 实体键盘处于合上状态，在此处添加额外的处理代码
			Log.i("Test", "this is HARDKEYBOARDHIDDEN_YES");
		}
		
	}
	
	
			
	//菜单
	
	//MainMenu监听器
    class TitleClickEvent implements OnItemClickListener{  

		@Override  
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                long arg3) {  
        	setTitle=arg2;
        		switch (arg2) {
    			case 0:
    				if(mConfig.getInNight()) {
    					//yejian
    					mConfig.setBackColour(0xff000000);
    					mConfig.setTxtColour(0xff444644);
    					mConfig.setInNight(false);
    				} else {
    					//rijian
    					mConfig.setBackColour(0xfffed189);
    					mConfig.setTxtColour(0xff000000);
    					mConfig.setInNight(true);
    				}
    				pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
    				try {
    					pagefactory.nextPage();
    					pagefactory.onDraw(mCurPageCanvas);
    					pagefactory.onDraw(mNextPageCanvas);
    					mPageWidget.invalidate();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    		        break;
    			case 1:
    				int size = pagefactory.getM_fontSize();
    				int newsize = size + 2;
    				mConfig.setTxtSize(newsize);
    				pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
    				try {
    					pagefactory.clearM_lines();
    					pagefactory.onDraw(mCurPageCanvas);
    					pagefactory.onDraw(mNextPageCanvas);
    					mPageWidget.invalidate();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    	
    				break;
    			case 2:
    				int size1 = pagefactory.getM_fontSize();
    				int newsize1 = size1 - 2;
    				mConfig.setTxtSize(newsize1);
    				
    				pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
    				try {
    					pagefactory.clearM_lines();
    					pagefactory.onDraw(mCurPageCanvas);
    					pagefactory.onDraw(mNextPageCanvas);
    					mPageWidget.invalidate();
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				break;

    			default:
    				break;
    			}

        }

    } 
    
	@Override
	//结果返回处理
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST) {
			if (resultCode == RESULT) {
//				Bundle bundle = data.getExtras();
//				mBook = mBookInfo.getABook(Integer.getInteger(bundle.getString("bId")));
				pagefactory.setM_mbBufBegin(mBookInfo.getReadRate(mBook.getId()));
				pagefactory.setM_mbBufEnd(mBookInfo.getReadRate(mBook.getId()));
//				pagefactory.setM_mbBufEnd(pagefactory.getM_mbBufBegin());
				pagefactory.clearM_lines();
				pagefactory.onDraw(mCurPageCanvas);
				pagefactory.onDraw(mNextPageCanvas);
				mPageWidget.invalidate();
				Log.i("onActivityResult", mBook.toString());		
			}
		}
	}
	
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	
//    	menu.add("menu");
//    	return super.onCreateOptionsMenu(menu);
//    }
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
    	
//        if (myMenu != null) {
//            if (myMenu.isShowing()) {
//            	myMenu.dismiss(); 
//            	myMenu.setTitleSelect(-1);
//            }
//            else {  
//            	myMenu.setTitleSelect(-1);
//            	myMenu.showAtLocation(this.mPageWidget,  
//                        Gravity.BOTTOM, 0, 0);  
//            }  
//        }  
//        return false;
    	return super.onMenuOpened(featureId, menu);
    }
    
}