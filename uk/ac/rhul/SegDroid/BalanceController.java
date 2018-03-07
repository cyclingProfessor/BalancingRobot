package uk.ac.rhul.SegDroid;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.EOPD;

/**
 * This class contains the parameters needed to keep the GELway balanced. It
 * contains a PID controller which reads in the angles and the angle velocities
 * from the gyro sensor and the left and right motor, and uses these (weighted)
 * values to calculate the motor output required to keep the GELway balanced.
 * 
 * @author Cycling Professor
 * @version April 2011
 */
public class BalanceController extends Thread {
	private GyroscopeSensor gyro;
	// The PID control parameters
	private final float Kp = 1.2F; // was 1.2
	private final float Ki = 0.25F; // Was 0.25;
	private final float Kd = 0.1F; // was 0.1
	private final int lightThresh = 10;
	// Testing error contributions.
	private final float K_psi = 44F; // Gyro angle weight
	private final float K_phi = 0.81F; // Motor angle weight
	private final float K_psidot = 0.62F; // Gyro angle velocity weight
	private final float K_phidot = 0.04F; // Motor angle velocity weight

	private static DirectionController mv;

	/**
	 * BalanceController constructor.
	 * 
	 * @param ctrl
	 *            The motor control parameters.
	 */
	public BalanceController(DirectionController mv, GyroscopeSensor gyro) {
		BalanceController.mv = mv;
		this.gyro = gyro;
		//setDaemon(true); // If set to Daemon the running of this thread will not prevent the process terminating.
	}

	/**
	 * The BalanceController thread which constantly runs to keep the GELway
	 * upright
	 */
	public void run() {
		MotorController motors = new MotorController(Motor.B, Motor.C);
		EOPD floorSensor = new EOPD(SensorPort.S1);
		float int_error = 0.0F;
		float prev_error = 0.0F;

		while (true) {
			while (floorSensor.processedValue() > lightThresh) {
				float Psi = gyro.getAngle();
				float PsiDot = gyro.getAngleVelocity();
				float Phi = motors.getAngle() - mv.getDesiredAngle();
				float PhiDot = motors.getAngleVelocity() - mv.getDesiredAngleVelocity();

				float error = Psi * K_psi + Phi * K_phi + PsiDot * K_psidot + PhiDot * K_phidot;
				float deriv_error = error - prev_error;
				int_error += error;
				prev_error = error;

				float pw = (error * Kp + deriv_error * Kd + int_error * Ki);
				motors.setPower(pw + mv.getLeftWheelDelta(),
						        pw - mv.getLeftWheelDelta());
				LMDutils.interruptedSleep(10);
			}
			motors.stop();
			mv.reset();
			while (floorSensor.processedValue() <= lightThresh) {
			}
			// Restart the robot after the third beep. Reset balance parameters
			for (int i = 0; i < 3; i++) {
				Sound.setVolume(50 + i * 25);
				Sound.beep();
				LMDutils.interruptedSleep(700);
			}
			gyro.resetGyro();
			motors.resetMotors();
			int_error = 0.0F;
			prev_error = 0.0F;
		}
	}
}