package grv.dvt.cilocks_gles20;

import android.graphics.PointF;
import android.util.SparseArray;

public class TouchVectorField<TAction extends Enum<TAction>> {
	
	public class TouchVector {
		
		public PointF init;
		public PointF previous;
		public PointF last;
		
		public TAction action;
	}
	
	
	private TAction mDefaultVectorAction;
	private SparseArray<TouchVector> mVectors;
	
	public TouchVectorField(TAction defaultAction) {
		this.mDefaultVectorAction = defaultAction;
		this.mVectors = new SparseArray<TouchVector>();
	}
	
	public void startVector(int id, PointF point) {
		TouchVector vector = new TouchVector();
		vector.init = point;
		vector.previous = point;
		vector.last = point;
		vector.action = this.mDefaultVectorAction;
		
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
