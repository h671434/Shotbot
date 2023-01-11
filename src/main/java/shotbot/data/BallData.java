package shotbot.data;


import rlbot.flat.BallInfo;
import shotbot.math.Vec3;

public class BallData {
	
	public static final double MAX_VELOCITY = 6000;
	public static final double MAX_ANGULAR_VELOCITY = 6;
	public static final double RADIUS = 92.75;
	
    public final Vec3 position;
    public final Vec3 velocity;
    public final Vec3 spin;
    
    public double latestTouch;

    public BallData(final BallInfo ball) {
        this.position = new Vec3(ball.physics().location());
        this.velocity = new Vec3(ball.physics().velocity());
        this.spin = new Vec3(ball.physics().angularVelocity());
        
        try {
        	latestTouch = ball.latestTouch().gameSeconds();
        } catch (Exception e) {
        	latestTouch = 0;
        } 
    }
}

