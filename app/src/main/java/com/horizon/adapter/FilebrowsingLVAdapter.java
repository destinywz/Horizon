package com.horizon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.horizon.R;
import com.horizon.ui.HomeT1Filebrowsing;

import java.util.List;
import java.util.Map;

public class FilebrowsingLVAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<Map<String, Object>> list; //�ļ��б�map����ĸ�key��fileImg��fileName��fileAddress��fileCheck
	

	public FilebrowsingLVAdapter(Context context, List<Map<String, Object>> list) {
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
		public ImageView img_file;
		public TextView tv_fileName;
		public CheckBox checkBox_fileCheck;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ����/��ȡholder
		ViewHolder holder = null;
		if (convertView == null) {
			// convertView��ʾĳһ�е�view���֣����Ѿ�inflate�˵Ĳ���
			convertView = layoutInflater.inflate(R.layout.filebrowsing_list_item, null);

			// ʵ����holder�е�weight
			holder = new ViewHolder();
			holder.img_file = (ImageView) convertView.findViewById(R.id.img_file);
			holder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_fileName);
			holder.checkBox_fileCheck = (CheckBox) convertView.findViewById(R.id.checkbox_fileCheck);

			// ��convertView��һ���������ݣ���ViewHolder���������������´ο���getTag()��ø�view��ֱ������
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// ��holder�еĿؼ���List<>����Դ��Ӧ����������Map�е�key�ֶζ�Ӧ
		holder.img_file.setImageResource(Integer.parseInt(String.valueOf(list.get(position).get("fileImg"))));
		holder.tv_fileName.setText((String) list.get(position).get("fileName"));
		holder.checkBox_fileCheck.setChecked((Boolean) list.get(position).get("fileCheck"));
		
		String address = (String)list.get(position).get("fileAddress");
		if(HomeT1Filebrowsing.isHandling && address.endsWith(".txt")) {
			holder.checkBox_fileCheck.setVisibility(View.VISIBLE);
		} else {
			holder.checkBox_fileCheck.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

}
