package gogo.test.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

public class UntypedActorSwapper
{

    public static class Swap
    {
        public static Swap SWAP = new Swap();

        private Swap()
        {

        }
    }

    public static class Swapper extends UntypedActor
    {
        @Override
        public void onReceive(Object msg) throws Exception
        {
            if (msg == Swap.SWAP)
            {
                System.out.println("Hi");
                getContext().become(new Procedure<Object>()
                {
                    @Override
                    public void apply(Object arg0) throws Exception
                    {
                        System.out.println("Ho");
                        getContext().unbecome();
                    }
                }, false);
            }
            else
            {
                unhandled(msg);
            }
        }
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("system");
        ActorRef swap = system.actorOf(Props.create(Swapper.class), "swap");

        for (int i = 0; i < 10000; i++)
        {
            swap.tell(Swap.SWAP, ActorRef.noSender());
            swap.tell(Swap.SWAP, ActorRef.noSender());
            swap.tell(Swap.SWAP, ActorRef.noSender());
            swap.tell(Swap.SWAP, ActorRef.noSender());
            swap.tell(Swap.SWAP, ActorRef.noSender());
            swap.tell(Swap.SWAP, ActorRef.noSender());
            swap.tell(Swap.SWAP, ActorRef.noSender());
        }
    }

}
