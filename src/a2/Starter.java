package a2;

import myGameEngine.Camera3Pcontroller;
import myGameEngine.MoveBackwardKBAction;
import myGameEngine.MoveForwardKBAction;
import myGameEngine.MoveForwardRYAction;
import myGameEngine.MoveLeftKBAction;
import myGameEngine.MoveRightKBAction;
import myGameEngine.MoveRightRYAction;
import myGameEngine.MyDisplaySystem;
import myGameEngine.MyScaleController;
import myGameEngine.MyTranslationController;
import myGameEngine.NodeMoveBackwardAction;
import myGameEngine.NodeMoveForwardAction;
import myGameEngine.NodeMoveLeftAction;
import myGameEngine.NodeMoveRightAction;
import myGameEngine.NodeTurnLeftAction;
import myGameEngine.NodeTurnRightAction;
import myGameEngine.PitchDownKBAction;
import myGameEngine.PitchRYAction;
import myGameEngine.PitchUpKBAction;
import myGameEngine.QuitGameAction;
import myGameEngine.RollLeftKBAction;
import myGameEngine.RollRightKBAction;
import myGameEngine.YawLeftKBAction;
import myGameEngine.YawRYAction;
import myGameEngine.YawRightKBAction;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.ThirdPersonCameraController;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDString;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.scene.shape.Sphere;

public class Starter extends BaseGame {
	private int score = 0, score2 = 0;
	private float time = 0;
	private HUDString scoreString1, scoreString2;
	private HUDString timeString;
	
	int veggieIdx;
	private static ArrayList<SceneNode> removeList = new ArrayList<SceneNode>(); //All 'plants' in gameworld set to be removed are added here
	private static ArrayList<SceneNode> veggieList = new ArrayList<SceneNode>(); //Add all obtainable 'plants' here
	private static ArrayList<SceneNode> veggieQueue = new ArrayList<SceneNode>(); //Add 'plants' recently collided with here to check which one to place in truck
	private static ArrayList<SceneNode> truckLoad = new ArrayList<SceneNode>(); //Contains all 'plants' after they've been moved to truck
	
	IDisplaySystem display;
	IRenderer renderer;
	SceneNode player1, player2;
	ICamera camera1, camera2;
	Camera3Pcontroller cam1Controller, cam2Controller;
	IInputManager im;
	IEventManager eventMgr;

	String gpName;
	String kbName;
	int numCrashes = 0;
	
	Group plants;
	Rectangle ground;
	Cube platform;
	Truck theTruck;
	Cube box1;
	Cube box1Done;
	Sphere ball;
	Sphere ballDone;
	Pyramid spike;
	Pyramid spikeDone;
	Pyramid spike2;
	Pyramid spike2Done;
	
	/*int secondsPassed = 0;
	int timeSinceCollision = 0;
	Timer myTimer;
	TimerTask task = new TimerTask() {
		public void run() {
			secondsPassed++;
		}
	};
	
	public void startTime() {
		myTimer.scheduleAtFixedRate(task, 1000, 1000);
	}*/
	
	private IDisplaySystem createDisplaySystem()
		{
			IDisplaySystem display = new MyDisplaySystem(1920, 1080, 32, 60, true, "sage.renderer.jogl.JOGLRenderer");
			System.out.print("\nWaiting for display creation...");
			int count = 0;
			// wait until display creation completes or a timeout occurs
			while (!display.isCreated())
			{
				try
				{ Thread.sleep(10); }
				catch (InterruptedException e)
				{ throw new RuntimeException("Display creation interrupted"); }
				count++;
				System.out.print("+");
				if (count % 80 == 0) { System.out.println(); }
				if (count > 2000) // 20 seconds (approx.)
				{ throw new RuntimeException("Unable to create display");
				}
			}
			System.out.println();
			return display ;
		}

	
	 protected void shutdown()
	 { display.close() ;
	 //...other shutdown methods here as necessary...
	 }
	 
