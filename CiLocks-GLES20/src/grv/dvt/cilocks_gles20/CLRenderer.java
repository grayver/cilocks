package grv.dvt.cilocks_gles20;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class CLRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = CLRenderer.class.getSimpleName();

	private float mScreenWidth;
	private float mScreenHeight;
	
	private CLMeshContainer mMeshContainer;
	private CLTextureContainer mTextureContainer;
	private CLShaderContainer mShaderContainer;

	private CLGame mGame;

	/** Matrix stack utility */
	private MatrixStack mMatrixStack;
	
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
	
	/** Inverse view matrix for pointer coordinates detection. */
	private float[] mViewInvMatrix = new float[16];
	
	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] mProjectionMatrix = new float[16];
	
	/** Inverse projection matrix for pointer coordinates detection. */
	private float[] mProjectionInvMatrix = new float[16];
	
	/** Allocate storage for modelview matrix. Will be passed into the shader program. */
	private float[] mMVMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	
	/** Combined projection and view inverted matrix. */
	private float[] mVPInvMatrix = new float[16];
	
	
	/** Matrix describes lightning parameters. In our model lightning consists of central and ring components.
	 *  This matrix stores lightning position and orientation in world coordinates.
	 *    [0] - central lightning position
	 *    [1] - ring lightning center position
	 *    [2] - ring lightning normal direction
	 *    [3] - ring lightning radius vector (actually, only length of this vector needed */
	private float[] mLightMatrix = new float[] {
			0.0f, 0.0f, 0.3f, 1.0f,
			0.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			1.3f, 0.0f, 0.0f, 0.0f
	};
	
	/** Lightning parameters in view coordinates. */
	private float[] mVLightMatrix = new float[16];
	
	
	/** Camera position in world coordinates. */
	private float[] mCameraPosition = new float[] { 0.0f, 0.0f, 9.0f, 1.0f };
	
	
	/**
	 * Infrastructure for touch point projection.
	 */
	private float[] mTouchPlaneNormal = new float[] { 0.0f, 0.0f, 1.0f };
	private float[] mTouchPlanePoint = new float[] { 0.0f, 0.0f, 0.05f };
	
	/**
	 * Infrastructure for circle collision detection
	 */
	private float[] mCircleBorders = new float[] { 0.32f, 0.545f, 0.775f, 1.00f };
	
	
	public CLRenderer(Context context, CLGame game) {
		mMeshContainer = new CLMeshContainer(context);
		mTextureContainer = new CLTextureContainer(context);
		mShaderContainer = new CLShaderContainer(context);
		
		mGame = game;
		
		mMatrixStack = new MatrixStack(3);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		// Specify common uniform
		GLES20.glUniformMatrix4fv(mShaderContainer.mLightMatrixHandle, 1, false, mVLightMatrix, 0);
		GLES20.glUniform1i(mShaderContainer.mColorMapHandle, 0);
		GLES20.glUniform1i(mShaderContainer.mNormalMapHandle, 1);
		
		// Init model matrix
		Matrix.setIdentityM(mModelMatrix, 0);
		
		CLLock circleLock = mGame.getCircleLock();
		synchronized (circleLock) {
			for (int i = 0; i < circleLock.getCircleCount(); i++) {
				CLCircle circle = circleLock.getCircle(i);
				
				mMatrixStack.push(mModelMatrix, 0);
				Matrix.rotateM(mModelMatrix, 0, circle.getAngleDeg(), 0.0f, 0.0f, 1.0f);
				
				for (int j = 0; j < circleLock.getCircle(i).getSectorCount(); j++) {
					CLSector sector = circle.getSector(j);
					
					mMatrixStack.push(mModelMatrix, 0);
					float sectorAngleRad = 2f * (float)Math.PI * (j + 0.5f) / circle.getSectorCount();
					if (sector.getAngleDeg() > 1e-6f) // angle is always between 0 and 360 degrees
						Matrix.rotateM(mModelMatrix, 0, sector.getAngleDeg(),
								(float)Math.cos(sectorAngleRad), (float)Math.sin(sectorAngleRad), 0.0f);
					
					// This multiplies the view matrix by the model matrix, and stores the result in the MV matrix.
					Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
					
					// This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix.
					Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
					
					// Specify matrix uniforms
					GLES20.glUniformMatrix4fv(mShaderContainer.mMVMatrixHandle, 1, false, mMVMatrix, 0);
					GLES20.glUniformMatrix4fv(mShaderContainer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
					
					mTextureContainer.bindTextures(sector.getColorIndex(), sector.getSymbolIndex());
					
					mMeshContainer.drawCircleSector(i, j, mShaderContainer.mPositionHandle, mShaderContainer.mUVHandle,
							mShaderContainer.mNormalHandle, mShaderContainer.mTangentHandle, mShaderContainer.mBitangentHandle);
					
					// This may be useful to fix Adreno 200 texture bug
					//mTextureContainer.unbindTextures();
					
					mMatrixStack.pop(mModelMatrix, 0);
				}
				
				mMatrixStack.pop(mModelMatrix, 0);
			}
			
			CLKeyCircle keyCircle = circleLock.getKeyCircle();
			for (int i = 0; i < keyCircle.getSectorCount(); i++) {
				CLKeySector keySector = keyCircle.getSector(i);
				
				mMatrixStack.push(mModelMatrix, 0);
				float sectorAngleRad = 2f * (float)Math.PI * (i + 0.5f) / keyCircle.getSectorCount();
				float sectorDistance = 0.16f * keySector.getDistance();
				if (sectorDistance > 1e-6f)
					Matrix.translateM(mModelMatrix, 0, sectorDistance * (float)Math.cos(sectorAngleRad),
							sectorDistance * (float)Math.sin(sectorAngleRad), 0.0f);
				
				// This multiplies the view matrix by the model matrix, and stores the result in the MV matrix.
				Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
				
				// This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix.
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
				
				// Specify matrix uniforms
				GLES20.glUniformMatrix4fv(mShaderContainer.mMVMatrixHandle, 1, false, mMVMatrix, 0);
				GLES20.glUniformMatrix4fv(mShaderContainer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
				
				mTextureContainer.bindTextures(keySector.getColorIndex(), 0);
				
				mMeshContainer.drawKeySector(i, mShaderContainer.mPositionHandle, mShaderContainer.mUVHandle,
						mShaderContainer.mNormalHandle, mShaderContainer.mTangentHandle, mShaderContainer.mBitangentHandle);
				
				// This may be useful to fix Adreno 200 texture bug
				//mTextureContainer.unbindTextures();
				
				mMatrixStack.pop(mModelMatrix, 0);
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		Log.d(TAG, "Changing surface.. Width: " + width + " height: " + height);
		
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);
		
		// Save width and height for point projection
		mScreenWidth = width;
		mScreenHeight = height;
		
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float)width / height;
		final float backRatio = (float)height / width;

		final float left = Math.min(-ratio, -1.0f);
		final float right = Math.max(ratio, 1.0f);
		
		final float bottom = Math.min(-backRatio, -1.0f);
		final float top = Math.max(backRatio, 1.0f);
		
		float cameraDist = (float)Math.sqrt(mCameraPosition[0] * mCameraPosition[0] +
				mCameraPosition[1] * mCameraPosition[1] + mCameraPosition[2] * mCameraPosition[2]);
		final float near = cameraDist - 1.0f;
		final float far = cameraDist + 1.0f;
		
		Log.d(TAG, String.format("Projection frustum. Left: %.2f right: %.2f bottom: %.2f top: %.2f near: %.2f far: %.2f",
				left, right, bottom, top, near, far));
		//Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		Matrix.invertM(mProjectionInvMatrix, 0, mProjectionMatrix, 0);
		Matrix.multiplyMM(mVPInvMatrix, 0, mViewInvMatrix, 0, mProjectionInvMatrix, 0);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		Log.d(TAG, "Creating surface..");
		
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Enable depth buffer
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		
		// Initialize view matrix
		Matrix.setLookAtM(mViewMatrix, 0, mCameraPosition[0], mCameraPosition[1], mCameraPosition[2],
				0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		Matrix.invertM(mViewInvMatrix, 0, mViewMatrix, 0);
		Matrix.multiplyMM(mVPInvMatrix, 0, mViewInvMatrix, 0, mProjectionInvMatrix, 0);
		
		// Compute light matrix in view coordinates
		Matrix.multiplyMM(mVLightMatrix, 0, mViewMatrix, 0, mLightMatrix, 0);
		
		// Load data and store handles
		try {
			mMeshContainer.loadMeshes();
			mTextureContainer.loadTextures();
			mShaderContainer.loadShaders();
			System.gc();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			throw new RuntimeException("Error loading resources.");
		}
	}

	public void releaseResources() {
		mMeshContainer.releaseBuffers();
		mTextureContainer.releaseTextures();
		mShaderContainer.releaseShaders();
	}
	
	public PointF getPointProjection(PointF point) {
		float normalizedX = 2.0f * point.x / mScreenWidth - 1.0f;
		float normalizedY = 1.0f - 2.0f * point.y / mScreenHeight;
		
		// points on near and far planes in normalized device coordinates 
		float[] nearNdc = new float[] { normalizedX, normalizedY, -1.0f, 1.0f };
		float[] farNdc = new float[] { normalizedX, normalizedY, 1.0f, 1.0f };
		
		// convert points to world coordinates
		float[] nearWorld = new float[4];
		float[] farWorld = new float[4];
		Matrix.multiplyMV(nearWorld, 0, mVPInvMatrix, 0, nearNdc, 0);
		Matrix.multiplyMV(farWorld, 0, mVPInvMatrix, 0, farNdc, 0);
		
		// divide by W
		nearWorld[0] /= nearWorld[3];
		nearWorld[1] /= nearWorld[3];
		nearWorld[2] /= nearWorld[3];
		
		farWorld[0] /= farWorld[3];
		farWorld[1] /= farWorld[3];
		farWorld[2] /= farWorld[3];
		
		// determine ray direction
		float[] ray = new float[] { farWorld[0] - nearWorld[0], farWorld[1] - nearWorld[1], farWorld[2] - nearWorld[2] };
		
		// determine ray with plane intersection
		PointF result = new PointF();
		
		float rayNDotProduct = ray[0] * mTouchPlaneNormal[0] + ray[1] * mTouchPlaneNormal[1] + ray[2] * mTouchPlaneNormal[2];
		if (Math.abs(rayNDotProduct) > 1e-6) {
			float d = ((mTouchPlanePoint[0] - nearWorld[0]) * mTouchPlaneNormal[0]
							+ (mTouchPlanePoint[1] - nearWorld[1]) * mTouchPlaneNormal[1]
							+ (mTouchPlanePoint[2] - nearWorld[2]) * mTouchPlaneNormal[2]) / rayNDotProduct;
			if (d > 0.0f) {
				result.x = nearWorld[0] + ray[0] * d;
				result.y = nearWorld[1] + ray[1] * d;
			}
		} else {
			Log.e(TAG, "Touch projection ray intersection error");
		}
		
		return result;
	}

	public float[] getCircleBorders() {
		return mCircleBorders;
	}
}
