package shotbot.mechanics;

import java.awt.Color;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.prediction.PredictionData;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class Drive implements Mechanic{
	
	private static final double VMAX = 1234; 
	private static final double TAU = 0.74704;
	
	private PredictionData target;
	private double startTime;
	private boolean done = false;
	
	public Drive(DataPacket data, PredictionData target) {
		this.target = target;
		this.startTime = data.time;
	}
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		double currentTime = data.time - startTime;
		double remainingTime = target.time - currentTime;
		
		double dist = target.position.minus(data.car.position).mag();
		double targetSpeed = dist / remainingTime;
		
		ControlsOutput controls = new ControlsOutput();
		controls = driveTowards(data, target.position, targetSpeed);
		
        data.bot.renderer.drawLine3d(Color.RED, data.car.position, target.position);
        
		done = remainingTime <= 0;
		
		data.bot.renderer.drawManeuverString("Drive   " + remainingTime);
		
		return controls;
	}

	@Override
	public boolean done() {
		return done;
	}
	
	/**
	 * Returns controls that steers towards target and throttles and boost to reach/maintain targetspeed.
	 */
	public static ControlsOutput driveTowards(DataPacket data, Vec3 target, double targetSpeed) {
		
		ControlsOutput controls = new ControlsOutput();
		
        Vec3 carToTarget = target.minus(data.car.position);
        Vec3 localTarget = data.car.orientation.local(target, data.car.position);
		
        // Controll speed
        double currentSpeed = data.car.velocity.dot(carToTarget.normalized());
        if (currentSpeed < targetSpeed) {
            // We need to speed up
            controls.withThrottle(1.0);
            if (targetSpeed > 1410 && currentSpeed + 60 < targetSpeed &&
                    data.car.orientation.forward.dot(carToTarget.normalized()) > 0.8) {
                controls.withBoost(true);
            }
        } else {
            // We are going too fast
        	double extraSpeed = currentSpeed - targetSpeed;
        	controls.withThrottle(0.3 - extraSpeed / 500);
        }
		
        // Steer towards target;
        controls.withSteer(localTarget.normalized().y * 5);
        
		if(data.car.hasWheelContact && !data.car.isUpright) 
			 controls.withSteer(localTarget.normalized().x * 5);
        
        data.bot.renderer.drawSteerString("DRIVING");
        data.bot.renderer.drawVectorStrings(new String[] {
        		"TARGET: " + target.toString(),
        		"CAR: " + data.car.position.toString()
        });
        
        return controls;
	}
	
	private static double throttleAcceleration(double velocity) {
		if(0 <= velocity && velocity < 1400) 
			return 1600;
		if(1400 <= velocity && velocity < 1410)
			return 160;
		if(1410 <= velocity)
			return 0;
		
		return 0;
	}
	
	private static double turnRadius(double velocity) {
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
	
	private static double SpeedFromTimeTurning(double time) {
		return VMAX * (1 - Math.exp(-(time / TAU))); 
	}

	// Turntime is always 0.775 second for 90 degree turn, 180 in 1.55s, 360 in 3.1s
	private static double TurnTimeFromSpeed(double speed) {
		double speedAdj = -(speed / VMAX - 1);
		return Math.log(speedAdj) * TAU;
	}
	
}
