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
		
		public void normalize() {
			double r = Math.sqrt(x *x + y * y + z * z);
			x = (float)(x / r);
			y = (float)(y / r);
			z = (float)(z / r);
		}
		
		public VertCoord diff(VertCoord arg) {
			return new VertCoord(x - arg.x, y - arg.y, z - arg.z);
		}
		
		public VertCoord mul(float arg) {
			return new VertCoord(x * arg, y * arg, z * arg);
		}
		
		public VertCoord div(float arg) {
			return new VertCoord(x / arg, y / arg, z / arg);
		}
		
		public float dot(VertCoord arg) {
			return x * arg.x + y * arg.y + z * arg.z;
		}
		
		public VertCoord cross(VertCoord arg) {
			return new VertCoord(y * arg.z - z * arg.y, z * arg.x - x * arg.z, x * arg.y - y * arg.x);
		}
		
		public void writeToStream(DataOutputStream dos) throws IOException {
			dos.writeFloat(x);
			dos.writeFloat(y);
			dos.writeFloat(z);
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
		
		public TexCoord diff(TexCoord arg) {
			return new TexCoord(s - arg.s, t - arg.t);
		}
		
		public void writeToStream(DataOutputStream dos) throws IOException {
			dos.writeFloat(s);
			dos.writeFloat(t);
		}
		
		public float s;
		public float t;
	}

	
	public static class Face {
		public VertCoord coordA;
		public TexCoord texA;
		public VertCoord normA;
		
		public VertCoord coordB;
		public TexCoord texB;
		public VertCoord normB;
		
		public VertCoord coordC;
		public TexCoord texC;
		public VertCoord normC;
	}
	
	
	protected static String objName = null;
	protected static ArrayList<VertCoord> vertCoords = new ArrayList<VertCoord>();
	protected static ArrayList<TexCoord> texCoords = new ArrayList<TexCoord>();
	protected static ArrayList<VertCoord> normCoords = new ArrayList<VertCoord>();
	protected static ArrayList<Face> faces = new ArrayList<Face>();
	
	protected static void reset() {
		faces.clear();
	}

	protected static void dump() {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("out/mesh_" + objName.substring(0, 2).toLowerCase() + ".bin"));
			
			dos.writeInt(faces.size());
			
			for (Face face : faces) {
				VertCoord deltaPos1 = face.coordB.diff(face.coordA);
				VertCoord deltaPos2 = face.coordC.diff(face.coordA);
				TexCoord deltaUV1 = face.texB.diff(face.texA);
				TexCoord deltaUV2 = face.texC.diff(face.texA);
				float r = deltaUV1.s * deltaUV2.t - deltaUV1.t * deltaUV2.s;
				VertCoord tangent = (deltaPos1.mul(deltaUV2.t).diff(deltaPos2.mul(deltaUV1.t))).div(r);
				VertCoord bitangent = (deltaPos2.mul(deltaUV1.s).diff(deltaPos1.mul(deltaUV2.s))).div(r);
				bitangent.normalize();
				
				VertCoord tangentA = tangent.diff(face.normA.mul(face.normA.dot(tangent)));
				tangentA.normalize();
				if (face.normA.cross(tangentA).dot(bitangent) < 0.0f)
					tangentA = tangentA.mul(-1.0f);

				VertCoord tangentB = tangent.diff(face.normB.mul(face.normB.dot(tangent)));
				tangentB.normalize();
				if (face.normB.cross(tangentB).dot(bitangent) < 0.0f)
					tangentB = tangentB.mul(-1.0f);
				
				VertCoord tangentC = tangent.diff(face.normC.mul(face.normC.dot(tangent)));
				tangentC.normalize();
				if (face.normC.cross(tangentC).dot(bitangent) < 0.0f)
					tangentC = tangentC.mul(-1.0f);
				
				face.coordA.writeToStream(dos);
				face.texA.writeToStream(dos);
				face.normA.normalize();
				face.normA.writeToStream(dos);
				tangentA.writeToStream(dos);
				bitangent.writeToStream(dos);
				
				face.coordB.writeToStream(dos);
				face.texB.writeToStream(dos);
				face.normB.normalize();
				face.normB.writeToStream(dos);
				tangentB.writeToStream(dos);
				bitangent.writeToStream(dos);
				
				face.coordC.writeToStream(dos);
				face.texC.writeToStream(dos);
				face.normC.normalize();
				face.normC.writeToStream(dos);
				tangentC.writeToStream(dos);
				bitangent.writeToStream(dos);
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
		
		//String filename = args[0];
		String filename = "D:\\Development\\Android\\Model\\mycircles2.obj";
		//String filename = "D:\\Dropbox\\CircleLocks\\model\\mycircles.obj";
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
					texCoords.add(new TexCoord(Float.parseFloat(lexems[1]), 1.0f - Float.parseFloat(lexems[2])));
					break;
					
				case "vn":
					normCoords.add(new VertCoord(Float.parseFloat(lexems[1]), Float.parseFloat(lexems[2]), Float.parseFloat(lexems[3])));
					break;
					
				case "f":
					Face face = new Face();
					
					String[] pairA = lexems[1].split("/");
					String[] pairB = lexems[2].split("/");
					String[] pairC = lexems[3].split("/");
					
					face.coordA = vertCoords.get(Integer.parseInt(pairA[0]) - 1);
					face.texA = texCoords.get(Integer.parseInt(pairA[1]) - 1);
					face.normA = normCoords.get(Integer.parseInt(pairA[2]) - 1);
					
					face.coordB = vertCoords.get(Integer.parseInt(pairB[0]) - 1);
					face.texB = texCoords.get(Integer.parseInt(pairB[1]) - 1);
					face.normB = normCoords.get(Integer.parseInt(pairB[2]) - 1);
					
					face.coordC = vertCoords.get(Integer.parseInt(pairC[0]) - 1);
					face.texC = texCoords.get(Integer.parseInt(pairC[1]) - 1);
					face.normC = normCoords.get(Integer.parseInt(pairC[2]) - 1);
					
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
