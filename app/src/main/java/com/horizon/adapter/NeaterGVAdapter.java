package com.horizon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import com.horizon.R;

import java.util.List;
import java.util.Map;

public class NeaterGVAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> list;
	//List�鼮���� Map�е�Key�� �� 
	//"id"����int�����鼮���       "name"����String�����鼮��
	//"address"����String�����鼮��ַ        "cover"����int�����鼮����,�����int��ָR�ļ��е�ͼƬ�Ķ�Ӧid 
	//"classifyId����int��������id  "latestReadTime����long�������һ���Ķ�ʱ�� 
	//"myLove����int�����ҵ��        "readRate����int�����Ķ����� 
	//"wordsNum����long����������        "isCheck����boolean����ͼ���Ƿ�ѡ�У�Ĭ�ϲ�ѡ�У�"


	public NeaterGVAdapter(Context context, List<Map<String, Object>> list) {
		// ���LayoutInflater��ʵ��
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size(); // �����б���
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);// ���ظ�λ�õ�Map<>
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public final class ViewHolder {
		public Button btn_book;
		public CheckBox checkBox_book;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ����/��ȡholder
		ViewHolder holder = null;
		if (convertView == null) {
			// convertView��ʾĳһ�е�view���֣����Ѿ�inflate�˵Ĳ���

			// ʵ����holder�е�weight
			holder = new ViewHolder();
			holder.btn_book = (Button)convertView.findViewById(R.id.btn_book);

			// ��convertView��һ���������ݣ���ViewHolder���������������´ο���getTag()��ø�view��ֱ������
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// ��holder�еĿؼ���List<>����Դ��Ӧ����������Map�е�key�ֶζ�Ӧ
		holder.btn_book.setBackgroundResource(Integer.parseInt(String.valueOf(list.get(position).get("cover"))));
		holder.btn_book.setText((String) list.get(position).get("name"));
		holder.checkBox_book.setChecked((Boolean)list.get(position).get("isCheck"));
		
		final CheckBox checkBox_book = holder.checkBox_book;
		final Integer bookId = Integer.parseInt(String.valueOf(list.get(position).get("id")));
		
		holder.btn_book.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox_book.isChecked()) {
					checkBox_book.setChecked(false);
				} else {
					checkBox_book.setChecked(true);
				}
			}
		});

		holder.checkBox_book.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox_book.isChecked()) {
					checkBox_book.setChecked(true);
				} else {
					checkBox_book.setChecked(false);
					
				}
			}
		});
		
		return convertView;
	}

	/** ��ȡ��adapter��list���ݼ��� */
	public List<Map<String, Object>> getList() {
		return list;
	}

	/** ���ô�adapter��list���ݼ��� */
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}
