package shotbot.mechanics;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class DriftTurn implements Mechanic {

	private Vec3 target; // The target we want to be looking at
	private boolean done = false;
	
	public DriftTurn(DataPacket data, Vec3 target) {
		this.target = target;
	}
	
	@Override
	public ControlsOutput exec(DataPacket data) {

		Vec3 localTarget = data.car.orientation.local(target, data.car.position);
		
		ControlsOutput controls = new ControlsOutput();
		
		controls.withSteer(MathUtils.sign(localTarget.normalized().y) * 1);
		
		if(Math.abs(localTarget.normalized().y) > 0.5)
			controls.withSlide(true);
		
		if(Math.abs(localTarget.normalized().y) < 0.6)
			controls.withSteer(localTarget.normalized().y * 5)
					.withThrottle(1)
					.withBoost(true);
	
		if(Math.abs(localTarget.normalized().y) < 0.3
				|| !data.car.isUpright)
			done = true;
		
		
		data.bot.renderer.drawManeuverString("DRIFT-TURN");
		
		return controls;
	}

	@Override
	public boolean done() {
		return done;
	}

}
