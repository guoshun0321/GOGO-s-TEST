package gogo.test.akka;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;

public class AskActor
{

    public static class ActorA extends UntypedActor
    {
        
        private int i = 0;
        
        @Override
        public void onReceive(Object obj) throws Exception
        {
            if(obj instanceof String) {
                System.out.println("message : " + i + " begin");
                TimeUnit.SECONDS.sleep(2);
                getSender().tell("message", getSelf());
                System.out.println("message : " + i + " end");
                i++;
            }
        }
    }

    public static class ActorB extends UntypedActor
    {
        @Override
        public void onReceive(Object arg0) throws Exception
        {
            // TODO Auto-generated method stub

        }
    }
    
    public static void main(String[] args) throws Exception
    {
        ActorSystem system = ActorSystem.create("system");
        ActorRef actorA = system.actorOf(Props.create(ActorA.class));
        ActorRef actorB = system.actorOf(Props.create(ActorB.class));
        
        Inbox inbox = Inbox.create(system);
//        Future<Object> future = Patterns.ask(actorA, "request", 10000);
//        Object obj = future.result(Duration.create(10, TimeUnit.SECONDS), null);
//        System.out.println("receive : " + obj);
        inbox.send(actorA, "msg");
        inbox.send(actorA, "msg");
//        system.stop(actorA);
        inbox.send(actorA, PoisonPill.getInstance());
        inbox.send(actorA, "msg");
        inbox.send(actorA, "msg");
        System.out.println("send over");
        
    }

}
