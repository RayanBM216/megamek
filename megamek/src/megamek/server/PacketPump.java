package megamek.server;

public class PacketPump implements Runnable{

    private boolean shouldStop = false;
    private final Server server;
    void signalEnd() {
        shouldStop = true;
    }

    // Constructeur ajouté
    public PacketPump(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (!shouldStop) {
            while (!server.getPacketQueue().isEmpty()) {
                Server.ReceivedPacket rp = server.getPacketQueue().poll();
                synchronized (server.getServerLock()) {
                    server.handle(rp.getConnectionId(), rp.getPacket());
                }
            }

            try {
                synchronized (server.gePacketQueue()) {
                    server.getPacketQueue().wait();
                }
            } catch (InterruptedException ignored) {
                // ne rien faire
            }
        }
    }
}
