package grv.dvt.cilocks_gles20;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;

public class CLMeshContainer {
	// Circle count in mesh geometry
	private int mCircleCount;
	
	// Sector count in mesh geometry
	private int mSectorCount;
	
	// Key sector count in mesh geometry
	private int mKeySectorCount;
	
	
	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;
	
	/** Offset of the position data in bytes. */
	private final int mPositionOffset = 0;
	
	/** Size of the position data in elements. */
	private final int mPositionSize = 3;
	
	/** Size of the position data in bytes. */
	private final int mPositionDataSize = mPositionSize * mBytesPerFloat;
	
	/** Offset of the texture mapping data in bytes. */
	private final int mUVOffset = mPositionOffset + mPositionDataSize;
	
	/** Size of the texture mapping data in elements. */
	private final int mUVSize = 2;
	
	/** Size of the texture mapping data in bytes. */
	private final int mUVDataSize = mUVSize * mBytesPerFloat;
	
	/** Offset of the normal data in bytes. */
	private final int mNormalOffset = mUVOffset + mUVDataSize;
	
	/** Size of the normal data in elements. */
	private final int mNormalSize = 3;
	
	/** Size of the normal data in bytes. */
	private final int mNormalDataSize = mNormalSize * mBytesPerFloat;
	
	/** Offset of the tangent data in bytes. */
	private final int mTangentOffset = mNormalOffset + mNormalDataSize;
	
	/** Size of the tangent data in elements. */
	private final int mTangentSize = 3;
	
	/** Size of the tangent data in bytes. */
	private final int mTangentDataSize = mTangentSize * mBytesPerFloat;
	
	/** Offset of the bitangent data in bytes.  */
	private final int mBitangentOffset = mTangentOffset + mTangentDataSize;
	
	/** Size of the bitangent data in elements. */
	private final int mBitangentSize = 3;
	
	/** Size of the bitangent data in bytes. */
	private final int mBitangentDataSize = mBitangentSize * mBytesPerFloat;
	
	/** How many bytes per vertex. */
	private final int mStrideBytes = mBitangentOffset + mBitangentDataSize;
	
	
	// Activity context
	private Context mContext;
	
	// GLES buffer identifiers
	private int[][] mCircleBufferIds;
	private int[] mKeyBufferIds;
	
	// Vertex counts
	private int[][] mCircleVertexCounts;
	private int[] mKeyVertexCounts;
	
	private boolean mIsLoaded;
	
	public CLMeshContainer(Context context) {
		mContext = context;
		mIsLoaded = false;
	}
	
	public int loadMesh(DataInputStream inputStream, int bufferId) throws IOException {
		int faceCount = inputStream.readInt();
		int vertexCount = faceCount * 3;
		int bufferSize = vertexCount * mStrideBytes;
		byte[] buffer = new byte[bufferSize];
		inputStream.read(buffer, 0, bufferSize);

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.BIG_ENDIAN);
		byteBuffer.put(buffer, 0, bufferSize);
		byteBuffer.position(0);

