package grv.dvt.cilocks_gles20;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

public class CircleLockView extends GLSurfaceView {

	private CircleLockRenderer mRenderer;
	private TouchVectorField mVectorField;
	private CircleLockLock mCircleLock;
	private TouchController mTouchController;
	private AnimationThread mAnimationThread;
	
	public CircleLockView(Context context) {
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
		mCircleLock = new CircleLockLock(3, 8, colorIndexes, symbolIndexes);
		
		// Set renderer
		mRenderer = new CircleLockRenderer(context, mCircleLock);
		setRenderer(mRenderer);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		
		mVectorField = new TouchVectorField();
		mAnimationThread = new AnimationThread(this);
		mTouchController = new TouchController(3, mRenderer.getCircleBorders(), mAnimationThread, this);
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
			mTouchController.processVectors(mVectorField, mCircleLock);
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++)
				mVectorField.moveVector(MotionEventCompat.getPointerId(event, i),
						mRenderer.getPointProjection(new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i))));
			mTouchController.processVectors(mVectorField, mCircleLock);
			break;
		case MotionEvent.ACTION_CANCEL:
			this.mVectorField.clearField();
			mTouchController.processVectors(mVectorField, mCircleLock);
			break;
		}

		return true;
	}
}
