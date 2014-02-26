package grv.dvt.cilocks_gles20;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class CircleLockRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = CircleLockRenderer.class.getSimpleName();

	private MeshContainer mMeshContainer;
	private TextureContainer mTextureContainer;
	private ShaderContainer mShaderContainer;

	private CircleLockLock mCircleLock;

	private int mLightHandle;
	private int mMVPMatrixHandle;
	private int mMVMatrixHandle;
	private int mColorMapHandle;
	private int mNormalMapHandle;

	private int mPositionHandle;
	private int mUVHandle;
	private int mNormalHandle;
	private int mTangentHandle;
	private int mBitangentHandle;
	
	/**
	 * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	 * of being located at the center of the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];
	
	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] mViewMatrix = new float[16];
	
	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] mProjectionMatrix = new float[16];
	
	/** Allocate storage for modelview matrix. Will be passed into the shader program. */
	private float[] mMVMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	
	/** Light position in world coordinates. */
	private float[] mLightPosition = new float[] { 0.0f, 0.0f, 0.15f, 1.0f };
	
	/** Light position in view coordinates. */
	private float[] mVLightPosition = new float[4];
	
	public CircleLockRenderer(Context context, CircleLockLock circleLock) {
		mMeshContainer = new MeshContainer(context);
		mTextureContainer = new TextureContainer(context);
		mShaderContainer = new ShaderContainer(context);
		
		mCircleLock = circleLock;
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		for (int i = 0; i < mCircleLock.getCircleCount(); i++)
			for (int j = 0; j < mCircleLock.getCircle(i).getSectorCount(); j++) {
				Matrix.setIdentityM(mModelMatrix, 0);
				
				// This multiplies the view matrix by the model matrix, and stores the result in the MV matrix.
				Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
				
				// This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix.
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
				
				GLES20.glUniform3fv(mLightHandle, 1, mVLightPosition, 0);
				GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
				GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
				
				CircleLockSector sector = mCircleLock.getCircle(i).getSector(j);
				mTextureContainer.bindTextures(sector.getColorIndex(), sector.getSymbolIndex());
				GLES20.glUniform1i(mColorMapHandle, 0);
				GLES20.glUniform1i(mNormalMapHandle, 1);
				
				mMeshContainer.drawMesh(i, j, mPositionHandle, mUVHandle, mNormalHandle, mTangentHandle, mBitangentHandle);
			}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);
		
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float)width / height;
		final float backRatio = (float)height / width;

		final float left = Math.min(-ratio, -1.0f);
		final float right = Math.max(ratio, 1.0f);
		
		final float bottom = Math.min(-backRatio, -1.0f);
		final float top = Math.max(backRatio, 1.0f);
		
		final float near = 4.0f;
		final float far = 6.0f;
		
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Initialize view matrix
		Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		// Set light position
		Matrix.multiplyMV(mVLightPosition, 0, mViewMatrix, 0, mLightPosition, 0);
		
		// Load data and store handles
		try {
			mMeshContainer.loadMeshes();
			mTextureContainer.loadTextures();
			mShaderContainer.loadShaders();
			System.gc();

			mLightHandle = mShaderContainer.getLightHandle();
			mMVPMatrixHandle = mShaderContainer.getMVPMatrixHandle();
			mMVMatrixHandle = mShaderContainer.getMVMatrixHandle();
			mColorMapHandle = mShaderContainer.getColorMapHandle();
			mNormalMapHandle = mShaderContainer.getNormalMapHandle();

			mPositionHandle = mShaderContainer.getPositionHandle();
			mUVHandle = mShaderContainer.getUVHandle();
			mNormalHandle = mShaderContainer.getNormalHandle();
			mTangentHandle = mShaderContainer.getTangentHandle();
			mBitangentHandle = mShaderContainer.getBitangentHandle();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			throw new RuntimeException("Error loading resources.");
		}
	}

}
