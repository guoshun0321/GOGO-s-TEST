package gogo.test.akka;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MyUntypedActor extends UntypedActor {
	
	private String name;
	
	public MyUntypedActor(String name) {
		this.name = name;
	}
	
	public static Props mkProps(String name) {
		return Props.create(MyUntypedActor.class, name);
	}

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

}
