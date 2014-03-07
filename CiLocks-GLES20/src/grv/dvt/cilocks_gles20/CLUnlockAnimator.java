package grv.dvt.cilocks_gles20;

public class CLUnlockAnimator extends Animator {

	private CLLock mCircleLock;
	
	private float mTargetDistance;
	
	public CLUnlockAnimator(long duration, CLLock circleLock, float targetDistance) {
		super(duration);
		mCircleLock = circleLock;
		
		mTargetDistance = targetDistance;
		
		synchronized (mCircleLock) {
			CLKeyCircle keyCircle = mCircleLock.getKeyCircle();
			for (int i = 0; i < keyCircle.getSectorCount(); i++)
				keyCircle.getSector(i).setDistance(0.0f);
		}
	}
	
	@Override
	protected void onAnimationUpdate() {
		// use parabolic curve
		float distance = mFraction * mFraction * mTargetDistance;
		
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
				keyCircle.getSector(i).setDistance(mTargetDistance);
		}
	}

}
