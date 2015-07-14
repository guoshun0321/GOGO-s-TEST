package gogo.test.akka;

import gogo.test.akka.HelloAkkaJava.Greet;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.Timeout;

public class ActorWithProp extends UntypedActor
{

    private final String name;

    public ActorWithProp(String name)
    {
        this.name = name;
    }

    public static Props mkProps(String name)
    {
        return Props.create(ActorWithProp.class, name);
    }

    @Override
    public void onReceive(Object msg) throws Exception
    {
        if (msg instanceof String)
        {
            System.out.println(this.getSelf() + " as " + name + " receive " + this.getSender() + ":" + msg);
        }
        else
        {
            unhandled(msg);
        }
    }

    private static class SendActor extends UntypedActor
    {
        
        public SendActor()
        {
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void onReceive(Object arg0) throws Exception
        {
            // TODO Auto-generated method stub

        }
    }

    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("TestSystem");
        final ActorRef recActor = system.actorOf(ActorWithProp.mkProps("rec"), "recActor");
        System.out.println(recActor);
        
        final ActorRef sendActor = system.actorOf(Props.create(SendActor.class), "senderActor");
        System.out.println(sendActor);
        
        ActorSelection as = system.actorSelection("user/senderActor");
        Future<ActorRef> actorF = as.resolveOne(Timeout.intToTimeout(1));
//        ActorRef actorS = actorF.result(arg0, arg1)
//        recActor.tell("msg from nosender", ActorRef.noSender());
//
//        final Inbox inbox = Inbox.create(system);
//        inbox.send(recActor, "msg from inbox");
        
        recActor.tell("msg from sender", sendActor);
    }

}
