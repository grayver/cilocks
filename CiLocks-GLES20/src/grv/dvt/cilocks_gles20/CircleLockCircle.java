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
	
	/** Circle rotation angle */
	private float mAngleRad;
	
	/** Sector count */
	private int mSectorCount;
	
	/** Sectors */
	private CircleLockSector[] mSectors;
	
	
	/** Constructor */
	public CircleLockCircle(int sectorCount, int[] colorIndexes, int[] symbolIndexes) {
		this.mState = CircleLockCircle.State.IDLE;
		
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
	
	public void setAngleRad(float value) {
		if (value >= 2 * Math.PI)
			value -= 2 * Math.PI;
		if (value < 0)
			value += 2 * Math.PI;
		
		this.mAngleRad = value;
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
