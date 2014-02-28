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

public class CircleLockRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = CircleLockRenderer.class.getSimpleName();

	private float mScreenWidth;
	private float mScreenHeight;
	
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
	
	/** Light position in world coordinates. */
	private float[] mLightPosition = new float[] { 0.0f, 0.0f, 0.3f, 1.0f };
	
	/** Light position in view coordinates. */
	private float[] mVLightPosition = new float[4];
	
	/** Camera position in world coordinates. */
	private float[] mCameraPosition = new float[] { 0.0f, 0.0f, 5.0f, 1.0f };
	
	
	/**
	 * Infrastructure for touch point projection.
	 */
	private float[] mTouchPlaneNormal = new float[] { 0.0f, 0.0f, 1.0f };
	private float[] mTouchPlanePoint = new float[] { 0.0f, 0.0f, 0.0485f };
	private float mTouchNCDotProduct =
			mTouchPlaneNormal[0] * mCameraPosition[0] +
			mTouchPlaneNormal[1] * mCameraPosition[1] +
			mTouchPlaneNormal[2] * mCameraPosition[2];
	private float mTouchNPDotProduct =
			mTouchPlaneNormal[0] * mTouchPlanePoint[0] +
			mTouchPlaneNormal[1] * mTouchPlanePoint[1] +
			mTouchPlaneNormal[2] * mTouchPlanePoint[2];
	
	/**
	 * Infrastructure for circle collision detection
	 */
	private float[] mCircleBorders = new float[] { 0.338f, 0.558f, 0.777f, 0.996f };
	
	
	public CircleLockRenderer(Context context, CircleLockLock circleLock) {
		mMeshContainer = new MeshContainer(context);
		mTextureContainer = new TextureContainer(context);
		mShaderContainer = new ShaderContainer(context);
		
		mCircleLock = circleLock;
		
		mMatrixStack = new MatrixStack(3);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		// Init model matrix
		Matrix.setIdentityM(mModelMatrix, 0);
		
		synchronized (mCircleLock) {
			for (int i = 0; i < mCircleLock.getCircleCount(); i++) {
				CircleLockCircle circle = mCircleLock.getCircle(i);
	
				mMatrixStack.push(mModelMatrix, 0);
				Matrix.rotateM(mModelMatrix, 0, circle.getAngleDeg(), 0.0f, 0.0f, 1.0f);
				
				for (int j = 0; j < mCircleLock.getCircle(i).getSectorCount(); j++) {
					CircleLockSector sector = circle.getSector(j);
					
					mMatrixStack.push(mModelMatrix, 0);
					float sectorAngleRad = (float)Math.PI * (1.0f - (j + 0.5f) * 2f / circle.getSectorCount());
					if (sector.getAngleDeg() > 1e-6f) // angle is always between 0 and 360 degrees
						Matrix.rotateM(mModelMatrix, 0, sector.getAngleDeg(),
								(float)Math.cos(sectorAngleRad), (float)Math.sin(sectorAngleRad), 0.0f);
					
					// This multiplies the view matrix by the model matrix, and stores the result in the MV matrix.
					Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
					
					// This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix.
					Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
					
					GLES20.glUniform3fv(mLightHandle, 1, mVLightPosition, 0);
					GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
					GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
					
					mTextureContainer.bindTextures(sector.getColorIndex(), sector.getSymbolIndex());
					GLES20.glUniform1i(mColorMapHandle, 0);
					GLES20.glUniform1i(mNormalMapHandle, 1);
					
					mMeshContainer.drawMesh(i, j, mPositionHandle, mUVHandle, mNormalHandle, mTangentHandle, mBitangentHandle);
					
					mMatrixStack.pop(mModelMatrix, 0);
				}
				
				mMatrixStack.pop(mModelMatrix, 0);
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
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
		
		final float near = 4.0f;
		final float far = 6.0f;
		
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		Matrix.invertM(mProjectionInvMatrix, 0, mProjectionMatrix, 0);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
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

	public void releaseResources() {
		mMeshContainer.releaseBuffers();
		mTextureContainer.releaseTextures();
		mShaderContainer.releaseShaders();
	}
	
	public PointF getPointProjection(PointF point) {
		float[] rayProj = new float[] { 2.0f * point.x / mScreenWidth - 1.0f, 1.0f - 2.0f * point.y / mScreenHeight, -1.0f, 1.0f };
		
		float[] rayView = new float[4];
		Matrix.multiplyMV(rayView, 0, mProjectionInvMatrix, 0, rayProj, 0);
		rayView[2] = -1.0f;
		rayView[3] = 0.0f;
		
		float[] ray = new float[4];
		Matrix.multiplyMV(ray, 0, mViewInvMatrix, 0, rayView, 0);
		
		PointF result = new PointF();
		result.x = ray[0];
		result.y = ray[1];
		return result; // temp stub
		/*
		float dist = Math.abs(mTouchNCDotProduct - mTouchNPDotProduct);
		float denom = ray[0] * mTouchPlaneNormal[0] + ray[1] * mTouchPlaneNormal[1] + ray[2] * mTouchPlaneNormal[2];

		if (Math.abs(denom) > 1e-6) {
			float t = -(mTouchNCDotProduct + dist) / denom;
			if (t > 0.0f) {
				result.x = mCameraPosition[0] + ray[0] * t;
				result.y = mCameraPosition[1] + ray[1] * t;
			}
		}
		
		return result;
		*/
	}

	public float[] getCircleBorders() {
		return mCircleBorders;
	}
}
