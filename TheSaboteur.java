package ee;
import robocode.*;
import robocode.util.Utils;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * EverlastEngineeringTheSaboteur - a robot by (your name here)
 */
public class TheSaboteur extends AdvancedRobot
{

	/**
	 * run: EverlastEngineering: TheSaboteur
	 */
	
	private double bulletStrength = 3;
	final private boolean shouldMove = true;
	final private boolean shouldFire = false;
	
	private double distanceToTarget = 0;
	private double bearingToTarget = 0;
	private double moveDirection = 1;
	private boolean activeRadar = false;
	private boolean firstScan = true;
//	private double previousBearingToTarget = 0;
//	private double previousDistance = 0;
	private String scannedTarget = "";
	private double closestTargetDistance = 100000;
//	private String closestTarget = "";
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
			//TODO: determine distance to wall, avoid bumping
			//TODO: determine if getting outta dodge (which will gentle turn while reversing) is going to turn into a wall. if so, go the other way to avoid entrapment
			
			
			
			
			
			//debug(this.getTurnRemaining());
			if (shouldMove) {
				weaveCounter ++;
				if (distanceToTarget > 200) // kamakazeeeee! If they're far away, then weave 
				{
					double missBy = 0; // setup for a weave pattern
					if (weaveCounter < 30) { 
						//weave left
						missBy = -40;
					}
					else if (weaveCounter < 60){
						//weave right
						missBy = 40;
					}
					else {
						weaveCounter = 0;
						missBy = 40;
					}
					setTurnRight(bearingToTarget+missBy);
					setAhead(50);
				}
				else if (distanceToTarget < 200 && distanceToTarget > 120){
					setTurnRight(bearingToTarget+90);
					setAhead(50*moveDirection);
				}
				else {
					//get outta dodge!
//					setTurnRight(bearingToTarget);
					setTurnRight(bearingToTarget-(90));
					setBack(50);
				}
			}
//			scan();
//			setTurnRight(0);
//			setAhead(0);
			setMaxVelocity(8);
			int closeness = 100;
			if (getX()<closeness) {
				debug("close to left");
			}
			else if (getY()<closeness) {
				debug("close to bottom");
				if (this.getHeading() > 200 && this.getHeading() < 270) {
					this.setTurnRight(50);
					setMaxVelocity(1);
				}
				else if (this.getHeading() < 200 && this.getHeading() > 90) {
					this.setTurnLeft(50);
					setMaxVelocity(1);
				}
			}
			else if (getX() > this.getBattleFieldWidth()-closeness) {
				debug("close to right");
			}
			else if (getY() > this.getBattleFieldHeight()-closeness) {
				debug("close to top");
				if (this.getHeading() > 20 && this.getHeading() < 90) {
					setMaxVelocity(1);
					this.setTurnRight(50);
				}
				else if (this.getHeading() < 20 && this.getHeading() > 270) {
					setMaxVelocity(1);
					this.setTurnLeft(50);
				}
				else if (this.getHeading() > 200 && this.getHeading() < 270) {
					setMaxVelocity(1);
					this.setTurnLeft(50);
				}
				else if (this.getHeading() < 00 && this.getHeading() > 90) {
					setMaxVelocity(1);
					this.setTurnRight(50);
				}
			}
			
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	
	private int targetPositionX = 0;
	private int targetPositionY = 0;
	private double targetLeadX = 0;
	private double targetLeadY = 0;
	
