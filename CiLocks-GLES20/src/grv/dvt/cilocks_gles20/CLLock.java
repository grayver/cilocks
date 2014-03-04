package grv.dvt.cilocks_gles20;

/*
 * CircleLock lock
 */
public class CLLock {

	/** Circle count */
	private int mCircleCount;
	
	/** Circles */
	private CLCircle[] mCircles;
	
	
	/** Constructor */
	public CLLock(int circleCount, int sectorCount,
			int[][] colorIndexes, int[][] symbolIndexes) {
		this.mCircleCount = circleCount;
		
		this.mCircles = new CLCircle[circleCount];
		for (int i = 0; i < circleCount; i++)
			this.mCircles[i] = new CLCircle(sectorCount, colorIndexes[i], symbolIndexes[i]);
	}
	
	public int getCircleCount() {
		return this.mCircleCount;
	}
	
	public CLCircle getCircle(int index) {
		return this.mCircles[index];
	}
}
