package grv.dvt.cilocks_gles20;

public class CLRollAnimator extends Animator {

	private CLLock mCircleLock;
	private CLGame mGame;
	private CLCircle mCircle;
	
	private float mStartAngleRad;
	private float mEndAngleRad;
	
	public CLRollAnimator(long duration, CLLock circleLock, CLGame game, CLCircle circle) {
		super(duration);
		mCircleLock = circleLock;
		mGame = game;
		mCircle = circle;
		
		float stepAngleRad = (float)(2f * Math.PI / circle.getSectorCount());
		int offset = Math.round(circle.getAngleRad() / stepAngleRad);
		
		mStartAngleRad = circle.getAngleRad();
		mEndAngleRad = offset * stepAngleRad;
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
			mCircle.setState(CLCircle.State.IDLE);
			mGame.updateState();
		}
	}
}
