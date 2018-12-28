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





	private GridView gv_book;// ��ܵ�gridView
	private TextView tv_classifyName;// ���top bar�Ϸ�������
	private ImageButton imgBtn_openMenuAc; //�򿪲˵�

	private BookshelfGVAdapter bookshelfGVAdapter;// ��ܵ��Զ���adapter
	private SimpleAdapter classifyLVAdapter;// �������adapter
	private List<Map<String, Object>> classifyLVList;// �����б��list���ݼ��ϣ�ע���ʼ�����벻Ҫ��������ָ���µ�list��

	private BookInfoDao bookInfoDao;
	private Context context;

	public static final int ALLBOOK = -4; //ȫ��ͼ��
	// ��ǰ�����ʾ�����ĸ�����ͼ�飬�������ʾĳ����ͼ��ʱ�������ڷ����ţ��������ʾ�������Ķ���ȫ����δ����ʱ������<0��ĳ���
	public static int currentCategory;

	private long exitTime = 0;// �����ʱ���ڵ�����ذ�ť�Ĵ���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_t2_bookshelf);
        // ��ʼ������Ԫ��
        initViews();
        fragmentManager=getSupportFragmentManager();
        // ��һ������ʱѡ�е�0��tab
        setTabSelection(0);

		gv_book = (GridView) findViewById(R.id.gridView_book);
		tv_classifyName = (TextView) findViewById(R.id.tv_classifyName);
		imgBtn_openMenuAc = (ImageButton) findViewById(R.id.imgBtn_openFileSAct);
		
		bookInfoDao = new BookInfoDao(this);
		context = this;

		// ���ü�����
		imgBtn_openMenuAc.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(HomeT2Bookshelf.this, HomeT1Filebrowsing.class);
				startActivity(intent) ;
				startActivityForResult(intent, 1);
			}

		});


		classifyLVAdapter = new SimpleAdapter(context, classifyLVList, R.layout.classify_list_item, 
				new String[] { "name" }, new int[] { R.id.tv_classifyName });

		// ���������������
		bookshelfGVAdapter = new BookshelfGVAdapter(context, bookInfoDao.getAllBooks());
		gv_book.setAdapter(bookshelfGVAdapter);
		tv_classifyName.setText("ȫ��ͼ��");
		currentCategory = ALLBOOK;
	}



	/** ����������listView��item���������¼����� */
	class Classify_lv_item_longClick implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
			 // ĳ���౻����
				final HashMap<String, Object> itemMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
				final int classifyId = Integer.parseInt(String.valueOf(itemMap.get("id")));
				final String classifyName = String.valueOf(itemMap.get("name"));
				final int itemId = (int)id;

				final Dialog classifyDialog = new Dialog(context,R.style.HorizonDialog);
				classifyDialog.setContentView(R.layout.dialog_classify_longclick);
				classifyDialog.setCanceledOnTouchOutside(true);// ������Ի�����������ʱ���رնԻ���

				Button btn_delete = (Button) classifyDialog.findViewById(R.id.btn_delete);
				Button btn_rename = (Button) classifyDialog.findViewById(R.id.btn_rename);

				final int p = position;
				
				// ɾ������
				btn_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog deleteDialog = new Dialog(context, R.style.HorizonDialog);
						deleteDialog.setContentView(R.layout.dialog_delete);

						Button btn_ok = (Button) deleteDialog.findViewById(R.id.btn_ok);
						Button btn_cancel = (Button) deleteDialog.findViewById(R.id.btn_cancel);
						final CheckBox checkBox_deleteSource = (CheckBox) deleteDialog.findViewById(R.id.checkBox_deleteSourceFile);
						checkBox_deleteSource.setText("ͬʱɾ���÷�����ͼ���Դ�ļ�");

						btn_ok.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (checkBox_deleteSource.isChecked()) {
									// ͬʱɾ���÷�����ͼ���Դ�ļ�
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
									updateAllBook(ALLBOOK, "ȫ��ͼ��");
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

				// ����������
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
									itemMap.put("name", newNameStr);// ���·����б���List����Դ�ĸ���
									classifyLVAdapter.notifyDataSetChanged();// ���·����б��adapter
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

	/** ������ҳ�淵�ص���ǰҳ��Ĵ���*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == 1) { //���ļ����ҳ�淵��
			updateAllBook(currentCategory, (String) tv_classifyName.getText());
		}
	}

	/**
	 * ���������*����*ͼ�飬��ʾĳ����ͼ�� ������ int
	 * currentCategory������ţ��������Ƿ����ţ����߱�ʾ�������Ķ���ȫ����δ����ı�� String
	 * currentCategoryName���������
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
	
	/** Ϊ��������listView���HeaderView*/

	/** ������ذ�ť2�Σ��˳�Ӧ�ó��� */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(HomeT2Bookshelf.this, "�ٵ��һ�Σ��˳�Ӧ�ó���",
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
                // ���������Ϣtabʱ��ѡ�е�1��tab
                setTabSelection(0);
                break;
            case R.id.readcity_layout:
                // ���������ϵ��tabʱ��ѡ�е�2��tab
                setTabSelection(1);
                break;
            case R.id.setting_layout:
                // ������˶�̬tabʱ��ѡ�е�3��tab
                setTabSelection(2);
                break;
            case R.id.mine_layout:
                // �����������tabʱ��ѡ�е�4��tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    /**
     * ���ݴ����index����������ѡ�е�tabҳ��
     *
     * @param index
     *            ÿ��tabҳ��Ӧ���±ꡣ0��ʾ��Ϣ��1��ʾ��ϵ�ˣ�2��ʾ��̬��3��ʾ���á�
     */
    private void setTabSelection(int index) {
        // ÿ��ѡ��֮ǰ��������ϴε�ѡ��״̬
        clearSelection();
        // ����һ��Fragment����
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // �����ص����е�Fragment���Է�ֹ�ж��Fragment��ʾ�ڽ����ϵ����
        hideFragments(transaction);
        switch (index) {
            case 0:
                // ���������Ϣtabʱ���ı�ؼ���ͼƬ��������ɫ
                shelfImage.setImageResource(R.drawable.read2);
                shelfText.setTextColor(Color.WHITE);
                if (bookshelfFragment == null) {
                    // ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
                    bookshelfFragment = new bookshelf_fragment();
                    transaction.add(R.id.content, bookshelfFragment);
                } else {
                    // ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
                    transaction.show(bookshelfFragment);
                }
                break;
            case 1:
                // ���������ϵ��tabʱ���ı�ؼ���ͼƬ��������ɫ
                readcityImage.setImageResource(R.drawable.readcity2);
                readcityText.setTextColor(Color.WHITE);
                if (readcityFragment == null) {
                    // ���ContactsFragmentΪ�գ��򴴽�һ������ӵ�������
                    readcityFragment = new readcity_fragment();
                    transaction.add(R.id.content, readcityFragment);
                } else {
                    // ���ContactsFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
                    transaction.show(readcityFragment);
                }
                break;
            case 2:
                // ������˶�̬tabʱ���ı�ؼ���ͼƬ��������ɫ
                settingImage.setImageResource(R.drawable.set2);
                settingText.setTextColor(Color.WHITE);
                if (settingFragment == null) {
                    // ���NewsFragmentΪ�գ��򴴽�һ������ӵ�������
                    settingFragment = new setting_fragment();
                    transaction.add(R.id.content, settingFragment);
                } else {
                    // ���NewsFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
                    transaction.show(settingFragment);
                }
                break;
            case 3:
            default:
                // �����������tabʱ���ı�ؼ���ͼƬ��������ɫ
                mineImage.setImageResource(R.drawable.mine2);
                mineText.setTextColor(Color.WHITE);
                if (mineFragment == null) {
                    // ���SettingFragmentΪ�գ��򴴽�һ������ӵ�������
                    mineFragment = new mine_fragment();
                    transaction.add(R.id.content, mineFragment);
                } else {
                    // ���SettingFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
                    transaction.show(mineFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * ��������е�ѡ��״̬��
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
     * �����е�Fragment����Ϊ����״̬��
     *
     * @param transaction
     *            ���ڶ�Fragmentִ�в���������
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