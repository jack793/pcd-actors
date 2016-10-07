package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.Message;

/**
 * Realizzato da Franceschini Marco
 * Implementazione dell'interfaccia Mail
 */

public final class MailImpl<T extends Message> implements Mail<T> {

    private final T message;
    private final ActorRef<T> sender;

    public MailImpl(T msg, ActorRef<T> sndr) {
        message = msg;
        sender = sndr;
    }

    /**
     * Ritorna il messaggio presente nella mail
     * @return T subtype of Message
     */
    @Override
    public T getMessage() {
        return message;
    }

    /**
     * Ritorna il mittente del messaggio
     * @return ActorRef
     */
    @Override
    public ActorRef<T> getSender() {
        return sender;
    }
}
