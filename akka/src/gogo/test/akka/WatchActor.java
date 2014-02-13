package gogo.test.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class WatchActor extends UntypedActor
{
    
    @Override
    public void preStart() throws Exception
    {
        ActorRef child = this.getContext().actorOf(Props.empty());
        this.getContext().watch(child);
    }
    
    @Override
    public void onReceive(Object arg0) throws Exception
    {
        // TODO Auto-generated method stub
        
    }
    
    

}
