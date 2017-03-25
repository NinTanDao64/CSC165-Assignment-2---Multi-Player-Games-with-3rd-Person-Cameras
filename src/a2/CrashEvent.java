package a2;

import sage.event.AbstractGameEvent;

public class CrashEvent extends AbstractGameEvent {
	//programmer-defined parts go here
	private float whichCrash;
	
	public CrashEvent(float n) {
		whichCrash = n;
	}
	
	public float getWhichCrash() {
		return whichCrash;
	}
}

