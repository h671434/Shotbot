package shotbot.data;

import rlbot.flat.GameTickPacket;
import shotbot.Shotbot;
import shotbot.data.boost.BoostPad;
import shotbot.data.boost.BoostManager;

import java.util.ArrayList;
import java.util.List;

public class DataPacket {
	
    public final Shotbot bot;

    public final CarData car;
    public final List<CarData> allCars;
    public final BallData ball;
    
    public final int team;
    public final int playerIndex;

    public final boolean isKickoff;
    public final double time;
    public final double timeRemaining;

    public final BoostPad closestFullBoost;
    public final double closestFullBoostDist;

    public DataPacket(Shotbot bot, GameTickPacket packet) {
        this.bot = bot;
        this.playerIndex = bot.getIndex();
        this.ball = new BallData(packet.ball());

        allCars = new ArrayList<>();
        for (int i = 0; i < packet.playersLength(); i++) {
            allCars.add(new CarData(packet.players(i), packet.gameInfo().secondsElapsed()));
        }

        this.car = allCars.get(playerIndex);
        this.team = this.car.team;

        // Game info
        isKickoff = packet.gameInfo().isKickoffPause() && ball.position.flatten().isZero();
        time = packet.gameInfo().secondsElapsed();
        timeRemaining = packet.gameInfo().gameTimeRemaining();

        // Calculate closest boost pad
        BoostPad closestPad = null;
        double shortestDist = 99999999;
        for (BoostPad pad : BoostManager.getFullBoosts()) {
            double dist = pad.getLocation().dist(car.position);
            if (closestPad == null || (dist < shortestDist && pad.isActive())) {
                closestPad = pad;
                shortestDist = dist;
            }
        }

        closestFullBoost = closestPad;
        closestFullBoostDist = shortestDist;
    }
}
