package grv.dvt.cilocks_gles20;

import java.util.ArrayList;

import android.os.SystemClock;

public class AnimationThread extends Thread {
	
	private static final int mMaxFPS = 30;
	private static final long mFramePeriod = 1000 / mMaxFPS;
	
	private boolean mIsRunning;
	
	private ArrayList<Animator> mAnimators;
	
	private CircleLockView mView;
	
	public AnimationThread(CircleLockView view) {
		super();
		mAnimators = new ArrayList<Animator>();
		mView = view;
		mIsRunning = false;
	}
	
	public void addAnimator(Animator animator) {
		mAnimators.add(animator);
		if (!isAlive()) {
			mIsRunning = true;
			start();
		}
	}
	
	@Override
	public void run() {
		while (this.mIsRunning) {
			long startTime = SystemClock.uptimeMillis();
			
			int i = 0;
			while (i < mAnimators.size()) {
				Animator animator = mAnimators.get(i);
				animator.updateAnimation();
				if (animator.getIsEnded())
					mAnimators.remove(i);
				else
					i++;
			}
			
			mView.requestRender();
			
			long elapsed = SystemClock.uptimeMillis() - startTime;
			long remaining = mFramePeriod - elapsed;
			
			if (remaining > 0) {
				try {
					Thread.sleep(remaining);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
			
			if (mAnimators.isEmpty())
				mIsRunning = false;
		}
	}
}
