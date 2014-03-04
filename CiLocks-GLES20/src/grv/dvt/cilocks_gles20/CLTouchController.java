package grv.dvt.cilocks_gles20;

import android.graphics.PointF;

public class CLTouchController {
	
	private float[] mInnerRadiusSquares;
	private float[] mOuterRadiusSquares;
	
	private CLView mView;
	
	public CLTouchController(int circleCount, float[] circleBorders, CLView view) {
		mInnerRadiusSquares = new float[circleCount];
		mOuterRadiusSquares = new float[circleCount];
		
		for (int i = 0; i < circleCount; i++) {
			mInnerRadiusSquares[i] = circleBorders[i] * circleBorders[i];
			mOuterRadiusSquares[i] = circleBorders[i + 1] * circleBorders[i + 1];
		}
		
		mView = view;
	}
	
	private float getTangentialComponent(PointF baseVector, PointF vector) {
		return (baseVector.x * vector.x + baseVector.y * vector.y)
				/ (float) Math.sqrt(baseVector.x * baseVector.x + baseVector.y * baseVector.y);
	}

	private float getNormalComponent(PointF baseVector, PointF vector) {
		return (-baseVector.y * vector.x + baseVector.x * vector.y)
				/ (float) Math.sqrt(baseVector.x * baseVector.x + baseVector.y * baseVector.y);
	}
	
	public void processVectors(CLTouchVectorField field, CLLock circleLock) {
		for (int i = 0; i < circleLock.getCircleCount(); i++) {
			CLCircle circle = circleLock.getCircle(i);
			float circleWidth = mOuterRadiusSquares[i] - mInnerRadiusSquares[i];
			int touchCount = 0;
			float maxPositiveAngleRad = 0f;
			float maxNegativeAngleRad = 0f;
			
			for (int j = 0; j < field.getVectorCount(); j++) {
				CLTouchVectorField.TouchVector vector = field.getVector(j);

				float initDistSquare = vector.init.x * vector.init.x + vector.init.y * vector.init.y;
				float prevDistSquare = vector.previous.x * vector.previous.x + vector.previous.y * vector.previous.y;
				boolean isInitiatedHere = (initDistSquare >= mInnerRadiusSquares[i]
						&& initDistSquare < mOuterRadiusSquares[i]);
				boolean isPrevHere = (prevDistSquare >= mInnerRadiusSquares[i]
						&& prevDistSquare < mOuterRadiusSquares[i]);
				
				switch (circle.getState()) {
				case ROLLING:
					
					if (isInitiatedHere && vector.action == CLTouchVectorField.VectorAction.UNKNOWN)
						vector.action = CLTouchVectorField.VectorAction.ROLL;
					
					if (vector.action == CLTouchVectorField.VectorAction.ROLL && isInitiatedHere) {
						float normalComponent = getNormalComponent(vector.previous,
							new PointF(vector.last.x - vector.previous.x, vector.last.y - vector.previous.y));
						float radius = (float)Math.sqrt(prevDistSquare);
						float angle = (float)Math.atan2(normalComponent, radius);
						if (angle > maxPositiveAngleRad)
							maxPositiveAngleRad = angle;
						if (angle < maxNegativeAngleRad)
							maxNegativeAngleRad = angle;
						touchCount++;
					}
					break;
					
				case IDLE:
					
					if (isInitiatedHere && vector.action == CLTouchVectorField.VectorAction.UNKNOWN) {
						PointF radialInitVector = vector.init;
						PointF lastInitVector = new PointF(vector.last.x - vector.init.x,
								vector.last.y - vector.init.y);
						float normalComponent = getNormalComponent(radialInitVector, lastInitVector);
						float tangentialComponent = getTangentialComponent(radialInitVector, lastInitVector);
						
						if (Math.abs(normalComponent) > 0.5f * circleWidth) {
							vector.action = CLTouchVectorField.VectorAction.ROLL;
							if (isPrevHere) {
								circle.setState(CLCircle.State.ROLLING);
								
								float radius = (float)Math.sqrt(initDistSquare);
								float angle = (float)Math.atan2(normalComponent, radius);
								if (angle > maxPositiveAngleRad)
									maxPositiveAngleRad = angle;
								if (angle < maxNegativeAngleRad)
									maxNegativeAngleRad = angle;								
							}
						} else if (Math.abs(tangentialComponent) > 0.5f * circleWidth) {
							vector.action = CLTouchVectorField.VectorAction.SWAP;
							
							float initAngleRad = (float)Math.atan2(vector.init.y, vector.init.x);
							if (initAngleRad < 0.0f)
								initAngleRad += 2f * Math.PI;
							
							int sectorIndex = circle.getSectorIndexAtAngle(initAngleRad);
							
							if (tangentialComponent > 0.0f && i < circleLock.getCircleCount() - 1) {
								if (circleLock.getCircle(i + 1).getState() == CLCircle.State.IDLE
										&& circle.isSwappable(sectorIndex, circleLock.getCircle(i + 1), initAngleRad)) {
									CLCircle swapCircle = circleLock.getCircle(i + 1);
									int swapSectorIndex = swapCircle.getSectorIndexAtAngle(initAngleRad);
									
									circle.setState(CLCircle.State.SWAPPING);
									swapCircle.setState(CLCircle.State.SWAPPING);
									
									mView.getAnimationThread().addAnimator(
											new CLSwapAnimator(1000, circleLock, circle, swapCircle, sectorIndex, swapSectorIndex));
								}
							} else if (tangentialComponent < 0.0f && i > 0) {
								if (circleLock.getCircle(i - 1).getState() == CLCircle.State.IDLE
										&& circle.isSwappable(sectorIndex, circleLock.getCircle(i - 1), initAngleRad)) {
									CLCircle swapCircle = circleLock.getCircle(i - 1);
									int swapSectorIndex = swapCircle.getSectorIndexAtAngle(initAngleRad);
									
									circle.setState(CLCircle.State.SWAPPING);
									swapCircle.setState(CLCircle.State.SWAPPING);
									
									mView.getAnimationThread().addAnimator(
											new CLSwapAnimator(1000, circleLock, swapCircle, circle, swapSectorIndex, sectorIndex));
								}
							}
						}
						
						touchCount++;
					}	
					break;
					
				case SWAPPING:
				case ANIMATING:
					if (isInitiatedHere && vector.action == CLTouchVectorField.VectorAction.UNKNOWN)
						vector.action = CLTouchVectorField.VectorAction.DUMMY;
					break;
				}
			}
			
			if (circle.getState() == CLCircle.State.ROLLING) {
				if (maxNegativeAngleRad < 0.0f || maxPositiveAngleRad > 0.0f) {
					float resultAngleRad = maxPositiveAngleRad + maxNegativeAngleRad;
					synchronized (circleLock) {
						circle.setAngleRad(circle.getAngleRad() + resultAngleRad);
						mView.requestRender();
					}
				} else if (touchCount == 0)
					synchronized (circleLock) {
						circle.setState(CLCircle.State.ANIMATING);
						mView.getAnimationThread().addAnimator(new CLRollAnimator(200, circleLock, circle));
					}
			}
		}
	}
}
