package shotbot.mechanics;

import shotbot.data.ControlsOutput;
import shotbot.data.DataPacket;

public interface Mechanic {

	ControlsOutput exec(DataPacket data);
	
	boolean done();
	
}