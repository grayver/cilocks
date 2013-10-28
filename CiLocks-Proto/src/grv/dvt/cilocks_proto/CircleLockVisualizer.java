package grv.dvt.cilocks_proto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.FloatMath;
import android.util.Log;

public class CircleLockVisualizer {

	private static final String TAG = CircleLockVisualizer.class
			.getSimpleName();

	private float mCenterX;
	private float mCenterY;

	private float mMinRadius;
	private float mMaxRadius;

	private float[] mCircleWidth;

	public CircleLockVisualizer(float centerX, float centerY, float minRadius,
			float maxRadius, int circleCount) {

		Log.d(TAG,
				String.format(
						"Init visualizer: cx=%.2f cy=%.2f minr=%.2f maxr=%.2f count=%d",
						centerX, centerY, minRadius, maxRadius, circleCount));

		this.mCenterX = centerX;
		this.mCenterY = centerY;

		this.mMinRadius = minRadius;
		this.mMaxRadius = maxRadius;

		float circleWidth = (this.mMaxRadius - this.mMinRadius) / circleCount;
		this.mCircleWidth = new float[circleCount];
		for (int i = 0; i < circleCount; i++)
			this.mCircleWidth[i] = circleWidth;
	}

	public void draw(CircleLock circleLock, Canvas canvas) {
		float innerRadius = this.mMinRadius;
		for (int i = 0; i < circleLock.getCircleCount(); i++) {
			this.drawCircle(circleLock.getCircle(i), canvas, innerRadius,
					this.mCircleWidth[i]);
			innerRadius += this.mCircleWidth[i];
		}
	}

	private void drawCircle(Circle circle, Canvas canvas, float innerRadius,
			float width) {
		float outerRadius = innerRadius + width;

		Paint sectorPaint = new Paint();
		sectorPaint.setAntiAlias(true);
		sectorPaint.setStyle(Paint.Style.STROKE);
		sectorPaint.setStrokeWidth(1);
		sectorPaint.setColor(Color.YELLOW);

		RectF innerRect = new RectF(this.mCenterX - innerRadius, this.mCenterY
				- innerRadius, this.mCenterX + innerRadius, this.mCenterY
				+ innerRadius);
		RectF outerRect = new RectF(this.mCenterX - outerRadius, this.mCenterY
				- outerRadius, this.mCenterX + outerRadius, this.mCenterY
				+ outerRadius);

		float stepAngleDeg = 360f / circle.getSectorCount();
		float stepAngleRad = (float) (2 * Math.PI / circle.getSectorCount());
		for (int i = 0; i < circle.getSectorCount(); i++) {
			float startAngleDeg = (float)Math.toDegrees(circle.getAngleRad()) + i * stepAngleDeg;
			float endAngleDeg = startAngleDeg + stepAngleDeg;
			float startAngleRad = circle.getAngleRad() + i * stepAngleRad;
			float endAngleRad = startAngleRad + stepAngleRad;
			
			Path path = new Path();
			path.addArc(outerRect, startAngleDeg, stepAngleDeg);
			path.lineTo(this.mCenterX + innerRadius * FloatMath.cos(endAngleRad),
					this.mCenterY + innerRadius * FloatMath.sin(endAngleRad));
			path.arcTo(innerRect, endAngleDeg, -stepAngleDeg);
			path.lineTo(this.mCenterX + outerRadius * FloatMath.cos(startAngleRad),
					this.mCenterY + outerRadius * FloatMath.sin(startAngleRad));
			path.close();
			canvas.drawPath(path, sectorPaint);
		}

	}
}