	 protected void initSystem()
	 { 	
		 //call a local method to create a DisplaySystem object
		 display = createDisplaySystem();
		 setDisplaySystem(display);
		 //create an Input Manager
		 im = new InputManager();
		 setInputManager(im);
		 //create an (empty) gameworld
		 ArrayList<SceneNode> gameWorld = new ArrayList<SceneNode>();
		 setGameWorld(gameWorld);
	 }

	
	protected void initGame() {
		renderer = getDisplaySystem().getRenderer();
		//renderer = display.getRenderer();
		eventMgr = EventManager.getInstance();
		im = getInputManager();
		initGameObjects();
		createPlayers();
		initInput();
		//gpName = im.getFirstGamepadName();
		//kbName = im.getKeyboardName();
		
		/*IAction zAxisMoveRY = new MoveForwardRYAction(camera, 0.01f);
		IAction zAxisMoveForwardKB = new MoveForwardKBAction(camera, 0.01f);
		IAction zAxisMoveBackwardKB = new MoveBackwardKBAction(camera, 0.01f);
		IAction xAxisMoveRY = new MoveRightRYAction(camera, 0.01f);
		IAction xAxisMoveRightKB = new MoveRightKBAction(camera, 0.01f);
		IAction xAxisMoveLeftKB = new MoveLeftKBAction(camera, 0.01f);
		IAction pitchRY = new PitchRYAction(camera, 0.03f);
		IAction yawRY = new YawRYAction(camera, 0.03f);
		IAction pitchUpKB = new PitchUpKBAction(camera, 0.03f);
		IAction pitchDownKB = new PitchDownKBAction(camera, 0.03f);
		IAction rollRightKB = new RollRightKBAction(camera, 0.01f);
		IAction yawRightKB = new YawRightKBAction(camera, 0.03f);
		IAction yawLeftKB = new YawLeftKBAction(camera, 0.03f);
		IAction rollLeftKB = new RollLeftKBAction(camera, 0.01f);
		IAction quitGame = new QuitGameAction(this);
		
		//Keybindings for first GamePad found in controller array returned by im.getFirstGamepadName();
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, zAxisMoveRY, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, xAxisMoveRY, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RY, pitchRY, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.RX, yawRY, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, quitGame, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		//Keybindings specific to my keyboard setup (Razer BlackWidow Ultimate USB)
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.W, zAxisMoveForwardKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.S, zAxisMoveBackwardKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.D, xAxisMoveRightKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.A, xAxisMoveLeftKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.E, rollRightKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.Q, rollLeftKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.UP, pitchUpKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.DOWN, pitchDownKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.RIGHT, yawRightKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.LEFT, yawLeftKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(3), net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		//Keybindings for first keyboard found in the controller array returned by im.getKeyboardName()
		//Meant for testing in RVR5029 lab
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, zAxisMoveForwardKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, zAxisMoveBackwardKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, xAxisMoveRightKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, xAxisMoveLeftKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.E, rollRightKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Q, rollLeftKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.UP, pitchUpKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.DOWN, pitchDownKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.RIGHT, yawRightKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.LEFT, yawLeftKB, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);*/
		super.update((float) 0.0);
	}
	
