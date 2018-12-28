package com.horizon.model;

import com.horizon.R;

import java.util.Random;

/**�����鼮��Ϣ���࣬�����ڸ�ģ��䴫���鼮��Ϣ*/
public class Book {
	private int _id; //�鼮��ţ����ݿ������ֶ�
	private String name; //�鼮���ƣ�����
	private String address; //�鼮��ַ������
	private int cover;//�鼮���棬����,�����int��ָR�ļ��е�ͼƬ�Ķ�Ӧid
	private int classifyId = 0; //������
	private String classifyName; //��������
	private long latestReadTime = 0; //�����һ���Ķ�ʱ��
	private int myLove = 1; //�Ƿ��ҵ����0��true��1��false
	private int readRate = 0; //�Ķ�����
	private long wordsNum = 0;//������
	
	public Book() {
		
	}
	
	public Book(String name, String address, int cover) {
		this.name = name;
		this.address = address;
		this.cover = cover;
	}
	
	/**��ȡ�鼮���*/
	public int getId() {
		return _id;
	}
	/**��ȡ�鼮����*/
	public String getName() {
		return name;
	}
	/**��ȡ�鼮��ַ*/
	public String getAddress() {
		return address;
	}
	/**��ȡ����*/
	public int getCover() {
		return cover;
	}
	/**��ȡ������*/
	public int getClassifyId() {
		return classifyId;
	}
	/**��ȡ��������*/
	public String getClassifyName() {
		if(classifyId == 0) {
			return "δ����";
		}
		return classifyName;
	}
	/**��ȡ�����һ���Ķ�ʱ��*/
	public long getLatestReadTime() {
		return latestReadTime;
	}
	/**��ȡ�Ƿ��ҵ����0��true��1��false*/
	public int getMyLove() {
		return myLove;
	}
	/**��ȡ�Ķ�����*/
	public int getReadRate() {
		return readRate;
	}
	/**��ȡ������*/
	public long getWordsNum() {
		return wordsNum;
	}
	
	
	/**�����鼮���*/
	public void set_id(int _id) {
		this._id = _id;
	}
	/**�����鼮����*/
	public void setName(String name) {
		this.name = name;
	}
	/**�����鼮��ַ*/
	public void setAddress(String address) {
		this.address = address;
	}
	/**�����鼮����*/
	public void setCover(int cover) {
		this.cover = cover;
	}
	/**���÷�����*/
	public void setClassifyId(int classifyId) {
		this.classifyId = classifyId;
	}
	/**���÷�������*/
	public void setClassifyName(String classifyName) {
		this.classifyName = classifyName;
	}
	/**���������һ���Ķ�ʱ��*/
	public void setLatestReadTime(long latestReadTime) {
		this.latestReadTime = latestReadTime;
	}
	/**�����Ƿ��ҵ����0��true��1��false*/
	public void setMyLove(int myLove) {
		this.myLove = myLove;
	}
	/**�����Ķ�����*/
	public void setReadRate(int readRate) {
		this.readRate = readRate;
	}
	/**����������*/
	public void setWordsNum(long wordsNum) {
		this.wordsNum = wordsNum;
	}
	
	/**��ȡ�������*/
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
