package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.ActorSystem;
import it.unipd.math.pcd.actors.Message;

// Realizzato da Franceschini Marco

public abstract class AbsActorRef <T extends Message> implements ActorRef<T> {

    protected final AbsActorSystem system;

    // Costruttore della classe
    public AbsActorRef(ActorSystem s) {system = (AbsActorSystem) s;}

    @Override
    public int compareTo(ActorRef ref) {
        if(this == ref)
            return 0;
        else
            return -1;
    }

    public void execute(Runnable r) {system.systemExecute(r);}

}
