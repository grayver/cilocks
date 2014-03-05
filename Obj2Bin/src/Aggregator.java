import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class Aggregator {

	private static final int mCircleCount = 3;
	private static final int mSectorCount = 8;
	private static final String[] mCirclePrefixes = new String[] { "i", "m", "o" };
	
	private static final int mKeyCount = 4;
	private static final String mKeyPrefix = "h";
	
	public static void main(String[] args) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(String.format("out/mesh_%dx%d.bin", mCircleCount, mSectorCount)));
			dos.writeInt(mCircleCount);
			dos.writeInt(mSectorCount);
			
			for (int i = 0; i < mCircleCount; i++) {
				
				for (int j = 0; j < mSectorCount; j++) {
					FileInputStream is = new FileInputStream(String.format("out/mesh_%s%d.bin", mCirclePrefixes[i], j + 1));
					byte[] buffer = new byte[is.available()];
					is.read(buffer, 0, buffer.length);
					dos.write(buffer);
					is.close();
				}
			}
			
			dos.flush();
			dos.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(String.format("out/mesh_key.bin", mKeyCount)));
			dos.writeInt(mKeyCount);
			
			for (int i = 0; i < mKeyCount; i++) {
				FileInputStream is = new FileInputStream(String.format("out/mesh_%s%d.bin", mKeyPrefix, i + 1));
				byte[] buffer = new byte[is.available()];
				is.read(buffer, 0, buffer.length);
				dos.write(buffer);
				is.close();
			}
			
			dos.flush();
			dos.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
