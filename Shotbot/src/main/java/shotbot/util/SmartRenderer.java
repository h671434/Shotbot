package shotbot.util;

import java.awt.Point;
import java.awt.Color;

import com.google.flatbuffers.FlatBufferBuilder;

import rlbot.cppinterop.RLBotDll;
import rlbot.cppinterop.RLBotInterfaceException;
import rlbot.flat.BallPrediction;
import rlbot.flat.PredictionSlice;
import rlbot.render.RenderPacket;
import rlbot.render.Renderer;
import shotbot.data.CarData;
import shotbot.data.DataPacket;
import shotbot.math.MathUtils;
import shotbot.math.Vec3;

public class SmartRenderer extends Renderer{
	
	private RenderPacket previousPacket;

    public SmartRenderer(int index) {
        super(index);
    }

    public void startPacket() {
        builder = new FlatBufferBuilder(1000);
    }

    public void finishAndSendIfDifferent() {
        RenderPacket packet = doFinishPacket();
        if (!packet.equals(previousPacket)) {
            RLBotDll.sendRenderPacket(packet);
            previousPacket = packet;
        }
    }

    /**
     * Draw a cube with the given center and size
     */
    public void drawCube(Color color, Vec3 center, double size) {

        double r = size / 2;

        drawLine3d(color, center.plus(new Vec3(-r, -r, -r)), center.plus(new Vec3(-r, -r, r)));
        drawLine3d(color, center.plus(new Vec3(r, -r, -r)), center.plus(new Vec3(r, -r, r)));
        drawLine3d(color, center.plus(new Vec3(-r, r, -r)), center.plus(new Vec3(-r, r, r)));
        drawLine3d(color, center.plus(new Vec3(r, r, -r)), center.plus(new Vec3(r, r, r)));

        drawLine3d(color, center.plus(new Vec3(-r, -r, -r)), center.plus(new Vec3(-r, r, -r)));
        drawLine3d(color, center.plus(new Vec3(r, -r, -r)), center.plus(new Vec3(r, r, -r)));
        drawLine3d(color, center.plus(new Vec3(-r, -r, r)), center.plus(new Vec3(-r, r, r)));
        drawLine3d(color, center.plus(new Vec3(r, -r, r)), center.plus(new Vec3(r, r, r)));

        drawLine3d(color, center.plus(new Vec3(-r, -r, -r)), center.plus(new Vec3(r, -r, -r)));
        drawLine3d(color, center.plus(new Vec3(-r, -r, r)), center.plus(new Vec3(r, -r, r)));
        drawLine3d(color, center.plus(new Vec3(-r, r, -r)), center.plus(new Vec3(r, r, -r)));
        drawLine3d(color, center.plus(new Vec3(-r, r, r)), center.plus(new Vec3(r, r, r)));
    }

    /**
     * Draw a cross with the given center and size
     */
    public void drawCross(Color color, Vec3 center, double size) {

        double r = size / 2;

        drawLine3d(color, center.plus(new Vec3(-r, 0, 0)), center.plus(new Vec3(r, 0, 0)));
        drawLine3d(color, center.plus(new Vec3(0, -r, 0)), center.plus(new Vec3(0, r, 0)));
        drawLine3d(color, center.plus(new Vec3(0, 0, -r)), center.plus(new Vec3(0, 0, r)));
    }

    /**
     * Draw the next few seconds of ball prediction
     */
    public void drawBallPrediction(Color color, double duration) {
        try {
            BallPrediction ballPrediction = RLBotDll.getBallPrediction();
            Vec3 previousLocation = null;
            int stop = (int) (60 * MathUtils.cap(duration, 0, 6.0));
            for (int i = 0; i < ballPrediction.slicesLength(); i += 4) {
                PredictionSlice slice = ballPrediction.slices(i);
                if (i >= stop) {
                    break;
                }
                Vec3 location = new Vec3(slice.physics().location());
                if (previousLocation != null) {
                    drawLine3d(color, previousLocation, location);
                }
                previousLocation = location;
            }

        } catch (RLBotInterfaceException ignored) {

        }
    }
	
    public void drawDebugLines(DataPacket data, boolean goLeft) {

    	DataPacket input = data;
    	CarData myCar = data.car;

        // Draw a line from the car to the ball
       drawLine3d(Color.LIGHT_GRAY, myCar.position, input.ball.position);

        // Draw a line that points out from the nose of the car.
       drawLine3d(goLeft ? Color.BLUE : Color.RED,
                myCar.position.plus(myCar.orientation.forward.scaled(150)),
                myCar.position.plus(myCar.orientation.forward.scaled(300)));

        drawString3d(goLeft ? "left" : "right", Color.WHITE, myCar.position, 2, 2);

        drawBallPrediction(Color.CYAN, myCar.elapsedSeconds + 3);
    }
    
    public void drawStateString(String state) {	
    	drawString2d("State : " + state, Color.RED, new Point(10, 10), 2, 2);  	
    }
    
    public void drawSteerString(String steerType) {
    	drawString2d("Steer : " + steerType, Color.RED, new Point(10, 50), 2, 2);
    }
    
    public void drawManeuverString(String maneuver) {
    	drawString2d("Mechanic : " + maneuver, Color.RED, new Point(10, 90), 2, 2);
    }
    
    public void drawVectorStrings(String[] vectors) {
    	for (int i = 0; i < vectors.length; i++) {
    		drawString2d(vectors[i], Color.RED, new Point(10, i*20 + 130), 1, 1);
    	}
    }
}
