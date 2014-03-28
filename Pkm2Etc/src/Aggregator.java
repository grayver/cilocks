import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class Aggregator {
	private static final int mTextureCount = 4;

	public static void main(String[] args) {
		try {
			String inputFilenameMask = "in/texture_metal%d_mip_%d.pkm";
			String outputFilenameMask = "out/texture_metal%d.etc1";
			
			for (int i = 0; i < mTextureCount; i++) {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(String.format(outputFilenameMask, i + 1)));
				
				int maxLevel = 0;
				while (new File(String.format(inputFilenameMask, i + 1, maxLevel)).exists())
					maxLevel++;
				maxLevel--;
				
				dos.writeInt(maxLevel); // max levels
				
				for (int j = 0; j <= maxLevel; j++) {
					FileInputStream is = new FileInputStream(String.format(inputFilenameMask, i + 1, j));
					byte[] buffer = new byte[is.available()];
					is.read(buffer, 0, buffer.length);
					dos.write(buffer); // data
					is.close();
				}
				
				dos.flush();
				dos.close();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
