package shotbot.state;

import shotbot.controls.ControlsOutput;
import shotbot.data.DataPacket;

public abstract class State {
	
	public abstract ControlsOutput exec(DataPacket data);

	public abstract boolean isViable(DataPacket data);
}
