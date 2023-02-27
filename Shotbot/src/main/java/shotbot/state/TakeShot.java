package shotbot.state;

import java.awt.Color;

import shotbot.controls.AerialControls;
import shotbot.controls.ControlsOutput;
import shotbot.controls.DriveControls;
import shotbot.data.DataPacket;
import shotbot.data.FieldData;
import shotbot.math.Mat3x3;
import shotbot.math.Vec3;
import shotbot.prediction.PredictionData;
import shotbot.prediction.SteerPrediction;

public class TakeShot extends State{

	private PredictionData prediction = null;
	private double startTime = 0;
	
	private Vec3 target = null;
	private double targetSpeed = 1400;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		if(prediction == null) {
			prediction = PredictionData.nextReachable(data, 10);
			startTime = data.time;
		} else if(data.time - startTime > prediction.time + 1) {
			return null;
		} 
		
		double remainingTime = prediction.time - (data.time - startTime);
		
		Vec3 ball = prediction.position;
		
		Vec3 carToBall = ball.minus(data.car.position);
		Vec3 interceptDirection = findInterceptDirection(ball, carToBall, data.team);
		
		Vec3 closestBallOffset = ball.offset(interceptDirection, carToBall.mag() - 150);
		Vec3 carToOffset = closestBallOffset.minus(data.car.position);
		
		target = ball;
		if(carToOffset.mag() > carToBall.mag())
			target = closestBallOffset;
		
		targetSpeed = carToBall.mag() / remainingTime;
		
		ControlsOutput controls = new DriveControls(data, target, targetSpeed);
		if(!data.car.hasWheelContact)
			controls = AerialControls.recover(data, null);
				
		data.bot.renderer.drawStateString("TAKE SHOT");
		data.bot.renderer.drawLine3d(Color.RED, data.car.position, closestBallOffset);
		data.bot.renderer.drawLine3d(Color.RED, data.car.position, target);
		data.bot.renderer.drawBallPrediction(Color.BLUE, 4);
		
		return controls;
	}

	@Override
	public boolean isViable(DataPacket data) {
		return true;
	}
	
	private Vec3 findInterceptDirection(Vec3 ball, Vec3 carToBall, int team) {
		Vec3 ballToLeftPost = FieldData.getGoalLeft(1 - team).minus(ball);
		Vec3 ballToRightPost = FieldData.getGoalRight(1 - team).minus(ball);
		
		Vec3 carToBallDirection = carToBall.normalized();
		Vec3 ballToLeftPostDirection = ballToLeftPost.normalized(); 
		Vec3 ballToRightPostDirection = ballToRightPost.normalized();
		
		return carToBallDirection.clamp(
				ballToRightPostDirection, 
				ballToLeftPostDirection);
	}

}
