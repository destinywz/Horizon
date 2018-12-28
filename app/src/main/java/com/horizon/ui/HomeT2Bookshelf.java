 package com.horizon.ui;

 import android.graphics.Color;
 import android.app.Activity;
 import android.app.Dialog;
 import android.content.Context;
 import android.content.Intent;
 import android.os.Bundle;
 import android.support.v4.app.FragmentActivity;
 import android.support.v4.app.FragmentManager;
 import android.support.v4.app.FragmentTransaction;
 import android.view.KeyEvent;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.widget.AdapterView;
 import android.widget.AdapterView.OnItemLongClickListener;
 import android.widget.Button;
 import android.widget.CheckBox;
 import android.widget.EditText;
 import android.widget.GridView;
 import android.widget.ImageButton;
 import android.widget.ImageView;
 import android.widget.ListView;
 import android.widget.SimpleAdapter;
 import android.widget.TextView;
 import android.widget.Toast;
 import android.view.Window;

 import com.horizon.R;
 import com.horizon.adapter.BookshelfGVAdapter;
 import com.horizon.dao.BookInfoDao;
 import com.horizon.fragment.bookshelf_fragment;
 import com.horizon.fragment.mine_fragment;
 import com.horizon.fragment.readcity_fragment;
 import com.horizon.fragment.setting_fragment;

 import java.io.File;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

public class HomeT2Bookshelf extends FragmentActivity implements OnClickListener {


    private bookshelf_fragment bookshelfFragment;
    private readcity_fragment readcityFragment;
    private setting_fragment settingFragment;
    private mine_fragment mineFragment;

    private View shelfLayout;
    private View readcityLayout;
    private View settingLayout;
    private View mineLayout;


    private ImageView shelfImage;
    private ImageView readcityImage;
    private ImageView settingImage;
    private ImageView mineImage;

    private TextView shelfText;
    private TextView readcityText;
    private TextView settingText;
    private TextView mineText;


    private FragmentManager fragmentManager;





	private GridView gv_book;// 书架的gridView
	private TextView tv_classifyName;// 书架top bar上分类名称
	private ImageButton imgBtn_openMenuAc; //打开菜单

	private BookshelfGVAdapter bookshelfGVAdapter;// 书架的自定义adapter
	private SimpleAdapter classifyLVAdapter;// 分类里的adapter
	private List<Map<String, Object>> classifyLVList;// 分类列表的list数据集合（注意初始化后，请不要把它重新指向新的list）

	private BookInfoDao bookInfoDao;
	private Context context;

	public static final int ALLBOOK = -4; //全部图书
	// 当前书架显示的是哪个类别的图书，当书架显示某分类图书时，它等于分类编号，当书架显示最爱、最近阅读、全部、未分类时它等于<0的某标记
	public static int currentCategory;

