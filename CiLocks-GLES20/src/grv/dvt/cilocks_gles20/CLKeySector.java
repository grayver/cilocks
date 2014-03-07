package grv.dvt.cilocks_gles20;

public class CLKeySector {
	
	/** Distance from center along radial axis */
	private float mDistance;
	
	/** Sector color */
	private int mColorIndex;
	
	
	/** Constructor */
	public CLKeySector(int colorIndex) {
		this.mColorIndex = colorIndex;
		
		this.mDistance = 0.0f;
	}
	
	public int getColorIndex() {
		return this.mColorIndex;
	}
	
	public float getDistance() {
		return this.mDistance;
	}
	
	public void setDistance(float value) {
		this.mDistance = value;
	}
}
