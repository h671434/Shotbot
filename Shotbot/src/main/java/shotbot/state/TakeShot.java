package shotbot.state;

import java.awt.Color;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.FieldData;
import shotbot.data.PredictionData;
import shotbot.math.SteerUtils;
import shotbot.math.Vec3;
import shotbot.mechanics.Drive;

public class TakeShot extends State{

	PredictionData prediction = null;
	double startTime = 0;
	Vec3 target;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		if(prediction == null) {
			prediction = PredictionData.getPredictionAt(data, 240);
			startTime = data.time;
		} else if(data.time - startTime > prediction.time + 1) {
			return null;
		}
		
		data.bot.renderer.drawStateString("TAKE SHOT");
		
		Vec3 ball = prediction.position;
		
		// Vectors poiting from car to ball and from ball to goal edges
		Vec3 carToBall = ball.minus(data.car.position);
		Vec3 ballToLeftPost = FieldData.getGoalLeft(1 - data.team).minus(ball);
		Vec3 ballToRightPost = FieldData.getGoalRight(1 - data.team).minus(ball);
		
		// Find the closest direction to hit ball towards goal
		Vec3 carToBallDirection = carToBall.normalized();
		Vec3 ballToLeftPostDirection = ballToLeftPost.normalized(); 
		Vec3 ballToRightPostDirection = ballToRightPost.normalized();
		Vec3 targetDirection = SteerUtils.clamp(
				carToBallDirection,
				ballToRightPostDirection, 
				ballToLeftPostDirection);
		
		// Offset ball by radius in target direction to find postion to intercept
		Vec3 offset = targetDirection.scaled(95);
		Vec3 finalTarget = ball.minus(offset);
		
		target = finalTarget;
		
		// Find which side of the direction path we are on
		double sideOfDirection = 
				((ball.x - finalTarget.x)*(data.car.position.y - finalTarget.y) 
				- (ball.y - finalTarget.y)*(data.car.position.x - finalTarget.x));
		
		if(Math.abs(sideOfDirection) > 600)
			target = ball.minus(targetDirection.scaled(carToBall.mag() - 250));
		
		// Target speed
		double remainingTime = prediction.time - (data.time - startTime);
		double targetSpeed = carToBall.mag() / remainingTime;
		
		Vec3 ballToDirection = ball.plus(targetDirection.scaledToMag(1000));
		data.bot.renderer.drawLine3d(Color.GRAY, ball, ballToDirection);
		data.bot.renderer.drawLine3d(Color.RED, ball, ball.minus(targetDirection.scaled(carToBall.mag())));
		data.bot.renderer.drawLine3d(Color.RED, data.car.position, target);
		data.bot.renderer.drawBallPrediction(Color.GREEN, 4);
		
		ControlsOutput controls = Drive.driveTowards(data, target, targetSpeed);
		return controls;
	}

	@Override
	public boolean isViable(DataPacket data) {
		return true;
	}

}
