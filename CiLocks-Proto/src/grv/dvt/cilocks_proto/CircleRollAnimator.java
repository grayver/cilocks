package grv.dvt.cilocks_proto;

import grv.dvt.cilocks_proto.Circle.CircleState;

public class CircleRollAnimator extends Animator {

	private Circle mCircle;
	private int mOffset;
	
	private float mStartAngleRad;
	private float mEndAngleRad;
	
	public CircleRollAnimator(long duration, Circle circle) {
		super(duration);
		this.mCircle = circle;
		
		float stepAngleRad = (float)(2 * Math.PI / circle.getSectorCount());
		this.mOffset = Math.round(circle.getAngleRad() / stepAngleRad);
		
		this.mStartAngleRad = circle.getAngleRad();
		this.mEndAngleRad = this.mOffset * stepAngleRad;
	}
	
	@Override
	protected void onAnimationUpdate() {
		float angleRad = this.mStartAngleRad + this.mFraction * (this.mEndAngleRad - this.mStartAngleRad);
		this.mCircle.setAngleRad(angleRad);
	}

	@Override
	protected void onAnimationEnd() {
		this.mCircle.getData().roll(this.mOffset);
		this.mCircle.setAngleRad(0);
		this.mCircle.setState(CircleState.IDLE);
	}

}
