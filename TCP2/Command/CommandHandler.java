package TCP2.Command;

import TCP2.Server;

public interface CommandHandler {


    default void execute(Server server, Server.ClientHandler clientHandler) {

    }
}
