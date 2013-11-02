package grv.dvt.cilocks_proto;

import java.util.ArrayList;

public class AnimationPool {
	
	private ArrayList<Animator> mAnimators;
	
	public AnimationPool() {
		this.mAnimators = new ArrayList<Animator>();
	}
	
	public void addAnimator(Animator animator) {
		this.mAnimators.add(animator);
	}
	
	public void updateAnimation() {
		for (int i = 0; i < this.mAnimators.size(); i++) {
			Animator animator = this.mAnimators.get(i);
			animator.updateAnimation();
			if (animator.getIsEnded())
				this.mAnimators.remove(i);
		}
	}
}
