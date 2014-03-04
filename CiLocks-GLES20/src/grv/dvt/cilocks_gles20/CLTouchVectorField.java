package grv.dvt.cilocks_gles20;

public class CLTouchVectorField extends TouchVectorField<CLTouchVectorField.VectorAction> {

	public enum VectorAction {
		UNKNOWN, DUMMY, ROLL, SWAP
	}
	
	public CLTouchVectorField() {
		super(VectorAction.UNKNOWN);
	}
}
