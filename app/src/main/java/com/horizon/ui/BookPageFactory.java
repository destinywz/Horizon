package com.horizon.ui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;
import com.horizon.dao.BookInfoDao;
import com.horizon.dao.ConfigDao;
import com.horizon.model.Book;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.widget.Toast;

public class BookPageFactory {

	/**  用户配置  */
	ConfigDao mConfig;
	Book mBook;
	BookInfoDao mBookInfo;
	
	final private Context context;
	/**  书籍文件  */
	private File book_file = null;
	/**  获取书籍内容缓存  */
	private MappedByteBuffer m_mbBuf = null;
	/**  字节总长度  */
	private int m_mbBufLen = 0;
	/**  字节开始位置 */
	private int m_mbBufBegin = 0;
	/**  字节结束位置 */
	private int m_mbBufEnd = 0;
	/**  汉字国标扩展码  */
	private String m_strCharsetName = "gbk";
	/**  背景图片  */
	private Bitmap m_book_bg = null;
	/**  屏幕宽度  */
	private int mWidth;
	/**  屏幕高度  */
	private int mHeight;
	/**  每页行集  */
	private Vector<String> m_lines = new Vector<String>();
	/**  文字大小  */
	private int m_fontSize;// = 24;//
	/**  文字颜色  */
	private int m_textColor;// = Color.BLACK;//
	/**  背景颜色  */
	private int m_backColor;// = Color.argb(255, 254, 209, 137); // 背景颜色  
	/**  左右与边缘的距离  */
	private int marginWidth;// = 15; // 左右与边缘的距离  
	/**  上下与边缘的距离  */
	private int marginHeight;// = 20; // 上下与边缘的距离  
	/**  每页可以显示的行数*/
	private int mLineCount; // 每页可以显示的行数
	/**  绘制内容的高  */
	private float mVisibleHeight; // 绘制内容的宽
	/**  绘制内容的宽  */
	private float mVisibleWidth; // 绘制内容的宽
	/**  判断是否为首页/尾页  */
	private boolean m_isfirstPage,m_islastPage;
	/**  行距*/
	private int m_nLineSpaceing;// = 10;//
	/**  描绘文本内容 */
	private Paint mPaint;
	/**  描绘百分比  */
	private Paint percentPaint;
	/**  百分比  */
	private String strPercent;
	
	
	/**
	 * 构造
	 * @param context 当前上下文
	 * @param w 屏幕宽度
	 * @param h 屏幕高度
	 */
	public BookPageFactory(Context context, int w, int h) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mConfig = new ConfigDao(context);
		mBookInfo = new BookInfoDao(context);
		mWidth = w;
		mHeight = h;
		
