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
	
	/**
	 * Determines sector index by specified angle
	 * @param angleRad should be between 0 and 2*Pi
	 */
	public int getSectorIndexAtAngle(float angleRad) {
		float stepAngleRad = (float)(2f * Math.PI / mSectorCount);
		
		float sectorAngleRad = angleRad - mAngleRad;
		if (sectorAngleRad < 0.0f)
			sectorAngleRad += 2f * Math.PI;
		
		return (int)Math.floor(sectorAngleRad / stepAngleRad);
	}
	
	/*
	 * Game logic
	 */
	
	/**
	 * Determines if sectors could be swapped
	 * @param sectorIndex sector index of current circle
	 * @param swapCandidate swap circle
	 * @param angleRad angle for swap sector index determination (should be between 0 and 2*Pi)
	 * @return true if sectors could be swapped
	 */
	public boolean isSwappable(int sectorIndex, CircleLockCircle swapCandidate, float angleRad) {
		int swapSectorIndex = swapCandidate.getSectorIndexAtAngle(angleRad);
		return this.mSectors[sectorIndex].getSymbolIndex() == swapCandidate.mSectors[swapSectorIndex].getSymbolIndex();
	}
	
	/**
	 * Swaps sectors
	 * @param sectorIndex sector index of current circle
	 * @param swapCircle swap circle
	 * @param swapSectorIndex swap circle sector index
	 */
	public void swap(int sectorIndex, CircleLockCircle swapCircle, int swapSectorIndex) {
		int colorIndex = this.mSectors[sectorIndex].getColorIndex();
		this.mSectors[sectorIndex].setColorIndex(swapCircle.mSectors[swapSectorIndex].getColorIndex());
		swapCircle.mSectors[swapSectorIndex].setColorIndex(colorIndex);
	}
}
