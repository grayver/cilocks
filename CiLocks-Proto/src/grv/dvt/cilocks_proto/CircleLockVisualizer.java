package grv.dvt.cilocks_proto;

import grv.dvt.cilocks_proto.Circle.CircleState;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class CircleLockVisualizer {

	private static final String TAG = CircleLockVisualizer.class.getSimpleName();
	
	private AnimationPool mAnimationPool;
	
	private SparseIntArray mSectorColors;
	private SparseArray<Bitmap> mSectorSymbols;
	
	private PointF mCenter;

	private float mMinRadius;
	private float mMaxRadius;
	private float mHoleRadius;

	private float[] mCircleWidth;
	private float[] mCircleBoundSquares;

	public CircleLockVisualizer(ViewAspect viewAspect, AnimationPool animationPool,
			int circleCount, SparseArray<Bitmap> sectorSymbols) {
		Log.d(TAG, String.format("Create visualizer. Circle count: %d", circleCount));
		
		this.mSectorSymbols = sectorSymbols;
		
		this.mSectorColors = new SparseIntArray();
		this.mSectorColors.put(0, Color.parseColor("#935639"));
		this.mSectorColors.put(1, Color.parseColor("#808581"));
		this.mSectorColors.put(2, Color.parseColor("#7E5F5A"));
		this.mSectorColors.put(3, Color.parseColor("#A3A5A4"));
		
		this.mAnimationPool = animationPool;
		
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
		Log.d(TAG, String.format("Circle width %.2f", circleWidth));
		for (int i = 0; i < this.mCircleWidth.length; i++) {
			this.mCircleWidth[i] = circleWidth;
			this.mCircleBoundSquares[i] = (this.mMinRadius + i * circleWidth)
					* (this.mMinRadius + i * circleWidth);
		}
		this.mCircleBoundSquares[this.mCircleWidth.length] = this.mMaxRadius * this.mMaxRadius;
	}

	public void draw(CircleLock circleLock, Canvas canvas) {
		synchronized (circleLock) {
			float innerRadius = this.mMinRadius;
			for (int i = 0; i < circleLock.getCircleCount(); i++) {
				this.drawCircle(circleLock.getCircle(i), canvas, innerRadius, this.mCircleWidth[i]);
				innerRadius += this.mCircleWidth[i];
			}
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
			holePaint.setColor(this.mSectorColors.get(circleLock.getKeyColor(i)));
			canvas.drawPath(path, holePaint);
		}
	}
	
	private void drawCircle(Circle circle, Canvas canvas, float innerRadius,
			float width) {
		float outerRadius = innerRadius + width;
		float medRadius = innerRadius + 0.5f * width;

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
			float medAngleRad = startAngleRad + 0.5f * stepAngleRad;
			float medAngleDeg = startAngleDeg + 0.5f * stepAngleDeg;

			Path path = new Path();
			Matrix symbolMatrix = new Matrix();
			Bitmap symbol = this.mSectorSymbols.get(circle.getData().getSymbolIndex(i));
			symbolMatrix.postTranslate(-0.5f * symbol.getWidth(), -0.5f * symbol.getHeight());
			
			if (circle.getState() == CircleState.SWAPPING && i == circle.swapSectorIndex) {
				float rotateScale = (float)Math.abs(Math.cos(circle.swapSectorAngleRad));

				float scaleStepAngleRad = rotateScale * stepAngleRad;
				float scaleStepAngleDeg = rotateScale * stepAngleDeg;
				float startScaleAngleRad = medAngleRad - 0.5f * scaleStepAngleRad;
				float endScaleAngleRad = medAngleRad + 0.5f * scaleStepAngleRad;
				float startScaleAngleDeg = medAngleDeg - 0.5f * scaleStepAngleDeg;
				float endScaleAngleDeg = medAngleDeg + 0.5f * scaleStepAngleDeg;
				
				path.addArc(outerRect, startScaleAngleDeg, scaleStepAngleDeg);
				path.lineTo(
						this.mCenter.x + innerRadius * (float) Math.cos(endScaleAngleRad),
						this.mCenter.y + innerRadius * (float) Math.sin(endScaleAngleRad));
				path.arcTo(innerRect, endScaleAngleDeg, -scaleStepAngleDeg);
				path.lineTo(
						this.mCenter.x + outerRadius * (float) Math.cos(startScaleAngleRad),
						this.mCenter.y + outerRadius * (float) Math.sin(startScaleAngleRad));
				path.close();
				
				symbolMatrix.postScale(rotateScale, 1f);
			} else {
				path.addArc(outerRect, startAngleDeg, stepAngleDeg);
				path.lineTo(
						this.mCenter.x + innerRadius * (float) Math.cos(endAngleRad),
						this.mCenter.y + innerRadius * (float) Math.sin(endAngleRad));
				path.arcTo(innerRect, endAngleDeg, -stepAngleDeg);
				path.lineTo(
						this.mCenter.x + outerRadius * (float) Math.cos(startAngleRad),
						this.mCenter.y + outerRadius * (float) Math.sin(startAngleRad));
				path.close();
			}
			
			// draw contour
			sectorPaint.setStyle(Paint.Style.STROKE);
			sectorPaint.setColor(circle.getState() == CircleState.ROLLING ? Color.GREEN : Color.YELLOW);
			canvas.drawPath(path, sectorPaint);

			// fill
			sectorPaint.setStyle(Paint.Style.FILL);
			sectorPaint.setColor(this.mSectorColors.get(circle.getData().getColorIndex(i)));
			canvas.drawPath(path, sectorPaint);
			
			// symbol
			symbolMatrix.postRotate(medAngleDeg + 90f);
			symbolMatrix.postTranslate(this.mCenter.x + medRadius * (float)Math.cos(medAngleRad),
					this.mCenter.y + medRadius * (float)Math.sin(medAngleRad));
			canvas.drawBitmap(symbol, symbolMatrix, null);
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

				float initDistSquare = getDistanceSquare(vector.init, this.mCenter);
				float prevDistSquare = getDistanceSquare(vector.previous, this.mCenter);
				boolean isInitiatedHere = (initDistSquare >= this.mCircleBoundSquares[i]
						&& initDistSquare < this.mCircleBoundSquares[i + 1]);
				boolean isPrevHere = (prevDistSquare >= this.mCircleBoundSquares[i]
						&& prevDistSquare < this.mCircleBoundSquares[i + 1]);
				
				switch (circle.getState()) {
				case ROLLING:
					
					if (isInitiatedHere && vector.action == TouchVector.Action.UNKNOWN)
						vector.action = TouchVector.Action.ROLL;
					
					if (vector.action == TouchVector.Action.ROLL) {
						if (isPrevHere && isInitiatedHere) {
							float normalComponent = getNormalComponent(
									new PointF(vector.previous.x - this.mCenter.x, vector.previous.y - this.mCenter.y),
									new PointF(vector.last.x - vector.previous.x, vector.last.y - vector.previous.y));
							float radius = (float)Math.sqrt(getDistanceSquare(vector.previous, this.mCenter));
							synchronized (circleLock) {
								circle.setAngleRad(circle.getAngleRad() + (float)Math.atan2(normalComponent, radius));
							}
							touchCount++;
						}
					}
					break;
					
				case IDLE:
					
					if (isPrevHere) {
						
						if (vector.action == TouchVector.Action.UNKNOWN) {
							float normalComponent = getNormalComponent(
									new PointF(vector.init.x - this.mCenter.x, vector.init.y - this.mCenter.y),
									new PointF(vector.last.x - vector.init.x, vector.last.y - vector.init.y));
							float tangentialComponent = getTangentialComponent(
									new PointF(vector.init.x - this.mCenter.x, vector.init.y - this.mCenter.y),
									new PointF(vector.last.x - vector.init.x, vector.last.y - vector.init.y));
							
							if (Math.abs(normalComponent) > 0.5f * this.mCircleWidth[i]) {
								vector.action = TouchVector.Action.ROLL;
								circle.setState(CircleState.ROLLING);
								
								float radius = (float)Math.sqrt(getDistanceSquare(vector.init, this.mCenter));
								synchronized (circleLock) {
									circle.setAngleRad(circle.getAngleRad() + (float)Math.atan2(normalComponent, radius));
								}
							} else if (Math.abs(tangentialComponent) > 0.4f * this.mCircleWidth[i]) {
								vector.action = TouchVector.Action.SWAP;
								
								float initAngleRad = (float)Math.atan2(vector.init.y - this.mCenter.y,
										vector.init.x - this.mCenter.x);
								if (initAngleRad < 0)
									initAngleRad += 2 * Math.PI;
								
								float stepAngleRad = (float)(2 * Math.PI / circle.getSectorCount());
								int sectorIndex = (int)Math.floor(initAngleRad / stepAngleRad);
								
								if (tangentialComponent > 0f && i < circleLock.getCircleCount() - 1) {
									if (circleLock.getCircle(i + 1).getState() == CircleState.IDLE
											&& circle.isSwappable(circleLock.getCircle(i + 1), sectorIndex)) {
										circle.swapSectorIndex = sectorIndex;
										circle.swapSectorAngleRad = 0;
										circleLock.getCircle(i + 1).swapSectorIndex = sectorIndex;
										circleLock.getCircle(i + 1).swapSectorAngleRad = 0;
										
										circle.setState(CircleState.SWAPPING);
										circleLock.getCircle(i + 1).setState(CircleState.SWAPPING);
										
										this.mAnimationPool.addAnimator(
												new SwapAnimator(1000, circle, circleLock.getCircle(i + 1)));
									}
								} else if (tangentialComponent < 0f && i > 0) {
									if (circleLock.getCircle(i - 1).getState() == CircleState.IDLE
											&& circle.isSwappable(circleLock.getCircle(i - 1), sectorIndex)) {
										circle.swapSectorIndex = sectorIndex;
										circle.swapSectorAngleRad = 0;
										circleLock.getCircle(i - 1).swapSectorIndex = sectorIndex;
										circleLock.getCircle(i - 1).swapSectorAngleRad = 0;
										
										circle.setState(CircleState.SWAPPING);
										circleLock.getCircle(i - 1).setState(CircleState.SWAPPING);
										
										this.mAnimationPool.addAnimator(
												new SwapAnimator(1000, circleLock.getCircle(i - 1), circle));
									}
								}
							}
						} else if (isInitiatedHere && vector.action == TouchVector.Action.ROLL) {
							circle.setState(CircleState.ROLLING);
							
							float normalComponent = getNormalComponent(
									new PointF(vector.previous.x - this.mCenter.x, vector.previous.y - this.mCenter.y),
									new PointF(vector.last.x - vector.previous.x, vector.last.y - vector.previous.y));
							float radius = (float)Math.sqrt(getDistanceSquare(vector.previous, this.mCenter));
							synchronized (circleLock) {
								circle.setAngleRad(circle.getAngleRad() + (float)Math.atan2(normalComponent, radius));
							}
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
				synchronized (circleLock) {
					this.releaseCircle(circle);
				}
		}
	}
	
	private void releaseCircle(Circle circle) {
		circle.setState(CircleState.ANIMATING);
		this.mAnimationPool.addAnimator(new CircleRollAnimator(200, circle));
	}

}
