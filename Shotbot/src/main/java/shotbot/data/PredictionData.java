package shotbot.data;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import shotbot.math.Vec3;

public class PredictionData {

	public Vec3 position;
	public Vec3 velocity;
	public double time;
	
	public PredictionData(Vec3 position, Vec3 velocity, double time) {
		this.position = position;
		this.velocity = velocity;
		this.time = time;
	}	
	
	/**
	 * Is ball reachable for given car?
	 */
	public boolean isReachable(CarData car) {
		Vec3 carToBall = position.minus(car.position);
		Vec3 carToBallDir = carToBall.normalized();
		double dist = carToBall.mag();
		 
		double speedTowardsBall = car.velocity.dot(carToBallDir);
		double averageSpeed = (speedTowardsBall + 2300) / 2.0;

		double travelTime = dist / averageSpeed;
		
		return (travelTime < time);
	}
	
	/**
	 * Returns next reachable ball position along with its velocity
	 * and when, relative to current point in time, it will be there.
	 */
	public static PredictionData nextReachable(DataPacket data, int startTime) {
		try {
			BallPrediction ballPrediction = RLBotDll.getBallPrediction();
			for (int i = startTime; i < 6 * 60; i += 6) {
				PredictionData ball = new PredictionData(
						new Vec3(ballPrediction.slices(i).physics().location()),
						new Vec3(ballPrediction.slices(i).physics().velocity()),
						i / 60.0);
				
				if(ball.isReachable(data.car) && ball.position.z < 200)  
					return ball;
				
				// Return null if any other car is closer
				for(CarData anyCar : data.allCars) 
					if (ball.isReachable(anyCar)) 
						return null;
			}

       } catch (RLBotInterfaceException ignored) {
    	 
       }

	return null;
	}
	
	/**
	 * Prediction at certain point in time from current time
	 */
	public static PredictionData getPredictionAt(DataPacket data, int time) {
		try {
			BallPrediction ballPrediction = RLBotDll.getBallPrediction();
			PredictionData ball = new PredictionData(
					new Vec3(ballPrediction.slices(time).physics().location()),
					new Vec3(ballPrediction.slices(time).physics().velocity()),
					time / 60.0);
			
			return ball;
					
		} catch (RLBotInterfaceException ignored) {

       	}

		return new PredictionData(data.ball.position, data.ball.velocity, 0);
	}
	
	/**
	 * Returns point where goal is recieved
	 */
	public static PredictionData nextOpponentGoal(DataPacket data) {
		try {
			BallPrediction ballPrediction = RLBotDll.getBallPrediction();
			for (int i = 0; i < 6 * 60; i += 6) {
				PredictionData ball = new PredictionData(
						new Vec3(ballPrediction.slices(i).physics().location()),
						new Vec3(ballPrediction.slices(i).physics().velocity()),
						i / 60.0);

				Vec3 goal = FieldData.getGoal(data.car.team);
				if (((goal.y < 0 && ball.position.y <= goal.y) 
						|| (goal.y > 0 && ball.position.y >= goal.y))
						&& ball.position.z < FieldData.GOAL_HEIGHT
						&& ball.position.x < FieldData.GOAL_LENGTH / 2
						&& ball.position.x < -FieldData.GOAL_LENGTH / 2) {
					return ball;
				}
			}

       } catch (RLBotInterfaceException ignored) {

       }

       return new PredictionData(data.ball.position, data.ball.velocity, 0);
	}
	
}
