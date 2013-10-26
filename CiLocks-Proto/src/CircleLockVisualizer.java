import android.graphics.Canvas;

public class CircleLockVisualizer {

	private float mCenterX;
	private float mCenterY;
	
	private float mMinRadius;
	private float mMaxRadius;

	public CircleLockVisualizer(float centerX, float centerY, float minRadius, float maxRadius) {
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		
		this.mMinRadius = minRadius;
		this.mMaxRadius = maxRadius;
	}

	public void draw(CircleLock circleLock, Canvas canvas) {
		float circleWidth = (this.mMaxRadius - this.mMinRadius)
				/ circleLock.getCircleCount();
		
		for (int i = 0; i < circleLock.getCircleCount(); i++)
			this.drawCircle(circleLock.getCircle(i), canvas, circleWidth);
	}

	private void drawCircle(Circle circle, Canvas canvas, float circleWidth) {
		canvas.
	}
}
