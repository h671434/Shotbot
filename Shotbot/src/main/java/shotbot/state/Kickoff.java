package shotbot.state;

import shotbot.data.BallData;
import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.prediction.PredictionData;
import shotbot.math.SteerUtils;
import shotbot.math.Vec3;
import shotbot.mechanics.Dodge;
import shotbot.mechanics.Drive;

public class Kickoff extends State {
	
	private static final double BALL_RADIUS = BallData.RADIUS;
	
	private Dodge dodge = null;
	
	@Override
	public ControlsOutput exec(DataPacket data) {
		
		data.bot.renderer.drawStateString("KICKOFF");
		
        if (dodge != null) {
            if (dodge.done()) {
                return null;
            } else {
                return dodge.exec(data);
            }
        }
                
        double sign = SteerUtils.sign(data.car.position.y);
        
        Vec3 ball = new Vec3(0, 0, BALL_RADIUS);
        Vec3 carToBall = ball.minus(data.car.position);
        double distToBall = carToBall.mag();
               
        if(dodge == null && distToBall < 600) {
        	Vec3 dodgeTarget = ball.withY(BALL_RADIUS * sign);
        	dodge = new Dodge(data, dodgeTarget);
        }
        
        Vec3 target = new Vec3(0, 250 * sign, 0);
        Vec3 targetVelocity = new Vec3(0, 2300 * -sign, 0);
        double targetTime = distToBall / 2000;

        
        return Drive.driveTowards(data, target, 2300);
	}

	@Override
	public boolean isViable(DataPacket data) {
		return data.isKickoff && !(data.bot.stateHandler.currentState instanceof Kickoff);
	}
	
}
