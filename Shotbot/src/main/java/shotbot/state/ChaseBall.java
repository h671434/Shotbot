package shotbot.state;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;

public class ChaseBall extends GoTo {

	private double startTime = 0;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		if(startTime == 0)
			startTime = data.time;
		
		data.bot.renderer.drawStateString("GO TO BALL");
		
		target = data.ball.position;
		targetSpeed = 1400;
		
		if(data.time > startTime + 1)
			return null;
		
		return super.exec(data);
	}
	
	@Override
	public boolean isViable(DataPacket data) {
		return true;
	}
	
}
