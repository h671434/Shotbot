package shotbot.mechanics;

import shotbot.data.CarData;
import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.prediction.PredictionData;
import shotbot.math.Mat3x3;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class Aerial implements Mechanic {
	
	private Vec3 target;
	private double startTime;
	private boolean done = false;
	
	public Aerial(DataPacket data, Vec3 target) {
		this.target = target;
		startTime = data.time;
	}
	

	@Override
	public ControlsOutput exec(DataPacket data) {
		return null;
	}

	@Override
	public boolean done() {
		return done;
	} 
	
	/**
	 * Return controls that rotates car towards target. 
	 * Vec3 up is the direction to roll the roof of the car. If up = (0, 0, 1), the car turns upright.
	 */
	public static ControlsOutput align(DataPacket data, Vec3 localTarget, Vec3 localUp) {
		Mat3x3 orientation = data.car.orientation;
		Vec3 angularVelocity = data.car.angularVelocity;
		Vec3 localAngVel = orientation.dot(angularVelocity);
		
		Vec3 targetAngles = new Vec3(
				Math.atan2(localTarget.z, localTarget.x),
				Math.atan2(localTarget.y, localTarget.x),
				Math.atan2(localUp.x, localUp.z));
			
		double steer = pitchYawPD(targetAngles.y , 0); 
		double pitch = pitchYawPD(targetAngles.x, localAngVel.y * 0.2);
		double yaw = pitchYawPD(targetAngles.y, -localAngVel.z * 0.15);
		double roll = rollPD(targetAngles.z, localAngVel.x);
		
		data.bot.renderer.drawStateString("                 " + targetAngles.z);
		
		ControlsOutput controls = new ControlsOutput();	
		controls.withThrottle(1)
				.withSteer(steer)
				.withPitch(pitch)
				.withYaw(yaw)
				.withRoll(roll);
		
		return controls;
	}
	
	private static double pitchYawPD(double angle, double rate) {
		return MathUtils.cap((Math.pow(35*(angle+rate), 3)) / 10, -1.0, 1.0);
	}
	
	private static double rollPD(double angle, double angVelx) {
		double angVelNorm = angVelx / 5.5;
		double angleNorm = angle / (Math.PI);
		double deltaTime = DataPacket.DELTA_TIME;
		
		double Dr = CarData.ANGULAR_DRAG.z;
		double Tr = CarData.ANGULAR_TORQUE.z;
		
		double roll = Math.pow(angleNorm + (MathUtils.sign(angleNorm - angVelNorm) * Tr + Dr) * angVelNorm * deltaTime , 3) * 10;
		
		return MathUtils.cap(roll, -1.0, 1.0);
	}
	
	/*
	 * Returns controls to rotate car upright in direction of velocity
	 */
	public static ControlsOutput recover(DataPacket data, Vec3 target) {
		Vec3 velocity = data.car.velocity;
		
		if (target == null) {
			try {
				Vec3 flatVelocity = velocity.flatten(Vec3.UP).normalized();
				
				target = data.car.orientation.dot(flatVelocity);
			} catch (IllegalStateException e) {
				target = data.car.orientation.dot(data.ball.position.flatten(Vec3.UP).normalized());
			}
		}
		Vec3 localUp = data.car.orientation.local(Vec3.UP);
		
        data.bot.renderer.drawSteerString("AERIAL RECOVERING");
        data.bot.renderer.drawVectorStrings(new String[] {
        		"TARGET: " + target.toString(),
        		"CAR: " + data.car.position.toString()
        });
		
		return align(data, target, localUp);
	}
	
}
