import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;


public class Compressor {

	public static void main(String[] args) {
		try {
			FileInputStream is = new FileInputStream("out/mesh_3x8x4.bin");
			GZIPOutputStream zos = new GZIPOutputStream(new FileOutputStream("out/mesh_3x8x4.gzbin"));
			
			byte[] buffer = new byte[is.available()];
			is.read(buffer, 0, buffer.length);
			zos.write(buffer);
			
			zos.close();
			is.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
