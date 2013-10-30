package grv.dvt.cilocks_proto;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class CircleLockVisualizer {

	private float mCenterX;
	private float mCenterY;

	private float mMinRadius;
	private float mMaxRadius;
	private float mHoleRadius;

	private float[] mCircleWidth;

	public CircleLockVisualizer(ViewAspect viewAspect, int circleCount) {
		this.mCircleWidth = new float[circleCount];
		init(viewAspect);
	}

	public void init(ViewAspect viewAspect) {
		this.mCenterX = viewAspect.getCenterX();
		this.mCenterY = viewAspect.getCenterY();
		
		float minDimension = Math.min(viewAspect.getWidth(), viewAspect.getHeight());
		
		this.mMaxRadius = 0.43f * minDimension;
		this.mMinRadius = 0.17f * minDimension;
		this.mHoleRadius= 0.1f * minDimension;
		
		float circleWidth = (this.mMaxRadius - this.mMinRadius) / this.mCircleWidth.length;
		for (int i = 0; i < this.mCircleWidth.length; i++)
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
		sectorPaint.setStrokeWidth(2);
		
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
			path.lineTo(this.mCenterX + innerRadius * (float)Math.cos(endAngleRad),
					this.mCenterY + innerRadius * (float)Math.sin(endAngleRad));
			path.arcTo(innerRect, endAngleDeg, -stepAngleDeg);
			path.lineTo(this.mCenterX + outerRadius * (float)Math.cos(startAngleRad),
					this.mCenterY + outerRadius * (float)Math.sin(startAngleRad));
			path.close();
			
			// draw contour
			sectorPaint.setStyle(Paint.Style.STROKE);
			sectorPaint.setColor(Color.YELLOW);
			canvas.drawPath(path, sectorPaint);
			
			// fill
			sectorPaint.setStyle(Paint.Style.FILL);
			sectorPaint.setColor(Color.DKGRAY);
			canvas.drawPath(path, sectorPaint);
		}

	}
	
	public void processVectors(TouchVectorField field) {
		//
	}
	
}
