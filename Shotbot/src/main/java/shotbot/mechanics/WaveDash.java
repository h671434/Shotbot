package shotbot.mechanics;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.math.Vec3;

public class WaveDash implements Mechanic {

	private Vec3 target;
	private double startTime;
	private boolean done = false;
	
	double firstJumpDur;
	double postFirstJumpPauseDur;
	
	double wheelContactTime = -1;
	
	public WaveDash(DataPacket data) {
		startTime = data.time;
		
		if(data.car.hasWheelContact) {
			firstJumpDur = 0.06;
			postFirstJumpPauseDur = 0.00;
		} else {
			firstJumpDur = 0;
			postFirstJumpPauseDur = 0;
		}
	}
	
	public WaveDash(DataPacket data, Vec3 target) {
		this(data);
		this.target = target;
	}
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		double currentTime = data.time - startTime;
		
		double t_firstJumpEnd = firstJumpDur;
		double t_aimBegin = t_firstJumpEnd + postFirstJumpPauseDur;
		
		ControlsOutput controls = new ControlsOutput();
		controls.withThrottle(1.0);
		
		if (currentTime < t_firstJumpEnd) {
			controls.withJump(true);
			
		} else if (currentTime < t_aimBegin && currentTime >= t_firstJumpEnd) {
			// Wait
			
		} else if (currentTime >= t_aimBegin) {
			Vec3 localTarget = data.car.orientation.local(target.withZ(data.car.position.z + 500), data.car.position);
			Vec3 localUp = data.car.orientation.dot(Vec3.UP);
			
			Vec3 localTargetNorm = localTarget.normalized();

			if(!data.car.hasWheelContact && data.car.position.z < 40 && data.car.velocity.z < -100) {
				
				controls.withYaw(localTargetNorm.y);
				controls.withPitch(-localTargetNorm.x);
				controls.withRoll(-localTarget.y);
				controls.withJump(true);
            	
			} else if(!data.car.hasWheelContact) {
				controls = Aerial.align(data, localTarget.normalized(), localUp);

				
			} else if (data.car.hasWheelContact) {
				controls.withSlide(true);
				if(wheelContactTime == -1) {
					wheelContactTime = currentTime;
				}
			}
		}	
		
		done = wheelContactTime != -1 ? currentTime >= wheelContactTime + 0.12 : false;
		
		data.bot.renderer.drawManeuverString("WAVE-DASH");
		
		return controls;
	}

	@Override
	public boolean done() {
		return done;
	}

}
