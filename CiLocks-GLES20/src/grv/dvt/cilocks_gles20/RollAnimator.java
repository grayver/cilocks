package grv.dvt.cilocks_gles20;

public class RollAnimator extends Animator {

	private CircleLockCircle mCircle;
	private int mOffset;
	
	private float mStartAngleRad;
	private float mEndAngleRad;
	
	public RollAnimator(long duration, CircleLockCircle circle) {
		super(duration);
		mCircle = circle;
		
		float stepAngleRad = (float)(2f * Math.PI / circle.getSectorCount());
		mOffset = Math.round(circle.getAngleRad() / stepAngleRad);
		
		mStartAngleRad = circle.getAngleRad();
		mEndAngleRad = mOffset * stepAngleRad;
	}
	
	@Override
	protected void onAnimationUpdate() {
		float angleRad = mStartAngleRad + mFraction * (mEndAngleRad - mStartAngleRad);
		mCircle.setAngleRad(angleRad);
	}

	@Override
	protected void onAnimationEnd() {
		mCircle.roll(mOffset);
		mCircle.setAngleRad(0.0f);
		mCircle.setState(CircleLockCircle.State.IDLE);
	}
}
