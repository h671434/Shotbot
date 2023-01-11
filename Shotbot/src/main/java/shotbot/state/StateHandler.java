package shotbot.state;

import rlbot.cppinterop.RLBotDll;
import rlbot.flat.DesiredGameState;
import rlbot.gamestate.BallState;
import rlbot.gamestate.DesiredVector3;
import rlbot.gamestate.GameState;
import rlbot.gamestate.GameStatePacket;
import rlbot.gamestate.PhysicsState;
import shotbot.Shotbot;
import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;

public class StateHandler {
	
	public State currentState;
	
	private Shotbot bot;
	
	public StateHandler(Shotbot bot) {
		this.bot = bot;
	}
	
	private State selectState(DataPacket data) {
		State states[] = {
				new TakeShot(),
				new ChasePrediction(),
				new PickUpBoost()
		};
		
		for(State state : states) 
			if(state.isViable(data)) 
				return state;
		
		return new ChaseBall();
	}
	
	public ControlsOutput exec(DataPacket data) {		

		if(data.ball.position.y > 5200) {
			GameState gs = new GameState()
					.withBallState(new BallState()
		            		.withPhysics(new PhysicsState()
		            				.withLocation(new DesiredVector3(null, null, 1000F))));
			RLBotDll.setGameState(gs.buildPacket());
		}
		
		if(currentState == null)
			currentState = selectState(data);
		
		if(data.isKickoff && !(currentState instanceof Kickoff)) 
			currentState = new Kickoff();
			
		ControlsOutput stateOutput = currentState.exec(data);
		
		if(stateOutput != null)
			return stateOutput;
		
		currentState = null;
		return exec(data);
	}
}
