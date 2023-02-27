package shotbot.state.gotopoint;

import java.awt.Color;

import shotbot.controls.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.FieldData;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;
import shotbot.prediction.PredictionData;

public class GoToReachable extends GoTo {
	
	private PredictionData nextReachable;
	double startTime;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		double currentTime = data.time - startTime;
		double remainingTime = nextReachable.time - currentTime;
		
		if(remainingTime < 0)
			return null;
		
		data.bot.renderer.drawStateString("GO TO NEXT REACHABLE");
		data.bot.renderer.drawBallPrediction(Color.RED, 300);
		
		Vec3 carToTarget = nextReachable.position.minus(data.car.position);
		double dist = carToTarget.mag();
		
		target = nextReachable.position;
		targetSpeed = dist / remainingTime;

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
