package grv.dvt.cilocks_proto;

public class CircleLock {

	private Circle[] mCircles;
	private int mCircleCount;

	private int mKeyColorCount;
	private int[] mKeyColorIndexes;
	
	public CircleLock(LockData lockData) {
		this.mCircleCount = lockData.rowCount;
		this.mCircles = new Circle[this.mCircleCount];
		
		for (int i = 0; i < lockData.rowCount; i++)
			this.mCircles[i] = new Circle(lockData.columnCount,
					lockData.colorIndexes[i], lockData.symbolIndexes[i]);
		
		this.mKeyColorCount = lockData.keyColorCount;
		this.mKeyColorIndexes = new int[lockData.keyColorCount];
		System.arraycopy(lockData.keyColorIndexes, 0, this.mKeyColorIndexes, 0, lockData.keyColorCount);
	}

	public int getCircleCount() {
		return this.mCircleCount;
	}
	
	public Circle getCircle(int index) {
		return this.mCircles[index];
	}
	
	public int getKeyColorCount() {
		return this.mKeyColorCount;
	}
	
	public int getKeyColor(int index) {
		return this.mKeyColorIndexes[index];
	}
}
