package grv.dvt.cilocks_gles20;

public class RollAnimator extends Animator {

	private CircleLockLock mCircleLock;
	private CircleLockCircle mCircle;
	private int mOffset;
	
	private float mStartAngleRad;
	private float mEndAngleRad;
	
	public RollAnimator(long duration, CircleLockLock circleLock, CircleLockCircle circle) {
		super(duration);
		mCircleLock = circleLock;
		mCircle = circle;
		
		float stepAngleRad = (float)(2f * Math.PI / circle.getSectorCount());
		mOffset = Math.round(circle.getAngleRad() / stepAngleRad);
		
		mStartAngleRad = circle.getAngleRad();
		mEndAngleRad = mOffset * stepAngleRad;
	}
	
	@Override
	protected void onAnimationUpdate() {
		float angleRad = mStartAngleRad + mFraction * (mEndAngleRad - mStartAngleRad);
		synchronized (mCircleLock) {
			mCircle.setAngleRad(angleRad);
		}
		
	}

	@Override
	protected void onAnimationEnd() {
		synchronized (mCircleLock) {
			mCircle.setAngleRad(mEndAngleRad);
			mCircle.setState(CircleLockCircle.State.IDLE);
		}
	}
}
