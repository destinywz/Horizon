package com.horizon.model;

import com.horizon.R;

import java.util.Random;

/**包含书籍信息的类，用来在各模块间传递书籍信息*/
public class Book {
	private int _id; //书籍编号，数据库自增字段
	private String name; //书籍名称，必填
	private String address; //书籍地址，必填
	private int cover;//书籍封面，必填,这里的int是指R文件中的图片的对应id
	private int classifyId = 0; //分类编号
	private String classifyName; //分类名称
	private long latestReadTime = 0; //最近后一次阅读时间
	private int myLove = 1; //是否我的最爱，0：true；1：false
	private int readRate = 0; //阅读进度
	private long wordsNum = 0;//总字数
	
	public Book() {
		
	}
	
	public Book(String name, String address, int cover) {
		this.name = name;
		this.address = address;
		this.cover = cover;
	}
	
	/**获取书籍编号*/
	public int getId() {
		return _id;
	}
	/**获取书籍名称*/
	public String getName() {
		return name;
	}
	/**获取书籍地址*/
	public String getAddress() {
		return address;
	}
	/**获取封面*/
	public int getCover() {
		return cover;
	}
	/**获取分类编号*/
	public int getClassifyId() {
		return classifyId;
	}
	/**获取分类名称*/
	public String getClassifyName() {
		if(classifyId == 0) {
			return "未分类";
		}
		return classifyName;
	}
	/**获取最近后一次阅读时间*/
	public long getLatestReadTime() {
		return latestReadTime;
	}
	/**获取是否我的最爱，0：true；1：false*/
	public int getMyLove() {
		return myLove;
	}
	/**获取阅读进度*/
	public int getReadRate() {
		return readRate;
	}
	/**获取总字数*/
	public long getWordsNum() {
		return wordsNum;
	}
	
	
	/**设置书籍编号*/
	public void set_id(int _id) {
		this._id = _id;
	}
	/**设置书籍名称*/
	public void setName(String name) {
		this.name = name;
	}
	/**设置书籍地址*/
	public void setAddress(String address) {
		this.address = address;
	}
	/**设置书籍封面*/
	public void setCover(int cover) {
		this.cover = cover;
	}
	/**设置分类编号*/
	public void setClassifyId(int classifyId) {
		this.classifyId = classifyId;
	}
	/**设置分类名称*/
	public void setClassifyName(String classifyName) {
		this.classifyName = classifyName;
	}
	/**设置最近后一次阅读时间*/
	public void setLatestReadTime(long latestReadTime) {
		this.latestReadTime = latestReadTime;
	}
	/**设置是否我的最爱，0：true；1：false*/
	public void setMyLove(int myLove) {
		this.myLove = myLove;
	}
	/**设置阅读进度*/
	public void setReadRate(int readRate) {
		this.readRate = readRate;
	}
	/**设置总字数*/
	public void setWordsNum(long wordsNum) {
		this.wordsNum = wordsNum;
	}
	
	/**获取随机封面*/
	public static int getRandomCover() {
		int[] coverId = new int[5];
		coverId[0] = R.drawable.bookcover;
		coverId[1] = R.drawable.bookcover;
		coverId[2] = R.drawable.bookcover;
		coverId[3] = R.drawable.bookcover;
		coverId[4] = R.drawable.bookcover;
		
		int ran = new Random().nextInt(5);
		return coverId[ran];
	}
}
