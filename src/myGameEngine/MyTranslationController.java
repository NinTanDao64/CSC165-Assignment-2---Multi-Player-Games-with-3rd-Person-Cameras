package myGameEngine;

import graphicslib3D.Matrix3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class MyTranslationController extends Controller {
	private double transRate = 0.002;
	private double cycleTime = 2000.0;
	private double totalTime;
	private double dir = 1.0;
	
	public void setCycleTime(double c) {
		cycleTime = c;
	}
	
	public void update(double time) {
		totalTime += time;
		double transAmount = transRate * time;
		
		if(totalTime > cycleTime) {
			dir = -dir;
			totalTime = 0.0;
		}
		
		transAmount = dir * transAmount;
		
		Matrix3D newTrans = new Matrix3D();
		newTrans.translate(transAmount, transAmount, transAmount);
		
		for(SceneNode node: controlledNodes) {
			Matrix3D curTrans = node.getLocalTranslation();
			curTrans.concatenate(newTrans);
			node.setLocalTranslation(curTrans);
		}
	}
}
