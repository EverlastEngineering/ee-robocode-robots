package ee;
import robocode.*;
import robocode.util.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * EverlastEngineeringTheSaboteur - a robot by (your name here)
 */
public class TheSaboteur extends AdvancedRobot
{

	/**
	 * run: EverlastEngineering: TheSaboteur
	 */
	
	final private int weaveWidth = 40;
	final private double bulletStrength = 3;
	final private boolean shouldMove = true;
	
	private double distanceToTarget = 0;
	private double bearingToTarget = 0;
	private double moveDirection = 1;
	private boolean activeRadar = false;
	private boolean firstScan = true;
	private double previous = 0;
	private double previousBearingToTarget = 0;
	private double previousDistance = 0;
	private String target = "";
	private double closestTargetDistance = 100000;
	private String closestTarget = "";
	private int numberOfTanks = 0;
	private String tankToTarget = "";
	private boolean tickSinceLastScan = false;

	
	public void run() {
		// Initialization of the robot should be put here
		Random rand = new Random(); 
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		setColors(Color.black,Color.red,Color.black); // body,gun,radar
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		// face direction for testing
//		this.setTurnLeft(this.getHeading()+90);
		
		// Robot main loop
		int weaveCounter = 0;
		setTurnRadarRight(10360);
		execute();

		while(true) {
			tickSinceLastScan = true;
			if (this.getVelocity() == 0) {
				moveDirection = moveDirection * -1;
			}

			if (firstScan) {
				if (this.getRadarTurnRemaining() < 10000) {
					out.printf("Enemies: %d\n", numberOfTanks);
					out.printf("First target: %s\n", tankToTarget);
					firstScan = false;
				}
			}
			
			if (getRadarTurnRemaining() == 0 && !activeRadar) {
//				go back to a 360 scan for more targets
				setTurnRadarRight(10360);
			}
			
			if (getRadarTurnRemaining() > 100) {
				distanceToTarget = 10000;
				bearingToTarget = 0;
			}
			
			//TODO: Search pattern if no targets are found
			
			if (shouldMove) {
				weaveCounter ++;
				if (distanceToTarget > 200) // kamakazeeeee! If they're far away, then weave 
				{
					double missBy = 0; // setup for a weave pattern
					if (weaveCounter < weaveWidth/2) { 
						//weave left
						missBy = -weaveWidth;
					}
					else if (weaveCounter < weaveWidth){
						//weave right
						missBy = weaveWidth;
					}
					else {
						weaveCounter = 0;
						missBy = weaveWidth;
					}
					setTurnRight(bearingToTarget+missBy);
					setAhead(50);
				}
				else if (distanceToTarget < 200 && distanceToTarget > 120){
					setTurnRight(bearingToTarget+90);
					setAhead(50*moveDirection);
				}
				else {
					setTurnRight(bearingToTarget);
					setBack(50*Math.abs(moveDirection));
				}
			}
//			scan();
//			setTurnRight(0);
//			setAhead(0);
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	
	public void onScannedRobot(ScannedRobotEvent e) {
		if (target != e.getName()) {
			//out.printf("Scanned: %s\n", e.getName());
		}
		target = e.getName();
		if (firstScan) {
			out.printf("Enemy: %s Distance: %f\n",target, e.getDistance());
			if (e.getDistance() < closestTargetDistance)
			{
				tankToTarget = target;
				closestTargetDistance = e.getDistance();
			}
			numberOfTanks++;
			return;
		}
		if (tankToTarget != "" && target != tankToTarget) {
			out.printf("Re-acquiring for target: %s\n", tankToTarget);
			if (!activeRadar) {
				setTurnRadarRight(10360);
				activeRadar = true;
				return;
			}
			if (this.getRadarTurnRemaining() > 10000) {
				scan();
				return;
			}
		}
		activeRadar = false;
		if (!tickSinceLastScan) {
			return;
		}
		tankToTarget = "";
		double gunBearing = getGunHeading();
		double heading = getHeading();
		double radarHeading = getRadarHeading();
				
		distanceToTarget = e.getDistance(); //relative to my robot //d
		bearingToTarget = e.getBearing(); //relative to my robots head
		double absoluteTargetBearing = heading+bearingToTarget; //a
		double targetVelocity = e.getVelocity(); //max 8 //s
		double targetHeading = e.getHeading(); //north 0 //b

		
//		double velocityToTarget = distanceToTarget - previousDistance;
//		double bearingDeltaToTarget = (heading+bearingToTarget)-previousBearingToTarget;
		double bulletSpeed = Rules.getBulletSpeed(bulletStrength);
//		double mySpeed = this.getVelocity();
		
//		Given two sides a, b and angle C. Find the third side of the triangle using law of cosines.
//		Input : a = 5, b = 8, C = 49 
//		Output : 6.04339
//		c = sqrt(a^2 + b^2 - 2*a*b*cos(C))
//		calculate the distance the bullet has to travel (side c)
		double a = distanceToTarget;
		double bestGuess = distanceToTarget;
		double B = 0;
		boolean reversed = false;
		for (int i=0;i<5;i++) {
			double b = bestGuess/bulletSpeed*targetVelocity;
			double C = 180 - (360 - absoluteTargetBearing) - targetHeading;
//			if (C > 180) {
//				C -= 180;
//			}
//			else if (C < -180) {
//				C += 180;
//			}
			C = Utils.normalRelativeAngleDegrees(C);
	//		c2=a2+b2−2abcosγ
			double c = Math.sqrt(Math.pow(a,2) + Math.pow(b,2) - (2*a*b*Math.cos(C*Math.PI/180)));
			// next find the B angle
			//cos B = (a2 + c2 − b2)/2ac
	
			B = Math.acos((Math.pow(a, 2) + Math.pow(c,2) - Math.pow(b,2))/(2*a*c))*180/Math.PI;
			if (C < 0) B = B * -1;
			bestGuess = c;
		}
		
		//TODO: determine if calculated trajectory would be outside the bounds of the playing field 
		
		//calculate target position
//		double absoluteBearing = heading+bearingToTarget-90;
//		if (absoluteBearing < 0) absoluteBearing += 360; // convert to 0 degrees east
//		double absoluteBearingRadians = (absoluteBearing)*Math.PI/180;
//		double myX = getX();
//		double myY = this.getBattleFieldHeight()-getY(); // getY returns 0,0 as the BOTTOM LEFT FFS LOL
//		double xx = myX + distanceToTarget * Math.cos(absoluteBearingRadians);
//		double yy = myY + distanceToTarget * Math.sin(absoluteBearingRadians);
		
//		out.printf("\n target position: %f %f", xx, yy);
		
		double radarResetTo = 1.5 * Utils.normalRelativeAngleDegrees(heading + bearingToTarget - radarHeading);
		setTurnRadarRight(radarResetTo);
		 

//		double relational_difference = distanceToTarget / 60; 
		
//		out.printf("\n distanceToTarget: %f",relational_difference);
//		out.printf(" cc: %f",cc);
//		double distance_offset = ((cc+previous)/2) * relational_difference;
//		out.printf(" distance_offset: %f",distance_offset);
//		out.printf(" unknown: %f",this.getGunTurnRemaining());
		double turnGun = Utils.normalRelativeAngleDegrees(bearingToTarget-gunBearing+heading+B);

		
		//		double delta = bearingDelta.average();
//		this.setTurnGunRight(Math.abs(turnGun)>5?turnGun:turnGun+(delta*3));
		this.setTurnGunRight(turnGun);
		
		//if we're within x degrees of the target's bearing or really close, shoot
		if (Math.abs(turnGun) < 5 || (distanceToTarget < 100 && Math.abs(turnGun) < 40))
		{ 
			this.setFire(bulletStrength);
		}
//		else 
//		{
//			out.printf("turnGun: %f\n", turnGun);
//		}
		
		
		
//		previous = cc;
		previousDistance = distanceToTarget;
		previousBearingToTarget = (heading+bearingToTarget);
		tickSinceLastScan = false;
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
//		back(10);
		tankToTarget = e.getName();
		out.printf("Hit by: %s\n", tankToTarget);
//		double turnGun = Utils.normalRelativeAngleDegrees(e.getBearing()-this.getGunHeading()+this.getHeading());
//		this.setTurnGunRight(turnGun);
	}
	
	public void onHitRobot(HitRobotEvent e) 
	{
		tankToTarget = e.getName();
		out.printf("Hit by: %s\n", tankToTarget);
//		double turnGun = Utils.normalRelativeAngleDegrees(e.getBearing()-this.getGunHeading()+this.getHeading());
//		this.setTurnGunRight(turnGun);
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
	
	public class AveragedArray {
		private ArrayList<Double> arrayList = new ArrayList<Double>();
		private int maxSampleSize;
		public AveragedArray(int _maxSampleSize) {
			maxSampleSize = _maxSampleSize; 
		}
		public void add(double d) {
			if (arrayList.size() == maxSampleSize) {
				arrayList.remove(0);
			}
			arrayList.add(d);
		}
		public double average() {
			double _average;
			double _total = 0;
			for (int i = 0; i<arrayList.size(); i++)
			{
				_total += arrayList.get(i);
			}
			_average = _total / arrayList.size();
			return _average;
		}
		public int getMaxSampleSize() {
			return maxSampleSize;
		}
	}
}
