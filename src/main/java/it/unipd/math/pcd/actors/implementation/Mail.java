package it.unipd.math.pcd.actors.implementation;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.Message;

/**
 * Realizzato da Franceschini Marco
 * Interfaccia Mail
 */

public interface Mail<T extends Message> {

    // Ritorna il messaggio presente nella mail
    T getMessage();

    // Ritorna il mittente del messaggio
    ActorRef<T> getSender();
}