	private long exitTime = 0;// 计算短时间内点击返回按钮的次数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_t2_bookshelf);
        // 初始化布局元素
        initViews();
        fragmentManager=getSupportFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);

		gv_book = (GridView) findViewById(R.id.gridView_book);
		tv_classifyName = (TextView) findViewById(R.id.tv_classifyName);
		imgBtn_openMenuAc = (ImageButton) findViewById(R.id.imgBtn_openFileSAct);
		
		bookInfoDao = new BookInfoDao(this);
		context = this;

		// 设置监听器
		imgBtn_openMenuAc.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(HomeT2Bookshelf.this, HomeT1Filebrowsing.class);
				startActivity(intent) ;
				startActivityForResult(intent, 1);
			}

		});


		classifyLVAdapter = new SimpleAdapter(context, classifyLVList, R.layout.classify_list_item, 
				new String[] { "name" }, new int[] { R.id.tv_classifyName });

		// ————设置书架
		bookshelfGVAdapter = new BookshelfGVAdapter(context, bookInfoDao.getAllBooks());
		gv_book.setAdapter(bookshelfGVAdapter);
		tv_classifyName.setText("全部图书");
		currentCategory = ALLBOOK;
	}



	/** 分类侧边栏的listView的item被长按的事件监听 */
	class Classify_lv_item_longClick implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
			 // 某分类被长按
				final HashMap<String, Object> itemMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
				final int classifyId = Integer.parseInt(String.valueOf(itemMap.get("id")));
				final String classifyName = String.valueOf(itemMap.get("name"));
				final int itemId = (int)id;

				final Dialog classifyDialog = new Dialog(context,R.style.HorizonDialog);
				classifyDialog.setContentView(R.layout.dialog_classify_longclick);
				classifyDialog.setCanceledOnTouchOutside(true);// 当点击对话框以外区域时，关闭对话框

				Button btn_delete = (Button) classifyDialog.findViewById(R.id.btn_delete);
				Button btn_rename = (Button) classifyDialog.findViewById(R.id.btn_rename);

				final int p = position;
				
				// 删除分类
				btn_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog deleteDialog = new Dialog(context, R.style.HorizonDialog);
						deleteDialog.setContentView(R.layout.dialog_delete);

						Button btn_ok = (Button) deleteDialog.findViewById(R.id.btn_ok);
						Button btn_cancel = (Button) deleteDialog.findViewById(R.id.btn_cancel);
						final CheckBox checkBox_deleteSource = (CheckBox) deleteDialog.findViewById(R.id.checkBox_deleteSourceFile);
						checkBox_deleteSource.setText("同时删除该分类下图书的源文件");

						btn_ok.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (checkBox_deleteSource.isChecked()) {
									// 同时删除该分类下图书的源文件
									List<String> addressList = bookInfoDao.getAllAddressInClassify(classifyId);
									for (int i = 0; i < addressList.size(); i++) {
										File file = new File(addressList.get(i));
										if (file.exists()) {
											file.delete();
										}
									}
								}

								
								//classifyLVList = bookInfoDao.getAllClassify();
								//classifyLVAdapter.notifyDataSetInvalidated();
								classifyLVList.remove((int)itemId);
								classifyLVAdapter.notifyDataSetChanged();

								if (currentCategory == classifyId) {
									updateAllBook(ALLBOOK, "全部图书");
								}
								
								deleteDialog.cancel();
							}
						});

						btn_cancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								deleteDialog.cancel();
							}
						});

						deleteDialog.show();
						classifyDialog.cancel();
					}
				});

				// 重命名分类
				btn_rename.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog renameDialog = new Dialog(context,R.style.HorizonDialog);
						renameDialog.setContentView(R.layout.dialog_rename);

						Button btn_ok = (Button) renameDialog.findViewById(R.id.btn_ok);
						Button btn_cancel = (Button) renameDialog.findViewById(R.id.btn_cancel);
						final EditText et_newName = (EditText) renameDialog.findViewById(R.id.et_newName);

						btn_ok.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								String newNameStr = String.valueOf(et_newName.getText());
								if (newNameStr != "" && newNameStr != null) {
									itemMap.put("name", newNameStr);// 更新分类列表里List数据源的该项
									classifyLVAdapter.notifyDataSetChanged();// 更新分类列表的adapter
									tv_classifyName.setText(newNameStr);
									
									renameDialog.cancel();
								}
							}
						});

						btn_cancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								renameDialog.cancel();
							}
						});

						renameDialog.show();
						classifyDialog.cancel();
					}
				});

				classifyDialog.show();
			return true;
		}
	}

	/** 从其它页面返回到当前页面的处理*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == 1) { //从文件浏览页面返回
			updateAllBook(currentCategory, (String) tv_classifyName.getText());
		}
	}

	/**
	 * 更新书架上*所有*图书，显示某类别的图书 参数： int
	 * currentCategory：类别编号，它可以是分类编号，或者表示最爱、最近阅读、全部或未分类的标记 String
	 * currentCategoryName：类别名称
	 */
	public void updateAllBook(int currentCategory, String currentCategoryName) {
		switch (currentCategory) {

		case ALLBOOK:
			bookshelfGVAdapter.setList(bookInfoDao.getAllBooks());
			break;

		}

		tv_classifyName.setText(currentCategoryName);
		this.currentCategory = currentCategory;
		bookshelfGVAdapter.notifyDataSetInvalidated();
	}
	
	/** 为分类栏的listView添加HeaderView*/

	/** 点击返回按钮2次，退出应用程序 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(HomeT2Bookshelf.this, "再点击一次，退出应用程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}












    private void initViews() {
        shelfLayout = findViewById(R.id.shelf_layout);
        readcityLayout = findViewById(R.id.readcity_layout);
        settingLayout = findViewById(R.id.setting_layout);
        mineLayout = findViewById(R.id.mine_layout);
        shelfImage = (ImageView) findViewById(R.id.shelf_image);
        readcityImage = (ImageView) findViewById(R.id.readcity_image);
        settingImage = (ImageView) findViewById(R.id.setting_image);
        mineImage = (ImageView) findViewById(R.id.mine_image);
        shelfText = (TextView) findViewById(R.id.shelf_text);
        readcityText = (TextView) findViewById(R.id.readcity_text);
        mineText = (TextView) findViewById(R.id.mine_text);
        settingText = (TextView) findViewById(R.id.setting_text);
        shelfLayout.setOnClickListener(this);
        readcityLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shelf_layout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.readcity_layout:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.setting_layout:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.mine_layout:
                // 当点击了设置tab时，选中第4个tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                shelfImage.setImageResource(R.drawable.read2);
                shelfText.setTextColor(Color.WHITE);
                if (bookshelfFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    bookshelfFragment = new bookshelf_fragment();
                    transaction.add(R.id.content, bookshelfFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(bookshelfFragment);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                readcityImage.setImageResource(R.drawable.readcity2);
                readcityText.setTextColor(Color.WHITE);
                if (readcityFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    readcityFragment = new readcity_fragment();
                    transaction.add(R.id.content, readcityFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(readcityFragment);
                }
                break;
            case 2:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                settingImage.setImageResource(R.drawable.set2);
                settingText.setTextColor(Color.WHITE);
                if (settingFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    settingFragment = new setting_fragment();
                    transaction.add(R.id.content, settingFragment);
                } else {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(settingFragment);
                }
                break;
            case 3:
            default:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                mineImage.setImageResource(R.drawable.mine2);
                mineText.setTextColor(Color.WHITE);
                if (mineFragment == null) {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    mineFragment = new mine_fragment();
                    transaction.add(R.id.content, mineFragment);
                } else {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(mineFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        shelfImage.setImageResource(R.drawable.read);
        shelfText.setTextColor(Color.parseColor("#82858b"));
        readcityImage.setImageResource(R.drawable.readcity);
        readcityText.setTextColor(Color.parseColor("#82858b"));
        settingImage.setImageResource(R.drawable.set);
        settingText.setTextColor(Color.parseColor("#82858b"));
        mineImage.setImageResource(R.drawable.mine);
        mineText.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (bookshelfFragment != null) {
            transaction.hide(bookshelfFragment);
        }
        if (readcityFragment != null) {
            transaction.hide(readcityFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }
}