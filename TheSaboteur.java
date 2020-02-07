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
	 * run: EverlastEngineering: TheSaboteur
	 */
//	private boolean activeRadar = true;
	private double distanceToTarget = 0;
	private double bearingToTarget = 0;
	private double moveDirection = 1;
	
	public void run() {
		// Initialization of the robot should be put here
		Random rand = new Random(); 
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		 //setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		// Robot main loop
		int counter = 0;
		while(true) {
			if (this.getVelocity() == 0) {
				moveDirection = moveDirection * -1;
			}
//			out.printf("\n DistanceToTarget: %f",distanceToTarget);
//			out.printf("\n BearingToTarget: %f",bearingToTarget);
			if (getRadarTurnRemaining() == 0) {
//				go back to a 360 scan for more targets
				setTurnRadarRight(Double.POSITIVE_INFINITY);
			}
			counter ++;
//			setTurnRight(50);
			if (distanceToTarget > 200) // kamakazeeeee
			{
				double missBy = 0;
				if (counter < 20) {
					//go left
					missBy = -40;
				}
				else if (counter < 40){
					//go right
					missBy = 40;
				}
				else {
					counter =0;
				}
				out.printf("\n output: %f",bearingToTarget+missBy);
				
				setTurnRight(bearingToTarget+missBy);
				setAhead(20);
			}
			else if (distanceToTarget < 200 && distanceToTarget > 120){
				setTurnRight(bearingToTarget+90);
				setAhead(50*moveDirection);
			}
			else {
				setTurnRight(bearingToTarget);
				setBack(50*moveDirection);
			}
			scan();
//			setTurnRight(bearingToTarget);
//			setAhead(2);
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
		distanceToTarget = e.getDistance();
		bearingToTarget = e.getBearing();
//		
		double relational_difference = (distanceToTarget / 15);
		double distance_offset = cc ;//* relational_difference;
		this.setTurnGunRight(Utils.normalRelativeAngleDegrees(heading + bearing-gunBearing+distance_offset));
//		if (Math.abs(distance_offset) < 3)
		{ 
			this.setFire(3);
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
		//turnRight(120);
//		double a = Utils.normalRelativeAngleDegrees(e.getBearing());
//		turnLeft(a);
//		setAhead(200);
//		execute();
	}	
}
