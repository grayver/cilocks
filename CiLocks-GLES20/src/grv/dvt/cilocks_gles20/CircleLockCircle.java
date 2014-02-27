package grv.dvt.cilocks_gles20;

/*
 * Circle lock circle
 */
public class CircleLockCircle {

	public enum State {
		IDLE, ROLLING, SWAPPING, ANIMATING
	}
	
	
	/** Circle state */
	private CircleLockCircle.State mState;
	
	/** Circle rotation angle in radians */
	private float mAngleRad;
	
	/** Circle rotation angle in degrees */
	private float mAngleDeg;
	
	/** Sector count */
	private int mSectorCount;
	
	/** Sectors */
	private CircleLockSector[] mSectors;
	
	
	/** Constructor */
	public CircleLockCircle(int sectorCount, int[] colorIndexes, int[] symbolIndexes) {
		this.mState = CircleLockCircle.State.IDLE;
		
		this.mAngleRad = 0.0f;
		this.mAngleDeg = 0.0f;
		
		this.mSectorCount = sectorCount;
		this.mSectors = new CircleLockSector[sectorCount];
		for (int i = 0; i < sectorCount; i++)
			this.mSectors[i] = new CircleLockSector(colorIndexes[i], symbolIndexes[i]);
	}
	
	public CircleLockCircle.State getState() {
		return this.mState;
	}
	
	public void setState(CircleLockCircle.State value) {
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
	
	public int getSectorCount() {
		return this.mSectorCount;
	}
	
	public CircleLockSector getSector(int index) {
		return this.mSectors[index];
	}
	
	/*
	 * Game logic
	 */
	
	public void roll(int offset) {
		CircleLockSector[] sectorBuffer = this.mSectors.clone();
		
		for (int i = 0; i < this.mSectorCount; i++) {
			int index = (i - offset) % this.mSectorCount;
			if (index < 0)
				index += this.mSectorCount;

			this.mSectors[i] = sectorBuffer[index];
		}
	}
	
	public boolean isSwappable(CircleLockCircle circle, int index) {
		return this.mSectors[index].getSymbolIndex() == circle.mSectors[index].getSymbolIndex();
	}
	
	public void swap(CircleLockCircle circle, int index) {
		int colorIndex = this.mSectors[index].getColorIndex();
		this.mSectors[index].setColorIndex(circle.mSectors[index].getColorIndex());
		circle.mSectors[index].setColorIndex(colorIndex);
	}
}
