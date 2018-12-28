package com.horizon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class BookshelfViewGroup extends ViewGroup {
	public static final int SCREEN_MENU = 0; //菜单
	public static final int SCREEN_MAIN = 1; //主屏幕
	private static final int SCREEN_INVALID = -1; //无效
	
	private int mCurrentScreen; //当前显示的屏幕
	private int mNextScreen = SCREEN_INVALID; //下一个要显示的屏幕

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	
	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private static final int SNAP_VELOCITY = 1000;

	public int mTouchState = TOUCH_STATE_REST;
	private boolean mLocked;
	private boolean mAllowLongPress;

	public BookshelfViewGroup(Context context) {
		this(context, null, 0);
	}

	public BookshelfViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BookshelfViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mScroller = new Scroller(getContext());
		mCurrentScreen = SCREEN_MAIN;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

    //测量视图以确定其内容宽度和高度,需要被子类重写以提供对其内容准确高效的测量。
    //调用measure(int, int)方法确认viewGroup下的子view/layout的宽和高。
    //参数应该是父view的宽和高
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//确定菜单屏幕的宽和高
		View menuView = getChildAt(0);
		menuView.measure(menuView.getLayoutParams().width + menuView.getLeft()
				+ menuView.getRight(), heightMeasureSpec);

		//确定主屏幕的宽和高
		View contentView = getChildAt(1);
		contentView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	//viewGroup需要为子视图分配大小和位置时候调用，子类继承必须要重载此方法并调用自己子视图的layout函数。
	//这里为菜单屏幕和主屏幕设定初始位置，这里菜单屏幕往左偏移，从而达到隐藏效果
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//为菜单layout设定位置
		View menuView = getChildAt(0);
		final int width = menuView.getMeasuredWidth(); //获取菜单屏幕的宽度
		menuView.layout(-width, 0, 0, menuView.getMeasuredHeight());//左、上、右、下的位置

		//为主屏幕layout设定位置
		View contentView = getChildAt(1);
		contentView.layout(0, 0, contentView.getMeasuredWidth(),
				contentView.getMeasuredHeight());
	}
	
	//当View以及所有子项从XML中导入时被调用，是导入的最后一步
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View child;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			child.setFocusable(true);//设置该child可获得焦点
			child.setClickable(true);//设置该child可被点击（可响应用户操作）
		}
	}
	
	/**实现此方法是为了拦截所有触摸屏幕时的运动事件。可以像处理发送给子视图的事件一样去监视这些事件，并且获取当前手势在任意点的ownership
	 * 使用此方法时候需要注意，因为它与View.onTouchEvent(MotionEvent)有相当复杂的交互，并且前提需要正确执行View.onTouchEvent(MotionEvent)。事件将按照如下顺序接收到：
	 * 1. 收到down事件
	 * 2. Down事件或者由视图组的一个子视图处理，或者被用户自己的onTouchEvent()方法处理；此处理意味你应该执行onTouchEvent()时返回true，这样才能继续看到剩下的手势（取代找一个父视图处理）。
	 *    如果onTouchEvent()返回true时，你不会收到onInterceptTouchEvent()的任何事件并且所有对触摸的处理必须在onTouchEvent()中发生。
	 * 3. 如果此方法返回false，接下来的事件（up to and including the final up）将最先被传递当此，然后是目标的onTouchEvent()。
	 * 4. 如果返回true，将不会收到以下任何事件：目标view将收到同样的事件但是会伴随ACTION_CANCEL，并且所有的更进一步的事件将会传递到你自己的onTouchEvent()方法中而不会再在这里出现。
	 * 参数    ev          体系向下发送的动作事件
	 * 返回值     如果将运动事件从子视图中截获并且通过onTouchEvent()发送到当前ViewGroup ，返回true。当前目标将会收到ACTION_CANCEL事件，并且不再会有其他消息传递到此。 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mLocked) {
			return true;
		}

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:

			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			final int touchSlop = mTouchSlop;
			boolean xMoved = xDiff > touchSlop;
			boolean yMoved = yDiff > touchSlop;

			if (xMoved || yMoved) {

				if (xMoved) {
					// Scroll if the user moved far enough along the X axis
					mTouchState = TOUCH_STATE_SCROLLING;
					enableChildrenCache();
				}
				// Either way, cancel any pending longpress
				if (mAllowLongPress) {
					mAllowLongPress = false;
					// Try canceling the long press. It could also have been
					// scheduled
					// by a distant descendant, so use the mAllowLongPress flag
					// to block
					// everything
					final View currentScreen = getChildAt(mCurrentScreen);
					currentScreen.cancelLongPress();
				}
			}
			break;

		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mAllowLongPress = true;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;

			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// Release the drag
			clearChildrenCache();
			mTouchState = TOUCH_STATE_REST;
			mAllowLongPress = false;
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mTouchState != TOUCH_STATE_REST;
	}
	
	/**执行此方法为了处理触摸屏幕的运动事件。
	 * 参数   ev          运动事件
	 * 返回值   事件被处理返回true，其它返回false。*/
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLocked) {
			return true;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			// Remember where the motion event started
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				// Scroll to follow the motion event
				final int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				if (deltaX < 0) {
					if (deltaX + getScrollX() >= -getChildAt(0).getWidth()) {
						scrollBy(deltaX, 0);
					}

				} else if (deltaX > 0) {
					final int availableToScroll = getChildAt(
							getChildCount() - 1).getRight()
							- getScrollX() - getWidth();

					if (availableToScroll > 0) {
						scrollBy(Math.min(availableToScroll, deltaX), 0);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > SNAP_VELOCITY && mCurrentScreen == SCREEN_MAIN) {
					// Fling hard enough to move left
					snapToScreen(SCREEN_MENU);
				} else if (velocityX < -SNAP_VELOCITY
						&& mCurrentScreen == SCREEN_MENU) {
					// Fling hard enough to move right
					snapToScreen(SCREEN_MAIN);
				} else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}
	
	//被父视图调用，用于必要时候对其子视图的值（mScrollX和mScrollY）进行更新。
	//典型的情况如：父视图中某个子视图使用一个Scroller对象来实现滚动操作，会使得此方法被调用。
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		} else if (mNextScreen != SCREEN_INVALID) {
			mCurrentScreen = Math.max(0,
					Math.min(mNextScreen, getChildCount() - 1));
			mNextScreen = SCREEN_INVALID;
			clearChildrenCache();
		}
	}

	//设置当前视图滚动到的位置
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	//调用此方法来绘出子视图。可被衍生类重写，以便在其子项被画出之前取得控制权。
    //参数    canvas  绘出View所用的canvas（画布）
	@Override
	protected void dispatchDraw(Canvas canvas) {
		final int scrollX = getScrollX();//当前View显示部分的左边到第一个View的左边的距离。
		super.dispatchDraw(canvas); 
		canvas.translate(scrollX, 0);//画布水平移动x,垂直移动y距离
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentScreen() > 0) {
				snapToScreen(getCurrentScreen() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentScreen() < getChildCount() - 1) {
				snapToScreen(getCurrentScreen() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}
	
	/**设置ViewGroup其下的childern(子layout/view)可以隐藏*/
	void enableChildrenCache() {
		final int count = getChildCount();//获取child数量
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);//设置该child可以隐藏
		}
	}

	/**设置ViewGroup其下的childern(子layout/view)不可以隐藏*/
	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}
	
	/**对齐指定屏幕（就是打开指定屏幕）*/
	protected void snapToScreen(int whichScreen) {
		
		enableChildrenCache();

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		boolean changingScreens = whichScreen != mCurrentScreen;

		mNextScreen = whichScreen; //标记下一个要打开的屏幕

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingScreens
				&& focusedChild == getChildAt(mCurrentScreen)) {
			focusedChild.clearFocus();
		}
		
		final int newX = (whichScreen - 1) * getChildAt(0).getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}

	protected void snapToDestination() {
		if (getScrollX() == 0) {
			return;
		}
		final int screenWidth = getChildAt(0).getWidth();
		final int whichScreen = (screenWidth + getScrollX() + (screenWidth / 2))
				/ screenWidth;
		snapToScreen(whichScreen);
	}
	
	/**查看当前显示的屏幕是那个*/
	public int getCurrentScreen() {
		return mCurrentScreen;
	}
	
	/**如果当前显示屏幕为主屏幕则返回true*/
	public boolean isMainScreenShowing() {
		return mCurrentScreen == SCREEN_MAIN;
	}
	
	/**打开菜单*/
	public void openMenu() {
		mCurrentScreen = SCREEN_MENU; //标记当前屏幕为菜单
		snapToScreen(mCurrentScreen);
	}
	
	/**关闭菜单*/
	public void closeMenu() {
		mCurrentScreen = SCREEN_MAIN; //标记当前屏幕为主屏幕
		snapToScreen(mCurrentScreen);
	}
	
	public void unlock() {
		mLocked = false;
	}

	public void lock() {
		mLocked = true;
	}
	
}
