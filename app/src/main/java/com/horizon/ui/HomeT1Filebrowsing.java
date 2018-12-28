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
	private ImageButton imgBtn_upOneLevel; //上一级按钮
	private TextView tv_path; //topBar上的当前目录地址
	private ImageButton imgBtn_handle; //处理按钮，点击显示bottomBar和文件的checkbox
	private LinearLayout layout_bottomBar; //bottomBar
	private Button btn_delete; //删除
	public static TextView tv_checkedNum; //选中的文件数目
	private CheckBox checkbox_checkAll; //全选
	private ImageButton imgBtn_backToBookshelf; //回到书架
	
	private Context context;
	private BookInfoDao bookInfoDao;
	private List<Map<String, Object>> listdata;
	private FilebrowsingLVAdapter fileLVAdapter; //file的列表的适配器

	private final String defaultRootStr = getDefaultRootPath(); //默认进入的目录
	private String currentDirStr; //当前目录
	public static boolean isHandling = false; //是否正在做删除或移动分类处理
	public static ArrayList<String> selectedAddress;//选中的文件地址
	
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
		isHandling = false; //由于这个变量是static，如果不在创建activity时重新设置，它会保留着之前退出此activity时的设置
		selectedAddress = new ArrayList<String>();
		
		// ——设置文件浏览的ListView
		listdata = getDate(defaultRootStr);
		fileLVAdapter = new FilebrowsingLVAdapter(context, listdata);
		lv_file.setAdapter(fileLVAdapter);
		lv_file.setOnItemClickListener(new ListItemClick());
		lv_file.setOnItemLongClickListener(new ListItemLongClick());
		
		// ——设置事件监听
		imgBtn_upOneLevel.setOnClickListener(new UpOneLevelListener());
		imgBtn_handle.setOnClickListener(new HandleListener());
		btn_delete.setOnClickListener(new DeleteListener());
		checkbox_checkAll.setOnCheckedChangeListener(new CheckAllListener());
		imgBtn_backToBookshelf.setOnClickListener(new backToBookshelfListener());
	}
	
	/**获取目录中的数据
	 * 参数：dirPath （String）:目录地址*/
	public List<Map<String, Object>> getDate(String dirPath) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		// 获取当前路径下的文件
		File currentFile = new  File(dirPath);
		File[] currentFiles = currentFile.listFiles();
		
		// 添加当前路径下的所有的文件名和路径
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
		Log.d("Horizon",dirPath+"：文件数："+list.size());
		return list;
	}
	
	/**获取文件对应图片
	 * 参数：file （File）:文件*/
	public int getFileImg(File file) {
		if(file.isDirectory()) {
			return R.drawable.file_dir;
		} else {
			return R.drawable.file_txt;
		}
	}
	
	/**获取默认进入的目录地址，如果sd卡存在则返回sd卡目录，没有则返回根目录*/
	public String getDefaultRootPath(){ 
	    File sdDir = null; 
	    //判断sd卡是否存在 
	    boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
	    if(sdCardExist) {                               
	        sdDir = Environment.getExternalStorageDirectory();//获取sd卡目录 
	        return sdDir.getAbsolutePath(); 
	    } 
	    else {
	    	return "/";//没有sd卡的话就返回根目录
	    }   
	} 
	
	/**listItem被单击的事件监听*/
	class ListItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Map<String, Object> map = (Map<String, Object>)parent.getItemAtPosition(position);
			String itemFileAddress = (String)map.get("fileAddress");
			File itemFile = new File(itemFileAddress);
			Log.d("Horizon",itemFileAddress);

			if(itemFile.isDirectory()) { //如果是文件夹————
				Log.d("Horizon","文件夹");
				if (isHandling) {
					Log.d("Horizon","在操作状态");
					return;
				} else { //进入目录
					Log.d("Horizon","进入目录");
					tv_path.setText(itemFileAddress);
					currentDirStr = itemFileAddress; //修改当前地址

					listdata.clear();
					listdata.addAll(getDate(itemFileAddress));
					fileLVAdapter.notifyDataSetChanged();
				}
			} 
			else { //否则是文件————
				Log.d("Horizon","文件");
				if(isHandling) { //修改选中状态
					Log.d("Horizon","在操作状态");
					CheckBox fileCheck = (CheckBox)view.findViewById(R.id.checkbox_fileCheck);
					if(fileCheck.isChecked()) {
						fileCheck.setChecked(false);
						
						if(selectedAddress.contains(itemFileAddress)) {//从选中列表中删除
							selectedAddress.remove(itemFileAddress);
							HomeT1Filebrowsing.tv_checkedNum.setText(selectedAddress.size()+"");
						}
					} else {
						fileCheck.setChecked(true);
						
						if(!selectedAddress.contains(itemFileAddress)) {//加入选中列表
							selectedAddress.add(itemFileAddress);
							HomeT1Filebrowsing.tv_checkedNum.setText(selectedAddress.size()+"");
						}
					}
				} else { //进入阅读
					Log.d("Horizon","打开书籍");
					//查询该书籍是否存在书架中
					Book book = bookInfoDao.getBookByAddress(itemFile.getAbsolutePath());
					if(book == null) { //不在书架中，添加书籍到未分类
						book = new Book();
						String[] strs = itemFile.getName().split("\\.");
						book.setName(strs[0]);
						book.setAddress(itemFile.getAbsolutePath());
						book.setCover(book.getRandomCover());
						bookInfoDao.addBook(book);
						
						book.set_id(bookInfoDao.getLastTBBookInfoId()); //最后插入的书籍的编号
					}
					else { //在书架中，更新书籍最后阅读时间
						Calendar calendar = Calendar.getInstance();
						long time = calendar.getTimeInMillis();
						bookInfoDao.alterLatestReadTime(book.getId(), time);
					}
					
					//跳转到阅读页面				
					Intent intent = new Intent(context, MainReader.class);
					Bundle bundle = new Bundle();
					bundle.putInt("id", book.getId());
					bundle.putString("name", book.getName());//书籍名
					bundle.putString("address", book.getAddress());//地址
					bundle.putInt("classifyId", book.getClassifyId());//分类编号
					bundle.putLong("latestReadTime", book.getLatestReadTime());//最后一次阅读时间
					bundle.putInt("readRate", book.getReadRate());//阅读进度
					bundle.putLong("wordsNum", book.getWordsNum());//总字数
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			} 
		}
	}
	
	/**listItem被长按的事件监听*/
	class ListItemLongClick implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final Map<String, Object> currentItemMap = (Map<String, Object>)parent.getItemAtPosition(position);
			final File currentItemFile = new File((String)currentItemMap.get("fileAddress"));
			//如果长按的是文件夹则直接退出方法
			if(currentItemFile.isDirectory()) {
				return true;
			}
			
			final Dialog dialog = new Dialog(context, R.style.HorizonDialog);
			dialog.setContentView(R.layout.dialog_file_longclick);
			dialog.setCanceledOnTouchOutside(true);//当点击对话框以外区域时，关闭对话框
			
			Button btn_delete = (Button)dialog.findViewById(R.id.btn_delete);
			Button btn_rename = (Button)dialog.findViewById(R.id.btn_rename);
			Button btn_removeToClassify = (Button)dialog.findViewById(R.id.btn_removeToClassify);
			
			//删除文件
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
							currentItemFile.delete(); //删除文件
							
							//检查书架上是否有该书，有的话，从数据库中删除
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
			
			//重命名文件
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
								Toast.makeText(context, "该文件名已经存在,请重新输入", Toast.LENGTH_SHORT).show();
							} else {
								currentItemFile.renameTo(renameFile);//重命名文件
								
								//检查书架上是否有该书，有的话，更改数据库中的书籍名和地址
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
			
			//加入书架
			btn_removeToClassify.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//没有分类时，加入未分类
						String currentFileAddress = (String)currentItemMap.get("fileAddress");
						Book book = new Book();
						File itemFile = new File(currentFileAddress);
						String[] strs = itemFile.getName().split("\\.");
						book.setName(strs[0]);
						book.setAddress(currentFileAddress);
						book.setCover(book.getRandomCover());
						bookInfoDao.addBook(book);

						Toast.makeText(context, "成功加入书架", Toast.LENGTH_SHORT).show();

					dialog.cancel();
				}
			});
			
			dialog.show();
			return true;
		}
	}
	
	/**返回上一级的事件监听*/
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
				Toast.makeText(context, "当前已经是根目录了", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**返回书架界面的事件监听*/
	class backToBookshelfListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			//Intent intent = new Intent(HomeT1Filebrowsing.this, HomeT2Bookshelf.class);
			setResult(1);
			finish();
		}
	}
	
	/**处理按钮的事件监听，点击显示bottomBar和文件的checkbox*/
	class HandleListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(!isHandling) {
				layout_bottomBar.setVisibility(View.VISIBLE);
				imgBtn_handle.setImageResource(R.drawable.dealing);
				isHandling = true;
				Log.d("Horizon","切换到：操作状态");
			} else {
				layout_bottomBar.setVisibility(View.GONE);
				imgBtn_handle.setImageResource(R.drawable.deal);
				isHandling = false;
				Log.d("Horizon","退出 操作状态");
				
				selectedAddress.clear(); //每次退出操作，都清空选中列表
			}
			fileLVAdapter.notifyDataSetChanged();
		}
	}
	
	/**删除的事件监听*/
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
						
						//检查书架上是否有该书，有的话，从数据库中删除
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
	
	/**加入书架的事件监听*/
	class RemoveListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			//直接把书籍加入未分类
				for(int i = 0; i < selectedAddress.size(); i++) {
					//检查书架上是否存在此书
					int bookId = bookInfoDao.checkBookExistByAddress(selectedAddress.get(i));
					if(bookId == -1) { //不存在，添加书籍记录
						Book book = new Book();
						File itemFile = new File(selectedAddress.get(i));
						String[] strs = itemFile.getName().split("\\.");
						book.setName(strs[0]);
						book.setAddress(selectedAddress.get(i));
						book.setCover(book.getRandomCover());
						bookInfoDao.addBook(book);
					}
				}
				Toast.makeText(context, "成功加入书架", Toast.LENGTH_SHORT).show();
				return;


		}
	}
	
	/**全选的事件监听*/
	class CheckAllListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			selectedAddress.clear();//清空已选中列表
			
			Iterator<Map<String, Object>> iter = listdata.iterator();
			if(isChecked) {	
				while (iter.hasNext()) {
					Map<String, Object> iterMap = iter.next();
					String address = (String)iterMap.get("fileAddress");
					if(address.endsWith(".txt")) { //如果是文件，则修改其选中状态
						iterMap.put("fileCheck", true);
						selectedAddress.add(address);//添加地址到已选中列表
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

	/**返回按钮，返回到书架界面*/
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
