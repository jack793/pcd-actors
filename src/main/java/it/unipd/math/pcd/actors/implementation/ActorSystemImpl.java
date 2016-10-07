package it.unipd.math.pcd.actors.implementation;

import it.unipd.math.pcd.actors.AbsActor;
import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;

import java.util.concurrent.*;

/**
 * Realizzato da Franceschini Marco
 * Implementazione dell'AbstractActor System
 */

public class ActorSystemImpl extends AbsActorSystem {

    private ExecutorService es;

    // Costruttore che inizializza la variabile "es" come a newCachedThreadPool
    public ActorSystemImpl() {
        es = Executors.newCachedThreadPool();
    }

    // Crea un ActorRef locale
    @Override
    protected ActorRef createActorReference(ActorMode mode)
    {
        if(mode == ActorMode.LOCAL)
            // MyActorRef is a class that represents the implementation of local ActorRef
            return new ActorRefImpl(this);
        else
            throw new IllegalArgumentException();
    }

    // Esegue un Runnable passato
    public void systemExecute(Runnable r) {es.execute(r);}


    /**
     * Classe interna che implementa un Callable, usato per fermare un actor.
     * Un Callable permette di sapere se un actor è stato fermato o meno
     */
    private static class Stoppable implements Callable<Boolean> {

        private AbsActor<?> actor;

        Stoppable(AbsActor<?> a){
            actor = a;
        }

        @Override
        public Boolean call() throws Exception {
            return actor.stop();
        }
    }


    /**
     * Stops {@code actor}.
     *
     * @param actor The actor to be stopped
     */
    @Override
    public void stop(ActorRef<?> actor) {
        final AbsActor<?> actorToStop = ((AbsActor<?>) find(actor));
        FutureTask<Boolean> future = new FutureTask<>(new Stoppable(actorToStop));
        es.execute(future);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException except) {
            except.printStackTrace();
        }
        remove(actor);
    }


    
    /**
     * Stops all actors of the system.
     */
    @Override
    public void stop() {
        for (ActorRef actor : getMapKeys()) {
            stop(actor);
        }
    }
}
