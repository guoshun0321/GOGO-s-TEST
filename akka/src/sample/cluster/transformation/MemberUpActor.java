package sample.cluster.transformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class MemberUpActor extends UntypedActor
{
    
    private static final Logger logger = LoggerFactory.getLogger(MemberUpActor.class);

    @Override
    public void preStart() throws Exception
    {
        logger.info("MemberUpActor START!!!");
    }

    @Override
    public void onReceive(Object arg0) throws Exception
    {
        // TODO Auto-generated method stub

    }

}
