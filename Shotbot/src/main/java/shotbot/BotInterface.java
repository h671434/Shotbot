package shotbot;

import rlbot.Bot;
import rlbot.manager.BotManager;
import rlbot.pyinterop.SocketServer;

public class BotInterface extends SocketServer {

    public BotInterface(int port, BotManager botManager) {
        super(port, botManager);
    }

    @Override
    protected Bot initBot(int index, String botType, int team) {
        return new Shotbot(index);
    }
}
