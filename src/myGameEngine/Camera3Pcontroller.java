package myGameEngine;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class Camera3Pcontroller {
	private ICamera cam;
	private SceneNode target;
	private float cameraAzimuth;
	private float cameraElevation;
	private float cameraDistanceFromTarget;
	private Point3D targetPos;
	private Vector3D worldUpVec;
	
	public Camera3Pcontroller(ICamera cam, SceneNode target, IInputManager inputMgr, String controllerName) {
		this.cam = cam;
		this.target = target;
		worldUpVec = new Vector3D(0,1,0);
		cameraDistanceFromTarget = 5.0f;
		cameraAzimuth = 180;
		cameraElevation = 20.0f;
		update(0.0f);
		setupInput(inputMgr, controllerName);
	}
	
	public Camera3Pcontroller(ICamera cam, SceneNode target, IInputManager inputMgr, net.java.games.input.Controller controller) {
		this.cam = cam;
		this.target = target;
		worldUpVec = new Vector3D(0,1,0);
		cameraDistanceFromTarget = 5.0f;
		cameraAzimuth = 180;
		cameraElevation = 20.0f;
		update(0.0f);
		setupInput2(inputMgr, controller);
	}
	
	public void update(float time) {
		updateTarget();
		updateCameraPosition();
		cam.lookAt(targetPos, worldUpVec);
	}
	
	private void updateTarget() {
		targetPos = new Point3D(target.getWorldTranslation().getCol(3));
	}
	
	private void updateCameraPosition() {
		double theta = cameraAzimuth;
		double phi = cameraElevation;
		double r = cameraDistanceFromTarget;
		
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta,phi,r);
		Point3D desiredCameraLoc = relativePosition.add(targetPos);
		cam.setLocation(desiredCameraLoc);
	}
	
	private void setupInput(IInputManager im, String cn) {
		IAction orbitUpDown = new OrbitVerticalAction();
		im.associateAction(cn, Axis.RY, orbitUpDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		IAction orbitLeftRight = new OrbitHorizontalAction();
		im.associateAction(cn, Axis.RX, orbitLeftRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	
	private void setupInput2(IInputManager im, Controller ctr) {
		IAction orbitUp = new OrbitUpKBAction();
		im.associateAction(ctr, net.java.games.input.Component.Identifier.Key.DOWN, orbitUp, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		IAction orbitDown = new OrbitDownKBAction();
		im.associateAction(ctr, net.java.games.input.Component.Identifier.Key.UP, orbitDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		IAction orbitLeft = new OrbitLeftKBAction();
		im.associateAction(ctr, net.java.games.input.Component.Identifier.Key.LEFT, orbitLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		IAction orbitRight = new OrbitRightKBAction();
		im.associateAction(ctr, net.java.games.input.Component.Identifier.Key.RIGHT, orbitRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	
	private class OrbitUpKBAction extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			float rotAmount = 0.1f;

			cameraElevation += rotAmount;
			cameraElevation = cameraElevation % 360;
			}
	}
	
	private class OrbitDownKBAction extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			float rotAmount = -0.1f;

			cameraElevation += rotAmount;
			cameraElevation = cameraElevation % 360;
			}
	}
	
	private class OrbitLeftKBAction extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			float rotAmount = 0.1f;

			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			}
	}
	
	private class OrbitRightKBAction extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			float rotAmount = -0.1f;

			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			}
	}
	
	private class OrbitVerticalAction extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			float rotAmount;
			if(evt.getValue() < -0.2) {rotAmount = -0.1f;}
			else { if(evt.getValue() > 0.2) {rotAmount = 0.1f;}
			else {rotAmount = 0.0f;}
			}
			cameraElevation += rotAmount;
			cameraElevation = cameraElevation % 360;
			}
	}
	
	private class OrbitHorizontalAction extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			float rotAmount;
			if(evt.getValue() < -0.2) {rotAmount = -0.1f;}
			else { if(evt.getValue() > 0.2) {rotAmount = 0.1f;}
			else {rotAmount = 0.0f;}
			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
			
			}
	}
}
