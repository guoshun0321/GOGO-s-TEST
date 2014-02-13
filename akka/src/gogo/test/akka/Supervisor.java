package gogo.test.akka;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.dispatch.sysmsg.Resume;
import akka.japi.Function;
import akka.pattern.Patterns;

public class Supervisor extends UntypedActor
{
    private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"), new Function<Throwable, Directive>()
    {
        public Directive apply(Throwable t)
        {
            if (t instanceof ArithmeticException)
            {
                return SupervisorStrategy.resume();
            }
            else if (t instanceof NullPointerException)
            {
                return SupervisorStrategy.restart();
            }
            else if (t instanceof IllegalArgumentException)
            {
                return SupervisorStrategy.stop();
            }
            else
            {
                return SupervisorStrategy.escalate();
            }
        }
    });

    @Override
    public SupervisorStrategy supervisorStrategy()
    {
        return strategy;
    }

    @Override
    public void onReceive(Object o) throws Exception
    {
        if (o instanceof Props)
        {
            getSender().tell(getContext().actorOf((Props) o), getSelf());
        }
        else
        {
            unhandled(o);
        }
    }

    public static void main(String[] args) throws Exception
    {
        Duration timeout = Duration.create(5, TimeUnit.SECONDS);
        ActorSystem system = ActorSystem.create("test");
        ActorRef supervisor = system.actorOf(Props.create(Supervisor.class), "supervisor");
        ActorRef child = (ActorRef) Await.result(Patterns.ask(supervisor, Props.create(Child.class), 5000), timeout);

        child.tell(42, ActorRef.noSender());
        System.out.println("result : " + Await.result(Patterns.ask(child, "get", 5000), timeout));
        child.tell(new ArithmeticException(), ActorRef.noSender());
        System.out.println("result : " + Await.result(Patterns.ask(child, "get", 5000), timeout));
        
        child.tell(new NullPointerException(), ActorRef.noSender());
        System.out.println("result : " + Await.result(Patterns.ask(child, "get", 5000), timeout));
        
        child.tell(new IllegalArgumentException(), ActorRef.noSender());
        System.out.println("result : " + Await.result(Patterns.ask(child, "get", 5000), timeout));
    }
}
