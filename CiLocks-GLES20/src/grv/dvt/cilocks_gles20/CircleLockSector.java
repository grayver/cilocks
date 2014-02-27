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
	
	/** Sector rotation angle in radians (in terms of radial axis) */
	private float mAngleRad;
	
	/** Sector rotation angle in degrees (in terms of radial axis) */
	private float mAngleDeg;
	
	/** Sector color */
	private int mColorIndex;
	
	/** Sector symbol */
	private int mSymbolIndex;
	
	
	/** Constructor */
	public CircleLockSector(int colorIndex, int symbolIndex) {
		this.mColorIndex = colorIndex;
		this.mSymbolIndex = symbolIndex;
		
		this.mAngleRad = 0.0f;
		this.mAngleDeg = 0.0f;
		
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
	
	public float getAngleDeg() {
		return this.mAngleDeg;
	}
	
	public void setAngleRad(float value) {
		if (value >= 2f * Math.PI)
			value -= 2f * Math.PI;
		if (value < 0f)
			value += 2f * Math.PI;
		
		this.mAngleRad = value;
		this.mAngleDeg = (float)Math.toDegrees(value);
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
