package shotbot.math;

import shotbot.data.CarData;

public class SteerUtils {

	public static final double DELTA_TIME = 1 / 120; // gameticks per second

	private static final double VMAX = 1234; // minimum forward velocity with full steer after 7.5 sec (TAU)
	private static final double TAU = 0.74704;
	
	private static final Vec3 GRAVITY = new Vec3(0, 0, -650); // 
	private static final Vec3 JUMP_FORCE = new Vec3(0, 0, 292); // local upwards force for nonflip jumps, instant up to 0.2s 
	private static final Vec3 STICKY_FORCE = new Vec3(0, 0, -325); // local downwards force makes wheels stick to surfaces

	public static double sign(double v) {
		return v >= 0 ? 1 : -1;
	}

	public static double cap(double v, double min, double max) {
		return (Math.min(Math.max(v, min), max));
	}
	
	public static Vec3 clamp(Vec3 direction, Vec3 start, Vec3 end) {
		Vec3 down = new Vec3(0, 0, -1);
		boolean isRight = direction.dot(end.cross(down)) < 0;
		boolean isLeft = direction.dot(start.cross(down)) > 0;
		
		if((end.dot(start.cross(down)) < 0) ? (isRight && isLeft) : (isRight || isLeft)) 
			return direction;
		
		if(start.dot(direction) < end.dot(direction))
			return end;
		
		return start;
	}
	
	public static double throttleAcceleration(double velocity) {
		if(0 <= velocity && velocity < 1400) 
			return 1600;
		if(1400 <= velocity && velocity < 1410)
			return 160;
		if(1410 <= velocity)
			return 0;
		
		return 0;
	}
	
	public static double turnRadius(double velocity) {
		return velocity == 0 ? 0 : 1.0 / curvature(velocity); 
	}

	private static double curvature(double velocity) {
		if (0 <= velocity && velocity < 500)
			return 0.0069 - 5.84e-6 * velocity;
		if (500 <= velocity && velocity < 1000)
			return 0.00561 - 3.26e-6 * velocity;
		if (1000 <= velocity && velocity < 1500)
			return 0.0043 - 1.95e-6 * velocity;
		if (1500 <= velocity && velocity < 1750)
			return 0.003025 - 1.1 - 6 * velocity;
		if (1750 <= velocity && velocity < 2500)
			return 0.0018 - 4e-7 * velocity;

		return 0;
	}
	
	public static double SpeedFromTimeTurning(double time) {
		// Turntime is always 0.775 second for 90 degree turn, 180 in 1.55s, 360 in 3.1s
		return VMAX * (1 - Math.exp(-(time / TAU))); 
	}

	public static double TurnTimeFromSpeed(double speed) {
		double speedAdj = -(speed / VMAX - 1);
		return Math.log(speedAdj) * TAU;
	}
	

	public static Vec3 fallPosition(Vec3 position, Vec3 velocity, double time) {
		return GRAVITY.scaled(Math.pow(time, 2)).scaled(0.5).plus(velocity.scaled(time)).plus(position);
	}

	public static Vec3 fallVelocity(Vec3 velocity, double time) {
		return GRAVITY.scaled(time).plus(velocity);
	}

}
