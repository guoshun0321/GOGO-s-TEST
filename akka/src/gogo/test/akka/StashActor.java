package gogo.test.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActorWithStash;
import akka.japi.Procedure;

public class StashActor
{

    public static class Stash extends UntypedActorWithStash
    {
        @Override
        public void onReceive(Object msg) throws Exception
        {
            if (msg.equals("open"))
            {
                unstashAll();
                getContext().become(new Procedure<Object>()
                {
                    @Override
                    public void apply(Object msg1) throws Exception
                    {
                        if (msg1.equals("write"))
                        {
                            System.out.println("msg1");
                        } else {
                            stash();
                        }
                    }
                });
            }
            else
            {
                System.out.println("stash");
                stash();
                stash();
            }
        }
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("system");
        ActorRef ref = system.actorOf(Props.create(Stash.class), "child1");
        
        Inbox inbox = Inbox.create(system);
        inbox.send(ref, "write");
        inbox.send(ref, "write");
        inbox.send(ref, "write1");
        inbox.send(ref, "write1");
        inbox.send(ref, "open");
        inbox.send(ref, "write1");
        inbox.send(ref, "write1");
        inbox.send(ref, "write1");
        inbox.send(ref, "write1");
    }

}
