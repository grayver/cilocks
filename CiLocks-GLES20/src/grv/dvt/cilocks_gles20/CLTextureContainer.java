package grv.dvt.cilocks_gles20;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class CLTextureContainer {
	// Color texture count
	private final int mColorCount = 4;
	
	// Symbol (normal) texture count
	private final int mSymbolCount = 4;
	
	
	// Activity context
	private Context mContext;
	
	// GLES color texture identifiers
	private int[] mColorTextureIds;
	
	// GLES normal texture identifiers
	private int[] mSymbolTextureIds;
	
	private boolean mIsLoaded;
	
	public CLTextureContainer(Context context) {
		mContext = context;
		mColorTextureIds = new int[mColorCount];
		mSymbolTextureIds = new int[mSymbolCount];
		mIsLoaded = false;
	}
	
	private void loadTexture(int texId, String resName, boolean isCompressed) throws IOException {
		int resId = mContext.getResources().getIdentifier(resName, "raw", mContext.getPackageName());
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
		
		InputStream is = mContext.getResources().openRawResource(resId);
		
		if (isCompressed) {
			DataInputStream dis = new DataInputStream(is);
			
			int maxLevel = dis.readInt();
			for (int level = 0; level <= maxLevel; level++)
				ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, level, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, dis);
		} else {
			Bitmap bm = BitmapFactory.decodeStream(is);
			
			int format = GLUtils.getInternalFormat(bm);
			int type = GLUtils.getType(bm);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, format, bm, type, 0);
			
			bm.recycle();
			bm = null;
			
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		}
		
		is.close();
		
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	public void loadTextures() throws IOException {
		int[] flatTexIds = new int[mColorCount + mSymbolCount];
		GLES20.glGenTextures(mColorCount + mSymbolCount, flatTexIds, 0);
		int counter = 0;
		
		for (int i = 0; i < mColorCount; i++) {
			String resName = String.format(Locale.ENGLISH, "texture_metal%d", i + 1);
			loadTexture(flatTexIds[counter], resName, true);
			
			mColorTextureIds[i] = flatTexIds[counter];
			counter++;
		}
		
		for (int i = 0; i < mSymbolCount; i++) {
			String resName = String.format(Locale.ENGLISH, "texture_symbol%d", i + 1);
			loadTexture(flatTexIds[counter], resName, false);
			
			mSymbolTextureIds[i] = flatTexIds[counter];
			counter++;
		}
		
		mIsLoaded = true;
	}
	
	public void bindTextures(int colorIndex, int symbolIndex) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mColorTextureIds[colorIndex]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mSymbolTextureIds[symbolIndex]);
	}
	
	public void unbindTextures() {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	public void releaseTextures() {
		if (mIsLoaded) {
			int[] flatTexIds = new int[mColorCount + mSymbolCount];
			int counter = 0;
			
			for (int i = 0; i < mColorCount; i++) {
				flatTexIds[counter] = mColorTextureIds[i];
				counter++;
			}
			
			for (int i = 0; i < mSymbolCount; i++) {
				flatTexIds[counter] = mSymbolTextureIds[i];
				counter++;
			}
			
			GLES20.glDeleteTextures(mColorCount + mSymbolCount, flatTexIds, 0);
		}
	}
}
