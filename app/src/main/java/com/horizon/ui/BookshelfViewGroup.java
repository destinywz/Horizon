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
	public static final int SCREEN_MENU = 0; //�˵�
	public static final int SCREEN_MAIN = 1; //����Ļ
	private static final int SCREEN_INVALID = -1; //��Ч
	
	private int mCurrentScreen; //��ǰ��ʾ����Ļ
	private int mNextScreen = SCREEN_INVALID; //��һ��Ҫ��ʾ����Ļ

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

    //������ͼ��ȷ�������ݿ�Ⱥ͸߶�,��Ҫ��������д���ṩ��������׼ȷ��Ч�Ĳ�����
    //����measure(int, int)����ȷ��viewGroup�µ���view/layout�Ŀ�͸ߡ�
    //����Ӧ���Ǹ�view�Ŀ�͸�
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//ȷ���˵���Ļ�Ŀ�͸�
		View menuView = getChildAt(0);
		menuView.measure(menuView.getLayoutParams().width + menuView.getLeft()
				+ menuView.getRight(), heightMeasureSpec);

		//ȷ������Ļ�Ŀ�͸�
		View contentView = getChildAt(1);
		contentView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	//viewGroup��ҪΪ����ͼ�����С��λ��ʱ����ã�����̳б���Ҫ���ش˷����������Լ�����ͼ��layout������
	//����Ϊ�˵���Ļ������Ļ�趨��ʼλ�ã�����˵���Ļ����ƫ�ƣ��Ӷ��ﵽ����Ч��
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//Ϊ�˵�layout�趨λ��
		View menuView = getChildAt(0);
		final int width = menuView.getMeasuredWidth(); //��ȡ�˵���Ļ�Ŀ��
		menuView.layout(-width, 0, 0, menuView.getMeasuredHeight());//���ϡ��ҡ��µ�λ��

		//Ϊ����Ļlayout�趨λ��
		View contentView = getChildAt(1);
		contentView.layout(0, 0, contentView.getMeasuredWidth(),
				contentView.getMeasuredHeight());
	}
	
	//��View�Լ����������XML�е���ʱ�����ã��ǵ�������һ��
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View child;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			child.setFocusable(true);//���ø�child�ɻ�ý���
			child.setClickable(true);//���ø�child�ɱ����������Ӧ�û�������
		}
	}
	
	/**ʵ�ִ˷�����Ϊ���������д�����Ļʱ���˶��¼������������͸�����ͼ���¼�һ��ȥ������Щ�¼������һ�ȡ��ǰ������������ownership
	 * ʹ�ô˷���ʱ����Ҫע�⣬��Ϊ����View.onTouchEvent(MotionEvent)���൱���ӵĽ���������ǰ����Ҫ��ȷִ��View.onTouchEvent(MotionEvent)���¼�����������˳����յ���
	 * 1. �յ�down�¼�
	 * 2. Down�¼���������ͼ���һ������ͼ�������߱��û��Լ���onTouchEvent()���������˴�����ζ��Ӧ��ִ��onTouchEvent()ʱ����true���������ܼ�������ʣ�µ����ƣ�ȡ����һ������ͼ������
	 *    ���onTouchEvent()����trueʱ���㲻���յ�onInterceptTouchEvent()���κ��¼��������жԴ����Ĵ��������onTouchEvent()�з�����
	 * 3. ����˷�������false�����������¼���up to and including the final up�������ȱ����ݵ��ˣ�Ȼ����Ŀ���onTouchEvent()��
	 * 4. �������true���������յ������κ��¼���Ŀ��view���յ�ͬ�����¼����ǻ����ACTION_CANCEL���������еĸ���һ�����¼����ᴫ�ݵ����Լ���onTouchEvent()�����ж���������������֡�
	 * ����    ev          ��ϵ���·��͵Ķ����¼�
	 * ����ֵ     ������˶��¼�������ͼ�нػ���ͨ��onTouchEvent()���͵���ǰViewGroup ������true����ǰĿ�꽫���յ�ACTION_CANCEL�¼������Ҳ��ٻ���������Ϣ���ݵ��ˡ� */
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
	
	/**ִ�д˷���Ϊ�˴�������Ļ���˶��¼���
	 * ����   ev          �˶��¼�
	 * ����ֵ   �¼���������true����������false��*/
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
	
	//������ͼ���ã����ڱ�Ҫʱ���������ͼ��ֵ��mScrollX��mScrollY�����и��¡�
	//���͵�����磺����ͼ��ĳ������ͼʹ��һ��Scroller������ʵ�ֹ�����������ʹ�ô˷��������á�
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

	//���õ�ǰ��ͼ��������λ��
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	//���ô˷������������ͼ���ɱ���������д���Ա������������֮ǰȡ�ÿ���Ȩ��
    //����    canvas  ���View���õ�canvas��������
	@Override
	protected void dispatchDraw(Canvas canvas) {
		final int scrollX = getScrollX();//��ǰView��ʾ���ֵ���ߵ���һ��View����ߵľ��롣
		super.dispatchDraw(canvas); 
		canvas.translate(scrollX, 0);//����ˮƽ�ƶ�x,��ֱ�ƶ�y����
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
	
	/**����ViewGroup���µ�childern(��layout/view)��������*/
	void enableChildrenCache() {
		final int count = getChildCount();//��ȡchild����
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);//���ø�child��������
		}
	}

	/**����ViewGroup���µ�childern(��layout/view)����������*/
	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}
	
	/**����ָ����Ļ�����Ǵ�ָ����Ļ��*/
	protected void snapToScreen(int whichScreen) {
		
		enableChildrenCache();

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		boolean changingScreens = whichScreen != mCurrentScreen;

		mNextScreen = whichScreen; //�����һ��Ҫ�򿪵���Ļ

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
	
	/**�鿴��ǰ��ʾ����Ļ���Ǹ�*/
	public int getCurrentScreen() {
		return mCurrentScreen;
	}
	
	/**�����ǰ��ʾ��ĻΪ����Ļ�򷵻�true*/
	public boolean isMainScreenShowing() {
		return mCurrentScreen == SCREEN_MAIN;
	}
	
	/**�򿪲˵�*/
	public void openMenu() {
		mCurrentScreen = SCREEN_MENU; //��ǵ�ǰ��ĻΪ�˵�
		snapToScreen(mCurrentScreen);
	}
	
	/**�رղ˵�*/
	public void closeMenu() {
		mCurrentScreen = SCREEN_MAIN; //��ǵ�ǰ��ĻΪ����Ļ
		snapToScreen(mCurrentScreen);
	}
	
	public void unlock() {
		mLocked = false;
	}

	public void lock() {
		mLocked = true;
	}
	
}
