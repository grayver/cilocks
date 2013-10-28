package grv.dvt.cilocks_proto;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

	private static final String TAG = MainThread.class.getSimpleName();

	private boolean mIsRunning;

	private SurfaceHolder mSurfaceHolder;
	private CircleLockPanel mPanel;

	public MainThread(SurfaceHolder surfaceHolder, CircleLockPanel panel) {
		super();
		this.mSurfaceHolder = surfaceHolder;
		this.mPanel = panel;
	}

	public void setRunning(boolean isRunning) {
		this.mIsRunning = isRunning;
	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		while (this.mIsRunning) {
			canvas = null;
			try {
				canvas = this.mSurfaceHolder.lockCanvas();
				if (canvas != null) {
					synchronized (this.mSurfaceHolder) {
						this.mPanel.onDraw(canvas);
					}
				}
			} finally {
				if (canvas != null) {
					this.mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
