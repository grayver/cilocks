package grv.dvt.cilocks_gles20;

/*
 * Circle lock sector
 */
public class CircleLockSector {
	
	public enum State {
		IDLE, SWAPPING
	}
	
	
	/** Sector state */
	private CircleLockSector.State mState;
	
	/** Sector rotation angle (in terms of radial axis) */
	private float mAngleRad;
	
	/** Sector color */
	private int mColorIndex;
	
	/** Sector symbol */
	private int mSymbolIndex;
	
	
	/** Constructor */
	public CircleLockSector(int colorIndex, int symbolIndex) {
		this.mColorIndex = colorIndex;
		this.mSymbolIndex = symbolIndex;
		
		this.mAngleRad = 0f;
		this.mState = CircleLockSector.State.IDLE;
	}
	
	public CircleLockSector.State getState() {
		return this.mState;
	}
	
	public void setState(CircleLockSector.State value) {
		this.mState = value;
	}
	
	public float getAngleRad() {
		return this.mAngleRad;
	}
	
	public void setAngleRad(float value) {
		if (value >= 2 * Math.PI)
			value -= 2 * Math.PI;
		if (value < 0)
			value += 2 * Math.PI;
		
		this.mAngleRad = value;
	}
	
	public int getColorIndex() {
		return this.mColorIndex;
	}
	
	public void setColorIndex(int value) {
		this.mColorIndex = value;
	}
	
	public int getSymbolIndex() {
		return this.mSymbolIndex;
	}
}
