import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Converter {

	public static class VertCoord {
		public VertCoord(float px, float py, float pz) {
			x = px;
			y = py;
			z = pz;
		}
		
		public float x;
		public float y;
		public float z;
	}
	
	public static class TexCoord {
		public TexCoord(float ps, float pt) {
			s = ps;
			t = pt;
		}
		
		public float s;
		public float t;
	}
	
	public static class Face {
		public VertCoord coordA;
		public TexCoord texA;
		public VertCoord coordB;
		public TexCoord texB;
		public VertCoord coordC;
		public TexCoord texC;
	}
	
	protected static String objName = null;
	protected static ArrayList<VertCoord> vertCoords = new ArrayList<VertCoord>();
	protected static ArrayList<TexCoord> texCoords = new ArrayList<TexCoord>();
	protected static ArrayList<Face> faces = new ArrayList<Face>();
	
	protected static void reset() {
		faces.clear();
	}

	protected static void dump() {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("out/" + objName.substring(0, 2).toLowerCase() + ".bin"));
			
			dos.writeInt(faces.size());
			
			for (Face face : faces) {
				dos.writeFloat(face.coordA.x);
				dos.writeFloat(face.coordA.y);
				dos.writeFloat(face.coordA.z);
				dos.writeFloat(face.texA.s);
				dos.writeFloat(face.texA.t);
				
				dos.writeFloat(face.coordB.x);
				dos.writeFloat(face.coordB.y);
				dos.writeFloat(face.coordB.z);
				dos.writeFloat(face.texB.s);
				dos.writeFloat(face.texB.t);
				
				dos.writeFloat(face.coordC.x);
				dos.writeFloat(face.coordC.y);
				dos.writeFloat(face.coordC.z);
				dos.writeFloat(face.texC.s);
				dos.writeFloat(face.texC.t);
			}
			
			dos.flush();
			dos.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		//if (args.length == 0)
		//	return;
		
		String filename = "D:\\Development\\Android\\Model\\mycircles.obj"; //args[0];
		objName = null;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			
			while (line != null) {
				
				String[] lexems = line.split(" ");
				
				switch (lexems[0]) {
				case "o":
					if (objName != null) {
						dump();
						reset();
					}
					objName = lexems[1];
					break;
					
				case "v":
					vertCoords.add(new VertCoord(Float.parseFloat(lexems[1]), Float.parseFloat(lexems[2]), Float.parseFloat(lexems[3])));
					break;
					
				case "vt":
					texCoords.add(new TexCoord(Float.parseFloat(lexems[1]), Float.parseFloat(lexems[2])));
					break;
					
				case "f":
					Face face = new Face();
					
					String[] pairA = lexems[1].split("/");
					String[] pairB = lexems[2].split("/");
					String[] pairC = lexems[3].split("/");
					
					face.coordA = vertCoords.get(Integer.parseInt(pairA[0]) - 1);
					face.texA = texCoords.get(Integer.parseInt(pairA[1]) - 1);
					
					face.coordB = vertCoords.get(Integer.parseInt(pairB[0]) - 1);
					face.texB = texCoords.get(Integer.parseInt(pairB[1]) - 1);
					
					face.coordC = vertCoords.get(Integer.parseInt(pairC[0]) - 1);
					face.texC = texCoords.get(Integer.parseInt(pairC[1]) - 1);
					
					faces.add(face);
					break;
				}
				
				line = reader.readLine();
			}
			
			dump();
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
