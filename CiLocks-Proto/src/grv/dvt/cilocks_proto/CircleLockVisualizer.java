package grv.dvt.cilocks_proto;

import grv.dvt.cilocks_proto.Circle.CircleState;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

public class CircleLockVisualizer {

	private PointF mCenter;

	private float mMinRadius;
	private float mMaxRadius;
	private float mHoleRadius;

	private float[] mCircleWidth;
	private float[] mCircleBoundSquares;

	public CircleLockVisualizer(ViewAspect viewAspect, int circleCount) {
		this.mCircleWidth = new float[circleCount];
		this.mCircleBoundSquares = new float[circleCount + 1];
		init(viewAspect);
	}

	public void init(ViewAspect viewAspect) {
		this.mCenter.x = viewAspect.getCenterX();
		this.mCenter.y = viewAspect.getCenterY();

		float minDimension = Math.min(viewAspect.getWidth(), viewAspect.getHeight());

		this.mMaxRadius = 0.43f * minDimension;
		this.mMinRadius = 0.17f * minDimension;
		this.mHoleRadius = 0.1f * minDimension;

		float circleWidth = (this.mMaxRadius - this.mMinRadius) / this.mCircleWidth.length;
		for (int i = 0; i < this.mCircleWidth.length; i++) {
			this.mCircleWidth[i] = circleWidth;
			this.mCircleBoundSquares[i] = (this.mMinRadius + i * circleWidth)
					* (this.mMinRadius + i * circleWidth);
		}
		this.mCircleBoundSquares[this.mCircleWidth.length] = this.mMaxRadius * this.mMaxRadius;
	}

	public void draw(CircleLock circleLock, Canvas canvas) {
		float innerRadius = this.mMinRadius;
		for (int i = 0; i < circleLock.getCircleCount(); i++) {
			this.drawCircle(circleLock.getCircle(i), canvas, innerRadius, this.mCircleWidth[i]);
			innerRadius += this.mCircleWidth[i];
		}
	}

	private void drawCircle(Circle circle, Canvas canvas, float innerRadius,
			float width) {
		float outerRadius = innerRadius + width;

		Paint sectorPaint = new Paint();
		sectorPaint.setAntiAlias(true);
		sectorPaint.setStrokeWidth(2);

		RectF innerRect = new RectF(this.mCenter.x - innerRadius,
				this.mCenter.y - innerRadius, this.mCenter.x + innerRadius,
				this.mCenter.y + innerRadius);
		RectF outerRect = new RectF(this.mCenter.x - outerRadius,
				this.mCenter.y - outerRadius, this.mCenter.x + outerRadius,
				this.mCenter.y + outerRadius);

		float stepAngleDeg = 360f / circle.getSectorCount();
		float stepAngleRad = (float) (2 * Math.PI / circle.getSectorCount());
		for (int i = 0; i < circle.getSectorCount(); i++) {
			float startAngleDeg = (float) Math.toDegrees(circle.getAngleRad())
					+ i * stepAngleDeg;
			float endAngleDeg = startAngleDeg + stepAngleDeg;
			float startAngleRad = circle.getAngleRad() + i * stepAngleRad;
			float endAngleRad = startAngleRad + stepAngleRad;

			Path path = new Path();
			path.addArc(outerRect, startAngleDeg, stepAngleDeg);
			path.lineTo(
					this.mCenter.x + innerRadius * (float) Math.cos(endAngleRad),
					this.mCenter.y + innerRadius * (float) Math.sin(endAngleRad));
			path.arcTo(innerRect, endAngleDeg, -stepAngleDeg);
			path.lineTo(
					this.mCenter.x + outerRadius* (float) Math.cos(startAngleRad),
					this.mCenter.y + outerRadius * (float) Math.sin(startAngleRad));
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

	private float getTangentalComponent(PointF baseVector, PointF vector) {
		return (baseVector.x * vector.x + baseVector.y * vector.y)
				/ (float) Math.sqrt(baseVector.x * baseVector.x + baseVector.y * baseVector.y);
	}

	private float getNormalComponent(PointF baseVector, PointF vector) {
		return (-baseVector.y * vector.x + baseVector.x * vector.y)
				/ (float) Math.sqrt(baseVector.x * baseVector.x + baseVector.y * baseVector.y);
	}

	public void processVectors(TouchVectorField field, CircleLock circleLock) {
		for (int i = 0; i < circleLock.getCircleCount(); i++) {
			Circle circle = circleLock.getCircle(i);

			for (int j = 0; j < field.getVectorCount(); j++) {
				TouchVector vector = field.getVector(j);

				switch (circle.getState()) {
				case ROLLING:

					break;
				case IDLE:
					break;
				case SWAPPING:
				case ANIMATING:
					break;
				}
			}
		}
	}

}
