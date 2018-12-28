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
	//List书籍集合 Map中的Key有 ： 
	//"id"——int——书籍编号       "name"——String——书籍名
	//"address"——String——书籍地址        "cover"——int——书籍封面,这里的int是指R文件中的图片的对应id 
	//"classifyId——int——分类id  "latestReadTime——long——最后一次阅读时间 
	//"myLove——int——我的最爱        "readRate——int——阅读进度 
	//"wordsNum——long——总字数        "isCheck——boolean——图书是否被选中（默认不选中）"


	public NeaterGVAdapter(Context context, List<Map<String, Object>> list) {
		// 获得LayoutInflater的实例
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size(); // 返回列表长度
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);// 返回该位置的Map<>
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
		// 设置/获取holder
		ViewHolder holder = null;
		if (convertView == null) {
			// convertView表示某一行的view布局，是已经inflate了的布局

			// 实例化holder中的weight
			holder = new ViewHolder();
			holder.btn_book = (Button)convertView.findViewById(R.id.btn_book);

			// 给convertView绑定一个额外数据，把ViewHolder缓存起来，方便下次可以getTag()获得该view，直接重用
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 把holder中的控件与List<>数据源对应起来，即与Map中的key字段对应
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

	/** 获取此adapter的list数据集合 */
	public List<Map<String, Object>> getList() {
		return list;
	}

	/** 设置此adapter的list数据集合 */
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}
