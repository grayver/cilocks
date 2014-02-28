package grv.dvt.cilocks_gles20;

import android.os.SystemClock;

public abstract class Animator {

	private long mDuration;
	protected float mFraction;
	private boolean mIsEnded;
	
	private long mStartTime;
	
	public Animator(long duration) {
		this.mDuration = duration;
		this.mStartTime = SystemClock.uptimeMillis();
		this.mIsEnded = false;
	}
	
	public long getDuration() {
		return this.mDuration;
	}
	
	public boolean getIsEnded() {
		return this.mIsEnded;
	}
	
	protected abstract void onAnimationUpdate();
	
	protected abstract void onAnimationEnd();
	
	public void updateAnimation() {
		long elapsed = SystemClock.uptimeMillis() - this.mStartTime;
		this.mFraction = elapsed / (float)this.mDuration;
		
		if (this.mFraction > 1f) {
			this.mIsEnded = true;
			onAnimationEnd();
		}
		else {
			onAnimationUpdate();
		}
	}
}
