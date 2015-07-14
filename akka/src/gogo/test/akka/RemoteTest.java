package gogo.test.akka;

import java.util.Arrays;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

public class RemoteTest
{

    public static class ExampleActor extends UntypedActor
    {

        @Override
        public void preStart() throws Exception
        {
            System.out.println("preStart");
            System.out.println("self : " + this.getSelf());
            System.out.println("parent : " + this.getContext().parent());
        }

        @Override
        public void onReceive(Object arg0) throws Exception
        {
            System.out.println("receive msg " + arg0 + " in actor " + getSelf().path().name());
        }
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("system");
        //        ActorRef router = system.actorOf(Props.create(ExampleActor.class).withRouter(new akka.routing.FromConfig()), "myrouter1");
        ActorRef router = system.actorOf(Props.create(ExampleActor.class).withRouter(new RoundRobinRouter(5)), "myrouter1");
        //        ActorRef actor1 = system.actorOf(Props.create(ExampleActor.class), "actor1");
        //        System.out.println(actor1);
        //        ActorRef actor2 = system.actorOf(Props.create(ExampleActor.class), "actor2");
        //        ActorRef actor3 = system.actorOf(Props.create(ExampleActor.class), "actor3");
        //        Iterable<ActorRef> routees = Arrays.asList(new ActorRef[] {actor1, actor2, actor3});
        //        Iterable<String> routees = Arrays.asList(new String[] {"/user/actor1", "/user/actor1", "/user/actor1"});
        //        ActorRef route1 = system.actorOf(Props.empty().withRouter(new RoundRobinRouter(routees)));
        
        Inbox inbox = Inbox.create(system);
        inbox.send(router, "msg1");
        inbox.send(router, "msg2");
        inbox.send(router, "msg3");
        inbox.send(router, "msg4");
        inbox.send(router, "msg5");
        inbox.send(router, "msg6");
        
        system.shutdown();
    }

}
