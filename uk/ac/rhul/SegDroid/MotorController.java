package uk.ac.rhul.SegDroid;
import lejos.nxt.*;

/**
 * A class used to handle controlling the motors. Has methods to set the motors
 * speed and get the motors angle and velocity. Based off original programmers
 * of Marvin Bent Bisballe Nyeng, Kasper Sohn and Johnny Rieper and Steven Jan
 * Witzand.
 * 
 * @author Cycling Professor
 * @version April 2011
 */
class MotorController {
	private Motor leftMotor;
	private Motor rightMotor;
	// Sinusoidal parameters used to smooth motors
//	private double sin_x = 0.0;
//	private final double sin_speed = 0.1;
//	private final double sin_amp = 20.0;
	private final float motorDifference = 28/25F;// 1.1; // Not all motors are the
												// same......

	/**
	 * MotorController constructor.
	 * 
	 * @param leftMotor
	 *            The GELways left motor.
	 * @param rightMotor
	 *            The GELways right motor.
	 */
	public MotorController(Motor leftMotor, Motor rightMotor) {
		this.leftMotor = leftMotor;
		this.leftMotor.resetTachoCount();

		this.rightMotor = rightMotor;
		this.rightMotor.resetTachoCount();
	}

	/**
	 * Method is used to set the power level to the motors required to keep it
	 * upright. A dampened sinusoidal curve is applied to the motors to reduce
	 * the rotation of the motors over time from moving forwards and backwards
	 * constantly.
	 * 
	 * @param leftPower
	 *            A double used to set the power of the left motor. Maximum
	 *            value depends on battery level but is approximately 815. A
	 *            negative value results in motors reversing.
	 * @param rightPower
	 *            A double used to set the power of the right motor. Maximum
	 *            value depends on battery level but is approximately 815. A
	 *            negative value results in motors reversing.
	 */
	public void setPower(float leftPower, float rightPower) {
//		sin_x += sin_speed;
//		int pwl = (int) (leftPower + Math.sin(sin_x) * sin_amp);
//		int pwr = (int) (motorDifference * rightPower - Math.sin(sin_x)
//				* sin_amp);

		int pwl = Math.round(leftPower);
		leftMotor.setSpeed(pwl);
		if (pwl < 0) {
			leftMotor.backward();
		} else if (pwl > 0) {
			leftMotor.forward();
		} else {
			leftMotor.stop();
		}

		int pwr = Math.round(rightPower * motorDifference);
		rightMotor.setSpeed(pwr);
		if (pwr < 0) {
			rightMotor.backward();
		} else if (pwr > 0) {
			rightMotor.forward();
		} else {
			rightMotor.stop();
		}
	}

	/**
	 * getAngle returns the average motor angle of the left and right motors
	 * 
	 * @return A double of the average motor angle of the left and right motors
	 *         in degrees.
	 */
	public float getAngle() {
		return (leftMotor.getTachoCount() + rightMotor.getTachoCount()) / 2.0F;
	}

	/**
	 * getAngle returns the average motor velocity of the left and right motors
	 * 
	 * @return a double of the average motor velocity of the left and right
	 *         motors in degrees.
	 */
	public float getAngleVelocity() {
		return (leftMotor.getRotationSpeed() + rightMotor.getRotationSpeed()) / 2.0F;
	}

	/**
	 * reset the motors tacho count
	 */
	public void resetMotors() {
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
	}

	/**
	 * stop both motors from rotating
	 */
	public void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
}