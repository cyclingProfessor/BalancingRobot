package uk.ac.rhul.SegDroid;


/**
 * A class which handles controlling the GELway directional movements. Setters
 * called by Bluetooth thread and Getters by BalanceController.
 * 
 * @author Cycling Professor
 * @version April 2011
 */
public class DirectionController implements CommandPerformer {
	private final static int HELLO_ROBOT_COMMAND = 0;
	private final static int BACH_MUSIC_COMMAND = 1;
	private final static int BEETHOVEN_MUSIC_COMMAND = 2;
//	private final static int USS_READER = 3;
//	private final static int LINE_COMMAND = 4;
//	private final static int FOLLOW_COMMAND = 5;

	private MINDdroidConnector droidConnection = null;

	private float angularVelocity;
	private float leftDeltaVelocity; // How fast are we moving?
	private int expectedWheelAngle;
	private long adjustedTime;
	private float desiredSpeed;
	private float desiredDelta;

	/**
	 * MotorDirection constructor.
	 */
	public DirectionController() {
		reset();
		droidConnection = new MINDdroidConnector(this);
		droidConnection.registerCommand("Hello, robot!", HELLO_ROBOT_COMMAND);
		droidConnection.registerCommand("Music from Bach", BACH_MUSIC_COMMAND);
		droidConnection.registerCommand("Music from Beethoven", BEETHOVEN_MUSIC_COMMAND);
//		droidConnection.registerCommand("Ultrasonic-Avoider", USS_READER);
//		droidConnection.registerCommand("Follow Me", FOLLOW_COMMAND);
//		droidConnection.registerCommand("Line Follower", LINE_COMMAND);
		droidConnection.registerCommand("OUTPUT", MINDdroidConnector.OUTPUT_COMMAND);
		droidConnection.registerCommand("ACTION", MINDdroidConnector.ACTION_COMMAND);

		droidConnection.start();

	}

	public synchronized void reset() {
		adjustedTime = System.currentTimeMillis();
		angularVelocity = 0.0F;
		leftDeltaVelocity = 0.0F;
		expectedWheelAngle = 0;
		desiredSpeed = 0;
		desiredDelta = 0;
	}

	public float getDesiredAngle() {
		adjust();
		return expectedWheelAngle;
	}

	public float getDesiredAngleVelocity() {
		return angularVelocity;
	}

	public float getLeftWheelDelta() {
		return leftDeltaVelocity;
	}

	private synchronized void adjust() {
		long currentTime = System.currentTimeMillis();
		long loopTime = currentTime - adjustedTime;
		expectedWheelAngle += (1 / 1000.0) * angularVelocity * loopTime;
		long maxDelta = 1 + loopTime / 10;
		if (desiredSpeed > angularVelocity + maxDelta) {
			angularVelocity += maxDelta;
		} else if (desiredSpeed < angularVelocity - maxDelta) {
			angularVelocity -= maxDelta;
		} else {
			angularVelocity = desiredSpeed;
		}
		if (desiredDelta > leftDeltaVelocity + maxDelta) {
			leftDeltaVelocity += maxDelta;
		} else if (desiredSpeed < angularVelocity - maxDelta) {
			leftDeltaVelocity -= maxDelta;
		} else {
			leftDeltaVelocity = desiredDelta;
		}
		//		LCD.drawString("mD: " + maxDelta + "    ", 0,0);
		//		LCD.drawString("aV: " + angularVelocity + "    ", 0,1);
		//		LCD.drawString("dS: " + desiredSpeed + "    ", 0,2);
		adjustedTime = currentTime;
	}

	/**
	 * Reads the value of the Ultrasonice-Sensor and sends it as TTS to the
	 * mobile phone when it is smaller than 30 cm.
	 * Tries to keep that distance from things.
	 */
/*	private void commandUSSReader() {
		if (ignoreBTMoves > 0) return;

		UltrasonicSensor myUSSensor = new UltrasonicSensor(SensorPort.S2);
		int distance;
		ignoreBTMoves += 1;
		while (true) {
			distance = myUSSensor.getDistance();
			if (myUSSensor.getDistance() < 100) {
				//Sound.playTone(1000, 500);
				if (droidConnection.sendTTS("Hello!", 0, 0, 2, 1000))
					break;
				if (droidConnection.sendTTS("Spotted you", 0, 0, 0, 2000))
					break;
				if (droidConnection.sendTTS("" + distance + " centimeters", 0, 0, 0, 2000))
					break;
				if (droidConnection.sendTTS("Move back and forwards", 0, 0, 0, 2000))
					break;
				if (droidConnection.sendTTS("Don't move too fast", 0, 0, 1, 2000))
					break;
			}
			if (LMDutils.interruptedSleep(200))
				break;
		}
		while (true) {
			if (myUSSensor.getDistance() < distance -20) {
				desiredSpeed += 50;
			}
			if (myUSSensor.getDistance() > distance +20) {
				desiredSpeed -= 50;
			}
			if (LMDutils.interruptedSleep(200))
				break;
		}
		ignoreBTMoves -= 1;
	}*/

	private void commandSpin() {
		// take control of movement....
		if (ignoreBTMoves > 0) return;

		ignoreBTMoves += 1;
		desiredSpeed = 0;
		desiredDelta = 350;
		LMDutils.interruptedSleep(10000);
		desiredDelta = 0;
		ignoreBTMoves -= 1;
	}
/*
	private void followLine() {

	}
	private void followMe() {

	}
*/
	private int leftValue;
	private volatile int ignoreBTMoves = 0;
	
	@Override
	public void performCommand(int command, byte[] parameter) {
		switch (command) {

		case HELLO_ROBOT_COMMAND:
			if (droidConnection.sendTTS("Hello, robot!", 0, 1, 2, 2000))
				break;
			droidConnection.sendTTS("How are you?", 0, 1, 2, 3000);
			break;

		case BACH_MUSIC_COMMAND:
			Compositions.play(Compositions.BACH_MUSETTE_D_MAJOR);
			break;
			
		case BEETHOVEN_MUSIC_COMMAND:
			Compositions.play(Compositions.BEETHOVEN_SYMPHONY_5_C_MINOR);
			break;

//		case USS_READER:
//			commandUSSReader();
//			break;
//
//		case SPIN_COMMAND:
//
//			break;

		case MINDdroidConnector.OUTPUT_COMMAND:
			if (ignoreBTMoves == 0) {
				switch (parameter[2]) {
				case 1: // Left hand Motor
					// THE LEFT Motor comes first, so just store it and wait....
					leftValue = parameter[3];
					break;
				case 2: // Right hand Motor
					// When we get the right hand value we can then update things.
					desiredSpeed = 2 * (parameter[3] + leftValue);
//					if ((desiredSpeed < 50  && desiredSpeed > -50) &&
//							(angularVelocity > 50 || angularVelocity < -50)){
//						droidConnection.vibratePhone(400);
//					}
					desiredDelta = (leftValue - parameter[3]) / 2;
					break;
				default:
					break;
				}}
			break;

		case MINDdroidConnector.ACTION_COMMAND:
			commandSpin();
			break;

		default:
			break;

		}

	}
}