	public void update(float elapsedTimeMS) {	
		cam1Controller.update(elapsedTimeMS);
		cam2Controller.update(elapsedTimeMS);
		
		for (SceneNode s : plants) {
			if(s.getWorldBound() != null) {
				if(s.getWorldBound().intersects(player1.getWorldBound())) {
					//plants.removeChild(s);
					removeList.add(s);
					veggieQueue.add(s);
					score += 10;
					//timeSinceCollision = secondsPassed;
					numCrashes++;
					CrashEvent newCrash = new CrashEvent(elapsedTimeMS);
					eventMgr.triggerEvent(newCrash);
				}
				
				if(s.getWorldBound().intersects(player2.getWorldBound())) {
					//plants.removeChild(s);
					removeList.add(s);
					veggieQueue.add(s);
					score2 += 10;
					//timeSinceCollision = secondsPassed;
					numCrashes++;
					CrashEvent newCrash2 = new CrashEvent(elapsedTimeMS);
					eventMgr.triggerEvent(newCrash2);
				}
				
			}
		}
		
		//Camera collision detection with plants. Adds plants to queue for removal if either camera touches them.
		/*for (SceneNode s : getGameWorld()) {
			if (s.getWorldBound() != null && !(s instanceof Truck) && !(s instanceof Rectangle) && (s != plants) && !(truckLoad.contains(s))) {
				if ((s.getWorldBound().contains(camera1.getLocation())) 
						&& !(player1.getWorldBound().contains(camera2.getLocation())) && !(player2.getWorldBound().contains(camera1.getLocation()))) {
					plants.removeChild(s);
					removeList.add(s);
					veggieQueue.add(s);
					score += 10;
					//timeSinceCollision = secondsPassed;
					numCrashes++;
					CrashEvent newCrash = new CrashEvent(elapsedTimeMS);
					eventMgr.triggerEvent(newCrash);
				}
				
				if ((s.getWorldBound().contains(camera2.getLocation())) 
						&& !(player1.getWorldBound().contains(camera2.getLocation())) && !(player2.getWorldBound().contains(camera1.getLocation()))) {
					//plants.removeChild(s);
					removeList.add(s);
					veggieQueue.add(s);
					score2 += 10;
					//timeSinceCollision = secondsPassed;
					numCrashes++;
					CrashEvent newCrash2 = new CrashEvent(elapsedTimeMS);
					eventMgr.triggerEvent(newCrash2);
				}
			}
		}*/
		
		//Removes objects that have collided with a player
		if(removeList.size() > 0) {
			plants.removeChild(removeList.get(0));
			removeGameWorldObject(removeList.get(0));
			removeList.remove(0);
		}
		
		//Add plants that have been collected onto the truck
		if(veggieQueue.size() > 0) {
			for(int i = 0; i < veggieList.size(); i++) {
				if(veggieQueue.get(0)==veggieList.get(i)) {
					addGameWorldObject(truckLoad.get(i));
				}
			}
			veggieQueue.remove(0);
		}
		
		//Change truck's color for 1.5s after a player collects a plant
		if(theTruck.getTimeSinceCrash() != 0) {
			theTruck.incrementTimeSinceCrash(elapsedTimeMS);
			if(theTruck.getTimeSinceCrash() > 1500) {
				theTruck.revertColor();
				theTruck.resetTimeSinceCrash();
			}
		}
		
		/*if(theTruck.colorChanged() && secondsPassed - timeSinceCollision > 1.5) {
			theTruck.revertColor();
		}*/
		
		scoreString1.setText("Score = " + score);
		scoreString2.setText("Score = " + score2);
		time += elapsedTimeMS;
		DecimalFormat df = new DecimalFormat("0.0");
		timeString.setText("Time = " + df.format(time/1000));
		
		super.update(elapsedTimeMS);
	}
	
	private void createPlayers() {
		player1 = new Pyramid("PLAYER1");
		player1.translate(0, 1, 50);
		player1.rotate(180, new Vector3D(0,1,0));
		addGameWorldObject(player1);
		
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(60, 2, 1, 1000);
		camera1.setViewport(0.0, 1.0, 0.0, 0.45);
		
		player2 = new Cube("PLAYER2");
		player2.translate(50, 1, 0);
		player2.rotate(-90, new Vector3D(0,1,0));
		addGameWorldObject(player2);
		
		camera2 = new JOGLCamera(renderer);
		camera2.setPerspectiveFrustum(60, 2, 1, 1000);
		camera2.setViewport(0.0, 1.0, 0.55, 1.0);
		createPlayerHUDs();
	}
	
