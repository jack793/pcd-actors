package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.ActorSystem;
import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;
import it.unipd.math.pcd.actors.utils.ActorSystemFactory;
import it.unipd.math.pcd.actors.utils.actors.StoreActor;
import it.unipd.math.pcd.actors.utils.actors.TrivialActor;
import it.unipd.math.pcd.actors.utils.messages.StoreMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Realizzato da Franceschini Marco
 * Classe per testare l'ActorSystem
 */

public class MyTest {

    // ActorSystem usato per i test
    private ActorSystem system;


    // Initializes the {@code system} with a concrete implementation before each test.
    @Before
    public void init() {
        system = ActorSystemFactory.buildActorSystem();
    }

    // Testa il metodo actorOf
    @Test
    public void shouldCreateAnActorRefWithActorOf() {
        ActorRef ref = system.actorOf(TrivialActor.class);
        Assert.assertNotNull("A reference was created and it is not null", ref);
    }


    /**
     * Una volta fermatao l'ActorSystem vengono fermati tutti gli actor
     * al proprio interno
     */
    @Test(expected = NoSuchActorException.class)
    public void shouldStopTheEntireActorSystemAndThenAnActorCouldNotBeStoppedASecondTime() {
        ActorRef ref1 = system.actorOf(TrivialActor.class);
        system.stop();
        system.stop(ref1);
    }


    /**
     * L'actor1 dovrebbe mandare una mail all'actor2 (che dovrebbe ricevere il
     * messaggio)
     */
    @Test
    public void shouldBeAbleToSendAMailAndTheOtherOneShouldBeAbleToReceiveIt() throws InterruptedException {
        TestActorRef ref1 = new TestActorRef(system.actorOf(StoreActor.class));
        TestActorRef ref2 = new TestActorRef(system.actorOf(StoreActor.class));
        StoreActor actor1 = (StoreActor) ref1.getUnderlyingActor(system);
        StoreActor actor2 = (StoreActor) ref2.getUnderlyingActor(system);

        // L'actor1 manda una mail all'actor2
        ref1.send(new StoreMessage("Hello, it's me (actor1)"), ref2);

        // Attesa per l'elaborazione del messaggio
        Thread.sleep(2000);

        // Verifica della ricezione
        Assert.assertEquals("The message has to be received by the actor", "Hello, it's me (actor1)", actor2.getData());
    }

    // L'actor2 dovrebbe elaborare il messaggio e rispondere al mittente
    @Test
    public void anActorShouldBeAbleToSendAnotherShouldBeAbleToReply() throws InterruptedException {
        TestActorRef ref1 = new TestActorRef(system.actorOf(StoreActor.class));
        TestActorRef ref2 = new TestActorRef(system.actorOf(StoreActor.class));

        // Conversazione dei due actor
        StoreActor actor1 = (StoreActor) ref1.getUnderlyingActor(system);
        StoreActor actor2 = (StoreActor) ref2.getUnderlyingActor(system);

        // L'actor1 manda una mail all'actor2
        ref1.send(new StoreMessage("Hello, it's me (actor2)"), ref2);

        // Attesa per l'elaborazione del messaggio
        Thread.sleep(2000);

        // Verifica della ricezione
        Assert.assertEquals("The message has to be received by the actor", "Hello, it's me (actor2)", actor2.getData());

        // Verifica che il messaggia sia dall'actor1
        Assert.assertEquals("The sender of the message has to be actor1", ref1, actor2.getSender());

        // L'actor2 può rispondere al mittente
        ref2.send(new StoreMessage("I was wondering if after all these years you'd like to meet"), actor2.getSender());

        // Attesa per l'elaborazione del messaggio
        Thread.sleep(2000);

        // Verifica della ricezione
        Assert.assertEquals("The message has to be received by the actor", "I was wondering if after all these years you'd like to meet", actor1.getData());
    }


    /**
     * Una volta fermato un actor, dovrebbe controllare tutte i messaggi
     * presenti nella MailBox.
     * Questa verifica viene fatto facendo mandare all'actor 30 mail verso sè
     * stesso. Così facendo dovrebbe avere alcune mail da leggere; l'actor viene
     * fermato per il tempo necessario per elaborare tutti i messaggidopodichè
     * non dovrebbero esserci mail da leggere.
     */
    @Test
    public void afterBeingStoppedElaborateAllTheRemainingMails() throws InterruptedException {

        TestActorRef ref = new TestActorRef(system.actorOf(StoreActor.class));
        StoreActor actor = (StoreActor) ref.getUnderlyingActor(system);

        // L'actor si autoinvia 15 mail
        for(int i = 0; i < 15; ++i)
            ref.send(new StoreMessage("Hello, it's me!"), ref);

        // Verifica che la MailBox non sia vuota
        Assert.assertEquals("There are some mails for you.", false, actor.nothingToRead());

        // Ferma l'actor
        system.stop(ref);

        // Attende che l'actor legga tutte le mail
        Thread.sleep(2000);

        // Verifica che la MailBox sia vuota
        Assert.assertEquals("There's no mails to read. See u soon!", true, actor.nothingToRead());
    }
}
