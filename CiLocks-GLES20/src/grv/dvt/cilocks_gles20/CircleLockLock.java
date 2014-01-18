package grv.dvt.cilocks_gles20;

/*
 * Circle lock lock
 */
public class CircleLockLock {

	/** Circle count */
	private int mCircleCount;
	
	/** Circles */
	private CircleLockCircle[] mCircles;
	
	
	/** Constructor */
	public CircleLockLock(int circleCount, int sectorCount,
			int[][] colorIndexes, int[][] symbolIndexes) {
		this.mCircleCount = circleCount;
		
		this.mCircles = new CircleLockCircle[circleCount];
		for (int i = 0; i < circleCount; i++)
			this.mCircles[i] = new CircleLockCircle(sectorCount, colorIndexes[i], symbolIndexes[i]);
	}
	
	public int getCircleCount() {
		return this.mCircleCount;
	}
	
	public CircleLockCircle getCircle(int index) {
		return this.mCircles[index];
	}
}
