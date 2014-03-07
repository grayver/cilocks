package grv.dvt.cilocks_gles20;

/*
 * CircleLock game
 */
public class CLGame {
	
	public enum State {
		ACTIVE, ANIMATING, OVER
	}
	
	
	/** Game state */
	private CLGame.State mState;
	
	/** Circle lock */
	private CLLock mCircleLock;
	
	private CLView mView;
	
	/** Constructor */
	public CLGame(CLLock circleLock, CLView view) {
		mState = CLGame.State.ACTIVE;
		
		mCircleLock = circleLock;
		mView = view;
	}
	
	public CLGame.State getState() {
		return mState;
	}
	
	public void setState(CLGame.State value) {
		mState = value;
	}
	
	public void updateState() {
		if (mState == CLGame.State.ACTIVE) {
			if (detectUnlock()) {
				mState = CLGame.State.ANIMATING;
				mView.getAnimationThread().addAnimator(new CLUnlockAnimator(200, mCircleLock, this));
			}
		}
	}
	
	
	private boolean detectUnlock() {
		int keyCount = mCircleLock.getKeyCircle().getSectorCount();
		int[] keyColorIndexes = new int[keyCount];
		for (int i = 0; i < keyCount; i++)
			keyColorIndexes[i] = mCircleLock.getKeyCircle().getSector(i).getColorIndex();
		
		for (int i = 0; i < mCircleLock.getCircleCount(); i++) {
			CLCircle circle = mCircleLock.getCircle(i);
			if (circle.getState() != CLCircle.State.IDLE)
				return false;
			
			float stepAngleRad = (float)(2f * Math.PI / circle.getSectorCount());
			int offset = Math.round(circle.getAngleRad() / stepAngleRad);
			int sectorToKeyFactor = circle.getSectorCount() / keyCount;
			
			for (int j = 0; j < circle.getSectorCount(); j++) {
				int sectorIndex = (j - offset) % circle.getSectorCount();
				if (sectorIndex < 0)
					sectorIndex += circle.getSectorCount();
				
				if (circle.getSector(sectorIndex).getColorIndex() != keyColorIndexes[j / sectorToKeyFactor])
					return false;
			}
		}
		
		return true;
	}
}
