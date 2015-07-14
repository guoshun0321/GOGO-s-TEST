package gogo.test.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class ActorStop
{

    public static class PActor extends UntypedActor
    {

        private ActorRef child1;
        private ActorRef child2;
        private ActorRef child3;

        @Override
        public void onReceive(Object obj) throws Exception
        {
            if (obj instanceof String)
            {
                String msg = (String) obj;
                if (msg.equals("child"))
                {
                    child1 = getContext().actorOf(Props.create(CActor.class), "child1");
                    child2 = getContext().actorOf(Props.create(CActor.class), "child2");
                    child3 = getContext().actorOf(Props.create(CActor.class), "child3");
                    getContext().watch(child1);
                    getContext().watch(child2);
                    getContext().watch(child3);
                }
                else if (msg.equals("kill"))
                {
                    this.getContext().stop(child1);
                    this.getContext().stop(child2);
                    this.getContext().stop(child3);
                }
                else
                {
                    System.out.println("kill1 : " + obj);
                }
            }
            else if (obj instanceof Terminated)
            {
                System.out.println(getSender() + " teminate : " + obj);
            }
            else
            {
                System.out.println("other : " + obj);
            }
        }
    }

    public static class CActor extends UntypedActor
    {

        @Override
        public void preStart() throws Exception
        {
            System.out.println("create child");
        }

        @Override
        public void onReceive(Object obj) throws Exception
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void postStop() throws Exception
        {
            System.out.println("child stop");
        }
    }

    public static void main(String[] args) throws Exception
    {
        ActorSystem system = ActorSystem.create("stopSystem");
        ActorRef pActor = system.actorOf(Props.create(PActor.class), "parent");

        Inbox inbox = Inbox.create(system);
        inbox.send(pActor, "child");
        inbox.send(pActor, "kill");

    }

}
