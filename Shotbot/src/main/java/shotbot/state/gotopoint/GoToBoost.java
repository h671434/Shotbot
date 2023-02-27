package shotbot.state.gotopoint;

import shotbot.controls.ControlsOutput;
import shotbot.data.DataPacket;

public class GoToBoost extends GoTo {
	
    @Override
    public ControlsOutput exec(DataPacket data) {       
    	
    	data.bot.renderer.drawStateString("GET BOOST");
    	
    	target =  data.closestFullBoost.getLocation();
    	targetSpeed = 2300;
    	
	    return super.exec(data);
    }
    
    @Override
    public boolean isViable(DataPacket data) {
    	return data.car.boost < 70;
    }
}