	public void onScannedRobot(ScannedRobotEvent e) {
//		if (currentTarget != e.getName()) {
//			//out.printf("Scanned: %s\n", e.getName());
//		}
		scannedTarget = e.getName();
		
		if (firstScan) {
			// this is run on the first 360 degree sweep, used to find the closest target
			out.printf("Enemy: %s Distance: %f\n",scannedTarget, e.getDistance());
			if (e.getDistance() < closestTargetDistance)
			{
				tankToTarget = scannedTarget;
				closestTargetDistance = e.getDistance();
			}
			numberOfTanks++;
			return;
		}
		if (tankToTarget != "" && scannedTarget != tankToTarget) {
			// there is a target we wish to target. if this isn't the currently scannedTarget, sweep for it once
			out.printf("Re-acquiring for target: %s\n", tankToTarget);
			if (!activeRadar) {
				setTurnRadarRight(10360);
				activeRadar = true; //prevent the main() from resetting the sweep
				return;
			}
			if (this.getRadarTurnRemaining() > 10000) {
				scan();
				return;
			}
		}
		activeRadar = false;
		if (!tickSinceLastScan) {
			// there has been not been a tick of the main while() loop since the last scan, then we're likely
			// looping through more targets in the radar's view, even though we already
			// have target lock on the one we want. so, abort this scan and continue focusing on the current target
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
		double bestGuessCalculatedDistanceToFutureTarget = distanceToTarget;
		double leadFiringAngle = 0;
		double proposedDistanceToTarget = 0;
//		boolean reversed = false;
//		Calculate a triangle based on the target's angle and velocity, bulletspeed, etc, to determine its likely location
//		The "long" side of the triangle won't account for how much longer (or shorter) the time the bullet will take to arrive
//		I'm sure there is smart math to anticipate this, but i found if I loop the calculation with my best guess, after
//		five loops the margin of error is well below 1 pixel.
		double targetAngle = 0;
		for (int i=0;i<5;i++) {
			proposedDistanceToTarget = bestGuessCalculatedDistanceToFutureTarget/bulletSpeed*targetVelocity;
			targetAngle = 180 - (360 - absoluteTargetBearing) - targetHeading;
//			if (C > 180) {
//				C -= 180;
//			}
//			else if (C < -180) {
//				C += 180;
//			}
			targetAngle = Utils.normalRelativeAngleDegrees(targetAngle);
//			c2=a2+b2−2abcosγ - determine length of 3rd side of triangle using SAS
//			remember cos and acos use RADIANS, so convert degrees to radians by degrees*pi/180
			double calculatedDistanceToFutureTarget = Math.sqrt(Math.pow(distanceToTarget,2) + Math.pow(proposedDistanceToTarget,2) - (2*distanceToTarget*proposedDistanceToTarget*Math.cos(targetAngle*Math.PI/180)));
//			next find the B angle using SS
//			cos B = (a2 + c2 − b2)/2ac
//			remember cos and acos use RADIANS, so convert acos BACK to degrees with radians/pi*180	
			leadFiringAngle = Math.acos((Math.pow(distanceToTarget, 2) + Math.pow(calculatedDistanceToFutureTarget,2) - Math.pow(proposedDistanceToTarget,2))/(2*distanceToTarget*calculatedDistanceToFutureTarget))*180/Math.PI;
			if (targetAngle < 0) leadFiringAngle = leadFiringAngle * -1; 
			bestGuessCalculatedDistanceToFutureTarget = calculatedDistanceToFutureTarget;
		}
		
		
		
		//TODO: determine if calculated trajectory would be outside the bounds of the playing field 
		
//		calculate target position
		double absoluteBearing = heading+bearingToTarget;
		if (absoluteBearing < 0) absoluteBearing += 360; 
		double absoluteBearingRadians = (absoluteBearing)*Math.PI/180;
		targetPositionX = (int) Math.round(getX() + distanceToTarget * Math.sin(absoluteBearingRadians));
		targetPositionY = (int) Math.round(getY() + distanceToTarget * Math.cos(absoluteBearingRadians));
		
		//calculate target lead position
		double targetTravel = bestGuessCalculatedDistanceToFutureTarget/bulletSpeed*targetVelocity; // distance target will travel
		double targetDirection = convertDegreesFromCompassToGraph(e.getHeading()); // converts angle from being 0 north clockwise to 0 east counter-clockwise
		targetLeadX = targetPositionX + (targetTravel * Math.cos(Math.toRadians(targetDirection)));
		targetLeadY = targetPositionY + (targetTravel * Math.sin(Math.toRadians(targetDirection)));
		
				
//		debug(String.format("\n target position: %f %f", targetLeadX, targetLeadY));
		
		double radarResetTo = 1 * Utils.normalRelativeAngleDegrees(heading + bearingToTarget - radarHeading);
		setTurnRadarRight(radarResetTo);

//		double relational_difference = distanceToTarget / 60; 
//		out.printf("\n distanceToTarget: %f",relational_difference);
//		out.printf(" cc: %f",cc);
//		double distance_offset = ((cc+previous)/2) * relational_difference;
//		out.printf(" distance_offset: %f",distance_offset);
//		out.printf(" unknown: %f",this.getGunTurnRemaining());
		
		double turnGun = Utils.normalRelativeAngleDegrees(bearingToTarget-gunBearing+heading+leadFiringAngle);
			
//		out.printf("turnGun: %f\n", turnGun);
		
		this.setTurnGunRight(turnGun);

//		if (targetLeadX > 0 && targetLeadX < this.getBattleFieldWidth() && targetLeadY > 0 && targetLeadY < this.getBattleFieldHeight())
//		{

//		}

		if (Math.abs(turnGun) < 5 || (distanceToTarget < 100 && Math.abs(turnGun) < 20))
			{ //if we're within x degrees of the target's bearing or really close, shoot
				if (shouldFire) this.setFire(bulletStrength);
			}
		tickSinceLastScan = false;
	}
	

	private double convertDegreesFromCompassToGraph(double a) {
		double hmm = (a-90);
		if (hmm < 0) hmm +=360;
		hmm = hmm * -1;
		return hmm;
	}
	
	private void debug(String ds) {
		debugString += "\n" + ds;
	}
	private void debug(double d) {
		debug(String.format("%f", d));
	}
	private void debug(int i) {
		debug((double)i);
	}
	
	private String debugString = ""; 
	public void onPaint(Graphics2D g) {
	    g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
	 
//	    g.drawLine((int)targetLeadX, (int)targetLeadY, (int)getX(), (int)getY());
	    
//	    g.drawLine((int)this.getX(),(int)this.getY(), (int)targetLeadX,(int)targetLeadY);
	 
	    // Draw a filled square on top of the scanned robot that covers it
	    g.fillRect(targetPositionX - 20, targetPositionY - 20, 40, 40);
//	    debug(String.format("Target Lead Point: %03d x %03d y", (int)targetLeadX,(int)targetLeadY));
	    
	    g.setColor(new Color(0xDD, 0xDD, 0xDD, 0xDD));
	    int y=0;
	    for (String line : debugString.split("\n")) {
	    		g.drawString(line, 10, (int) Math.round(this.getBattleFieldHeight()-10)+y);
	    		y -= 15;
	    }
	    debugString = "";
	    
	    Ellipse2D.Double hole = new Ellipse2D.Double();
	    hole.width = 28;
	    hole.height = 28;
	    hole.x = (int)targetLeadX;
	    hole.y = (int)targetLeadY;
	    g.draw(hole);
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
