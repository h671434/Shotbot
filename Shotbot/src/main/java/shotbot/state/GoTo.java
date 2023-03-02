package shotbot.state;

import java.awt.Color;

import shotbot.controls.AerialControls;
import shotbot.controls.ControlsOutput;
import shotbot.controls.DriveControls;
import shotbot.data.CarData;
import shotbot.data.DataPacket;
import shotbot.data.FieldData;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;
import shotbot.mechanics.Dodge;
import shotbot.mechanics.DriftTurn;
import shotbot.mechanics.HalfFlip;
import shotbot.mechanics.WaveDash;

public class GoTo extends State {
	
	protected Vec3 target = Vec3.ZERO;
	protected double targetSpeed = 1400;
	
	protected AerialControls aerial = null;
	protected DriftTurn driftturn = null;
	protected Dodge dodge = null;
	protected HalfFlip halfflip = null;
	protected WaveDash wavedash = null;
	protected double lastDodgeEnd = 0;
	
	protected boolean allowDodge = true;
	    
    @Override
    public ControlsOutput exec(DataPacket data) {    
    	
    	data.bot.renderer.drawLine3d(Color.BLUE, data.car.position, target);
    	
        if (dodge != null) {
            if (dodge.done()) {
                dodge = null;
                lastDodgeEnd = data.time;
            } else {
                return dodge.exec(data);
            }
        }
        
        if (driftturn != null) {
            if (driftturn.done()) {
                driftturn = null;
                lastDodgeEnd = data.time;
            } else {
                return driftturn.exec(data);
            }
        }
         	
        if (halfflip != null) {
        	if (halfflip.done()) {
        		halfflip = null;
        		lastDodgeEnd = data.time;
        	} else {
        		return halfflip.exec(data);
        	}
        }   
        
        if (wavedash != null) {
        	if (wavedash.done()) {
        		wavedash = null;
        		lastDodgeEnd = data.time;
        	} else {
        		return wavedash.exec(data);
        	}
        }  
        
        CarData myCar = data.car;
        Vec3 carPosition = myCar.position;
    	
    	Vec3 target = this.target;
    	target = target
    			.withY(MathUtils.cap(target.y, FieldData.BACK_WALL_B.y, FieldData.BACK_WALL_A.y))
    			.withX(MathUtils.cap(target.x, FieldData.SIDE_WALL_B.x, FieldData.SIDE_WALL_A.x));

        // Subtract the two positions to get a vector pointing from the car to the ball.
        Vec3 carToTarget = target.minus(carPosition);
    	
        double dist = carToTarget.mag();
        double velocity = myCar.velocity.mag();
        double forwardDotTarget = myCar.orientation.forward.dot(carToTarget.normalized());
    	
    	
    	if(data.car.hasWheelContact && !data.car.isUpright)
    		target = myCar.orientation.local(carToTarget);
        
        
    	if (!data.car.hasWheelContact) {
    		return AerialControls.recover(data, null);
    	}
    	
    	allowDodge = allowDodge && lastDodgeEnd + 1 < data.time;
    	
    	// Consider mechanics
    	if (forwardDotTarget > 0.95
    			&& velocity < 2200
    			&& velocity - dist > 600
    			&& velocity < targetSpeed
    			&& allowDodge) {
    		dodge = new Dodge(data, target);
    		
    	} else if (forwardDotTarget < -0.5
    			&& velocity > 1300
    			&& dist > 500
    			&& data.car.isUpright) {
    		driftturn = new DriftTurn(data, target);
    		
    	} else if (forwardDotTarget < -0.5
    			&& velocity < 800
    			&& dist > 800
    			&& allowDodge) {
    		halfflip = new HalfFlip(data, target);
    		
    	} else if ((dist > 4000
    			&& velocity < 600
    			&& velocity < targetSpeed
    			&& allowDodge
    			&& data.car.hasWheelContact)
    			|| (data.car.position.z < 1000
    			&& data.car.position.z > 100
    			&& data.car.velocity.z < -50
    			&& allowDodge)) {
    		wavedash = new WaveDash(data, target);
    	}
	    
	    return new DriveControls(data, target, targetSpeed);
    }

	@Override
	public boolean isViable(DataPacket data) {
		return true;
	}
}
