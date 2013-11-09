package grv.dvt.cilocks_gles20;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

public class CircleLockView extends GLSurfaceView {

	private CircleLockRenderer mRenderer;
	private TouchVectorField mVectorField;
	
	public CircleLockView(Context context) {
		super(context);
		
		// Request an OpenGL ES 2.0 compatible context.
		setEGLContextClientVersion(2);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		// Set renderer
		mRenderer = new CircleLockRenderer();
		setRenderer(mRenderer);
		
		// Initialize vector field
		mVectorField = new TouchVectorField();
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
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < MotionEventCompat.getPointerCount(event); i++)
				this.mVectorField.moveVector(MotionEventCompat.getPointerId(event, i),
					new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i)));
			break;
		case MotionEvent.ACTION_CANCEL:
			this.mVectorField.clearField();
			break;
		}

		return true;
	}
}
