package gogo.test.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

public class HotSwapActor extends UntypedActor
{

    Procedure<Object> angry = new Procedure<Object>()
    {
        public void apply(Object msg)
        {
            if (msg.equals("bar"))
            {
                System.out.println("I am already angry");
                getSender().tell("I am already angry ?", getSelf());
            }
            else if (msg.equals("foo"))
            {
                getContext().become(happy);
            }
        }
    };

    Procedure<Object> happy = new Procedure<Object>()
    {
        public void apply(Object msg)
        {
            if (msg.equals("bar"))
            {
                System.out.println("I am already happy");
                getSender().tell("I am already happy :-", getSelf());
            }
            else if (msg.equals("foo"))
            {
                getContext().become(angry);
            }
        }
    };

    @Override
    public void onReceive(Object msg) throws Exception
    {
        if (msg.equals("bar"))
        {
            getContext().become(angry);
        }
        else if (msg.equals("foo"))
        {
            getContext().become(happy);
        }
        else
        {
            unhandled(msg);
        }
    }

    public static class SenderActor extends UntypedActor
    {
        @Override
        public void onReceive(Object msg) throws Exception
        {
            if (msg instanceof String)
            {
                String str = (String) msg;
                if (msg.equals("bar"))
                {

                }
            }
        }
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("HotswapSystem");
        ActorRef hotRef = system.actorOf(Props.create(HotSwapActor.class), "hotswapActor");

        Inbox inbox = Inbox.create(system);
        inbox.send(hotRef, "bar");
        for (int i = 0; i < 10; i++)
        {
            inbox.send(hotRef, "bar");
            inbox.send(hotRef, "foo");
        }
    }

}
