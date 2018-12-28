package com.horizon.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.horizon.R;
import com.horizon.dao.BookInfoDao;
import com.horizon.ui.HomeT2Bookshelf;
import com.horizon.ui.MainReader;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookshelfGVAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> list;

	public BookshelfGVAdapter(Context context, List<Map<String, Object>> list) {
		//获得LayoutInflater的实例
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size(); //返回列表长度
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);//返回该位置的Map<>
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	public final class ViewHolder {
		public Button btn_book;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//设置/获取holder
		ViewHolder holder = null;
		if(convertView == null) {
			//convertView表示某一行的view布局，是已经inflate了的布局
			convertView = layoutInflater.inflate(R.layout.bookshelf_grid_item, null);
			
			//实例化holder中的weight
			holder = new ViewHolder();
			holder.btn_book = (Button)convertView.findViewById(R.id.btn_book);
			
			//给convertView绑定一个额外数据，把ViewHolder缓存起来，方便下次可以getTag()获得该view，直接重用
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//把holder中的控件与List<>数据源对应起来，即与Map中的key字段对应
		final int p = position;
		holder.btn_book.setBackgroundResource(Integer.parseInt(String.valueOf(list.get(position).get("cover"))));
		holder.btn_book.setText((String)list.get(position).get("name"));
		
		//单击书籍
		holder.btn_book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到阅读界面
				Intent intent = new Intent(context, MainReader.class);
				Bundle bundle = new Bundle();
				Map<String, Object> currItemMap = list.get(p);
				bundle.putInt("id", Integer.parseInt(String.valueOf(currItemMap.get("id"))));
				bundle.putString("name", String.valueOf(currItemMap.get("name")));//书籍名
				bundle.putString("address", String.valueOf(currItemMap.get("address")));//地址
				bundle.putInt("classifyId", Integer.parseInt(String.valueOf(currItemMap.get("classifyId"))));//分类编号
				bundle.putLong("latestReadTime", Long.parseLong(String.valueOf(currItemMap.get("latestReadTime"))));//最后一次阅读时间
				bundle.putInt("readRate", Integer.parseInt(String.valueOf(currItemMap.get("readRate"))));//阅读进度
				bundle.putLong("wordsNum", Long.parseLong(String.valueOf(currItemMap.get("wordsNum"))));//总字数
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		//长按书籍
		holder.btn_book.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final Dialog dialog = new Dialog(context, R.style.HorizonDialog);
				dialog.setContentView(R.layout.dialog_book_longclick);
				dialog.setCanceledOnTouchOutside(true);//当点击对话框以外区域时，关闭对话框
				
				Button btn_delete = (Button)dialog.findViewById(R.id.btn_delete);
				Button btn_rename = (Button)dialog.findViewById(R.id.btn_rename);
				Button btn_removeToClassify = (Button)dialog.findViewById(R.id.btn_removeToClassify);
				Button btn_mylove = (Button)dialog.findViewById(R.id.btn_mylove);

				Button btn_detailedInfo = (Button)dialog.findViewById(R.id.btn_detailedInfo);
				
				//删除书籍
				btn_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog deleteDialog = new Dialog(context, R.style.HorizonDialog);
						deleteDialog.setContentView(R.layout.dialog_delete);
						
						Button btn_ok = (Button)deleteDialog.findViewById(R.id.btn_ok);
						Button btn_cancel = (Button)deleteDialog.findViewById(R.id.btn_cancel);
						final CheckBox checkBox_deleteSource = (CheckBox)deleteDialog.findViewById(R.id.checkBox_deleteSourceFile);
						
						btn_ok.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if(checkBox_deleteSource.isChecked()) {
									//删除源文件
									File file = new File(String.valueOf(list.get(p).get("address")));
									if(file.exists()) {
										file.delete();
									}
								}
								
								BookInfoDao bookInfoDao = new BookInfoDao(context);
								bookInfoDao.deleteBook(Integer.parseInt(String.valueOf(list.get(p).get("id"))));
								
								//更新书架
								updateAllBook(HomeT2Bookshelf.currentCategory);
								
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
				
				//重命名书籍
				btn_rename.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog renameDialog = new Dialog(context, R.style.HorizonDialog);
						renameDialog.setContentView(R.layout.dialog_rename);
						
						Button btn_ok = (Button)renameDialog.findViewById(R.id.btn_ok);
						Button btn_cancel = (Button)renameDialog.findViewById(R.id.btn_cancel);
						final EditText et_newName = (EditText)renameDialog.findViewById(R.id.et_newName);
						et_newName.setText(String.valueOf(list.get(p).get("name")));
						
						btn_ok.setOnClickListener(new OnClickListener() {
							@Override 
							public void onClick(View arg0) {
								int id = Integer.parseInt(String.valueOf(list.get(p).get("id")));
								String newNameStr = String.valueOf(et_newName.getText());
								String newAddressStr = "";
								
								if(newNameStr != "" && newNameStr != null) {
									newNameStr += ".txt";
									
									//修改文件地址和文件名,然后给newAddressStr赋值  
									File file = new File((String)list.get(p).get("address"));
									if(file.exists()) {
										newAddressStr = file.getParent() + "/" + newNameStr;
										File renameFile = new File(newAddressStr);
										if(renameFile.exists()) {
											Toast.makeText(context, "该图书文件名已经存在,请重新输入", Toast.LENGTH_LONG).show();
										} else {
											file.renameTo(renameFile);//重命名文件
											
											//重命名数据库中图书的名和地址
											BookInfoDao bookInfoDao = new BookInfoDao(context);
											bookInfoDao.alterBookName(id, newNameStr, newAddressStr);
											
											//更新书架
											list.get(p).put("name", newNameStr);
											list.get(p).put("address", newAddressStr);
											notifyDataSetChanged();
											
											renameDialog.cancel();
										}
									}
								} else {
									Toast.makeText(context, "请输入分类名", Toast.LENGTH_SHORT).show();
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
				
				//移动到分类
				btn_removeToClassify.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog removeDialog = new Dialog(context, R.style.HorizonDialog);
						removeDialog.setContentView(R.layout.dialog_remove_to_classify);
						removeDialog.setCanceledOnTouchOutside(true);
						
						ListView lv_classify = (ListView)removeDialog.findViewById(R.id.listview_classify);
						
						final BookInfoDao bookInfoDao = new BookInfoDao(context);
						List<Map<String, Object>> tsList;
						
						lv_classify.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								int bookId = Integer.parseInt(String.valueOf(list.get(p).get("id")));
								HashMap<String, Object> hereMap = (HashMap<String, Object>)parent.getItemAtPosition(position);
								int toClassifyId = Integer.parseInt(String.valueOf(hereMap.get("id")));

								
								//更新书架
								updateAllBook(HomeT2Bookshelf.currentCategory);
								
								removeDialog.cancel();
							}
						});
						
						removeDialog.show();
						dialog.cancel();
					}
				});
				

				
				//详细信息
				btn_detailedInfo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog detailedInfoDialog = new Dialog(context, R.style.HorizonDialog);
						detailedInfoDialog.setContentView(R.layout.dialog_detailed_info);
						detailedInfoDialog.setCanceledOnTouchOutside(true);//当点击对话框以外区域时，关闭对话框
						
						String bookName = String.valueOf(list.get(p).get("name"));
						String address = String.valueOf(list.get(p).get("address"));
						
						File file = new File(address);
						String fileSizeStr = "";
						try {
							fileSizeStr = FormetFileSize(getFileSizes(file));
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						long latestReadTime = Long.parseLong(String.valueOf(list.get(p).get("latestReadTime")));
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(latestReadTime);
						String lateReadTimeStr = String.format("%tF", calendar);
						
						String infoStr = "书名： " + bookName + "\n格式： txt\n大小：" + fileSizeStr + 
						        "\n最后阅读时间：" + lateReadTimeStr + "\n地址： " + address;
						
						TextView tv_info = (TextView)detailedInfoDialog.findViewById(R.id.tv_info);
						tv_info.setText(infoStr);
						
						detailedInfoDialog.show();
						dialog.cancel();
					}
					
					/**获取文件大小*/
					public long getFileSizes(File f) throws Exception { 
				        long s = 0; 
				        if (f.exists()) { 
				            FileInputStream fis = new FileInputStream(f); 
				            s = fis.available(); 
				        }
				        return s; 
				    } 
					
					/**转换文件大小*/
					public String FormetFileSize(long fileS) { 
				        DecimalFormat df = new DecimalFormat("#.00"); 
				        String fileSizeString = ""; 
				        if (fileS < 1024) { 
				            fileSizeString = df.format((double) fileS) + "B"; 
				        } else if (fileS < 1048576) { 
				            fileSizeString = df.format((double) fileS / 1024) + "K"; 
				        } else if (fileS < 1073741824) { 
				            fileSizeString = df.format((double) fileS / 1048576) + "M"; 
				        } else { 
				            fileSizeString = df.format((double) fileS / 1073741824) + "G"; 
				        } 
				        return fileSizeString; 
				    }  

				});
				
				dialog.show();
				return true;
			}
		});
		
		return convertView;
	}
	
	/**单击书籍*/
//	class BookClick implements OnClickListener {
//		@Override
//		public void onClick(View v) {
//			
//		}
//	}
	
//	/**长按书籍*/
//	class ClassifyBookLongClick implements OnLongClickListener {
//		@Override
//		public boolean onLongClick(View v) {
//			
//		}
//	}
	
	/**获取此adapter的list数据集合*/
	public List<Map<String, Object>> getList() {
		return list;
	}

	/**设置此adapter的list数据集合*/
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	
	/**
	 * 更新书架上当前显示类别的图书
	 * 参数： int currentCategory：类别编号，它可以是分类编号，或者表示最爱、最近阅读、全部或未分类的标记 String
	 */
	public void updateAllBook(int currentCategory) {
		BookInfoDao bookInfoDao = new BookInfoDao(context);
		switch (currentCategory) {



		case HomeT2Bookshelf.ALLBOOK:
			setList(bookInfoDao.getAllBooks());
			break;

		}
		notifyDataSetInvalidated();
	}
}