		FloatBuffer flBuffer;
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
			flBuffer = byteBuffer.asFloatBuffer();
		} else {
			FloatBuffer cnvBuffer = byteBuffer.asFloatBuffer();
			float[] cnvArray = new float[cnvBuffer.capacity()];
			cnvBuffer.get(cnvArray);
			flBuffer = FloatBuffer.allocate(cnvArray.length);
			flBuffer.put(cnvArray);
		}
		flBuffer.position(0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, flBuffer.capacity() * mBytesPerFloat, flBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		return vertexCount;
	}
	
	public void loadMeshes() throws IOException {
		int resId = mContext.getResources().getIdentifier("mesh_3x8x4", "raw", mContext.getPackageName());
		DataInputStream is = new DataInputStream(mContext.getResources().openRawResource(resId));
		
		mCircleCount = is.readInt();
		mSectorCount = is.readInt();
		mKeySectorCount = is.readInt();
		
		mCircleBufferIds = new int[mCircleCount][mSectorCount];
		mCircleVertexCounts = new int[mCircleCount][mSectorCount];
		mKeyBufferIds = new int[mKeySectorCount];
		mKeyVertexCounts = new int[mKeySectorCount];
		
		int bufferCount = mCircleCount * mSectorCount + mKeySectorCount;
		int[] flatBufferIds = new int[bufferCount];
		GLES20.glGenBuffers(bufferCount, flatBufferIds, 0);
		int counter = 0;
		
		for (int i = 0; i < mCircleCount; i++)
			for (int j = 0; j < mSectorCount; j++) {
				mCircleVertexCounts[i][j] = loadMesh(is, flatBufferIds[counter]);
				mCircleBufferIds[i][j] = flatBufferIds[counter];
				counter++;
			}
		
		for (int i = 0; i < mKeySectorCount; i++) {
			mKeyVertexCounts[i] = loadMesh(is, flatBufferIds[counter]);
			mKeyBufferIds[i] = flatBufferIds[counter];
			counter++;
		}
		
		is.close();
		mIsLoaded = true;
	}
	
	private void drawMesh(
			int bufferId,
			int vertexCount,
			int positionHandle,
			int UVHandle,
			int normalHandle,
			int tangentHandle,
			int bitangentHandle) {
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId);
		
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glEnableVertexAttribArray(UVHandle);
		GLES20.glEnableVertexAttribArray(normalHandle);
		GLES20.glEnableVertexAttribArray(tangentHandle);
		GLES20.glEnableVertexAttribArray(bitangentHandle);
		
		GLES20.glVertexAttribPointer(positionHandle, mPositionSize, GLES20.GL_FLOAT, false, mStrideBytes, mPositionOffset);
		GLES20.glVertexAttribPointer(UVHandle, mUVSize, GLES20.GL_FLOAT, false, mStrideBytes, mUVOffset);
		GLES20.glVertexAttribPointer(normalHandle, mNormalSize, GLES20.GL_FLOAT, false, mStrideBytes, mNormalOffset);
		GLES20.glVertexAttribPointer(tangentHandle, mTangentSize, GLES20.GL_FLOAT, false, mStrideBytes, mTangentOffset);
		GLES20.glVertexAttribPointer(bitangentHandle, mBitangentSize, GLES20.GL_FLOAT, false, mStrideBytes, mBitangentOffset);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		
		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(UVHandle);
		GLES20.glDisableVertexAttribArray(normalHandle);
		GLES20.glDisableVertexAttribArray(tangentHandle);
		GLES20.glDisableVertexAttribArray(bitangentHandle);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	public void drawCircleSector(
			int circleIndex,
			int sectorIndex,
			int positionHandle,
			int UVHandle,
			int normalHandle,
			int tangentHandle,
			int bitangentHandle) {
		
		drawMesh(mCircleBufferIds[circleIndex][sectorIndex], mCircleVertexCounts[circleIndex][sectorIndex],
				positionHandle, UVHandle, normalHandle, tangentHandle, bitangentHandle);
	}
	
	public void drawKeySector(
			int sectorIndex,
			int positionHandle,
			int UVHandle,
			int normalHandle,
			int tangentHandle,
			int bitangentHandle) {
		
		drawMesh(mKeyBufferIds[sectorIndex], mKeyVertexCounts[sectorIndex],
				positionHandle, UVHandle, normalHandle, tangentHandle, bitangentHandle);
	}
	
	public void releaseBuffers() {
		if (mIsLoaded) {
			int[] flatBufferIds = new int[mCircleCount * mSectorCount + mKeySectorCount];
			int counter = 0;
			
			for (int i = 0; i < mCircleCount; i++)
				for (int j = 0; j < mSectorCount; j++) {
					flatBufferIds[counter] = mCircleBufferIds[i][j];
					counter++;
				}
			
			for (int i = 0; i < mKeySectorCount; i++) {
				flatBufferIds[counter] = mKeyBufferIds[i];
				counter++;
			}
			
			GLES20.glDeleteBuffers(mCircleCount * mSectorCount + mKeySectorCount, flatBufferIds, 0);
		}
	}
}
