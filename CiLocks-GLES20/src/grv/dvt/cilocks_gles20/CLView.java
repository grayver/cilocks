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
	private CLTouchController mTouchController;
	private CLGame mGame;
	
	
	public CLView(Context context) {
		super(context);
		
		// Create game object
		mGame = new CLGame(this);
		
		
		// Request an OpenGL ES 2.0 compatible context.
		setEGLContextClientVersion(2);
		
		// Set OpenGL configuration
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		
		// Set renderer
		mRenderer = new CLRenderer(context, mGame);
		setRenderer(mRenderer);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		// Create game logic objects
		mVectorField = new CLTouchVectorField();
		mTouchController = new CLTouchController(3, mRenderer.getCircleBorders(), this);
	}
	
	@Override
	public void onPause() {
		Log.d(TAG, "Pausing..");
		super.onPause();
		
		mRenderer.releaseResources();
		
		mGame.releaseContext();
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "Resuming..");
		super.onResume();
		
		mGame.createContext();
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
			mTouchController.processVectors(mVectorField, mGame);
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++)
				mVectorField.moveVector(MotionEventCompat.getPointerId(event, i),
						mRenderer.getPointProjection(new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i))));
			mTouchController.processVectors(mVectorField, mGame);
			break;
		case MotionEvent.ACTION_CANCEL:
			this.mVectorField.clearField();
			mTouchController.processVectors(mVectorField, mGame);
			break;
		}

		return true;
	}
}
