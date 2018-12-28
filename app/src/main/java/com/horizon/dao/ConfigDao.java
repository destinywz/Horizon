package com.horizon.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/** 用来访问用户配置信息 */
public class ConfigDao {
	private SharedPreferences sp;

	public ConfigDao(Context context) {
		sp = context.getSharedPreferences("userConfig", Context.MODE_PRIVATE);
	}

	/**获取用户密码
	 * 参数：无 
	 * 返回值：String型，用户密码，默认不使用密码时返回值为null */
	public String getPaw() {
		return sp.getString("pwd", null);// 获取密码
	}

	/**判断软件是否夜间模式inNight 
	 * 参数：无 
	 * 返回值：boolean，true：夜间模式，false：日间模式*/
	public boolean getInNight() {
		return sp.getBoolean("inNight", false);
	}

	/**判断屏幕亮度是否跟随系统lightFollowSys 
	 * 参数：无 
	 * 返回值：boolean，true：跟随系统，false：自定义*/
	public boolean getLightFollowSys() {
		return sp.getBoolean("lightFollowSys", true);
	}

	/**获取屏幕亮度screenLight 
	 * 参数：无 
	 * 返回值：int*/
	public int getScreenLight() {
		return sp.getInt("screenLight", 1);// 注：默认值暂定为1，待商量
	}

	/**获取休息提醒时间restTime 
	 * 参数：无 
	 * 返回值：int （分钟）*/
	public int getRestTime() {
		return sp.getInt("restTime", -1);
	}

	/**获取屏幕关闭时间screenCloseTime 
	 * 参数：无 
	 * 返回值：int（分钟）*/
	public int getScreenCloseTime() {
		return sp.getInt("screenCloseTime", -1);
	}



	/**获取阅读页面左右边距pageRLMargin default:15
	 * 参数：无 
	 * 返回值：int*/
	public int getPageRLMargin() {
		return sp.getInt("pageRLMargin", 15);
	}

	/**获取阅读页面上下边距pageTBMargin default:20
	 * 参数：无 
	 * 返回值：int*/
	public int getPageTBMargin() {
		return sp.getInt("pageTBMargin", 25);
	}

	/**获取阅读页面行间距lineSpacing default:10
	 * 参数：无 
	 * 返回值：int*/
	public int getLineSpacing() {
		return sp.getInt("lineSpacing", 15);
	}

	//删除
	/**获取阅读页面段间距passageSpacing default:10
	 * 参数：无 
	 * 返回值：int*/
	public int getPassageSpacing() {
		return sp.getInt("passageSpacing", 10);
	}

	//删除
	/**判断阅读页面是否首行缩进textIndent 
	 * 参数：无 
	 * 返回值：boolean，true：首行缩进，false：首行不缩进*/
	public boolean getTextIndent() {
		return sp.getBoolean("textIndent", true);
	}

	//删除
	/**获取阅读页面翻页模式pageTurnType 
	 * 参数：无 
	 * 返回值：int 
	 *     com.horizon.global包中的相关全局变量： 
	 *     无动画：PageTurnType. NO_ANIMATION = 0 
	 *     真实翻页：PageTurnType. IMITATE = 1
	 *     横向覆盖：PageTurnType. CROSSWISE = 2 
	 *     自由拖动：PageTurnType. FREE_DRAG = 3*/
	//删除
	/**获取阅读页面默认主题defaultTheme。 default:
	 * 参数：无
	 * 返回值：int:返回R文件中的对应id*/
	public int getDefaultTheme() {
		return sp.getInt("defaultTheme", 0);// 注：默认主题待商量
	}

	/**获取阅读页面文字颜色txtColour default:0xff000000
	 * 参数：无 
	 * 返回值：int:返回R文件中的对应id*/
	public int getTxtColour() {
		return sp.getInt("txtColour", 0xff000000);
	}

	/**获取阅读页面文字大小txtSize default:24
	 * 参数：无 
	 * 返回值：int:返回R文件中的对应id*/
	public int getTxtSize() {
		return sp.getInt("txtSize", 60);
	}

	/**获取阅读页面背景颜色backColour default:0xfffed189
	 * 参数：无 
	 * 返回值：int:返回R文件中的对应id*/
	public int getBackColour() {
		return sp.getInt("backColour", 0xfffed189);
	}

	//删除
	/**获取阅读页面背景图片
	 * backImg 参数：无 
	 * 返回值：int:返回R文件中的对应id*/
	public int getBackImg() {
		return sp.getInt("backImg", 0);// 注：背景图片待商榷
	}

	
	
	/**设置用户密码
	 *  参数：paw (String) 
	 *  返回值：无*/
	public void setPaw(String paw) {
		Editor editor = sp.edit();
		editor.putString("pwd", paw);
		editor.commit();
	}
	
	/**删除用户密码
	 *  参数：paw (String) 
	 *  返回值：无*/
	public void removePaw() {
		Editor editor = sp.edit();
		editor.remove("pwd");
		editor.commit();
	}

	/**设置夜间模式inNight 
	 * 参数：inNight(boolean) 
	 * 返回值：无*/
	public void setInNight(boolean inNight) {
		Editor editor = sp.edit();
		editor.putBoolean("inNight", inNight);
		editor.commit();
	}

