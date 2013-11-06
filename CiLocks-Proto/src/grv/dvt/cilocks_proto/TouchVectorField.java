package grv.dvt.cilocks_proto;

import android.graphics.PointF;
import android.util.SparseArray;

public class TouchVectorField {

	private SparseArray<TouchVector> mVectors;
	
	public TouchVectorField() {
		this.mVectors = new SparseArray<TouchVector>();
	}
	
	public void startVector(int id, PointF point) {
		TouchVector vector = new TouchVector();
		vector.init = point;
		vector.previous = point;
		vector.last = point;
		
		this.mVectors.put(id, vector);
	}
	
	public void releaseVector(int id) {
		this.mVectors.remove(id);
	}
	
	public void moveVector(int id, PointF point) {
		this.mVectors.get(id).previous = this.mVectors.get(id).last;
		this.mVectors.get(id).last = point;
	}
	
	public int getVectorCount() {
		return this.mVectors.size();
	}
	
	public TouchVector getVector(int index) {
		return this.mVectors.valueAt(index);
	}
	
	public void clearField() {
		this.mVectors.clear();
	}
}
