/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;
import it.unipd.math.pcd.actors.implementation.AbsActorRef;
import it.unipd.math.pcd.actors.implementation.Mail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T> {

    // Self-reference of the actor
    protected ActorRef<T> self;

    // Mittente del messaggio
    protected ActorRef<T> sender;

    // True se viene fermato dall'actor system
    private volatile boolean stopped;

    /**
     * Tutte le mail ricevute verranno inserite dentro una coda.
     * Quest'ultima verrà indicata come mailBox.
     * 
     */
    private BlockingQueue<Mail<T>> mailBox;

    // True se il "gestore" è già stato creato
    private volatile boolean createdManager;

    // Costruttore della classe astratta
    public AbsActor() {
        self = null;
        sender = null;
        stopped = false;
        mailBox = new LinkedBlockingQueue<>();
        createdManager = false;
    }

    // Per verificare se ci sono mail non ancora lette
    public boolean nothingToRead() { return mailBox.isEmpty(); }

    // "Setta" il campo sender dell'ultimo ActorRed che manda un messaggio all'actor corrente
    protected final void setSender(ActorRef<T> sender) {
        this.sender = sender;
    }

    // Sender of the current message
    public ActorRef<T> getSender() { return sender; }

    /**
     * Sets the self-referece.
     *
     * @param self The reference to itself
     * @return The actor.
     */
    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

    // Metta a true il campo stopped, se è già così viene lanciata un'eccezione
    public boolean stop() {
        synchronized (this){
            if (!stopped)
                stopped = true;
            else
                throw new NoSuchActorException();
        }
        return stopped;
    }


    /**
     * Aggiunge una mail alla mailBox
     * @param mail type Mail
     * @throws NoSuchActorException
     */
    public void newMail(Mail<T> mail){
        try {
            if (stopped)
                throw new NoSuchActorException();
            mailBox.put(mail);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!createdManager) {
            createTheMailBoxBoxManager();
        }
    }

    // Crea il gestore della mailBox
    private synchronized void createTheMailBoxBoxManager() {
        ((AbsActorRef<T>)self).execute(new mailBoxManager());
        createdManager = true;
    }

    // Per gestire le mail nella mailBox
    private class mailBoxManager implements Runnable {

        /**
        * Fino a quanto il gestore della mailBox non viene fermato esso lavorerà
        * Dopodichè legge tutte le mail prima di terminare.
        */
        @Override
        public void run() {
            while(!stopped)
                mailManagement();
            if (stopped)
                while(!(mailBox.isEmpty()))
                    mailManagement();
        }


        /**
         * Inserisce il "sender" alle mail da leggere ed usa il metodo "receive"
         * per elaborare i messaggi
         */
        private void mailManagement() {
            try {
                Mail m = mailBox.take();
                setSender(m.getSender());
                receive((T) m.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
