package grv.dvt.cilocks_proto;

public class Circle {

	public enum CircleState {
		IDLE, ROLLING, SWAPPING, ANIMATING
	}

	private CircleData mData;
	private int mSectorCount;

	private float mAngleRad;
	private CircleState mState;

	public Circle(int size, int[] colorIndexes, int[] symbolIndexes) {
		this.mData = new CircleData(size, colorIndexes, symbolIndexes);
		this.mSectorCount = size;

		this.mAngleRad = 0;
		this.mState = CircleState.IDLE;
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

	public CircleData getData() {
		return this.mData;
	}

	public CircleState getState() {
		return this.mState;
	}
	
	public void setState(CircleState value) {
		this.mState = value;
	}

	/**
	 * Game logic
	 */

	public boolean isSwappable(Circle neighbour, int sectorIndex) {
		return this.mData.getSymbolIndex(sectorIndex) == neighbour.mData
				.getSymbolIndex(sectorIndex);
	}
}
