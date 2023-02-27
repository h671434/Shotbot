package shotbot.util;

import rlbot.cppinterop.RLBotDll;
import rlbot.gamestate.BallState;
import rlbot.gamestate.CarState;
import rlbot.gamestate.DesiredVector3;
import rlbot.gamestate.GameState;
import rlbot.gamestate.PhysicsState;
import shotbot.Shotbot;
import shotbot.data.DataPacket;
import shotbot.state.Kickoff;
import shotbot.state.TakeShot;

public class Tester {

	private Shotbot bot;
	
	public Tester(Shotbot bot) {
		this.bot = bot;
	}
	
	public void shotTest(DataPacket data) {
		if(!(bot.stateHandler.currentState instanceof TakeShot)) {
			GameState gs = new GameState()
					.withBallState(new BallState()
		            		.withPhysics(new PhysicsState()
		            				.withLocation(new DesiredVector3(0F, 0F, 100F))
		            				.withVelocity(new DesiredVector3(0F, 0F, 0F))
		            				.withAngularVelocity(new DesiredVector3(0F, 0F, 0F))))
					.withCarState(data.playerIndex, new CarState()
							.withPhysics(new PhysicsState()
									.withLocation(new DesiredVector3(-1000F, -2000F, 20F))));
			RLBotDll.setGameState(gs.buildPacket());
			
			bot.stateHandler.currentState = null;
		}
	}
	
	public void kickoffTest(DataPacket data) {
		if(!(bot.stateHandler.currentState instanceof Kickoff)) {
			GameState gs = new GameState()
					.withBallState(new BallState()
		            		.withPhysics(new PhysicsState()
		            				.withLocation(new DesiredVector3(null, null, 500F))
									.withVelocity(new DesiredVector3(0F, 0F, 0F))));
			RLBotDll.setGameState(gs.buildPacket());
			
			bot.stateHandler.currentState = new Kickoff();
		}
	}
}
