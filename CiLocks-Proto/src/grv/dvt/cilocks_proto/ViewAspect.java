package grv.dvt.cilocks_proto;

public class ViewAspect {

	private float mWidth;
	private float mHeight;

	public ViewAspect(float width, float height) {
		this.mWidth = width;
		this.mHeight = height;
	}

	public void setSize(float width, float height) {
		this.mWidth = width;
		this.mHeight = height;
	}

	public float getWidth() {
		return this.mWidth;
	}
	
	public float getHeight() {
		return this.mHeight;
	}
	
	public float getCenterX() {
		return 0.5f * this.mWidth;
	}

	public float getCenterY() {
		return 0.5f * this.mHeight;
	}
}
