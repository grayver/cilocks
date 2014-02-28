package grv.dvt.cilocks_gles20;

public class SwapAnimator extends Animator {

	private CircleLockCircle mInnerCircle;
	private CircleLockCircle mOuterCircle;
	
	private int mSectorIndex;
	
	private boolean mIsSwapped;
	
	public SwapAnimator(long duration, CircleLockCircle innerCircle, CircleLockCircle outerCircle, int sectorIndex) {
		super(duration);
		
		innerCircle.getSector(sectorIndex).setAngleRad(0.0f);
		outerCircle.getSector(sectorIndex).setAngleRad(0.0f);
		
		mInnerCircle = innerCircle;
		mOuterCircle = outerCircle;
		mSectorIndex = sectorIndex;
		
		mIsSwapped = false;
	}
	
	@Override
	protected void onAnimationUpdate() {
		if (mFraction > 0.5f && !mIsSwapped) {
			mInnerCircle.swap(mOuterCircle, mSectorIndex);
			mIsSwapped = true;
		}

		if (mFraction > 0.5f) {
			mInnerCircle.getSector(mSectorIndex).setAngleRad((mFraction - 1.0f) * (float)Math.PI);
			mOuterCircle.getSector(mSectorIndex).setAngleRad((1.0f - mFraction) * (float)Math.PI);
		} else {
			mInnerCircle.getSector(mSectorIndex).setAngleRad(mFraction * (float)Math.PI);
			mOuterCircle.getSector(mSectorIndex).setAngleRad(-mFraction * (float)Math.PI);
		}
	}

	@Override
	protected void onAnimationEnd() {
		mInnerCircle.getSector(mSectorIndex).setAngleRad(0.0f);
		mOuterCircle.getSector(mSectorIndex).setAngleRad(0.0f);
		
		mInnerCircle.setState(CircleLockCircle.State.IDLE);
		mOuterCircle.setState(CircleLockCircle.State.IDLE);
	}
}
