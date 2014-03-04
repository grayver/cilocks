package grv.dvt.cilocks_gles20;

/*
 * CircleLock sector
 */
public class CLSector {
	
	public enum State {
		IDLE, SWAPPING
	}
	
	
	/** Sector state */
	private CLSector.State mState;
	
	/** Sector rotation angle in radians (in terms of radial axis) */
	private float mAngleRad;
	
	/** Sector rotation angle in degrees (in terms of radial axis) */
	private float mAngleDeg;
	
	/** Sector color */
	private int mColorIndex;
	
	/** Sector symbol */
	private int mSymbolIndex;
	
	
	/** Constructor */
	public CLSector(int colorIndex, int symbolIndex) {
		this.mColorIndex = colorIndex;
		this.mSymbolIndex = symbolIndex;
		
		this.mAngleRad = 0.0f;
		this.mAngleDeg = 0.0f;
		
		this.mState = CLSector.State.IDLE;
	}
	
	public CLSector.State getState() {
		return this.mState;
	}
	
	public void setState(CLSector.State value) {
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
