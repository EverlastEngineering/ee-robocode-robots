package ee;
import robocode.*;
import robocode.util.Utils;

import java.util.Random;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * EverlastEngineeringTheSaboteur - a robot by (your name here)
 */
public class TheSaboteur extends AdvancedRobot
{
	// private 
	/**
	 * run: EverlastEngineeringTheSaboteur's default behavior
	 */
	private boolean activeRadar = true;
	
	public void run() {
		// Initialization of the robot should be put here
		Random rand = new Random(); 
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		 //setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		boolean way = true;
		// Robot main loop
		while(true) {
			
			out.printf("\nremaining: %f",getRadarTurnRemaining());
			if (activeRadar && getRadarTurnRemaining() == 0) {
				setTurnRadarRight(Double.POSITIVE_INFINITY);
			}
			
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
		double heading = getHeading();
		double bearing = e.getBearing();
		double radarHeading = getRadarHeading();
		double aa = heading + bearing - radarHeading;
		double cc = 1.95 * Utils.normalRelativeAngleDegrees(aa);
		setTurnRadarRight(cc);
		
		double gunBearing = this.getGunHeading();
		
//		this.setTurnGunRight(Utils.normalRelativeAngleDegrees(gunBearing+radarHeading));
		double relational_difference = (e.getDistance() / 100);
		double distance_offset = cc * relational_difference;
		this.setTurnGunRight(radarHeading-gunBearing+distance_offset);
		out.printf("\nlock: %f",distance_offset);
//		if (Math.abs(distance_offset) < 3)
		{ 
			this.setFire(1);
		}
		return;
		// Replace the next line with any behavior you would like
//		double d = this.getGunHeading();
//		double f = this.getRadarHeading();
//		double g = Math.abs(d-f);
//		double h = this.getGunTurnRemaining();
//		double i = this.getRadarTurnRemaining();
//		if (g > 2)
//		{
//			this.setTurnRadarLeft(i);
//			this.setTurnGunLeft(d-f);
//			this.setTurnRadarRight(d-f);
////			activeRadar = true;
//		}
//		else {
////			activeRadar = false;
//			fire(3);
//		}
		//this.setTurnRadarLeft(this.getRadarTurnRemaining());
////		
//		scan();
//		return;
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
//		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//back(20);
	}	
}
