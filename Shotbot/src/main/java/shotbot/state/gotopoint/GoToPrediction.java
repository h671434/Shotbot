package shotbot.state.gotopoint;

import java.awt.Color;

import shotbot.data.BallData;
import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.PredictionData;
import shotbot.math.Vec3;
import shotbot.mechanics.Aerial;
import shotbot.mechanics.Dodge;
import shotbot.mechanics.DriftTurn;
import shotbot.mechanics.Drive;
import shotbot.mechanics.HalfFlip;
import shotbot.mechanics.WaveDash;
import shotbot.state.State;

/**
 * Similar to GoTo, except it plans a path to reach target position at a
 * certain point in time at a target velocity.
 * Car will try to face the direction of target velocity at target time;
 */
public class GoToPrediction extends State {

	protected PredictionData target;
	protected double startTime;
	
	protected Drive drive = null;
	protected Dodge dodge = null;
	protected HalfFlip halfflip = null;
	protected WaveDash wavedash = null;
	protected DriftTurn driftturn = null;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		double currentTime = data.time - startTime;
		double remainingTime = target.time - currentTime;
		
		if(remainingTime < 0)
			return null;
		
		// Vector pointing from car to target position
		Vec3 carToTarget = target.position.minus(data.car.position).flatten();
		
		double dist = carToTarget.mag();
		double targetSpeed = dist / remainingTime;
		
		// Vector pointing in the direction we wanna move through the target
		Vec3 targetDirection = target.velocity.normalized();
		
		Vec3 directionToTarget = target.position.plus(targetDirection.scaled(1000));
		
		data.bot.renderer.drawLine3d(Color.RED, data.car.position, target.position);
		data.bot.renderer.drawLine3d(Color.RED, data.car.position, directionToTarget);
		data.bot.renderer.drawLine3d(Color.RED, target.position, directionToTarget);
		
		ControlsOutput controls = new ControlsOutput();
		
		if(!data.car.hasWheelContact)
			controls = Aerial.recover(data, target.position);
		else
			controls = Drive.driveTowards(data, directionToTarget, targetSpeed);	
		
		return controls;
	}

	@Override
	public boolean isViable(DataPacket data) {
		return false;
	}

}