		// percentPaint.设置不变样式  ；写百分比专用
		percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		percentPaint.setTextAlign(Align.LEFT);
		percentPaint.setTextSize(24);//字体大小 24
		// 下面是几个设置paint的。
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
//		//用户配置
		getUserConfig();
	}
	
	/** 
	 * 打开书籍
	 * @param book 书籍实体
	 *  */
	public void openbook(Book book) throws IOException {
		this.mBook = book;
		book_file = new File(book.getAddress());
		/** 总字节长度  */
		long lLen;//字节长度，有多少个字节
		if(book.getWordsNum() == 0) {
			lLen = book_file.length();
			m_mbBufLen = (int) lLen;//一样的
			book.setWordsNum(m_mbBufLen);
		} else {
			m_mbBufLen = (int)book.getWordsNum();
		}
		m_mbBufBegin = book.getReadRate();
		m_mbBufEnd = book.getReadRate();
		Log.d("openbook", Integer.toString(m_mbBufLen)+"   "+Integer.toString(m_mbBufBegin)+"   "+Integer.toString(m_mbBufEnd));
		//MappedByteBuffer 缓存
		m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map( FileChannel.MapMode.READ_ONLY, 0, m_mbBufLen);
		
		getCutChapters();
	}
	
	public void getCutChapters() {
		//匹配目录资料
			Thread handlerBarThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					byte[] buf = new byte[m_mbBufLen];
					for (int j = 0; j < m_mbBufLen; j++) {
						buf[j] = m_mbBuf.get(j);
					}
					String cont ="";
					try {
						cont = new String(buf, m_strCharsetName);
						cont.replaceAll("\r\n", "");
						cont.replaceAll("\n", "");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Log.i("m_mbBuf", cont.substring(0, 100));
					
//					String cont = "";
//					Vector<String> sTemp = new Vector<String>();
//					sTemp = getAllString(0);
//					for(int i = 0; i<sTemp.size();i++) {
//						cont += sTemp.get(i); 
//					}
//					Log.i("getAllString()", Integer.toString(sTemp.size()));
//					Log.i("getAllString()", cont.substring(0, 100));
//					CutChapters cutChap = new CutChapters(context, mBook.getId(), cont);
					


					
				}
				
			});
			handlerBarThread.start();

	}
	
	/**
	 * 获取所有文本
	 * @return Vector<String> 字符串矢量集
	 */
	protected Vector<String> getAllString(int abegin) {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (abegin < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(abegin); // 读取一个段落
			abegin += paraBuf.length;//新的结束点为每一段的最后位置
//			Log.e("BookPageFactory.pageDown()", Integer.toString(abegin));
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
//				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
//				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}
			lines.add(strParagraph);
		}
		return lines;//返回下一页的内容
	}
	
	/** 
	 * 读取前一段落 
	 *  @param nFromPos 结束点
	 *  */
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;//前一段的开始位置
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {//0x0a为换行,0x00为补位符
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;// 之前的结束点， 往回获取
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		
		int nParaSize = nEnd - i;//前一段的长度
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}

	/** 
	 * 读取下一段落 
	 *  @param nFromPos 结束点
	 * */
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;//下一段结束的位置
		byte b0, b1;
		// 根据编码格式判断换行
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {  // \r\n?
					break;
				}
			}
		}
		int nParaSize = i - nStart;//下一段的长度
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	/**
	 * 获取下一页
	 * @return Vector<String> 字符串矢量集
	 */
	protected Vector<String> pageDown() {
//		if(m_mbBufEnd > 0 && m_mbBufEnd != m_mbBufLen) {
//			m_mbBufBegin = m_mbBufEnd;
//		}
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 读取一个段落
			m_mbBufEnd += paraBuf.length;//新的结束点为每一段的最后位置
			Log.e("BookPageFactory.pageDown()", Integer.toString(m_mbBufEnd));
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) { 
				float str_pixel = mPaint.measureText(strParagraph);
				System.out.println(str_pixel);
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);//切割文本, 返回本行长度
				lines.add(strParagraph.substring(0, nSize));// 行集中添加本行
				strParagraph = strParagraph.substring(nSize);// 去掉已添加的行
				if (lines.size() >= mLineCount) {
					System.out.println(lines.size());
					if (strParagraph.length() != 0) {
						try {
//							m_mbBufEnd -= strParagraph.getBytes(m_strCharsetName).length;
							m_mbBufEnd -= (strParagraph + strReturn).getBytes(m_strCharsetName).length;
							Log.e("BookPageFactory.pageDown()--lines.size() >= mLineCount--strParagraph.length() != 0", Integer.toString(m_mbBufEnd));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				}
			}
		}
		return lines;//返回下一页的内容
	}
	
	/**
	 * 获取前一页
	 */
	protected void pageUp() { 
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
		} //else {
