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
	
	/** Constructor */
	public CLGame(CLLock circleLock) {
		mState = CLGame.State.ACTIVE;
		
		mCircleLock = circleLock;
	}
	
	public CLGame.State getState() {
		return mState;
	}
	
	public boolean detectUnlock() {
		int keyCount = mCircleLock.getKeyCircle().getSectorCount();
		int[] keyColorIndexes = new int[keyCount];
		for (int i = 0; i < keyCount; i++)
			keyColorIndexes[i] = mCircleLock.getKeyCircle().getSector(i).getColorIndex();
		
		for (int i = 0; i < mCircleLock.getCircleCount(); i++) {
			CLCircle circle = mCircleLock.getCircle(i);
			if (circle.getState() != CLCircle.State.IDLE)
				return false;
			
			int sectorToKeyFactor = circle.getSectorCount() / keyCount;
			for (int j = 0; j < circle.getSectorCount(); j++)
				if (circle.getSector(j).getColorIndex() != keyColorIndexes[j / sectorToKeyFactor])
					return false;
		}
		
		return true;
	}
}
