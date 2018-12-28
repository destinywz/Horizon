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
		//���LayoutInflater��ʵ��
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size(); //�����б���
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);//���ظ�λ�õ�Map<>
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
		//����/��ȡholder
		ViewHolder holder = null;
		if(convertView == null) {
			//convertView��ʾĳһ�е�view���֣����Ѿ�inflate�˵Ĳ���
			convertView = layoutInflater.inflate(R.layout.bookshelf_grid_item, null);
			
			//ʵ����holder�е�weight
			holder = new ViewHolder();
			holder.btn_book = (Button)convertView.findViewById(R.id.btn_book);
			
			//��convertView��һ���������ݣ���ViewHolder���������������´ο���getTag()��ø�view��ֱ������
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//��holder�еĿؼ���List<>����Դ��Ӧ����������Map�е�key�ֶζ�Ӧ
		final int p = position;
		holder.btn_book.setBackgroundResource(Integer.parseInt(String.valueOf(list.get(position).get("cover"))));
		holder.btn_book.setText((String)list.get(position).get("name"));
		
		//�����鼮
		holder.btn_book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��ת���Ķ�����
				Intent intent = new Intent(context, MainReader.class);
				Bundle bundle = new Bundle();
				Map<String, Object> currItemMap = list.get(p);
				bundle.putInt("id", Integer.parseInt(String.valueOf(currItemMap.get("id"))));
				bundle.putString("name", String.valueOf(currItemMap.get("name")));//�鼮��
				bundle.putString("address", String.valueOf(currItemMap.get("address")));//��ַ
				bundle.putInt("classifyId", Integer.parseInt(String.valueOf(currItemMap.get("classifyId"))));//������
				bundle.putLong("latestReadTime", Long.parseLong(String.valueOf(currItemMap.get("latestReadTime"))));//���һ���Ķ�ʱ��
				bundle.putInt("readRate", Integer.parseInt(String.valueOf(currItemMap.get("readRate"))));//�Ķ�����
				bundle.putLong("wordsNum", Long.parseLong(String.valueOf(currItemMap.get("wordsNum"))));//������
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		//�����鼮
		holder.btn_book.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final Dialog dialog = new Dialog(context, R.style.HorizonDialog);
				dialog.setContentView(R.layout.dialog_book_longclick);
				dialog.setCanceledOnTouchOutside(true);//������Ի�����������ʱ���رնԻ���
				
				Button btn_delete = (Button)dialog.findViewById(R.id.btn_delete);
				Button btn_rename = (Button)dialog.findViewById(R.id.btn_rename);
				Button btn_removeToClassify = (Button)dialog.findViewById(R.id.btn_removeToClassify);
				Button btn_mylove = (Button)dialog.findViewById(R.id.btn_mylove);

				Button btn_detailedInfo = (Button)dialog.findViewById(R.id.btn_detailedInfo);
				
				//ɾ���鼮
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
									//ɾ��Դ�ļ�
									File file = new File(String.valueOf(list.get(p).get("address")));
									if(file.exists()) {
										file.delete();
									}
								}
								
								BookInfoDao bookInfoDao = new BookInfoDao(context);
								bookInfoDao.deleteBook(Integer.parseInt(String.valueOf(list.get(p).get("id"))));
								
								//�������
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
				
				//�������鼮
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
									
									//�޸��ļ���ַ���ļ���,Ȼ���newAddressStr��ֵ  
									File file = new File((String)list.get(p).get("address"));
									if(file.exists()) {
										newAddressStr = file.getParent() + "/" + newNameStr;
										File renameFile = new File(newAddressStr);
										if(renameFile.exists()) {
											Toast.makeText(context, "��ͼ���ļ����Ѿ�����,����������", Toast.LENGTH_LONG).show();
										} else {
											file.renameTo(renameFile);//�������ļ�
											
											//���������ݿ���ͼ������͵�ַ
											BookInfoDao bookInfoDao = new BookInfoDao(context);
											bookInfoDao.alterBookName(id, newNameStr, newAddressStr);
											
											//�������
											list.get(p).put("name", newNameStr);
											list.get(p).put("address", newAddressStr);
											notifyDataSetChanged();
											
											renameDialog.cancel();
										}
									}
								} else {
									Toast.makeText(context, "�����������", Toast.LENGTH_SHORT).show();
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
				
				//�ƶ�������
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

								
								//�������
								updateAllBook(HomeT2Bookshelf.currentCategory);
								
								removeDialog.cancel();
							}
						});
						
						removeDialog.show();
						dialog.cancel();
					}
				});
				

				
				//��ϸ��Ϣ
				btn_detailedInfo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final Dialog detailedInfoDialog = new Dialog(context, R.style.HorizonDialog);
						detailedInfoDialog.setContentView(R.layout.dialog_detailed_info);
						detailedInfoDialog.setCanceledOnTouchOutside(true);//������Ի�����������ʱ���رնԻ���
						
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
						
						String infoStr = "������ " + bookName + "\n��ʽ�� txt\n��С��" + fileSizeStr + 
						        "\n����Ķ�ʱ�䣺" + lateReadTimeStr + "\n��ַ�� " + address;
						
						TextView tv_info = (TextView)detailedInfoDialog.findViewById(R.id.tv_info);
						tv_info.setText(infoStr);
						
						detailedInfoDialog.show();
						dialog.cancel();
					}
					
					/**��ȡ�ļ���С*/
					public long getFileSizes(File f) throws Exception { 
				        long s = 0; 
				        if (f.exists()) { 
				            FileInputStream fis = new FileInputStream(f); 
				            s = fis.available(); 
				        }
				        return s; 
				    } 
					
					/**ת���ļ���С*/
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
	
	/**�����鼮*/
//	class BookClick implements OnClickListener {
//		@Override
//		public void onClick(View v) {
//			
//		}
//	}
	
//	/**�����鼮*/
//	class ClassifyBookLongClick implements OnLongClickListener {
//		@Override
//		public boolean onLongClick(View v) {
//			
//		}
//	}
	
	/**��ȡ��adapter��list���ݼ���*/
	public List<Map<String, Object>> getList() {
		return list;
	}

	/**���ô�adapter��list���ݼ���*/
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	
	/**
	 * ��������ϵ�ǰ��ʾ����ͼ��
	 * ������ int currentCategory������ţ��������Ƿ����ţ����߱�ʾ�������Ķ���ȫ����δ����ı�� String
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
