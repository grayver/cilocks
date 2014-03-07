package grv.dvt.cilocks_gles20;

public class CLUnlockAnimator extends Animator {

	private CLLock mCircleLock;
	private CLGame mGame;
	
	public CLUnlockAnimator(long duration, CLLock circleLock, CLGame game) {
		super(duration);
		mCircleLock = circleLock;
		mGame = game;
		
		synchronized (mCircleLock) {
			CLKeyCircle keyCircle = mCircleLock.getKeyCircle();
			for (int i = 0; i < keyCircle.getSectorCount(); i++)
				keyCircle.getSector(i).setDistance(0.0f);
		}
	}
	
	@Override
	protected void onAnimationUpdate() {
		// use parabolic curve
		float distance = mFraction * mFraction;
		
		synchronized (mCircleLock) {
			CLKeyCircle keyCircle = mCircleLock.getKeyCircle();
			for (int i = 0; i < keyCircle.getSectorCount(); i++)
				keyCircle.getSector(i).setDistance(distance);
		}
	}

	@Override
	protected void onAnimationEnd() {
		synchronized (mCircleLock) {
			CLKeyCircle keyCircle = mCircleLock.getKeyCircle();
			for (int i = 0; i < keyCircle.getSectorCount(); i++)
				keyCircle.getSector(i).setDistance(1.0f);
			mGame.setState(CLGame.State.OVER);
		}
	}

}
