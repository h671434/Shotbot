package shotbot.state;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.PredictionData;
import shotbot.math.SteerUtils;
import shotbot.math.Vec3;
import shotbot.mechanics.Drive;

public class SaveNet extends State{

	private PredictionData inGoalPrediction;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		data.bot.renderer.drawStateString("SAVE NET");
		
		Vec3 target = inGoalPrediction.position;
		double dist = target.minus(data.car.position).mag();
		
		double targetSpeed = SteerUtils.cap(dist / inGoalPrediction.time, -1000, 2300);
		
		return Drive.driveTowards(data, target, targetSpeed);
	}

	@Override
	public boolean isViable(DataPacket data) {
		inGoalPrediction = PredictionData.nextOpponentGoal(data);
		
		if(inGoalPrediction.time == 0)
			return false;
		
		return true;
	}

}
