package gogo.test.akka;

import java.util.concurrent.TimeUnit;

import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.Timeout;

public class MyUntypedActor1 extends UntypedActor
{

    private int i;

    private ActorRef child;

    public MyUntypedActor1()
    {
        i = 0;
    }

    @Override
    public void preStart() throws Exception
    {
        System.out.println("preStart");
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception
    {
        System.out.println("old : " + i);
        System.out.println("preRestart");
    }

    @Override
    public void postRestart(Throwable reason) throws Exception
    {
        System.out.println("new : " + i);
        System.out.println("postRestart");
    }

    @Override
    public void postStop() throws Exception
    {
        System.out.println("before postStop");
        TimeUnit.SECONDS.sleep(5);
        System.out.println("postStop");
    }

    @Override
    public void onReceive(Object obj) throws Exception
    {
        if (obj instanceof String)
        {
            String msg = (String) obj;
            if (msg.equals("new"))
            {

            }
            else if (msg.equals("child"))
            {
                child = getContext().actorOf(Props.create(ChildActor.class), "child");
                System.out.println(child);
            }
            else if (msg.equals("get"))
            {
                getSender().tell(i++, getSelf());
            }
            else if (msg.equals("exception"))
            {
                throw new NullPointerException();
            }
            else if (msg.equals("identity"))
            {
                ActorSelection sel = getContext().actorSelection("/user/level1/child");
                sel.tell(new Identify("1"), getSelf());
            }
        }
        else if (obj instanceof ActorIdentity)
        {
            ActorIdentity identity = (ActorIdentity) obj;
            System.out.println("identity : " + identity.getRef());
        }
        else
        {
            unhandled(obj);
        }
    }

    private static class ChildActor extends UntypedActor
    {
        @Override
        public void onReceive(Object arg0) throws Exception
        {
            // TODO Auto-generated method stub

        }
    }

    public static void main(String[] args) throws Exception
    {
        ActorSystem system = ActorSystem.create("GoGO-s-system");
        ActorRef ref = system.actorOf(Props.create(MyUntypedActor1.class), "level1");

        Inbox inbox = Inbox.create(system);
        inbox.send(ref, "get");
        System.out.println(inbox.receive(Duration.create(10, TimeUnit.SECONDS)));

        inbox.send(ref, "get");
        System.out.println(inbox.receive(Duration.create(10, TimeUnit.SECONDS)));

//        inbox.send(ref, "exception");

        inbox.send(ref, "get");
        System.out.println(inbox.receive(Duration.create(10, TimeUnit.SECONDS)));

        inbox.send(ref, "get");
        
        inbox.send(ref, "child");

        inbox.send(ref, "identity");
        
        ActorSelection as = system.actorSelection("/user/level1/child");
        Future<ActorRef> asFuture = as.resolveOne(Timeout.durationToTimeout(Duration.create(10, TimeUnit.SECONDS)));
        ActorRef asActor = asFuture.result(Duration.create(10, TimeUnit.SECONDS), null);
        System.out.println("main : " + asActor);
    }
}
