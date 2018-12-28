package com.horizon.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.horizon.model.Book;
import com.horizon.ui.HomeT2Bookshelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookInfoDao {
	public static DBOpenHelper helper; //����DBOpenHelper���ݿ���������
	public static SQLiteDatabase db;  //����SQLiteDatabase���ݿ����
	
	public BookInfoDao(Context context) {
		if(db == null) {
			helper = new DBOpenHelper(context);
			db = helper.getWritableDatabase();
		}
	}
	
	/**���һ���鼮��¼
	 * ������Book��name, address, cover�ֶα��cover��ʹ��Book��getRandomCover()������ȡ������棩
	 *         ��id:���ݿ����������classifyIdĬ��Ϊ0��Ϊ0�������ݿ�д��null��ѡ�myloveĬ��Ϊ1ѡ������ֶ�Ĭ��Ϊ0��
	 * ����ֵ��boolean �Ƿ�ִ�гɹ�*/
	public boolean addBook(Book bookInfo) {
		Object[] values = new Object[8];
		values[0] = bookInfo.getName();
		values[1] = bookInfo.getAddress();
		values[2] = bookInfo.getCover();
		int classifyId = bookInfo.getClassifyId();
		values[3] = classifyId == 0 ? null:classifyId;
		values[4] = bookInfo.getLatestReadTime();
		values[5] = bookInfo.getMyLove();
		values[6] = bookInfo.getReadRate();
		values[7] = bookInfo.getWordsNum();
		boolean success = true;
		try {
			db.execSQL("insert into tb_bookInfo(name,address,cover,classifyId,latestReadTime,myLove,readRate,wordsNum) "
					+ "values(?,?,?,?,?,?,?,?)",values);
		} catch (SQLException e) {
			success = false;
		}
		return success;
	}



    
    /**ɾ��һ���鼮��¼
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ����*/
    public void deleteBook(int bookId) {
    	db.execSQL("delete from tb_bookInfo where _id=?",new Object[]{bookId});
    }

    
	/**ɾ��һ�����Ŀ¼
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ����*/
    public void deleteCatalogue(int bookId) {
    	db.execSQL("delete from tb_catalogue where bookId=?",new Object[]{bookId});
    }

    
    /**ɾ��δ������鼮��¼
	 * ��������
	 * ����ֵ����*/
    public void deleteNoClassifyBooks() {
    	db.execSQL("delete from tb_bookInfo where classifyId is null");
    }
    
    /**ɾ�������鼮��¼
	 * ��������
	 * ����ֵ����*/
    public void deleteAllBooks() {
    	db.execSQL("delete from tb_bookInfo");
    }
    
	/**��ѯһ���������
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ��String ������ */
    public String getBookName(int bookId) {
    	Cursor cur = null;
    	String name = null;
    	try {
    		cur = db.rawQuery("select name from tb_bookInfo where _id=?", new String[]{String.valueOf(bookId)});
        	if (cur.moveToNext()) {
    			name = cur.getString(cur.getColumnIndex("name"));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	
    	return name;
    }

    
    /**��ȡ�鼮�����²����¼��_id��
	 * ��������
	 * ����ֵ��int �����²����_id�� */
    public int getLastTBBookInfoId() {
    	Cursor cur = null;
    	int id = -1;
    	try {
    		cur = db.rawQuery("select last_insert_rowid() from tb_bookInfo", null);
        	if (cur.moveToNext()) {
    			id = cur.getInt(0);
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return id;
    }
    
    /**ͨ����ַ����ѯһ�����������Ϣ
	 * ������address ��String�� ���鼮��ַ
	 * ����ֵ��Book ��һ���鼮����*/
    public Book getBookByAddress(String address) {
    	Cursor cur = null;
    	Book book = null;
    	try {
    		cur = db.rawQuery("select * from tb_bookInfo where address=?", new String[]{address});
        	if (cur.moveToNext()) {
        		book = new Book();
        		book.set_id(cur.getInt(cur.getColumnIndex("_id")));
        		book.setName(cur.getString(cur.getColumnIndex("name")));
        		book.setAddress(cur.getString(cur.getColumnIndex("address")));
        		book.setCover(cur.getInt(cur.getColumnIndex("cover")));
        		book.setClassifyId(cur.getInt(cur.getColumnIndex("classifyId")));
        		book.setLatestReadTime(cur.getLong(cur.getColumnIndex("latestReadTime")));
        		book.setMyLove(cur.getInt(cur.getColumnIndex("myLove")));
        		book.setReadRate(cur.getInt(cur.getColumnIndex("readRate")));
        		book.setWordsNum(cur.getLong(cur.getColumnIndex("wordsNum")));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return book;
    }
    
    /**ͨ����ַ����ȡ�鼮���
	 * ������address ��String�� ���鼮��ַ
	 * ����ֵ��int ���鼮��ţ��������򷵻�-1*/
    public int checkBookExistByAddress(String address) {
    	Cursor cur = null;
    	int id = -1;
    	try {
    		cur = db.rawQuery("select _id from tb_bookInfo where address=?", new String[]{address});
        	if (cur.moveToNext()) {
        		id = cur.getInt(cur.getColumnIndex("_id"));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return id;
    }
    
    /**��ȡĳ�����鼮�������鼮��ַ
	 * ������address ��String�� ���鼮��ַ
	 * ����ֵ��int ���鼮��ţ��������򷵻�-1*/
    public List<String> getAllAddressInClassify(int classifyId) {
    	Cursor cur = null;
    	List<String> list = new ArrayList<String>();
    	try {
    		cur = db.rawQuery("select address from tb_bookInfo where classifyId=?", new String[]{classifyId+""});
        	if (cur.moveToNext()) {
        		list.add(cur.getString(cur.getColumnIndex("address")));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return list;
    } 
    
	/**��ѯһ������鼮��ַ
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ��String ���鼮��ַ*/
    public String getBookAddress(int bookId) {
    	Cursor cur = null;
    	String str = null;
    	try {
    		cur = db.rawQuery("select address from tb_bookInfo where _id=?", new String[]{String.valueOf(bookId)});
        	if (cur.moveToNext()) {
    			str = cur.getString(cur.getColumnIndex("address"));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return str;
    }
	
    /**��ѯһ������Ķ�����
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ��int ���Ķ����ȣ��ֽ�����*/
    public int getReadRate(int bookId) {
    	Cursor cur = null;
    	int result = 0;
    	try {
    		cur = db.rawQuery("select readRate from tb_bookInfo where _id=?", new String[]{String.valueOf(bookId)});
        	if (cur.moveToNext()) {
        		result = cur.getInt(cur.getColumnIndex("readRate"));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return result;
    }
    
    /**��ѯһ�����������
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ��int ��������*/
    public int getWordsNum(int bookId) {
    	Cursor cur = null;
    	int result = -1;
    	try {
    		cur = db.rawQuery("select wordsNum from tb_bookInfo where _id=?", new String[]{String.valueOf(bookId)});
        	if (cur.moveToNext()) {
        		result = cur.getInt(cur.getColumnIndex("wordsNum"));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return result;
    }
    
	/**��ѯһ�����������Ϣ
	 * ������bookId ��int�� ���鼮���
	 * ����ֵ��Book ��һ���鼮����*/
    public Book getABook(int bookId) {
    	String sql = "select b._id,b.name,address,cover,classifyId,c.name as classifyName,latestReadTime,myLove,readRate,wordsNum "
    			+ "from tb_bookInfo as b left join tb_classifyInfo as c where b._id = ?";
    	Cursor cur = null;
    	Book book = null;
    	try {
        	cur = db.rawQuery(sql, new String[]{String.valueOf(bookId)});
        	if (cur.moveToNext()) {
        		book = new Book();
        		book.set_id(cur.getInt(cur.getColumnIndex("_id")));
        		book.setName(cur.getString(cur.getColumnIndex("name")));
        		book.setAddress(cur.getString(cur.getColumnIndex("address")));
        		book.setCover(cur.getInt(cur.getColumnIndex("cover")));
        		book.setClassifyId(cur.getInt(cur.getColumnIndex("classifyId")));
        		book.setClassifyName(cur.getString(cur.getColumnIndex("classifyName")));
        		book.setLatestReadTime(cur.getLong(cur.getColumnIndex("latestReadTime")));
        		book.setMyLove(cur.getInt(cur.getColumnIndex("myLove")));
        		book.setReadRate(cur.getInt(cur.getColumnIndex("readRate")));
        		book.setWordsNum(cur.getLong(cur.getColumnIndex("wordsNum")));
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
    	return book;
    }
    


    
	/**��������鼮��¼
	 * ��������
	 * ����ֵ��List<Map<String,Object>>
	 *       Map�е�Key�� ��
	 *           "id"����int�����鼮���
	 *           "name"����String�����鼮��
	 *           "address"����String�����鼮��ַ
	 *           "cover"����int�����鼮����,�����int��ָR�ļ��е�ͼƬ�Ķ�Ӧid
	 *           "classifyId����int��������id
	 *           "latestReadTime����long�������һ���Ķ�ʱ��
	 *           "myLove����int�����ҵ��
	 *           "readRate����int�����Ķ�����
	 *           "wordsNum����long����������"*/
	public List<Map<String, Object>> getAllBooks() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Cursor cur = null;
    	try {
    		cur = db.rawQuery("select * from tb_bookInfo", null);
    		while (cur.moveToNext()) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("id", cur.getString(cur.getColumnIndex("_id")));
    			map.put("name", cur.getString(cur.getColumnIndex("name")));
    			map.put("address", cur.getString(cur.getColumnIndex("address")));
    			map.put("cover", cur.getInt(cur.getColumnIndex("cover")));
    			map.put("classifyId", cur.getInt(cur.getColumnIndex("classifyId")));
    			map.put("latestReadTime", cur.getLong(cur.getColumnIndex("latestReadTime")));
    			map.put("myLove", cur.getInt(cur.getColumnIndex("myLove")));
    			map.put("readRate", cur.getInt(cur.getColumnIndex("readRate")));
    			map.put("wordsNum", cur.getLong(cur.getColumnIndex("wordsNum")));
    			list.add(map);
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
		return list;
	}


	/**��ѯδ���������ͼ��
	 * ��������
	 * ����ֵ��List<Map<String,Object>> ���鼮����
	 *      Map�е�Key�� ��
	 *           "id"����int�����鼮���
	 *           "name"����String�����鼮��
	 *           "address"����String�����鼮��ַ
	 *           "cover"����int�����鼮����,�����int��ָR�ļ��е�ͼƬ�Ķ�Ӧid
	 *           "classifyId����int��������id
	 *           "latestReadTime����long�������һ���Ķ�ʱ��
	 *           "myLove����int�����ҵ��
	 *           "readRate����int�����Ķ�����
	 *           "wordsNum����long����������"*/
	public List<Map<String, Object>> getNoClassifyBooks() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Cursor cur = null;
    	try {
    		cur = db.rawQuery("select * from tb_bookInfo where classifyId is null", null);
    		while (cur.moveToNext()) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("id", cur.getString(cur.getColumnIndex("_id")));
    			map.put("name", cur.getString(cur.getColumnIndex("name")));
    			map.put("address", cur.getString(cur.getColumnIndex("address")));
    			map.put("cover", cur.getInt(cur.getColumnIndex("cover")));
    			map.put("classifyId", cur.getInt(cur.getColumnIndex("classifyId")));
    			map.put("latestReadTime", cur.getLong(cur.getColumnIndex("latestReadTime")));
    			map.put("myLove", cur.getInt(cur.getColumnIndex("myLove")));
    			map.put("readRate", cur.getInt(cur.getColumnIndex("readRate")));
    			map.put("wordsNum", cur.getLong(cur.getColumnIndex("wordsNum")));
    			list.add(map);
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
		return list;
	}
	

	/**��ѯ����Ķ���ͼ��
	 * ������num��int��������Ķ���ǰnum��ͼ��
	 * ����ֵ��List<Map<String,Object>> ���鼮����
	 *      Map�е�Key�� ��
	 *           "id"����int�����鼮���
	 *           "name"����String�����鼮��
	 *           "address"����String�����鼮��ַ
	 *           "cover"����int�����鼮����,�����int��ָR�ļ��е�ͼƬ�Ķ�Ӧid
	 *           "classifyId����int��������id
	 *           "latestReadTime����long�������һ���Ķ�ʱ��
	 *           "myLove����int�����ҵ��
	 *           "readRate����int�����Ķ�����
	 *           "wordsNum����long����������"*/
	public List<Map<String, Object>> getLatestBooks(int num) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select * from tb_bookInfo order by latestReadTime desc limit " + num;
		Cursor cur = null;
    	try {
    		cur = db.rawQuery(sql, null);
    		while (cur.moveToNext()) {
    			long time = cur.getLong(cur.getColumnIndex("latestReadTime"));
    			if(time != 0) {
    				Map<String, Object> map = new HashMap<String, Object>();
    				map.put("id", cur.getString(cur.getColumnIndex("_id")));
    				map.put("name", cur.getString(cur.getColumnIndex("name")));
    				map.put("address", cur.getString(cur.getColumnIndex("address")));
    				map.put("cover", cur.getInt(cur.getColumnIndex("cover")));
    				map.put("classifyId", cur.getInt(cur.getColumnIndex("classifyId")));
    				map.put("latestReadTime", time);
    				map.put("myLove", cur.getInt(cur.getColumnIndex("myLove")));
    				map.put("readRate", cur.getInt(cur.getColumnIndex("readRate")));
    				map.put("wordsNum", cur.getLong(cur.getColumnIndex("wordsNum")));
    				list.add(map);
    			}
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
		
		return list;
	}
	
	/**ģ����ѯ����������ͼ��
	 * ������name��String������������ 
	 * ����ֵ��List<Map<String,Object>> ���鼮����
	 *      Map�е�Key�� ��
	 *           "id"����int�����鼮���
	 *           "name"����String�����鼮��
	 *           "address"����String�����鼮��ַ
	 *           "cover"����int�����鼮����,�����int��ָR�ļ��е�ͼƬ�Ķ�Ӧid
	 *           "classifyId����int��������id
	 *           "latestReadTime����long�������һ���Ķ�ʱ��
	 *           "myLove����int�����ҵ��
	 *           "readRate����int�����Ķ�����
	 *           "wordsNum����long����������"*/
	public List<Map<String, Object>> getSimilarNameBooks(String name) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select * from tb_bookInfo where name like '%" + name + "%'";
		Cursor cur = null;
    	try {
    		cur = db.rawQuery(sql, null);
    		while (cur.moveToNext()) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("id", cur.getString(cur.getColumnIndex("_id")));
    			map.put("name", cur.getString(cur.getColumnIndex("name")));
    			map.put("address", cur.getString(cur.getColumnIndex("address")));
    			map.put("cover", cur.getInt(cur.getColumnIndex("cover")));
    			map.put("classifyId", cur.getInt(cur.getColumnIndex("classifyId")));
    			map.put("latestReadTime", cur.getLong(cur.getColumnIndex("latestReadTime")));
    			map.put("myLove", cur.getInt(cur.getColumnIndex("myLove")));
    			map.put("readRate", cur.getInt(cur.getColumnIndex("readRate")));
    			map.put("wordsNum", cur.getLong(cur.getColumnIndex("wordsNum")));
    			list.add(map);
    		}
		} 
    	catch (SQLException e) {
		}
    	finally {
    		if(cur != null) {
    			cur.close();
    		}
    	}
		return list;
	}
	
	/**�޸��鼮������ע����ʱͬʱҲ�޸����鼮��ַ��
	 * ������bookId ��int�� ���鼮���
	 *     newBookName (String) �����鼮��
	 *     newAddress(String):�µ�ַ
	 * ����ֵ����*/
	public void alterBookName(int bookId, String newBookName, String newAddress) {
		String sql = "update tb_bookInfo set name=?,address=? where _id=?";
		db.execSQL(sql, new Object[]{newBookName, newAddress, bookId});
	}
	

	

	
	/**�޸��鼮�����һ���Ķ�ʱ��
	 * ������bookId ��int�� ���鼮���
	 *     latestReadTime (long) �����һ���Ķ�ʱ�䣨��������
	 * ����ֵ����*/
	public void alterLatestReadTime(int bookId, long latestReadTime) {
		String sql = "update tb_bookInfo set latestReadTime=? where _id=?";
		db.execSQL(sql, new Object[]{latestReadTime, bookId});
	}
	
	/**�޸��鼮���Ķ�����
	 * ������bookId ��int�� ���鼮���
	 *     rate(int) ���Ķ����ȣ��ֽ�����
	 * ����ֵ����*/
	public void alterReadRate(int bookId, int rate) {
		String sql = "update tb_bookInfo set readRate=? where _id=?";
		db.execSQL(sql, new Object[]{rate, bookId});
	}
}
