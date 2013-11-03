package grv.dvt.cilocks_proto;

import grv.dvt.cilocks_proto.Circle.CircleState;

public class SwapAnimator extends Animator {

	private Circle mInnerCircle;
	private Circle mOuterCircle;
	
	private boolean mIsSwapped;
	
	public SwapAnimator(long duration, Circle innerCircle, Circle outerCircle) {
		super(duration);
		
		innerCircle.swapSectorAngleRad = 0;
		outerCircle.swapSectorAngleRad = 0;
		
		this.mInnerCircle = innerCircle;
		this.mOuterCircle = outerCircle;
		
		this.mIsSwapped = false;
	}
	
	@Override
	protected void onAnimationUpdate() {
		if (this.mFraction > 0.5f && !this.mIsSwapped) {
			this.mInnerCircle.getData().swap(this.mOuterCircle.getData(), this.mInnerCircle.swapSectorIndex);
			this.mIsSwapped = true;
		}
		
		this.mInnerCircle.swapSectorAngleRad = this.mFraction * (float)Math.PI;
		this.mOuterCircle.swapSectorAngleRad = -this.mFraction * (float)Math.PI;
	}

	@Override
	protected void onAnimationEnd() {
		this.mInnerCircle.setState(CircleState.IDLE);
		this.mOuterCircle.setState(CircleState.IDLE);
	}

}
