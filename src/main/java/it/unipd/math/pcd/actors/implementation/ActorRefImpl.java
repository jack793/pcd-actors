package it.unipd.math.pcd.actors.implementation;
import it.unipd.math.pcd.actors.*;
import java.util.concurrent.ThreadFactory;

/**
 * Realizzato da Franceschini Marco
 * Implementazione dell'ActorRef locale
 */
public class ActorRefImpl<T extends Message> extends AbsActorRef<T> {

    public ActorRefImpl(ActorSystem system) { super((AbsActorSystem)system); }


    @Override
    public void send(T message, ActorRef to) {
        ((AbsActor<T>)system.find(to)).newMail(new MailImpl<T>(message, this));
    }


}
