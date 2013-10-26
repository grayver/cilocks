public class CircleLock {

	private Circle[] mCircles;
	private int mCircleCount;

	public CircleLock(LockData lockData) {
		this.mCircleCount = lockData.rowCount;
		this.mCircles = new Circle[this.mCircleCount];
		
		for (int i = 0; i < lockData.rowCount; i++)
			this.mCircles[i] = new Circle(lockData.columnCount,
					lockData.colorIndexes[i], lockData.symbolIndexes[i]);
	}

	public Circle getCircle(int index) {
		return this.mCircles[index];
	}
	
	public int getCircleCount() {
		return this.mCircleCount;
	}
}
