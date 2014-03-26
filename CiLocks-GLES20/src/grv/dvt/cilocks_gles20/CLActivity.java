package grv.dvt.cilocks_gles20;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CLActivity extends Activity {

	private static final String TAG = CLActivity.class.getSimpleName();

	private CLView mView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Creating..");
		super.onCreate(savedInstanceState);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		
		if (supportsEs2) {
			mView = new CLView(this);
		} else {
			// This is where you could create an OpenGL ES 1.x compatible
			// renderer if you wanted to support both ES 1 and ES 2.
			Log.e(TAG, "OpenGL ES 2.0 is not supported");
			this.finish();
			return;
		}

		// Make application full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// Assign view
		setContentView(mView);
		Log.d(TAG, "View added");
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying..");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping..");
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG, "Resuming..");
		super.onResume();
		mView.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "Pausing..");
		super.onPause();
		mView.onPause();
	}
}
