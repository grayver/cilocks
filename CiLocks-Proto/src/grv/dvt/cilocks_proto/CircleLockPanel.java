package grv.dvt.cilocks_proto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CircleLockPanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = CircleLockPanel.class.getSimpleName();
	
	private ViewAspect mViewAspect;
	private AnimationPool mAnimationPool;

	private MainThread mThread;
	private CircleLock mCircleLock;
	private CircleLockVisualizer mVisualizer;
	private TouchVectorField mVectorField;
	private SparseArray<Bitmap> mSectorSymbols;

	public CircleLockPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		this.mThread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "Changing panel");
		
		this.mViewAspect.setSize(width, height);
		this.mVisualizer.init(this.mViewAspect);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "Creating panel");
		
		this.mSectorSymbols = new SparseArray<Bitmap>();
		this.mSectorSymbols.put(0, BitmapFactory.decodeResource(getResources(), R.drawable.sign1));
		this.mSectorSymbols.put(1, BitmapFactory.decodeResource(getResources(), R.drawable.sign2));
		this.mSectorSymbols.put(2, BitmapFactory.decodeResource(getResources(), R.drawable.sign3));
		
		LockData initData = new LockData();
		initData.columnCount = 8;
		initData.rowCount = 3;
		initData.colorIndexes = new int[][] {
				{ 0, 1, 0, 0, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 0 },
				{ 0, 1, 0, 0, 1, 1, 0, 1 } };
		initData.symbolIndexes = new int[][] {
				{ 0, 1, 2, 1, 1, 0, 1, 2 },
				{ 2, 0, 1, 2, 1, 1, 0, 1 },
				{ 0, 1, 2, 1, 1, 0, 1, 2 } };
		initData.keyColorCount = 4;
		initData.keyColorIndexes = new int[] { 0, 1, 0, 1 };

		this.mCircleLock = new CircleLock(initData);

		this.mViewAspect = new ViewAspect(this.getWidth(), this.getHeight());
		this.mAnimationPool = new AnimationPool();
		this.mVisualizer = new CircleLockVisualizer(this.mViewAspect,
				this.mAnimationPool, initData.rowCount, this.mSectorSymbols);
		this.mVectorField = new TouchVectorField();

		this.mThread.setRunning(true);
		this.mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.d(TAG, "Destroying panel");
		
		boolean retry = true;
		while (retry) {
			try {
				this.mThread.join();
				retry = false;
			} catch (InterruptedException e) {
				//
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		int index = MotionEventCompat.getActionIndex(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			this.mVectorField.startVector(MotionEventCompat.getPointerId(event, index),
				new PointF(MotionEventCompat.getX(event, index), MotionEventCompat.getY(event, index)));
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			this.mVectorField.releaseVector(MotionEventCompat.getPointerId(event, index));
			this.mVisualizer.processVectors(this.mVectorField, this.mCircleLock);
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++)
				this.mVectorField.moveVector(MotionEventCompat.getPointerId(event, i),
					new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i)));
			this.mVisualizer.processVectors(this.mVectorField, this.mCircleLock);
			break;
		}

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		this.mAnimationPool.updateAnimation();
		this.mVisualizer.draw(this.mCircleLock, canvas);
	}

}