	/**设置屏幕亮度跟随系统lightFollowSys 
	 * 参数：lightFollowSys(boolean) 
	 * 返回值：无*/
	public void setLightFollowSys(boolean lightFollowSys) {
		Editor editor = sp.edit();
		editor.putBoolean("lightFollowSys", lightFollowSys);
		editor.commit();
	}
	
	/**设置屏幕亮度screenLight
	 * 参数：screenLight  (int)
	 * 返回值：无*/
	public void setScreenLight(int screenLight) {
		Editor editor = sp.edit();
		editor.putInt("screenLight", screenLight);
		editor.commit();
	}
	
	/**设置休息提醒时间restTime
	 * 参数：restTime  (int)
	 * 返回值：无*/
	public void setRestTime(int restTime) {
		Editor editor = sp.edit();
		editor.putInt("restTime", restTime);
		editor.commit();
	}
	
	/**设置屏幕关闭时间screenCloseTime
	 * 参数：screenCloseTime  (int)
	 * 返回值：无*/
	public void setScreenCloseTime(int screenCloseTime) {
		Editor editor = sp.edit();
		editor.putInt("screenCloseTime", screenCloseTime);
		editor.commit();
	}
	
	//删除
	/**设置阅读图书时的屏幕方向screenOrientation，默认为垂直方向：ScreenOrientation. VERTICAL。
	 * 参数：screenOrientation (int)
	 * com . horizon . global包中的相关全局变量：
	 * 屏幕水平显示： ScreenOrientation. HORIZONTAL = 0
	 * 屏幕垂直显示：ScreenOrientation. VERTICAL = 1
	 * 返回值：无*/
	public void setScreenOrientation(int screenOrientation) {
		Editor editor = sp.edit();
		editor.putInt("screenOrientation", screenOrientation);
		editor.commit();
	}
	
	/**设置阅读页面左右边距pageRLMargin。
	 * 参数：pageRLMargin (int)
	 * 返回值：无*/
	public void setPageRLMargin(int pageRLMargin) {
		Editor editor = sp.edit();
		editor.putInt("pageRLMargin", pageRLMargin);
		editor.commit();
	}
	
	/**设置阅读页面上下边距pageTBMargin。
	 * 参数：pageTBMargin (int)
	 * 返回值：无*/
	public void setPageTBMargin(int pageTBMargin) {
		Editor editor = sp.edit();
		editor.putInt("pageTBMargin", pageTBMargin);
		editor.commit();
	}
	
	/**设置阅读页面行间距lineSpacing。
	 * 参数：lineSpacing (int)
	 * 返回值：无*/
	public void setLineSpacing(int lineSpacing) {
		Editor editor = sp.edit();
		editor.putInt("lineSpacing", lineSpacing);
		editor.commit();
	}
	
	//删除
	/**设置阅读页面段间距passageSpacing。
	 * 参数：passageSpacing (int)
	 * 返回值：无*/
	public void setPassageSpacing(int passageSpacing) {
		Editor editor = sp.edit();
		editor.putInt("passageSpacing", passageSpacing);
		editor.commit();
	}
	
	//删除
	/**设置阅读页面首行缩进textIndent。
	 * 参数：textIndent (boolean)
	 * 返回值：无*/
	public void setTextIndent(boolean textIndent) {
		Editor editor = sp.edit();
		editor.putBoolean("textIndent", textIndent);
		editor.commit();
	}
	
	//删除
	/**设置阅读页面翻页模式pageTurnType。
	 * 参数：pageTurnType (int)
	 * com . horizon . global包中的相关全局变量：
	 * 无动画：  PageTurnType. NO_ANIMATION = 0
	 * 真实翻页：PageTurnType. IMITATE = 1
	 * 横向覆盖：PageTurnType. CROSSWISE = 2
	 * 自由拖动：PageTurnType. FREE_DRAG = 3
	 * 返回值：无*/
	public void setPageTurnType(int pageTurnType) {
		Editor editor = sp.edit();
		editor.putInt("pageTurnType", pageTurnType);
		editor.commit();
	}
	
	//删除
	/**设置阅读页面默认主题defaultTheme。
	 * 参数：defaultTheme (int)，传入R文件中的对应id
	 * 返回值：无*/
	public void setDefaultTheme(int defaultTheme) {
		Editor editor = sp.edit();
		editor.putInt("defaultTheme", defaultTheme);
		editor.commit();
	}
	
	/**设置阅读页面文字颜色txtColour。
	 * 参数：txtColour (int)，传入R文件中的对应id
	 * 返回值：无*/
	public void setTxtColour(int txtColour) {
		Editor editor = sp.edit();
		editor.putInt("txtColour", txtColour);
		editor.commit();
	}

	/**设置阅读页面文字大小txtSize
	 * 参数：无 
	 * 返回值：无*/
	public void setTxtSize(int txtSize) {
		Editor editor = sp.edit();
		editor.putInt("txtSize", txtSize);
		editor.commit();
	}

	/**设置阅读页面背景颜色backColour。
	 * 参数：backColour (int)，传入R文件中的对应id
	 * 返回值：无*/
	public void setBackColour(int backColour) {
		Editor editor = sp.edit();
		editor.putInt("backColour", backColour);
		editor.commit();
	}
	
	//删除
	/**设置阅读页面背景图片backImg
	 * 参数：backImg (int)，传入R文件中的对应id
	 * 返回值：无*/
	public void setBackImg(int backImg) {
		Editor editor = sp.edit();
		editor.putInt("backImg", backImg);
		editor.commit();
	}
}
