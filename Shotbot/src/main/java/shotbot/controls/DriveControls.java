package shotbot.controls;

import java.awt.Color;

import shotbot.data.CarData;
import shotbot.data.DataPacket;
import shotbot.math.Vec3;
import shotbot.mechanics.Mechanic;

public class DriveControls extends ControlsOutput{
	
	private DataPacket data;
	private	Vec3 target;
	private double targetSpeed;
	
	public DriveControls(DataPacket data, Vec3 target, double targetSpeed) {
		this.data = data;
		this.target = target;
		this.targetSpeed = targetSpeed;
		getControls();
	}
	
	private void getControls() {
		controlSpeed();
		steerTowards();
		
        data.bot.renderer.drawLine3d(Color.RED, data.car.position, target);
		data.bot.renderer.drawManeuverString("DRIVE");
        data.bot.renderer.drawVectorStrings(new String[] {
        		"TARGET: " + target.toString(),
        		"CAR: " + data.car.position.toString()
        });
	}
	
	private void controlSpeed() {
        Vec3 carToTarget = target.minus(data.car.position);
        
        double currentSpeed = data.car.velocity.dot(carToTarget.normalized());
        if (currentSpeed < targetSpeed) {
            // We need to speed up
            withThrottle(1.0);
            if (targetSpeed > 1410 && currentSpeed + 60 < targetSpeed &&
                    data.car.orientation.forward.dot(carToTarget.normalized()) > 0.8) {
               withBoost(true);
            }
        } else {
            // We are going too fast
        	double extraSpeed = currentSpeed - targetSpeed;
        	withThrottle(0.3 - extraSpeed / 500);
        }
	}

	private void steerTowards() {
        Vec3 localTarget = data.car.orientation.local(target, data.car.position);
        
		if(data.car.hasWheelContact && !data.car.isUpright) 
			withSteer(localTarget.normalized().x * 5);
		else
			withSteer(localTarget.normalized().y * 5);
	}
}
