package gogo.test.net;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.ClusterDomainEvent;
import akka.cluster.ClusterEvent.CurrentClusterState;

public class SampleClusterApp
{

    public static class ClusterActor extends UntypedActor
    {
        @Override
        public void onReceive(Object obj) throws Exception
        {
            System.out.println("rec : " + obj);
        }
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("ClusterSystem");
        ActorRef listener = system.actorOf(Props.create(ClusterActor.class), "clusterListener");

        Cluster.get(system).subscribe(listener, CurrentClusterState.class);
    }

}
