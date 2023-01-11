package shotbot.mechanics;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class Dodge implements Mechanic {

	private Vec3 target;
	private double startTime;
	private boolean done = false;
	
	private double firstJumpDur;
	private double postFirstJumpPauseDur;
	private double aimDur;
	private double secondJumpDur;
	private double postSecondJumpPauseDur;
	
	public Dodge(DataPacket data, Vec3 target) {
        this.target = target;
		this.startTime = data.time;
		
		// TODO Calculate appropriate timing
		firstJumpDur = 0.08;
		postFirstJumpPauseDur = 0.0;
		aimDur = 0.18;
		secondJumpDur = 0.20;
		postSecondJumpPauseDur = 0.14;
	}
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		double t_firstJumpEnd = firstJumpDur;
		double t_aimBegin = t_firstJumpEnd + postFirstJumpPauseDur;
		double t_secondJumpBegin = t_aimBegin + aimDur;
		double t_secondJumpEnd = t_secondJumpBegin + secondJumpDur;
		double t_dodgeEnd = t_secondJumpEnd + postSecondJumpPauseDur;
		
		double currentTime = data.time - startTime;
		
		ControlsOutput controls = new ControlsOutput();
		controls.withThrottle(1.0);
		
		if (currentTime < t_firstJumpEnd) {
			controls.withJump(true);
			
		} else if (t_firstJumpEnd <= currentTime && currentTime < t_aimBegin) {
			// Wait
			
        } else if (t_aimBegin <= currentTime && currentTime < t_secondJumpEnd) {
            if (target == null) {
                controls.withPitch(-1.0);

            } else {
                Vec3 localTarget = data.car.orientation.local(target, data.car.position);
                Vec3 direction = localTarget.withZ(0).normalized();

                controls.withPitch(-direction.x);
                controls.withYaw(MathUtils.sign(data.car.orientation.up.z) * direction.y);
                controls.withRoll(MathUtils.sign(data.car.orientation.up.z) * direction.y);
            }

            if (currentTime >= t_secondJumpBegin) {
                controls.withJump(true);
            }
			
		} else if (t_secondJumpEnd <= currentTime && currentTime < t_dodgeEnd) {
			//Wait 
			
		} else if (currentTime >= t_dodgeEnd + 0.5){
			done = true;
		}
		
		data.bot.renderer.drawManeuverString("DODGE");
		
		return controls;
	}

	@Override
	public boolean done() {
		return done;
	}

}
