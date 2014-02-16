package grv.dvt.cilocks_gles20;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import android.content.Context;
import android.opengl.GLES20;

public class MeshContainer {
	// Circle count in mesh geometry
	private final int mCircleCount = 3;
	
	// Sector count int mesh geometry
	private final int mSectorCount = 8;
	
	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;
	
	/** How many elements per vertex. */
	private final int mStrideBytes = 7 * mBytesPerFloat;
	
	/** Offset of the position data. */
	private final int mPositionOffset = 0;
	
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	
	/** Offset of the texture data. */
	private final int mTextureOffset = 3;
	
	/** Size of the color data in elements. */
	private final int mTextureDataSize = 2;
	
	
	// Activity context
	private Context mContext;
	
	// GLES buffer identifiers
	private int[][] mBufferIds;
	
	// Vertex counts
	private int[][] mVertexCounts;
	
	// Resource name prefixes
	private final String[] mCirclePrefixes = new String[] { "i", "m", "o" };
	
	public MeshContainer(Context context) {
		mContext = context;
		mBufferIds = new int[mCircleCount][mSectorCount];
		mVertexCounts = new int[mCircleCount][mSectorCount];
	}
	
	protected int[][] resolveResources() {
		int[][] resIds = new int[mCircleCount][mSectorCount];
		for (int i = 0; i < mCircleCount; i++)
			for (int j = 0; j < mSectorCount; j++) {
				String resName = String.format(Locale.ENGLISH, "%s%d.bin", mCirclePrefixes[i], j + 1);
				resIds[i][j] = mContext.getResources().getIdentifier(resName, "raw/meshes", mContext.getPackageName());
			}
		
		return resIds;
	}
	
	public void loadMeshes() throws IOException {
		int[][] resIds = resolveResources();
		
		int[] flatBufferIds = new int[mCircleCount * mSectorCount];
		GLES20.glGenBuffers(mCircleCount * mSectorCount, flatBufferIds, 0);
		int counter = 0;
		
		for (int i = 0; i < mCircleCount; i++)
			for (int j = 0; j < mSectorCount; j++) {
				DataInputStream is = new DataInputStream(mContext.getResources().openRawResource(resIds[i][j]));
				
				int vertexCount = is.readInt();
				int bufferSize = vertexCount * mStrideBytes;
				byte[] buffer = new byte[bufferSize];
				is.read(buffer, 0, bufferSize);
				is.close();
				
				ByteBuffer glBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.BIG_ENDIAN);
				glBuffer.put(buffer, 0, bufferSize);
				glBuffer.position(0);
				
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, flatBufferIds[counter]);
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferSize, glBuffer, GLES20.GL_STATIC_DRAW);
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
				
				mBufferIds[i][j] = flatBufferIds[counter];
				mVertexCounts[i][j] = vertexCount;
				counter++;
			}
	}
	
	public void drawMesh(int circleIndex, int sectorIndex, int positionHandle, int textureHandle) {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferIds[circleIndex][sectorIndex]);
		
		GLES20.glVertexAttribPointer(positionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mPositionOffset);
		GLES20.glEnableVertexAttribArray(positionHandle);
		
		GLES20.glVertexAttribPointer(textureHandle, mTextureDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mTextureOffset);
		GLES20.glEnableVertexAttribArray(textureHandle);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCounts[circleIndex][sectorIndex]);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	public void releaseBuffers() {
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
