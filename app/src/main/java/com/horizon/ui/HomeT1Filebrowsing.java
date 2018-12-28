package com.horizon.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.R;
import com.horizon.adapter.FilebrowsingLVAdapter;
import com.horizon.dao.BookInfoDao;
import com.horizon.model.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeT1Filebrowsing extends Activity {
	private ListView lv_file;
	private ImageButton imgBtn_upOneLevel; //��һ����ť
	private TextView tv_path; //topBar�ϵĵ�ǰĿ¼��ַ
	private ImageButton imgBtn_handle; //����ť�������ʾbottomBar���ļ���checkbox
	private LinearLayout layout_bottomBar; //bottomBar
	private Button btn_delete; //ɾ��
	public static TextView tv_checkedNum; //ѡ�е��ļ���Ŀ
	private CheckBox checkbox_checkAll; //ȫѡ
	private ImageButton imgBtn_backToBookshelf; //�ص����
	
	private Context context;
	private BookInfoDao bookInfoDao;
	private List<Map<String, Object>> listdata;
	private FilebrowsingLVAdapter fileLVAdapter; //file���б��������

	private final String defaultRootStr = getDefaultRootPath(); //Ĭ�Ͻ����Ŀ¼
	private String currentDirStr; //��ǰĿ¼
	public static boolean isHandling = false; //�Ƿ�������ɾ�����ƶ����ദ��
	public static ArrayList<String> selectedAddress;//ѡ�е��ļ���ַ
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_t1_filebrowsing);
		
		lv_file = (ListView)findViewById(R.id.listview_file);
		imgBtn_upOneLevel = (ImageButton)findViewById(R.id.imgBtn_upOneLevel);
		tv_path = (TextView)findViewById(R.id.tv_path);
		imgBtn_handle = (ImageButton)findViewById(R.id.imgBtn_handle);
		layout_bottomBar = (LinearLayout)findViewById(R.id.layout_bottomBar);
		btn_delete = (Button)findViewById(R.id.btn_delete);
		tv_checkedNum = (TextView)findViewById(R.id.tv_num);
		checkbox_checkAll = (CheckBox)findViewById(R.id.checkbox_checkAll);
		imgBtn_backToBookshelf = (ImageButton)findViewById(R.id.imgBtn_backBookshelf);
		
		context = this;
		bookInfoDao = new BookInfoDao(context);
		
		tv_path.setText(defaultRootStr);
		currentDirStr = defaultRootStr;
		isHandling = false; //�������������static��������ڴ���activityʱ�������ã����ᱣ����֮ǰ�˳���activityʱ������
		selectedAddress = new ArrayList<String>();
		
		// ���������ļ������ListView
		listdata = getDate(defaultRootStr);
		fileLVAdapter = new FilebrowsingLVAdapter(context, listdata);
		lv_file.setAdapter(fileLVAdapter);
		lv_file.setOnItemClickListener(new ListItemClick());
		lv_file.setOnItemLongClickListener(new ListItemLongClick());
		
		// ���������¼�����
		imgBtn_upOneLevel.setOnClickListener(new UpOneLevelListener());
		imgBtn_handle.setOnClickListener(new HandleListener());
		btn_delete.setOnClickListener(new DeleteListener());
		checkbox_checkAll.setOnCheckedChangeListener(new CheckAllListener());
		imgBtn_backToBookshelf.setOnClickListener(new backToBookshelfListener());
	}
	
	/**��ȡĿ¼�е�����
	 * ������dirPath ��String��:Ŀ¼��ַ*/
	public List<Map<String, Object>> getDate(String dirPath) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		// ��ȡ��ǰ·���µ��ļ�
		File currentFile = new  File(dirPath);
		File[] currentFiles = currentFile.listFiles();
		
		// ��ӵ�ǰ·���µ����е��ļ�����·��
		for (File file : currentFiles) {
			if(file.isDirectory() || file.getName().endsWith(".txt")) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("fileImg", getFileImg(file));
				map.put("fileName", file.getName());
				map.put("fileAddress", file.getAbsolutePath());
				map.put("fileCheck", false);
				list.add(map);
			}
		}
		Log.d("Horizon",dirPath+"���ļ�����"+list.size());
		return list;
	}
	
	/**��ȡ�ļ���ӦͼƬ
	 * ������file ��File��:�ļ�*/
	public int getFileImg(File file) {
		if(file.isDirectory()) {
			return R.drawable.file_dir;
		} else {
			return R.drawable.file_txt;
		}
	}
	
	/**��ȡĬ�Ͻ����Ŀ¼��ַ�����sd�������򷵻�sd��Ŀ¼��û���򷵻ظ�Ŀ¼*/
	public String getDefaultRootPath(){ 
	    File sdDir = null; 
	    //�ж�sd���Ƿ���� 
	    boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
	    if(sdCardExist) {                               
	        sdDir = Environment.getExternalStorageDirectory();//��ȡsd��Ŀ¼ 
	        return sdDir.getAbsolutePath(); 
	    } 
	    else {
	    	return "/";//û��sd���Ļ��ͷ��ظ�Ŀ¼
	    }   
	} 
	
	/**listItem���������¼�����*/
	class ListItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Map<String, Object> map = (Map<String, Object>)parent.getItemAtPosition(position);
			String itemFileAddress = (String)map.get("fileAddress");
			File itemFile = new File(itemFileAddress);
			Log.d("Horizon",itemFileAddress);

			if(itemFile.isDirectory()) { //������ļ��С�������
				Log.d("Horizon","�ļ���");
				if (isHandling) {
					Log.d("Horizon","�ڲ���״̬");
					return;
				} else { //����Ŀ¼
					Log.d("Horizon","����Ŀ¼");
					tv_path.setText(itemFileAddress);
					currentDirStr = itemFileAddress; //�޸ĵ�ǰ��ַ

					listdata.clear();
					listdata.addAll(getDate(itemFileAddress));
					fileLVAdapter.notifyDataSetChanged();
				}
			} 
			else { //�������ļ���������
				Log.d("Horizon","�ļ�");
				if(isHandling) { //�޸�ѡ��״̬
					Log.d("Horizon","�ڲ���״̬");
					CheckBox fileCheck = (CheckBox)view.findViewById(R.id.checkbox_fileCheck);
					if(fileCheck.isChecked()) {
						fileCheck.setChecked(false);
						
						if(selectedAddress.contains(itemFileAddress)) {//��ѡ���б���ɾ��
							selectedAddress.remove(itemFileAddress);
							HomeT1Filebrowsing.tv_checkedNum.setText(selectedAddress.size()+"");
						}
					} else {
						fileCheck.setChecked(true);
						
						if(!selectedAddress.contains(itemFileAddress)) {//����ѡ���б�
							selectedAddress.add(itemFileAddress);
							HomeT1Filebrowsing.tv_checkedNum.setText(selectedAddress.size()+"");
						}
					}
				} else { //�����Ķ�
					Log.d("Horizon","���鼮");
					//��ѯ���鼮�Ƿ���������
					Book book = bookInfoDao.getBookByAddress(itemFile.getAbsolutePath());
					if(book == null) { //��������У�����鼮��δ����
						book = new Book();
						String[] strs = itemFile.getName().split("\\.");
						book.setName(strs[0]);
						book.setAddress(itemFile.getAbsolutePath());
						book.setCover(book.getRandomCover());
						bookInfoDao.addBook(book);
						
						book.set_id(bookInfoDao.getLastTBBookInfoId()); //��������鼮�ı��
					}
					else { //������У������鼮����Ķ�ʱ��
						Calendar calendar = Calendar.getInstance();
						long time = calendar.getTimeInMillis();
						bookInfoDao.alterLatestReadTime(book.getId(), time);
					}
					
					//��ת���Ķ�ҳ��				
					Intent intent = new Intent(context, MainReader.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", book.getId());
					bundle.putString("name", book.getName());//�鼮��
					bundle.putString("address", book.getAddress());//��ַ
					bundle.putInt("classifyId", book.getClassifyId());//������
					bundle.putLong("latestReadTime", book.getLatestReadTime());//���һ���Ķ�ʱ��
					bundle.putInt("readRate", book.getReadRate());//�Ķ�����
					bundle.putLong("wordsNum", book.getWordsNum());//������
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			} 
		}
	}
	
	/**listItem���������¼�����*/
	class ListItemLongClick implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final Map<String, Object> currentItemMap = (Map<String, Object>)parent.getItemAtPosition(position);
			final File currentItemFile = new File((String)currentItemMap.get("fileAddress"));
			//������������ļ�����ֱ���˳�����
			if(currentItemFile.isDirectory()) {
				return true;
			}
			
			final Dialog dialog = new Dialog(context, R.style.HorizonDialog);
			dialog.setContentView(R.layout.dialog_file_longclick);
			dialog.setCanceledOnTouchOutside(true);//������Ի�����������ʱ���رնԻ���
			
			Button btn_delete = (Button)dialog.findViewById(R.id.btn_delete);
			Button btn_rename = (Button)dialog.findViewById(R.id.btn_rename);
			Button btn_removeToClassify = (Button)dialog.findViewById(R.id.btn_removeToClassify);
			
			//ɾ���ļ�
			btn_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final Dialog deleteDialog = new Dialog(context, R.style.HorizonDialog);
					deleteDialog.setContentView(R.layout.dialog_delete2);
					
					Button btn_ok = (Button)deleteDialog.findViewById(R.id.btn_ok);
					Button btn_cancel = (Button)deleteDialog.findViewById(R.id.btn_cancel);
					
					btn_ok.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							currentItemFile.delete(); //ɾ���ļ�
							
							//���������Ƿ��и��飬�еĻ��������ݿ���ɾ��
							String currentFileAddress = (String)currentItemMap.get("fileAddress");
							int bookId = bookInfoDao.checkBookExistByAddress(currentFileAddress);
							if(bookId != -1) { 
								bookInfoDao.deleteBook(bookId);
							}

							listdata.clear();
							listdata.addAll(getDate(currentDirStr));
							fileLVAdapter.notifyDataSetChanged();
							
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
					dialog.cancel();
				}
			});
			
			//�������ļ�
			btn_rename.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final Dialog renameDialog = new Dialog(context, R.style.HorizonDialog);
					renameDialog.setContentView(R.layout.dialog_rename);
					
					Button btn_ok = (Button)renameDialog.findViewById(R.id.btn_ok);
					Button btn_cancel = (Button)renameDialog.findViewById(R.id.btn_cancel);
					final EditText et_newName = (EditText)renameDialog.findViewById(R.id.et_newName);
					et_newName.setText(currentItemFile.getName());
					
					btn_ok.setOnClickListener(new OnClickListener() {
						@Override 
						public void onClick(View arg0) {
							String newName = String.valueOf(et_newName.getText());
							String newAddress = currentItemFile.getParent() + "/" + newName;
							
							File renameFile = new File(newAddress);
							if(renameFile.exists()) {
								Toast.makeText(context, "���ļ����Ѿ�����,����������", Toast.LENGTH_SHORT).show();
							} else {
								currentItemFile.renameTo(renameFile);//�������ļ�
								
								//���������Ƿ��и��飬�еĻ����������ݿ��е��鼮���͵�ַ
								String currentFileAddress = (String)currentItemMap.get("fileAddress");
								int bookId = bookInfoDao.checkBookExistByAddress(currentFileAddress);
								if(bookId != -1) { 
									bookInfoDao.alterBookName(bookId, newName, newAddress);
								}

								listdata.clear();
								listdata.addAll(getDate(currentDirStr));
								fileLVAdapter.notifyDataSetChanged();
								
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
					dialog.cancel();
				}
			});
			
			//�������
			btn_removeToClassify.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//û�з���ʱ������δ����
						String currentFileAddress = (String)currentItemMap.get("fileAddress");
						Book book = new Book();
						File itemFile = new File(currentFileAddress);
						String[] strs = itemFile.getName().split("\\.");
						book.setName(strs[0]);
						book.setAddress(currentFileAddress);
						book.setCover(book.getRandomCover());
						bookInfoDao.addBook(book);

						Toast.makeText(context, "�ɹ��������", Toast.LENGTH_SHORT).show();

					dialog.cancel();
				}
			});
			
			dialog.show();
			return true;
		}
	}
	
	/**������һ�����¼�����*/
	class UpOneLevelListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			File file = new File((String)tv_path.getText());
			String parentFileStr = file.getParent();
			if(parentFileStr != null) {
				tv_path.setText(parentFileStr);
				currentDirStr = parentFileStr;

				listdata.clear();
				listdata.addAll(getDate(parentFileStr));
				fileLVAdapter.notifyDataSetChanged();
			} 
			else {
				Toast.makeText(context, "��ǰ�Ѿ��Ǹ�Ŀ¼��", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**������ܽ�����¼�����*/
	class backToBookshelfListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			//Intent intent = new Intent(HomeT1Filebrowsing.this, HomeT2Bookshelf.class);
			setResult(1);
			finish();
		}
	}
	
	/**����ť���¼������������ʾbottomBar���ļ���checkbox*/
	class HandleListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(!isHandling) {
				layout_bottomBar.setVisibility(View.VISIBLE);
				imgBtn_handle.setImageResource(R.drawable.dealing);
				isHandling = true;
				Log.d("Horizon","�л���������״̬");
			} else {
				layout_bottomBar.setVisibility(View.GONE);
				imgBtn_handle.setImageResource(R.drawable.deal);
				isHandling = false;
				Log.d("Horizon","�˳� ����״̬");
				
				selectedAddress.clear(); //ÿ���˳������������ѡ���б�
			}
			fileLVAdapter.notifyDataSetChanged();
		}
	}
	
	/**ɾ�����¼�����*/
	class DeleteListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			final Dialog deleteDialog = new Dialog(context, R.style.HorizonDialog);
			deleteDialog.setContentView(R.layout.dialog_delete2);
			
			Button btn_ok = (Button)deleteDialog.findViewById(R.id.btn_ok);
			Button btn_cancel = (Button)deleteDialog.findViewById(R.id.btn_cancel);
			
			btn_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					for(int i = 0; i < selectedAddress.size(); i++) {
						File file = new File(selectedAddress.get(i));
						file.delete();
						
						//���������Ƿ��и��飬�еĻ��������ݿ���ɾ��
						int bookId = bookInfoDao.checkBookExistByAddress(selectedAddress.get(i));
						if(bookId != -1) { 
							bookInfoDao.deleteBook(bookId);;
						} 
					}

					listdata.clear();
					listdata.addAll(getDate(currentDirStr));
					fileLVAdapter.notifyDataSetChanged();
					
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
		}
	}
	
	/**������ܵ��¼�����*/
	class RemoveListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			//ֱ�Ӱ��鼮����δ����
				for(int i = 0; i < selectedAddress.size(); i++) {
					//���������Ƿ���ڴ���
					int bookId = bookInfoDao.checkBookExistByAddress(selectedAddress.get(i));
					if(bookId == -1) { //�����ڣ�����鼮��¼
						Book book = new Book();
						File itemFile = new File(selectedAddress.get(i));
						String[] strs = itemFile.getName().split("\\.");
						book.setName(strs[0]);
						book.setAddress(selectedAddress.get(i));
						book.setCover(book.getRandomCover());
						bookInfoDao.addBook(book);
					}
				}
				Toast.makeText(context, "�ɹ��������", Toast.LENGTH_SHORT).show();
				return;


		}
	}
	
	/**ȫѡ���¼�����*/
	class CheckAllListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			selectedAddress.clear();//�����ѡ���б�
			
			Iterator<Map<String, Object>> iter = listdata.iterator();
			if(isChecked) {	
				while (iter.hasNext()) {
					Map<String, Object> iterMap = iter.next();
					String address = (String)iterMap.get("fileAddress");
					if(address.endsWith(".txt")) { //������ļ������޸���ѡ��״̬
						iterMap.put("fileCheck", true);
						selectedAddress.add(address);//��ӵ�ַ����ѡ���б�
					}
				}
			} else {
				while (iter.hasNext()) {
					Map<String, Object> iterMap = iter.next();
					iterMap.put("fileCheck", false);
				}
			}
			tv_checkedNum.setText("" + selectedAddress.size());
			fileLVAdapter.notifyDataSetChanged();
		}
	}

	/**���ذ�ť�����ص���ܽ���*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			//Intent intent = new Intent(HomeT1Filebrowsing.this, HomeT2Bookshelf.class);
			setResult(1);
			finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
}
