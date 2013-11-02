package grv.dvt.cilocks_proto;
public class CircleData {

	private int mSize;

	private int[] mColorIndexes;
	private int[] mSymbolIndexes;

	public CircleData(int size, int[] colorIndexes, int[] symbolIndexes) {
		this.mSize = size;
		this.mColorIndexes = new int[size];
		this.mSymbolIndexes = new int[size];
		System.arraycopy(colorIndexes, 0, this.mColorIndexes, 0, size);
		System.arraycopy(symbolIndexes, 0, this.mSymbolIndexes, 0, size);
	}

	public int getColorIndex(int index) {
		return this.mColorIndexes[index];
	}

	public int getSymbolIndex(int index) {
		return this.mSymbolIndexes[index];
	}

	public void roll(int offset) {
		int[] colorBuffer = this.mColorIndexes.clone();
		int[] symbolBuffer = this.mSymbolIndexes.clone();

		for (int i = 0; i < this.mSize; i++) {
			int index = (i - offset) % this.mSize;
			if (index < 0)
				index += this.mSize;

			this.mColorIndexes[i] = colorBuffer[index];
			this.mSymbolIndexes[i] = symbolBuffer[index];
		}
	}
	
	public void swap(CircleData data, int index) {
		int colorIndex = this.mColorIndexes[index];
		this.mColorIndexes[index] = data.mColorIndexes[index];
		data.mColorIndexes[index] = colorIndex;
	}
}
