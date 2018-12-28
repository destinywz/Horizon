package com.horizon.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1; //数据库版本号
    public static final String DBNAME = "horizon.db"; //数据库名
    public static final String TB_BOOK = "tb_bookInfo"; //书籍信息表
    public static final String TB_CLASSIFY = "tb_classifyInfo"; //书籍分类表
    public static final String TB_CATALOGUE = "tb_catalogue"; //书籍目录表
    public static final String TB_LABEL = "tb_bookLabel"; //书籍书签表
    
    public DBOpenHelper(Context context) {
    	super(context, DBNAME, null, VERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建书籍信息表
		db.execSQL("create table tb_bookInfo(_id integer primary key autoincrement,name text NOT NULL,address text UNIQUE," + 
	            "cover integer NOT NULL,classifyId integer CONSTRAINT fk_classify_id REFERENCES tb_classifyInfo(_id)," + 
	            "latestReadTime integer,myLove integer NOT NULL,readRate integer,wordsNum integer)");
		//创建书籍分类表
		db.execSQL("create table tb_classifyInfo(_id integer primary key autoincrement,name text UNIQUE)");
		//创建书籍目录表
		db.execSQL("create table tb_catalogue(bookId integer CONSTRAINT fk_book_id1 REFERENCES tb_bookInfo(_id)," + 
		         "chaptersName text NOT NULL,rate integer NOT NULL)");
		
		//创建书籍书签表                                                                     
				db.execSQL("create table tb_bookLabel(_Id integer primary key autoincrement,bookId integer CONSTRAINT fk_book_id2 REFERENCES tb_bookInfo(_id)," + 
				         "labelName text NOT NULL,rate integer NOT NULL,progress text NOT NULL)");
		
		//插入3本示例书籍
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
