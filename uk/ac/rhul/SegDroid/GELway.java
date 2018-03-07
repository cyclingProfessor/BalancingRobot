package uk.ac.rhul.SegDroid;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

/**
 * This is the GELways main program. It initiates the Bluetooth connection and
 * establishes the balancing and behavioural threads. Note this program is based
 * on Marvin the Balancing robot and has been modified by Steven Jan Witzand. As
 * such, all original authors should be properly referenced if you intend on
 * modifying this code. The creators of Marvin are Bent Bisballe Nyeng, Kasper
 * Sohn and Johnny Rieper
 * 
 * @author Cycling Professor
 * @version April 2011
 */
public final class GELway {
	// private static BluetoothReader br;
	private static GyroscopeSensor gyro;

	public static void main(String[] args) throws InterruptedException {
		gyro = new GyroscopeSensor(SensorPort.S3);
		gyro.calcOffset();


		// Start the direction command mechanism
		DirectionController mv = new DirectionController();
		BalanceController bc = new BalanceController(mv, gyro);
		bc.start();
		LCD.clear();
//		int speed = 0;int MAXSPEED = 400;int RAMP=50;
//		int DELAY = 400;
//		while (true) {
//			while (speed < MAXSPEED) {
//				speed += RAMP;
//				mv.setSpeed(speed);
//				Thread.sleep(DELAY);
//			}
//			mv.setTurn(50);
//			while (speed > -MAXSPEED) {
//				speed -= RAMP;
//				mv.setSpeed(speed);
//				Thread.sleep(DELAY);
//			}
//			mv.setTurn(-50);
//		}
	}
}