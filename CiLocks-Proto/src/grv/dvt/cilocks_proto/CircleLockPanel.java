package grv.dvt.cilocks_proto;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CircleLockPanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private MainThread mThread;
	private CircleLock mCircleLock;
	private CircleLockVisualizer mVisualizer;

	public CircleLockPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		this.mThread = new MainThread(getHolder(), this);
		setFocusable(true);

		LockData initData = new LockData();
		initData.columnCount = 6;
		initData.rowCount = 3;
		initData.symbolIndexes = new int[][] { { 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };
		initData.colorIndexes = new int[][] { { 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };

		this.mCircleLock = new CircleLock(initData);

		Point size = new Point(this.getWidth(), this.getHeight());
		this.mVisualizer = new CircleLockVisualizer((float) size.x / 2,
				(float) size.y / 2, (float) 20, ((float) size.x / 2) - 20,
				initData.rowCount);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		this.mThread.setRunning(true);
		this.mThread.run();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
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
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		//this.mVisualizer.draw(this.mCircleLock, canvas);
	}

}
