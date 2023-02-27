package shotbot.state;

import shotbot.controls.ControlsOutput;
import shotbot.controls.DriveControls;
import shotbot.data.DataPacket;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;
import shotbot.prediction.PredictionData;

public class SaveNet extends State{

	private PredictionData inGoalPrediction;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		data.bot.renderer.drawStateString("SAVE NET");
		
		Vec3 target = inGoalPrediction.position;
		double dist = target.minus(data.car.position).mag();
		
		double targetSpeed = MathUtils.cap(dist / inGoalPrediction.time, -1000, 2300);
		
		return new DriveControls(data, target, targetSpeed);
	}

	@Override
	public boolean isViable(DataPacket data) {
		inGoalPrediction = PredictionData.nextOpponentGoal(data);
		
		if(inGoalPrediction.time == 0)
			return false;
		
		return true;
	}

}
