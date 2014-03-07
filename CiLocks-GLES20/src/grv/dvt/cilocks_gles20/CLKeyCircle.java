package grv.dvt.cilocks_gles20;

public class CLKeyCircle {
	
	/** Sector count */
	private int mSectorCount;
	
	/** Sectors */
	private CLKeySector[] mSectors;
	
	
	/** Constructor */
	public CLKeyCircle(int sectorCount, int[] colorIndexes) {
		this.mSectorCount = sectorCount;
		this.mSectors = new CLKeySector[sectorCount];
		for (int i = 0; i < sectorCount; i++)
			this.mSectors[i] = new CLKeySector(colorIndexes[i]);
	}
	
	public int getSectorCount() {
		return this.mSectorCount;
	}
	
	public CLKeySector getSector(int index) {
		return this.mSectors[index];
	}
}
