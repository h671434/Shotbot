package shotbot.data;

import shotbot.math.Mat3x3;
import shotbot.math.Vec3;

public class CarData {
	
	public static final double MAX_VELOCITY = 2300;
	public static final double MAX_THROTTLE_VELOCITY = 1410; // forward and backward with no boost
	public static final double SUPERSONIC_TRESHOLD = 2200;
	public static final double AIR_THROTTLE_ACCELERATION = 66.667; // per second ^2
	public static final double BOOST_ACCELERATION = 991.666; // per second ^2 + Throttle acceleration
	public static final double BOOST_CONSUMPTION = 33.3; // per second
	public static final double BRAKE_ACCELERATION = -3500; // per second ^2 for any amount of braking
	public static final double NO_THROTTLE_ACCELERATION = -525; // per second ^2 when throttle is zero
	
	public static final double MAX_ANGULAR_VELOCITY = 5.5;
	public static final Vec3 ANGULAR_ACCELERATION = new Vec3(9.11, 12.46, 37.34); // pitch, yaw, roll per second ^2 
	public static final Vec3 ANGULAR_TORQUE = new Vec3(-12.15, 8.92, -36.08); // pitch, yaw, roll torque coeffiction
	public static final Vec3 ANGULAR_DRAG = new Vec3(-2.80, -1.89, -4.47); // pitch, yaw, roll drag coeffiction
	
	public final Vec3 position; // 0, 0, 0 is centerfield
	public final Vec3 velocity;
	
	public final double roll;
	public final double yaw;
	public final double pitch;
	public final Mat3x3 orientation;
	public final Vec3 angularVelocity;

	public final double boost;
	public final boolean hasWheelContact;
	public final boolean isSupersonic;
	public final boolean isUpright;

	public final int team; 	// 0 for blue team, 1 for orange team.
	public final float elapsedSeconds;

	public final Vec3 hitboxCenter;
	public final Vec3 hitboxSize;

	public CarData(rlbot.flat.PlayerInfo playerInfo, float elapsedSeconds) {
		this.position = new Vec3(playerInfo.physics().location());
		this.velocity = new Vec3(playerInfo.physics().velocity());
		
		this.roll = playerInfo.physics().rotation().roll();
		this.yaw = playerInfo.physics().rotation().yaw();
		this.pitch = playerInfo.physics().rotation().pitch();
		this.orientation = Mat3x3.eulerToRotation(playerInfo.physics().rotation().pitch(),
				playerInfo.physics().rotation().yaw(),
				playerInfo.physics().rotation().roll());
		this.angularVelocity = new Vec3(playerInfo.physics().angularVelocity());
		
		this.boost = playerInfo.boost();
		this.isSupersonic = playerInfo.isSupersonic();
		this.team = playerInfo.team();
		this.hasWheelContact = playerInfo.hasWheelContact();
		this.elapsedSeconds = elapsedSeconds;
		this.isUpright = orientation.up.dot(Vec3.UP) > 0.5;

		this.hitboxCenter = position.plus(orientation.local(new Vec3(playerInfo.hitboxOffset())));
		this.hitboxSize = new Vec3(
				playerInfo.hitbox().length(),
				playerInfo.hitbox().width(),
				playerInfo.hitbox().height());
	}
	
}
