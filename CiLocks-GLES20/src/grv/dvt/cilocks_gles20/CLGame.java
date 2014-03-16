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
	
	/** Own objects */
	private CLLock mCircleLock;
	private AnimationThread mAnimationThread;
	
	/** Reference objects */
	private CLView mView;
	
	/** Constructor */
	public CLGame(CLView view) {
		mState = CLGame.State.ACTIVE;
		
		// Test circle lock
		int[][] colorIndexes = new int[][] {
				{ 3, 1, 0, 3, 1, 2, 3, 1 },
				{ 1, 0, 1, 0, 0, 1, 1, 0 },
				{ 0, 1, 3, 0, 1, 1, 2, 1 } };
		int[][] symbolIndexes = new int[][] {
				{ 0, 1, 2, 1, 1, 2, 3, 2 },
				{ 2, 0, 1, 2, 1, 1, 0, 1 },
				{ 0, 1, 2, 1, 1, 2, 3, 2 } };
		int[] keyColorIndexes = new int[] { 0, 1, 2, 3 };
		mCircleLock = new CLLock(3, 8, colorIndexes, symbolIndexes, 4, keyColorIndexes);
		
		mView = view;
	}
	
	public void createContext() {
		// create animation thread
		mAnimationThread = new AnimationThread(mView);
	}
	
	public void releaseContext() {
		// release animation thread
		boolean retry = true;
		mAnimationThread.terminate();
		while (retry) {
			try {
				mAnimationThread.join();
				retry = false;
			} catch (InterruptedException e) {
				//
			}
		}
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
				mAnimationThread.addAnimator(new CLUnlockAnimator(200, mCircleLock, this));
			}
		}
	}
	
	public CLLock getCircleLock() {
		return mCircleLock;
	}
	
	public AnimationThread getAnimationThread() {
		return mAnimationThread;
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
