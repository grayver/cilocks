package grv.dvt.cilocks_proto;

import android.graphics.PointF;

public class TouchVector {
	public enum Action {
		UNKNOWN, DUMMY, ROLL, SWAP
	}
	
	public PointF init;
	public PointF previous;
	public PointF last;
	
	public Action action;
	
	public TouchVector() {
		this.action = Action.UNKNOWN;
	}
}
