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
	public static DBOpenHelper helper; //定义DBOpenHelper数据库帮助类对象
	public static SQLiteDatabase db;  //定义SQLiteDatabase数据库对象
	
	public BookInfoDao(Context context) {
		if(db == null) {
			helper = new DBOpenHelper(context);
			db = helper.getWritableDatabase();
		}
	}
	
	/**添加一条书籍记录
	 * 参数：Book（name, address, cover字段必填，cover可使用Book的getRandomCover()方法获取随机封面）
	 *         （id:数据库自增不必填，classifyId默认为0（为0则往数据库写进null）选填，mylove默认为1选填，其他字段默认为0）
	 * 返回值：boolean 是否执行成功*/
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



    
    /**删除一条书籍记录
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：无*/
    public void deleteBook(int bookId) {
    	db.execSQL("delete from tb_bookInfo where _id=?",new Object[]{bookId});
    }

    
	/**删除一本书的目录
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：无*/
    public void deleteCatalogue(int bookId) {
    	db.execSQL("delete from tb_catalogue where bookId=?",new Object[]{bookId});
    }

    
    /**删除未分类的书籍记录
	 * 参数：无
	 * 返回值：无*/
    public void deleteNoClassifyBooks() {
    	db.execSQL("delete from tb_bookInfo where classifyId is null");
    }
    
    /**删除所有书籍记录
	 * 参数：无
	 * 返回值：无*/
    public void deleteAllBooks() {
    	db.execSQL("delete from tb_bookInfo");
    }
    
	/**查询一本书的书名
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：String ：书名 */
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

    
    /**获取书籍表最新插入记录的_id号
	 * 参数：无
	 * 返回值：int ：最新插入的_id号 */
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
    
    /**通过地址，查询一本书的所有信息
	 * 参数：address （String） ：书籍地址
	 * 返回值：Book ：一个书籍对象*/
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
    
    /**通过地址，获取书籍编号
	 * 参数：address （String） ：书籍地址
	 * 返回值：int ：书籍编号，不存在则返回-1*/
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
    
    /**获取某分类书籍的所有书籍地址
	 * 参数：address （String） ：书籍地址
	 * 返回值：int ：书籍编号，不存在则返回-1*/
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
    
	/**查询一本书的书籍地址
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：String ：书籍地址*/
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
	
    /**查询一本书的阅读进度
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：int ：阅读进度（字节数）*/
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
    
    /**查询一本书的总字数
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：int ：总字数*/
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
    
	/**查询一本书的所有信息
	 * 参数：bookId （int） ：书籍编号
	 * 返回值：Book ：一个书籍对象*/
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
    


    
	/**获得所有书籍记录
	 * 参数：无
	 * 返回值：List<Map<String,Object>>
	 *       Map中的Key有 ：
	 *           "id"——int——书籍编号
	 *           "name"——String——书籍名
	 *           "address"——String——书籍地址
	 *           "cover"——int——书籍封面,这里的int是指R文件中的图片的对应id
	 *           "classifyId——int——分类id
	 *           "latestReadTime——long——最后一次阅读时间
	 *           "myLove——int——我的最爱
	 *           "readRate——int——阅读进度
	 *           "wordsNum——long——总字数"*/
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


	/**查询未分类的所有图书
	 * 参数：无
	 * 返回值：List<Map<String,Object>> ：书籍集合
	 *      Map中的Key有 ：
	 *           "id"——int——书籍编号
	 *           "name"——String——书籍名
	 *           "address"——String——书籍地址
	 *           "cover"——int——书籍封面,这里的int是指R文件中的图片的对应id
	 *           "classifyId——int——分类id
	 *           "latestReadTime——long——最后一次阅读时间
	 *           "myLove——int——我的最爱
	 *           "readRate——int——阅读进度
	 *           "wordsNum——long——总字数"*/
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
	

	/**查询最近阅读的图书
	 * 参数：num（int）：最近阅读的前num本图书
	 * 返回值：List<Map<String,Object>> ：书籍集合
	 *      Map中的Key有 ：
	 *           "id"——int——书籍编号
	 *           "name"——String——书籍名
	 *           "address"——String——书籍地址
	 *           "cover"——int——书籍封面,这里的int是指R文件中的图片的对应id
	 *           "classifyId——int——分类id
	 *           "latestReadTime——long——最后一次阅读时间
	 *           "myLove——int——我的最爱
	 *           "readRate——int——阅读进度
	 *           "wordsNum——long——总字数"*/
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
	
	/**模糊查询类似书名的图书
	 * 参数：name（String）：类似书名 
	 * 返回值：List<Map<String,Object>> ：书籍集合
	 *      Map中的Key有 ：
	 *           "id"——int——书籍编号
	 *           "name"——String——书籍名
	 *           "address"——String——书籍地址
	 *           "cover"——int——书籍封面,这里的int是指R文件中的图片的对应id
	 *           "classifyId——int——分类id
	 *           "latestReadTime——long——最后一次阅读时间
	 *           "myLove——int——我的最爱
	 *           "readRate——int——阅读进度
	 *           "wordsNum——long——总字数"*/
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
	
	/**修改书籍名。（注：此时同时也修改了书籍地址）
	 * 参数：bookId （int） ：书籍编号
	 *     newBookName (String) ：新书籍名
	 *     newAddress(String):新地址
	 * 返回值：无*/
	public void alterBookName(int bookId, String newBookName, String newAddress) {
		String sql = "update tb_bookInfo set name=?,address=? where _id=?";
		db.execSQL(sql, new Object[]{newBookName, newAddress, bookId});
	}
	

	

	
	/**修改书籍的最后一次阅读时间
	 * 参数：bookId （int） ：书籍编号
	 *     latestReadTime (long) ：最后一次阅读时间（毫秒数）
	 * 返回值：无*/
	public void alterLatestReadTime(int bookId, long latestReadTime) {
		String sql = "update tb_bookInfo set latestReadTime=? where _id=?";
		db.execSQL(sql, new Object[]{latestReadTime, bookId});
	}
	
	/**修改书籍的阅读进度
	 * 参数：bookId （int） ：书籍编号
	 *     rate(int) ：阅读进度（字节数）
	 * 返回值：无*/
	public void alterReadRate(int bookId, int rate) {
		String sql = "update tb_bookInfo set readRate=? where _id=?";
		db.execSQL(sql, new Object[]{rate, bookId});
	}
}
