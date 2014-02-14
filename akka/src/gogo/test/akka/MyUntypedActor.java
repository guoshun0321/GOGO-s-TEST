package gogo.test.akka;

<<<<<<< HEAD
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
=======
import akka.actor.ActorSystem;
>>>>>>> 1de4a6b5fd4513771bc3f4735a563bb267367a35
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class MyUntypedActor extends UntypedActor {
	
	private String name;
	
	public MyUntypedActor(String name) {
		this.name = name;
	}
	
	public static Props mkProps(String name) {
		return Props.create(MyUntypedActor.class, name);
	}

<<<<<<< HEAD
    private String name;

    public MyUntypedActor()
    {
        // TODO Auto-generated constructor stub
    }

    public MyUntypedActor(String name)
    {
        this.name = name;
    }

    @Override
    public void onReceive(Object message) throws Exception
    {
        if (message instanceof String)
        {
            //            TimeUnit.SECONDS.sleep(3);
            System.out.println("receive message : " + getSender());
            getSender().tell("return message", getSelf());
        }
        else
        {
            unhandled(message);
        }
    }
=======
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			System.out.println("receive message : " + message);
			getSender().tell(message, getSelf());
		} else {
			unhandled(message);
		}
	}

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("system");
		system.actorOf(MyUntypedActor.mkProps("test"));
	}
>>>>>>> 1de4a6b5fd4513771bc3f4735a563bb267367a35

    static class MyCreator implements Creator<MyUntypedActor>
    {
        @Override
        public MyUntypedActor create() throws Exception
        {
            return new MyUntypedActor("creatorName");
        }
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("mySystem");
        System.out.println(system);

        ActorRef ref = system.actorOf(Props.create(MyUntypedActor.class), "refName");
        System.out.println(ref);

        Inbox inbox = Inbox.create(system);
        inbox.send(ref, "test");
        System.out.println(inbox.receive(Duration.create(10, TimeUnit.SECONDS)));

//        inbox.watch(ref);
//        inbox.send(ref, PoisonPill.getInstance());
//        System.out.println(inbox.receive(Duration.create(10, TimeUnit.SECONDS)));
        
        
        inbox.send(ref, PoisonPill.getInstance());
        System.out.println(inbox.receive(Duration.create(10, TimeUnit.SECONDS)));

        System.out.println("\nend");
    }

}
