package shotbot.mechanics;

import shotbot.controls.AerialControls;
import shotbot.controls.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class HalfFlip implements Mechanic {

	private Vec3 target;
	private double startTime;
	private boolean done = false;
	
	double reverseThrottleDur = 0.52;
	double firstJumpDur = 0.08;
	double postFirstJumpPauseDur = 0.04;
	double secondJumpDur = 0.32;
	double leanBackDur = 0.58;
	double straightenDur = 0.38;
	
	public HalfFlip(DataPacket data, Vec3 target) {
		this.target = target;
		this.startTime = data.time;
	}
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		double t_reverseThrottleEnd = reverseThrottleDur;
		double t_firstJumpEnd = t_reverseThrottleEnd + firstJumpDur;
		double t_secondJumpBegin = t_firstJumpEnd + postFirstJumpPauseDur;
		double t_secondJumpEnd = t_secondJumpBegin + secondJumpDur;
		double t_leanBackEnd = t_secondJumpEnd + leanBackDur;
		double t_straightenEnd = t_leanBackEnd + straightenDur;
		
		double currentTime = data.time - startTime;
		
		ControlsOutput controls = new ControlsOutput();
		controls.withThrottle(1.0);
		
        Vec3 localTarget = data.car.orientation.local(target, data.car.position);
        Vec3 direction = localTarget.flatten().normalized();
		
		if (t_reverseThrottleEnd > currentTime) {
			controls.withThrottle(-1.0);
		
		} else if (t_reverseThrottleEnd <= currentTime && currentTime < t_firstJumpEnd) {
			controls.withThrottle(-1.0);
			controls.withPitch(1.0);
			controls.withJump(true);
			controls.withYaw(MathUtils.sign(data.car.orientation.up.z) * direction.y / 5);
			
		} else if (t_firstJumpEnd <= currentTime && currentTime < t_leanBackEnd) {
			controls.withThrottle(-1.0);
			controls.withYaw(MathUtils.sign(data.car.orientation.up.z) * -direction.y / 5);
						
			if (currentTime >= t_secondJumpBegin && currentTime < t_secondJumpEnd) {

                controls.withYaw(MathUtils.sign(data.car.orientation.up.z) * -direction.y / 5);
				controls.withJump(true);
				controls.withPitch(1.0);
				
			} else {
				controls = AerialControls.recover(data, target.flatten());
				controls.withPitch(-1.0);
				controls.withRoll(MathUtils.sign(controls.getRoll()));
			}
			
		} else if (t_leanBackEnd <= currentTime && currentTime < t_straightenEnd) {
			controls = AerialControls.recover(data, target.flatten());
			controls.withRoll(MathUtils.sign(controls.getRoll()));
			controls.withBoost(true);
			
		} else if (t_straightenEnd <= currentTime) {
			controls.withSlide(true);
		}	
	
		done =  currentTime >= t_straightenEnd + 0.12;
		
		data.bot.renderer.drawManeuverString("HALF-FLIP");
		
		return controls;
	}

	@Override
	public boolean done() {
		return done;
	}

}
