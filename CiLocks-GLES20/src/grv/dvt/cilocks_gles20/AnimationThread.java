package grv.dvt.cilocks_gles20;

import java.util.ArrayList;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

public class AnimationThread extends Thread {
	
	private static final String TAG = AnimationThread.class.getSimpleName();
	
	private static final int mMaxFPS = 30;
	private static final long mFramePeriod = 1000 / mMaxFPS;
	
	private boolean mIsRunning;
	
	private ArrayList<Animator> mAnimators;
	
	private GLSurfaceView mView;
	
	public AnimationThread(GLSurfaceView view) {
		super();
		mAnimators = new ArrayList<Animator>();
		mView = view;
		mIsRunning = false;
	}
	
	public synchronized void addAnimator(Animator animator) {
		mAnimators.add(animator);
		if (!isAlive()) {
			Log.d(TAG, "Starting animation thread..");
			mIsRunning = true;
			start();
		} else {
			Log.d(TAG, "Notifying about new animations..");
			notify();
		}
	}
	
	public synchronized void terminate() {
		mIsRunning = false;
		if (isAlive()) {
			Log.d(TAG, "Notifying about termination..");
			notify();
		}
	}
	
	@Override
	public synchronized void run() {
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
				try {
					Log.d(TAG, "Waiting for animations..");
					wait();
				} catch (InterruptedException e) {
					// do nothing
				}
		}
	}
}
