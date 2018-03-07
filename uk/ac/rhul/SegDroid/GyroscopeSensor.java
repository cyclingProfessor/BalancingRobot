package uk.ac.rhul.SegDroid;
/* -*- tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;

/**
 * This class is designed to work with the HiTechnic Gyrosensor. It estimates
 * the the gyro offset in the constructor. There are methods to find the current
 * gyro's angle and velocity. Since the getAngle method assumes a constant
 * angular velocity since the last time it was called this method must be called
 * frequently to avoid innacuracies. From original ideas by Bent Bisballe Nyeng,
 * Kasper Sohn and Johnny Rieper Steven Jan Witzand
 * 
 * @author Cycling Professor
 * @version April 2011
 */
public class GyroscopeSensor {
	private float angle = 0.0F;
	private int lastGetAngleTime = 0;
	private float offset = 0;
	private GyroSensor theSensor;

	/**
	 * The GyroscopeSensor constructor.
	 * 
	 * @param port
	 *            The NXT Sensor port of the gyro sensor.
	 */
	public GyroscopeSensor(SensorPort port) {
		theSensor = new GyroSensor(port);
	}

	/**
	 * Calculates the offset specific to the HiTechnic gyro sensor. Needs to
	 * read the gyro sensor in a stationary position.
	 */
	public void calcOffset() {
		offset = -2.5F;
	}

	/**
	 * Get the angle velocity of the gyro sensor.
	 * 
	 * @return A float containing the angular velocity of the gyro sensor in
	 *         degrees per second
	 */
	public float getAngleVelocity() {
		return (float) offset - theSensor.readValue();
	}

	/**
	 * Get the calculated gyro angle (angular velocity integrated over time).
	 * 
	 * @return The angle in degrees.
	 */
	public float getAngle() {
		int now = (int) System.currentTimeMillis();
		int delta_t = now - lastGetAngleTime;

		// Make sure we only add to the sum when there has actually
		// been a previous call (delta_t == now if its the first call).
		if (delta_t != now) {
			angle += getAngleVelocity() * (delta_t / 1000.0F);
		}
		lastGetAngleTime = now;

		return angle;
	}

	/**
	 * Reset the gyro angle
	 */
	public void resetGyro() {
		angle = 0.0F;
		lastGetAngleTime = 0;
	}

}
