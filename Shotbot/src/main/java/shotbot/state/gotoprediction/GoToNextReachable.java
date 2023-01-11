package shotbot.state.gotoprediction;

import java.awt.Color;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.FieldData;
import shotbot.data.prediction.PredictionData;
import shotbot.math.SteerUtils;
import shotbot.math.Vec3;

public class GoToNextReachable extends GoToPrediction {
	
	private PredictionData nextReachable;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		data.bot.renderer.drawStateString("GO TO NEXT REACHABLE");
		
		Vec3 carToReachable = nextReachable.position.minus(data.car.position);
		
		Vec3 carToTargetDirection = carToReachable.normalized();
		
		Vec3 goalLeft = FieldData.getGoalLeft(data.team);
		Vec3 goalRight = FieldData.getGoalRight(data.team);
		
		Vec3 ballToLeftGoalDirection = goalLeft.minus(nextReachable.position).normalized();
		Vec3 ballToRightGoalDirection = goalRight.minus(nextReachable.position).normalized();
		
		Vec3 targetDirection = SteerUtils.clamp(
				carToTargetDirection, 
				ballToLeftGoalDirection, 
				ballToRightGoalDirection);
		
		Vec3 targetVelocity = targetDirection.scaledToMag(2300);
		
		data.bot.renderer.drawLine3d(Color.BLUE, nextReachable.position, targetDirection);
		
		target = new PredictionData(nextReachable.position, targetVelocity, nextReachable.time);
		
		data.bot.renderer.drawBallPrediction(Color.RED, 300);

		return super.exec(data);
	}
	
	@Override
	public boolean isViable(DataPacket data) {
		nextReachable = PredictionData.nextReachable(data, 0);
		
		if (nextReachable == null)
			return false;
		
		startTime = data.time;
		
		return true;
	}

}
