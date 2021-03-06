package grv.dvt.cilocks_gles20;

public class CLSwapAnimator extends Animator {

	private CLLock mCircleLock;
	private CLCircle mInnerCircle;
	private CLCircle mOuterCircle;
	
	private int mInnerSectorIndex;
	private int mOuterSectorIndex;
	
	private boolean mIsSwapped;
	
	public CLSwapAnimator(
			long duration,
			CLLock circleLock,
			CLCircle innerCircle,
			CLCircle outerCircle,
			int innerSectorIndex,
			int outerSectorIndex) {
		super(duration);
		mCircleLock = circleLock;
		
		synchronized (mCircleLock) {
			innerCircle.getSector(innerSectorIndex).setAngleRad(0.0f);
			outerCircle.getSector(outerSectorIndex).setAngleRad(0.0f);
		}
		
		mInnerCircle = innerCircle;
		mOuterCircle = outerCircle;
		mInnerSectorIndex = innerSectorIndex;
		mOuterSectorIndex = outerSectorIndex;
		
		mIsSwapped = false;
	}
	
	@Override
	protected void onAnimationUpdate() {
		synchronized (mCircleLock) {
			if (mFraction > 0.5f && !mIsSwapped) {
				mInnerCircle.swap(mInnerSectorIndex, mOuterCircle, mOuterSectorIndex);
				mIsSwapped = true;
			}

			if (mFraction > 0.5f) {
				mInnerCircle.getSector(mInnerSectorIndex).setAngleRad((mFraction - 1.0f) * (float)Math.PI);
				mOuterCircle.getSector(mOuterSectorIndex).setAngleRad((1.0f - mFraction) * (float)Math.PI);
			} else {
				mInnerCircle.getSector(mInnerSectorIndex).setAngleRad(mFraction * (float)Math.PI);
				mOuterCircle.getSector(mOuterSectorIndex).setAngleRad(-mFraction * (float)Math.PI);
			}
		}
	}

	@Override
	protected void onAnimationEnd() {
		synchronized (mCircleLock) {
			mInnerCircle.getSector(mInnerSectorIndex).setAngleRad(0.0f);
			mOuterCircle.getSector(mOuterSectorIndex).setAngleRad(0.0f);
			
			mInnerCircle.setState(CLCircle.State.IDLE);
			mOuterCircle.setState(CLCircle.State.IDLE);
		}
	}
}
