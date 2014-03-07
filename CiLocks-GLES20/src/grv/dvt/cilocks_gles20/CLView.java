package grv.dvt.cilocks_gles20;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

public class CLView extends GLSurfaceView {
	
	private static final String TAG = CLView.class.getSimpleName();
	
	private CLRenderer mRenderer;
	private CLTouchVectorField mVectorField;
	private CLLock mCircleLock;
	private CLTouchController mTouchController;
	private CLGame mGame;
	private AnimationThread mAnimationThread;
	
	public CLView(Context context) {
		super(context);
		
		// Request an OpenGL ES 2.0 compatible context.
		setEGLContextClientVersion(2);
		
		// Set OpenGL configuration
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		
		// Test circle lock
		int[][] colorIndexes = new int[][] {
				{ 0, 1, 0, 0, 1, 1, 0, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 0 },
				{ 0, 1, 0, 0, 1, 1, 0, 1 } };
		int[][] symbolIndexes = new int[][] {
				{ 0, 1, 2, 1, 1, 0, 1, 2 },
				{ 2, 0, 1, 2, 1, 1, 0, 1 },
				{ 0, 1, 2, 1, 1, 0, 1, 2 } };
		int[] keyColorIndexes = new int[] { 0, 1, 0, 1 };;
		mCircleLock = new CLLock(3, 8, colorIndexes, symbolIndexes, 4, keyColorIndexes);
		
		// Set renderer
		mRenderer = new CLRenderer(context, mCircleLock);
		setRenderer(mRenderer);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		
		mGame = new CLGame(mCircleLock, this);
		mVectorField = new CLTouchVectorField();
		mTouchController = new CLTouchController(3, mRenderer.getCircleBorders(), this);
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "Pausing..");
		super.onPause();
		
		mRenderer.releaseResources();
		
		boolean retry = true;
		mAnimationThread.terminate();
		while (retry) {
			try {
				mAnimationThread.join();
				retry = false;
			} catch (InterruptedException e) {
				//
			}
		}
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "Resuming..");
		super.onResume();
		
		mAnimationThread = new AnimationThread(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		int index = MotionEventCompat.getActionIndex(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			mVectorField.startVector(MotionEventCompat.getPointerId(event, index),
				mRenderer.getPointProjection(new PointF(MotionEventCompat.getX(event, index), MotionEventCompat.getY(event, index))));
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mVectorField.releaseVector(MotionEventCompat.getPointerId(event, index));
			mTouchController.processVectors(mVectorField, mCircleLock, mGame);
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++)
				mVectorField.moveVector(MotionEventCompat.getPointerId(event, i),
						mRenderer.getPointProjection(new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i))));
			mTouchController.processVectors(mVectorField, mCircleLock, mGame);
			break;
		case MotionEvent.ACTION_CANCEL:
			this.mVectorField.clearField();
			mTouchController.processVectors(mVectorField, mCircleLock, mGame);
			break;
		}

		return true;
	}
	
	public AnimationThread getAnimationThread() {
		return mAnimationThread;
	}
}
