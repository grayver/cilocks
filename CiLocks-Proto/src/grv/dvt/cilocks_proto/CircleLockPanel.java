package grv.dvt.cilocks_proto;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CircleLockPanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private ViewAspect mViewAspect;
	
	private MainThread mThread;
	private CircleLock mCircleLock;
	private CircleLockVisualizer mVisualizer;

	public CircleLockPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		this.mThread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.mViewAspect.setSize(width, height);
		this.mVisualizer.init(this.mViewAspect);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LockData initData = new LockData();
		initData.columnCount = 6;
		initData.rowCount = 3;
		initData.symbolIndexes = new int[][] { { 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };
		initData.colorIndexes = new int[][] { { 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };

		this.mCircleLock = new CircleLock(initData);

		this.mViewAspect = new ViewAspect(this.getWidth(), this.getHeight());
		this.mVisualizer = new CircleLockVisualizer(this.mViewAspect, initData.rowCount);
		
		this.mThread.setRunning(true);
		this.mThread.start();
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
		this.mVisualizer.draw(this.mCircleLock, canvas);
	}

}
