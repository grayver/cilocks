package grv.dvt.cilocks_gles20;

public class MatrixStack {
	
	/** Matrix size in elements */
	private final int mMatrixSize = 16;
	
	private float[] mBuffer;
	private int mOffset;
	private int mCapacity;
	
	public MatrixStack(int capacity) {
		mCapacity = mMatrixSize * capacity;
		mBuffer = new float[mCapacity];
		mOffset = 0;
	}
	
	public void push(float[] src, int srcOffset) {
		if (mOffset + mMatrixSize > mCapacity)
			throw new RuntimeException("Matrix stack overflow.");
		System.arraycopy(src, srcOffset, mBuffer, mOffset, mMatrixSize);
		mOffset += mMatrixSize;
	}
	
	public void pop(float[] dst, int dstOffset) {
		if (mOffset <= 0)
			throw new RuntimeException("Matrix stack underflow.");
		mOffset -= mMatrixSize;
		System.arraycopy(mBuffer, mOffset, dst, dstOffset, mMatrixSize);
	}
}
