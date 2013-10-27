package grv.dvt.cilocks_proto;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.FloatMath;

public class CircleLockVisualizer {

	private float mCenterX;
	private float mCenterY;

	private float mMinRadius;
	private float mMaxRadius;

	private float[] mCircleWidth;

	public CircleLockVisualizer(float centerX, float centerY, float minRadius,
			float maxRadius, int circleCount) {
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
		sectorPaint.setColor(Color.YELLOW);

		canvas.drawCircle(this.mCenterX, this.mCenterY, innerRadius, sectorPaint);
		canvas.drawCircle(this.mCenterX, this.mCenterY, outerRadius, sectorPaint);
		
		/*
		RectF innerRect = new RectF(this.mCenterX - innerRadius, this.mCenterY
				- innerRadius, this.mCenterX + innerRadius, this.mCenterY
				+ innerRadius);
		RectF outerRect = new RectF(this.mCenterX - outerRadius, this.mCenterY
				- outerRadius, this.mCenterX + outerRadius, this.mCenterY
				+ outerRadius);

		float stepAngleDeg = 360 / circle.getSectorCount();
		float stepAngleRad = (float) (Math.PI / circle.getSectorCount());
		for (int i = 0; i < circle.getSectorCount(); i++) {
			Path path = new Path();
			path.addArc(outerRect, i * stepAngleDeg, stepAngleDeg);
			path.lineTo(innerRadius * FloatMath.cos((i + 1) * stepAngleRad),
					innerRadius * FloatMath.sin((i + 1) * stepAngleRad));
			path.arcTo(innerRect, (i + 1) * stepAngleDeg, -stepAngleDeg);
			path.lineTo(outerRadius * FloatMath.cos((i + 1) * stepAngleRad),
					outerRadius * FloatMath.sin((i + 1) * stepAngleRad));
			path.close();
			canvas.drawPath(path, sectorPaint);
		}
		*/
	}
}
