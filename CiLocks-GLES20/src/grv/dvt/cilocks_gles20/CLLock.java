package grv.dvt.cilocks_gles20;

/*
 * CircleLock lock
 */
public class CLLock {

	/** Circle count */
	private int mCircleCount;
	
	/** Circles */
	private CLCircle[] mCircles;
	
	/** Key circle */
	private CLKeyCircle mKeyCircle;
	
	
	/** Constructor */
	public CLLock(
			int circleCount,
			int sectorCount,
			int[][] colorIndexes,
			int[][] symbolIndexes,
			int keySectorCount,
			int[] keyColorIndexes) {
		
		this.mCircleCount = circleCount;
		
		this.mCircles = new CLCircle[circleCount];
		for (int i = 0; i < circleCount; i++)
			this.mCircles[i] = new CLCircle(sectorCount, colorIndexes[i], symbolIndexes[i]);
		
		this.mKeyCircle = new CLKeyCircle(keySectorCount, keyColorIndexes);
	}
	
	public int getCircleCount() {
		return this.mCircleCount;
	}
	
	public CLCircle getCircle(int index) {
		return this.mCircles[index];
	}
	
	public CLKeyCircle getKeyCircle() {
		return this.mKeyCircle;
	}
}
