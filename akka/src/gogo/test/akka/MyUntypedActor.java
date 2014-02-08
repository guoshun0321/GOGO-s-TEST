package gogo.test.akka;

import akka.actor.UntypedActor;

public class MyUntypedActor extends UntypedActor
{

    @Override
    public void onReceive(Object message) throws Exception
    {
        if (message instanceof String)
        {
            System.out.println("receive message : " + message);
            getSender().tell(message, getSelf());
        }
        else
        {
            unhandled(message);
        }
    }

}
