package shotbot.controls;

import shotbot.data.CarData;
import shotbot.data.DataPacket;
import shotbot.math.Mat3x3;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;
import shotbot.mechanics.Mechanic;
import shotbot.prediction.PredictionData;

public class AerialControls extends ControlsOutput {

	private DataPacket data;
	private Vec3 localTarget;
	private Vec3 localUp;
	
	public AerialControls(DataPacket data,  Vec3 localTarget, Vec3 localUp) {
		this.data = data;
		this.localTarget = localTarget;
		this.localUp = localUp;
		getControls();
	}
	
	private void getControls() {
		align();
	}
	
	private void align() {
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
		
		withThrottle(1);
		withSteer(steer);
		withPitch(pitch);
		withYaw(yaw);
		withRoll(roll);
		
	}
	
	private static double pitchYawPD(double angle, double rate) {
		return MathUtils.cap((Math.pow(35*(angle+rate), 3)) / 10, -1.0, 1.0);
	}
	
	private static double rollPD(double angle, double angVelx) {
		double angVelNorm = angVelx / 5.5;
		double angleNorm = angle / (Math.PI);
		double deltaTime = MathUtils.DELTA_TIME;
		
		double Dr = CarData.ANGULAR_DRAG.z;
		double Tr = CarData.ANGULAR_TORQUE.z;
		
		double roll = Math.pow(angleNorm + (MathUtils.sign(angleNorm - angVelNorm) * Tr + Dr) * angVelNorm * deltaTime , 3) * 10;
		
		return MathUtils.cap(roll, -1.0, 1.0);
	}
	
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
		
		return new AerialControls(data, target, localUp);
	}
	
}
