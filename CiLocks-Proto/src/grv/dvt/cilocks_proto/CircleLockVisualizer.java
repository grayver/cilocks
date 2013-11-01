package grv.dvt.cilocks_proto;

import grv.dvt.cilocks_proto.Circle.CircleState;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class CircleLockVisualizer {

	private static final String TAG = CircleLockVisualizer.class.getSimpleName();
	
	private PointF mCenter;

	private float mMinRadius;
	private float mMaxRadius;
	private float mHoleRadius;

	private float[] mCircleWidth;
	private float[] mCircleBoundSquares;

	public CircleLockVisualizer(ViewAspect viewAspect, int circleCount) {
		Log.d(TAG, String.format("Create visualizer. Circle count: %d", circleCount));
		
		this.mCircleWidth = new float[circleCount];
		this.mCircleBoundSquares = new float[circleCount + 1];
		this.mCenter = new PointF();
		init(viewAspect);
	}

	public void init(ViewAspect viewAspect) {
		Log.d(TAG, String.format("Init visualizer. Aspect size x=%.0f y=%.0f", viewAspect.getWidth(), viewAspect.getHeight()));
		
		this.mCenter.x = viewAspect.getCenterX();
		this.mCenter.y = viewAspect.getCenterY();

		float minDimension = Math.min(viewAspect.getWidth(), viewAspect.getHeight());

		this.mMaxRadius = 0.43f * minDimension;
		this.mMinRadius = 0.17f * minDimension;
		this.mHoleRadius = 0.08f * minDimension;

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

		this.drawHole(circleLock, canvas);
	}

	private void drawHole(CircleLock circleLock, Canvas canvas) {
		Paint holePaint = new Paint();
		holePaint.setAntiAlias(true);
		holePaint.setStrokeWidth(2);
		
		RectF holeRect = new RectF(this.mCenter.x - this.mHoleRadius, this.mCenter.y - this.mHoleRadius,
				this.mCenter.x + this.mHoleRadius, this.mCenter.y + this.mHoleRadius);
		
		float stepAngleDeg = 360f / circleLock.getKeyColorCount();
		float stepAngleRad = (float) (2 * Math.PI / circleLock.getKeyColorCount());
		for (int i = 0; i < circleLock.getKeyColorCount(); i++) {
			float startAngleDeg = i * stepAngleDeg;
			float startAngleRad = i * stepAngleRad;
			
			Path path = new Path();
			path.moveTo(this.mCenter.x, this.mCenter.y);
			path.lineTo(
					this.mCenter.x + this.mHoleRadius * (float) Math.cos(startAngleRad),
					this.mCenter.y + this.mHoleRadius * (float) Math.sin(startAngleRad));
			path.arcTo(holeRect, startAngleDeg, stepAngleDeg);
			path.lineTo(this.mCenter.x, this.mCenter.y);
			path.close();
			
			// draw contour
			holePaint.setStyle(Paint.Style.STROKE);
			holePaint.setColor(Color.YELLOW);
			canvas.drawPath(path, holePaint);

			// fill
			holePaint.setStyle(Paint.Style.FILL);
			holePaint.setColor(Color.DKGRAY);
			canvas.drawPath(path, holePaint);
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

	private float getTangentialComponent(PointF baseVector, PointF vector) {
		return (baseVector.x * vector.x + baseVector.y * vector.y)
				/ (float) Math.sqrt(baseVector.x * baseVector.x + baseVector.y * baseVector.y);
	}

	private float getNormalComponent(PointF baseVector, PointF vector) {
		return (-baseVector.y * vector.x + baseVector.x * vector.y)
				/ (float) Math.sqrt(baseVector.x * baseVector.x + baseVector.y * baseVector.y);
	}

	private float getDistanceSquare(PointF point1, PointF point2) {
		return (point1.x - point2.x) * (point1.x - point2.x)
				+ (point1.y - point2.y) * (point1.y - point2.y);
	}
	
	public void processVectors(TouchVectorField field, CircleLock circleLock) {
		for (int i = 0; i < circleLock.getCircleCount(); i++) {
			Circle circle = circleLock.getCircle(i);
			int touchCount = 0;
			
			for (int j = 0; j < field.getVectorCount(); j++) {
				TouchVector vector = field.getVector(j);

				switch (circle.getState()) {
				case ROLLING:
					float initDistSquare = getDistanceSquare(vector.init, this.mCenter);
					if (initDistSquare >= this.mCircleBoundSquares[i]
							&& initDistSquare < this.mCircleBoundSquares[i + 1]
									&& vector.action == TouchVector.Action.UNKNOWN)
						vector.action = TouchVector.Action.ROLL;
					
					if (vector.action == TouchVector.Action.ROLL) {
						float prevDistSquare = getDistanceSquare(vector.previous, this.mCenter);
						if (prevDistSquare >= this.mCircleBoundSquares[i]
								&& prevDistSquare < this.mCircleBoundSquares[i + 1]) {
							float normalComponent = getNormalComponent(
									new PointF(vector.previous.x - this.mCenter.x, vector.previous.y - this.mCenter.y),
									new PointF(vector.last.x - vector.previous.x, vector.last.y - vector.previous.y));
							float radius = (float)Math.sqrt(getDistanceSquare(vector.previous, this.mCenter));
							circle.setAngleRad(circle.getAngleRad() + (float)Math.atan2(normalComponent, radius));
							touchCount++;
						}
					}
					break;
					
				case IDLE:
					float prevDistSquare = getDistanceSquare(vector.previous, this.mCenter);
					if (prevDistSquare >= this.mCircleBoundSquares[i]
							&& prevDistSquare < this.mCircleBoundSquares[i + 1]
									&& vector.action == TouchVector.Action.UNKNOWN) {
						float normalComponent = getNormalComponent(
								new PointF(vector.init.x - this.mCenter.x, vector.init.y - this.mCenter.y),
								new PointF(vector.last.x - vector.init.x, vector.last.y - vector.init.y));
						float tangentialComponent = getTangentialComponent(
								new PointF(vector.init.x - this.mCenter.x, vector.init.y - this.mCenter.y),
								new PointF(vector.last.x - vector.init.x, vector.last.y - vector.init.y));
						
						if (normalComponent > 0.5f * this.mCircleWidth[i]) {
							vector.action = TouchVector.Action.ROLL;
							circle.setState(CircleState.ROLLING);
							
							float radius = (float)Math.sqrt(getDistanceSquare(vector.init, this.mCenter));
							circle.setAngleRad(circle.getAngleRad() + (float)Math.atan2(normalComponent, radius));
						} else if (tangentialComponent > 0.5f * this.mCircleWidth[i]) {
							vector.action = TouchVector.Action.SWAP;
							circle.setState(CircleState.SWAPPING);
						}
						
						touchCount++;
					}
					break;
					
				case SWAPPING:
				case ANIMATING:
					break;
				}
			}
			
			if (circle.getState() == CircleState.ROLLING && touchCount == 0)
				this.releaseCircle(circle);
		}
	}
	
	private void releaseCircle(Circle circle) {
		float stepAngleRad = (float) (2 * Math.PI / circle.getSectorCount());
		circle.setAngleRad(Math.round(circle.getAngleRad() / stepAngleRad) * stepAngleRad);
		circle.setState(CircleState.IDLE);
	}

}