	private void createPlayerHUDs() {
		HUDString player1ID = new HUDString("Player1");
		player1ID.setName("Player1ID");
		player1ID.setLocation(0.01,0.06);
		player1ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		player1ID.setColor(Color.red);
		player1ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera1.addToHUD(player1ID);
		scoreString1 = new HUDString("Score = " + score);
		scoreString1.setLocation(0.01, 0);
		camera1.addToHUD(scoreString1);
		
		HUDString player2ID = new HUDString("Player2");
		player2ID.setName("Player2ID");
		player2ID.setLocation(0.01,0.06);
		player2ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		player2ID.setColor(Color.yellow);
		player2ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera2.addToHUD(player2ID);
		scoreString2 = new HUDString("Score = " + score2);
		scoreString2.setColor(Color.yellow);
		scoreString2.setLocation(0.01, 0);
		camera2.addToHUD(scoreString2);
	}
	
	private void initInput() {
		kbName = im.getKeyboardName();
		gpName = im.getFirstGamepadName();
		
		cam1Controller = new Camera3Pcontroller(camera1, player1, im, im.getKeyboardController(4));
		cam2Controller = new Camera3Pcontroller(camera2, player2, im, gpName);
		
		//player 1 actions
		IAction moveForward1 = new NodeMoveForwardAction(player1);
		IAction moveBackward1 = new NodeMoveBackwardAction(player1);
		IAction moveLeft1 = new NodeMoveLeftAction(player1);
		IAction moveRight1 = new NodeMoveRightAction(player1);
		IAction turnLeft1 = new NodeTurnLeftAction(player1);
		IAction turnRight1 = new NodeTurnRightAction(player1);
		
		IAction quitGame1 = new QuitGameAction(this);
		
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.S, moveForward1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.W, moveBackward1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.A, moveLeft1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.D, moveRight1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.Q, turnLeft1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.E, turnRight1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(im.getKeyboardController(4), net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame1, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		//player 2 actions
		IAction moveForward2 = new NodeMoveForwardAction(player2);
		IAction moveBackward2 = new NodeMoveBackwardAction(player2);
		IAction moveLeft2 = new NodeMoveLeftAction(player2);
		IAction moveRight2 = new NodeMoveRightAction(player2);
		IAction turnLeft2 = new NodeTurnLeftAction(player2);
		IAction turnRight2 = new NodeTurnRightAction(player2);
		
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._1, moveForward2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._2, moveBackward2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._0, moveLeft2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, moveRight2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._4, turnLeft2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._5, turnRight2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	
	private void initGameObjects() {
		/*myTimer = new Timer();
		startTime();*/
		
		//display = createDisplaySystem();
		//setDisplaySystem(display);
		//display = getDisplaySystem();
		//display.setTitle("SpaceFarming3D");
		
		timeString = new HUDString("Time = " + time);
		timeString.setLocation(0.01, 0.5);
		timeString.setColor(Color.blue);
		addGameWorldObject(timeString);
		
		ground = new Rectangle();
		Matrix3D newRot = new Matrix3D(90, new Vector3D(1,0,0));
		Matrix3D curRot = ground.getLocalRotation();
		curRot.concatenate(newRot);
		ground.setLocalRotation(curRot);
		ground.scale(1000f, 1000f, 1000f);
		ground.setColor(Color.lightGray);
		addGameWorldObject(ground);
		
		platform = new Cube();
		Matrix3D platformM = platform.getLocalTranslation();
		platformM.translate(40, 5, 25);
		platform.setLocalTranslation(platformM);
		platform.scale(2, 0.4f, 2);
		addGameWorldObject(platform);
		
		box1 = new Cube();
		Matrix3D box1M = box1.getLocalTranslation();
		box1M.translate(-10, 1, -5);
		box1.setLocalTranslation(box1M);
		box1.scale(1f, 1f, 1f);
		veggieList.add(box1);
		//addGameWorldObject(box1);
		
		box1Done = new Cube();
		Matrix3D box1DoneM = box1Done.getLocalTranslation();
		box1DoneM.translate(-0.5, 0.5, -12);
		box1Done.setLocalTranslation(box1DoneM);
		box1Done.scale(0.4f, 0.4f, 0.4f);
		truckLoad.add(box1Done);
		//addGameWorldObject(box1Done);
		
		ball = new Sphere();
		Matrix3D ballM = ball.getLocalTranslation();
		ballM.translate(25, 3, 25);
		ball.setLocalTranslation(ballM);
		ball.scale(2, 2, 2);
		veggieList.add(ball);
		//addGameWorldObject(ball);
		
		ballDone = new Sphere();
		Matrix3D ballDoneM = ballDone.getLocalTranslation();
		ballDoneM.translate(0.5, 0.5, -12);
		ballDone.setLocalTranslation(ballDoneM);
		ballDone.scale(0.4f, 0.4f, 0.4f);
		truckLoad.add(ballDone);
		//addGameWorldObject(ballDone);
		
		spike = new Pyramid();
		Matrix3D spikeM = spike.getLocalTranslation();
		spikeM.translate(10, 1, -8);
		spike.setLocalTranslation(spikeM);
		spike.scale(0.5f, 2, 0.5f);
		veggieList.add(spike);
		//addGameWorldObject(spike);
		
		spikeDone = new Pyramid();
		Matrix3D spikeDoneM = spikeDone.getLocalTranslation();
		spikeDoneM.translate(2, 2, -12);
		spikeDone.setLocalTranslation(spikeDoneM);
		spikeDone.scale(0.5f, 2, 0.5f);
		truckLoad.add(spikeDone);
		
		spike2 = new Pyramid();
		Matrix3D spike2M = spike2.getLocalTranslation();
		spike2M.translate(5, 1, 2);
		spike2.setLocalTranslation(spike2M);
		spike2.scale(1, 1, 0.5f);
		veggieList.add(spike2);
		//addGameWorldObject(spike2);
		
		spike2Done = new Pyramid();
		Matrix3D spike2DoneM = spike2Done.getLocalTranslation();
		spike2DoneM.translate(4, 1, -12);
		spike2Done.setLocalTranslation(spike2DoneM);
		spike2Done.scale(1, 1, 0.5f);
		truckLoad.add(spike2Done);
		
		plants = new Group();
		plants.addChild(spike);
	    plants.addChild(box1);
		plants.addChild(ball);
		plants.addChild(spike2);
		addGameWorldObject(plants);
		
		//-------- Node Controllers -------------
		
		MyScaleController scaleCtr = new MyScaleController();
		scaleCtr.addControlledNode(plants);
		plants.addController(scaleCtr);
		
		MyTranslationController transCtr = new MyTranslationController();
		transCtr.addControlledNode(platform);
		platform.addController(transCtr);
		
		//---------------------------------------
			
		theTruck = new Truck();
		Matrix3D truckM = theTruck.getLocalTranslation();
		truckM.translate(2, 0, -12);
		theTruck.setLocalTranslation(truckM);
		addGameWorldObject(theTruck);
		
		eventMgr.addListener(theTruck, CrashEvent.class);
		
		//----------- X,Y,Z Axes --------------
		
		Point3D origin = new Point3D(0,0,0);
		Point3D xEnd = new Point3D(100,0,0);
		Point3D yEnd = new Point3D(0,100,0);
		Point3D zEnd = new Point3D(0,0,100);
		Line xAxis = new Line(origin, xEnd, Color.red, 2);
		Line yAxis = new Line(origin, yEnd, Color.green, 2);
		Line zAxis = new Line(origin, zEnd, Color.blue, 2);
		addGameWorldObject(xAxis);
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);
	}
	
	protected void render() {
		renderer.setCamera(camera1);
		super.render();
		
		renderer.setCamera(camera2);
		super.render();
	}
	
	public static void main(String[] args) {
		new Starter().start();
	}
}
