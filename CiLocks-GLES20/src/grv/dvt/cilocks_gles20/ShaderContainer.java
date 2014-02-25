package grv.dvt.cilocks_gles20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES20;

public class ShaderContainer {
	
	// Activity context
	private Context mContext;
	
	private int mVertexShaderHandle;
	
	private int mFragmentShaderHandle;
	
	private int mProgramHandle;
	
	public ShaderContainer(Context context) {
		mContext = context;
	}
	
	private String loadShaderSource(String resName) throws IOException {
		int resId = mContext.getResources().getIdentifier(resName, "raw/shaders", mContext.getPackageName());
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(resId)));
		String line;
		StringBuilder builder = new StringBuilder();
		
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append('\n');
		}
		
		reader.close();
		return builder.toString();
	}
	
	public void loadShaders() throws IOException {
		// Load in the vertex shader.
		mVertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		if (mVertexShaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(mVertexShaderHandle, loadShaderSource("cl_vertex.shr"));

			// Compile the shader.
			GLES20.glCompileShader(mVertexShaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(mVertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {				
				GLES20.glDeleteShader(mVertexShaderHandle);
				mVertexShaderHandle = 0;
			}
		}
		
		if (mVertexShaderHandle == 0) {
			throw new RuntimeException("Error creating vertex shader.");
		}
		
		
		// Load in the fragment shader shader.
		mFragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		
		if (mFragmentShaderHandle != 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(mFragmentShaderHandle, loadShaderSource("cl_fragment.shr"));

			// Compile the shader.
			GLES20.glCompileShader(mFragmentShaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(mFragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {				
				GLES20.glDeleteShader(mFragmentShaderHandle);
				mFragmentShaderHandle = 0;
			}
		}
		
		if (mFragmentShaderHandle == 0) {
			throw new RuntimeException("Error creating fragment shader.");
		}
		
		// Create a program object and store the handle to it.
		int mProgramHandle = GLES20.glCreateProgram();
		
		if (mProgramHandle != 0) {
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(mProgramHandle, mVertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(mProgramHandle, mFragmentShaderHandle);
			
			// Bind attributes
			GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
			GLES20.glBindAttribLocation(mProgramHandle, 1, "a_UV");
			GLES20.glBindAttribLocation(mProgramHandle, 2, "a_Normal");
			GLES20.glBindAttribLocation(mProgramHandle, 3, "a_Tangent");
			GLES20.glBindAttribLocation(mProgramHandle, 4, "a_Bitangent");
			
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(mProgramHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(mProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {				
				GLES20.glDeleteProgram(mProgramHandle);
				mProgramHandle = 0;
			}
		}
		
		if (mProgramHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}
		
        // Release shader compiler
        GLES20.glReleaseShaderCompiler();
		
        // Set program handles. These will later be used to pass in values to the program.
        //mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");        
        //mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        //mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");        
        
        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(mProgramHandle);
	}
	
	public void releaseShaders() {
		GLES20.glDeleteProgram(mProgramHandle);
		GLES20.glDeleteShader(mFragmentShaderHandle);
		GLES20.glDeleteShader(mVertexShaderHandle);
	}
}
