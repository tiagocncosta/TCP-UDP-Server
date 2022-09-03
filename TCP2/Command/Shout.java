package TCP2.Command;

import TCP2.Server;

public class Shout implements CommandHandler {
    @Override
    public void execute(Server server, Server.ClientHandler clientHandler) {
        String message = clientHandler.getMessage();
        String messageToSend = message.substring(6).toUpperCase();
        server.broadCast(clientHandler, messageToSend);
    }
}
