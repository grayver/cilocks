package grv.dvt.cilocks_gles20;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;

public class MeshContainer {
	// Circle count in mesh geometry
	private int mCircleCount;
	
	// Sector count in mesh geometry
	private int mSectorCount;
	
	
	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;
	
	/** Offset of the position data. */
	private final int mPositionOffset = 0;
	
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	
	/** Offset of the texture mapping data. */
	private final int mUVOffset = mPositionOffset + mPositionDataSize;
	
	/** Size of the texture mapping data in elements. */
	private final int mUVDataSize = 2;
	
	/** Offset of the normal data. */
	private final int mNormalOffset = mUVOffset + mUVDataSize;
	
	/** Size of the normal data in elements. */
	private final int mNormalDataSize = 3;
	
	/** Offset of the tangent data. */
	private final int mTangentOffset = mNormalOffset + mNormalDataSize;
	
	/** Size of the tangent data in elements. */
	private final int mTangentDataSize = 3;
	
	/** Offset of the bitangent data.  */
	private final int mBitangentOffset = mTangentOffset + mTangentDataSize;
	
	/** Size of the bitangent data in elements */
	private final int mBitangentDataSize = 3;
	
	/** How many elements per vertex. */
	private final int mElementsPerVertex = mBitangentOffset + mBitangentDataSize;
	
	/** How many bytes per vertex. */
	private final int mStrideBytes = mElementsPerVertex * mBytesPerFloat;
	
	
	// Activity context
	private Context mContext;
	
	// GLES buffer identifiers
	private int[][] mBufferIds;
	
	// Vertex counts
	private int[][] mVertexCounts;
	
	private boolean mIsLoaded;
	
	public MeshContainer(Context context) {
		mContext = context;
		mIsLoaded = false;
	}
	
	public void loadMeshes() throws IOException {
		int resId = mContext.getResources().getIdentifier("mesh_3x8", "raw", mContext.getPackageName());
		DataInputStream is = new DataInputStream(mContext.getResources().openRawResource(resId));
		
		mCircleCount = is.readInt();
		mSectorCount = is.readInt();
		mBufferIds = new int[mCircleCount][mSectorCount];
		mVertexCounts = new int[mCircleCount][mSectorCount];
		
		int[] flatBufferIds = new int[mCircleCount * mSectorCount];
		GLES20.glGenBuffers(mCircleCount * mSectorCount, flatBufferIds, 0);
		int counter = 0;
		
		for (int i = 0; i < mCircleCount; i++)
			for (int j = 0; j < mSectorCount; j++) {
				
				int faceCount = is.readInt();
				int vertexCount = faceCount * 3;
				int bufferSize = vertexCount * mStrideBytes;
				byte[] buffer = new byte[bufferSize];
				is.read(buffer, 0, bufferSize);

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
				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, flatBufferIds[counter]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, flBuffer.capacity() * mBytesPerFloat, flBuffer, GLES20.GL_STATIC_DRAW);
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

				mBufferIds[i][j] = flatBufferIds[counter];
				mVertexCounts[i][j] = vertexCount;
				counter++;
			}
		
		is.close();
		mIsLoaded = true;
	}
	
	public void drawMesh(
			int circleIndex,
			int sectorIndex,
			int positionHandle,
			int UVHandle,
			int normalHandle,
			int tangentHandle,
			int bitangentHandle) {
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIds[circleIndex][sectorIndex]);
		
		GLES20.glVertexAttribPointer(positionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mPositionOffset);
		GLES20.glEnableVertexAttribArray(positionHandle);
		
		GLES20.glVertexAttribPointer(UVHandle, mUVDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mUVOffset);
		GLES20.glEnableVertexAttribArray(UVHandle);
		
		GLES20.glVertexAttribPointer(normalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mNormalOffset);
		GLES20.glEnableVertexAttribArray(normalHandle);
		
		GLES20.glVertexAttribPointer(tangentHandle, mTangentDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mTangentOffset);
		GLES20.glEnableVertexAttribArray(tangentHandle);

		GLES20.glVertexAttribPointer(bitangentHandle, mBitangentDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mBitangentOffset);
		GLES20.glEnableVertexAttribArray(bitangentHandle);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCounts[circleIndex][sectorIndex]);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	public void releaseBuffers() {
		if (mIsLoaded) {
			int[] flatBufferIds = new int[mCircleCount * mSectorCount];
			int counter = 0;
			
			for (int i = 0; i < mCircleCount; i++)
				for (int j = 0; j < mSectorCount; j++) {
					flatBufferIds[counter] = mBufferIds[i][j];
					counter++;
				}
			
			GLES20.glDeleteBuffers(mCircleCount * mSectorCount, flatBufferIds, 0);
		}
	}
}