//			m_mbBufEnd = m_mbBufBegin;
//		}
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;//设置新的起点为前一段首字符的位置
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;//设置新起点为上一页首字符的位置
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	/**
	 * 描绘上一页
	 * @throws IOException
	 */
	protected void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;//第一页
			m_isfirstPage=true;
			Toast.makeText(context, "已经是第一页", Toast.LENGTH_SHORT).show();
			return;
		}else m_isfirstPage=false;
		m_lines.clear();//清空
		pageUp();// m_mbBufEnd = m_mbBufBegin;
		m_lines = pageDown();
	}

	/**
	 * 描绘下一页
	 * @throws IOException
	 */
	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage=true;
			Toast.makeText(context, "已经是最后一页", Toast.LENGTH_SHORT).show();
			return;
		}else m_islastPage=false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;//把之前的结尾地方 为下一个开始位置
		m_lines = pageDown();
	}

	public void onDraw(Canvas c) {
		getUserConfig();

		Log.e("BookPageFactory.onDraw()", Integer.toString(m_mbBufBegin));
		
		if (m_lines.size() == 0)
			m_lines = pageDown();//初始化时为0
		if (m_lines.size() > 0) {
//			if (m_book_bg == null)
				c.drawColor(m_backColor);
//			else
//				c.drawBitmap(m_book_bg, 0, 0, null);
			int y = marginHeight;
			for (String strLine : m_lines) {//每读取一行就绘一行的文字起点是marginwidth,x,marginHeight+字体大小
				y += (m_fontSize + m_nLineSpaceing);//+ 行距
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		//开始描绘百分比
		calcStrPercent(m_mbBufBegin);
		int nPercentWidth = (int) percentPaint.measureText("999.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, percentPaint);
		Log.e("BookPageFactory.onDraw()", strPercent);
		mBookInfo.alterReadRate(mBook.getId(), m_mbBufBegin);
		Log.e("BookPageFactory.onDraw()", Integer.toString(mBook.getId())+" and "+Integer.toString(m_mbBufBegin));
        mBookInfo.alterLatestReadTime(mBook.getId(), Calendar.getInstance().getTimeInMillis());
//		Toast.makeText(context, "Time is " + Long.toString(day1)+"  "+sdf.format(day1), Toast.LENGTH_LONG).show();
	}

	/**
	 * 设置阅读模式
	 * @param textColor 文字颜色
	 * @param bgColor 背景颜色
	 */
	public void setTheme(int textColor, int bgColor) {
		this.m_textColor = textColor;
		this.m_backColor = bgColor;
	}
	
	/**
	 * 获取用户配置信息
	 */
	public void getUserConfig() {
		marginWidth = mConfig.getPageRLMargin();
		marginHeight = mConfig.getPageTBMargin();
		m_nLineSpaceing = mConfig.getLineSpacing();
		m_backColor = mConfig.getBackColour();
		m_textColor = mConfig.getTxtColour();
		m_fontSize = mConfig.getTxtSize();
		mVisibleWidth = mWidth - marginWidth * 2;// 绘制内容的宽   上下左右的边缘都空一定的距离 所以*2
		mVisibleHeight = mHeight - marginHeight * 2;// 绘制内容的搞
		mLineCount = (int) (mVisibleHeight / (m_fontSize + m_nLineSpaceing)); // 可显示的行数 / 可显示的高度除于每个字体的高度+行距
		
		//更新设置
		percentPaint.setColor(m_textColor);
		mPaint.setColor(m_textColor);//黑体
		mPaint.setTextSize(m_fontSize);//字体大小 24
	}
	
	
	/**
	 * 是否是第一页
	 * @return m_isfirstPage
	 */
	public boolean isfirstPage() {
		return m_isfirstPage;
	}
	/**
	 * 是否是最后一页
	 * @return m_islastPage
	 */
	public boolean islastPage() {
		return m_islastPage;
	}
	
	
	// 下面是书签 需要使用到的两个数据
	public int getM_mbBufBegin() {
		return m_mbBufBegin;
	}
	public void setM_mbBufBegin(int m_mbBufBegin) {
		this.m_mbBufBegin = m_mbBufBegin;
	}

	public Vector<String> getM_lines() {
		return m_lines;
	}
	public void setM_lines(Vector<String> m_lines) {
		this.m_lines = m_lines;
	}
	public void clearM_lines(){
		this.m_lines.clear();
	}
	
	public int getM_mbBufEnd() {
		return m_mbBufEnd;
	}
	public void setM_mbBufEnd(int m_mbBufEnd) {
		this.m_mbBufEnd = m_mbBufEnd;
	}

	public int getM_fontSize() {
		return m_fontSize;
	}
	public void setM_fontSize(int m_fontSize) {
		this.m_fontSize = m_fontSize;
		mPaint.setTextSize(m_fontSize);
		mLineCount = (int) (mVisibleHeight / m_fontSize); // 可显示的行数 / 可显示的高度除于每个字体的高度
	}

	public String getStrPercent() {
		return strPercent;
	}
	public void setStrPercent(String strPercent) {
		this.strPercent = strPercent;
	}
	public String calcStrPercent(int position) {
		float fPercent= (float) (position * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		 strPercent = df.format(fPercent * 100) + "%";
		return strPercent;
	}
	
}
