package shotbot;

import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.flat.GameTickPacket;
import shotbot.controls.ControlsOutput;
import shotbot.data.DataPacket;
import shotbot.data.boost.BoostManager;
import shotbot.state.StateHandler;
import shotbot.util.SmartRenderer;

public class Shotbot implements Bot {

    private final int playerIndex;
    
    public final SmartRenderer renderer;

    public StateHandler stateHandler;
    
    public Shotbot(int playerIndex) {
        this.playerIndex = playerIndex;
        this.renderer = new SmartRenderer(playerIndex);
        this.stateHandler = new StateHandler(this);
    }

    @Override
    public ControllerState processInput(GameTickPacket packet) {
    	
        if (packet.playersLength() <= playerIndex 
        		|| packet.ball() == null 
        		|| !packet.gameInfo().isRoundActive()) 
        	return new ControlsOutput();
        
        BoostManager.loadGameTickPacket(packet);
        
        DataPacket data = new DataPacket(this, packet);
        
        renderer.startPacket();
        
        ControlsOutput controls = stateHandler.exec(data);
        
        renderer.finishAndSendIfDifferent();
        
        return controls;
    }

    @Override
    public int getIndex() {
        return this.playerIndex;
    }

    @Override
    public void retire() {
        System.out.println("Retiring sample bot " + playerIndex);
    }
}
