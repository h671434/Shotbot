package shotbot.state;

import shotbot.Shotbot;
import shotbot.controls.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.state.gotopoint.GoToReachable;
import shotbot.util.Tester;
import shotbot.state.gotopoint.GoToBall;
import shotbot.state.gotopoint.GoToBoost;

public class StateHandler {
	
	public State currentState;
	
	private Shotbot bot;
	private Tester tester;
	
	public StateHandler(Shotbot bot) {
		this.bot = bot;
		this.tester = new Tester(bot);
	}
	
	private State selectState(DataPacket data) {
		State states[] = {
//				new SaveNet(),
				new TakeShot(),
				new GoToReachable(),
				new GoToBoost()
		};
		
		for(State state : states) 
			if(state.isViable(data)) 
				return state;
		
		return new GoToBall();
	}
	
	public ControlsOutput exec(DataPacket data) {		
		
		if(currentState == null)
			currentState = selectState(data);
			
		ControlsOutput stateOutput = currentState.exec(data);
		
		if(stateOutput != null)
			return stateOutput;
		
		currentState = null;
		return exec(data);
	}
}
