package ee;
import robocode.*;
import java.util.Random;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * EverlastEngineeringTheSaboteur - a robot by (your name here)
 */
public class TheDummy extends AdvancedRobot
{
	// private 
	/**
	 * run: EverlastEngineeringTheSaboteur's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here
		Random rand = new Random(); 
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		 setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		 
		// Robot main loop
		while(true) {
			setTurnRight(50);
			setAhead(20);
			execute();
		}
	}

	public void target() {

	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		return;
//		double bearing = e.getBearing();
//		double distance = e.getDistance();
//		
//		if (distance > 120) {
//			// sprint right at the fucker
//			this.turnRight(bearing);
//			ahead(distance-80);
//			turnGunRight(360);
//			fire(1);
//		}
//		if (distance < 120 && distance > 80) {
//			this.turnRight(90);
//			turnGunLeft(90);
//			fire(1);
//			ahead(30);
//			turnGunLeft(90);
//			fire(1);
//		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
