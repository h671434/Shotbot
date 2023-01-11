package shotbot.mechanics;

import java.awt.Color;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.prediction.PredictionData;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class Drive implements Mechanic {
	
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
	
}
