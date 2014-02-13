package gogo.test.akka;

import akka.actor.UntypedActor;

public class Child extends UntypedActor
{

    int state = 0;

    @Override
    public void onReceive(Object o) throws Exception
    {
        if (o instanceof Exception)
        {
            throw (Exception) o;
        }
        else if (o instanceof Integer)
        {
            state = (Integer) o;
        }
        else if (o.equals("get"))
        {
            getSender().tell(state, getSelf());
        }
        else
        {
            unhandled(o);
        }
    }

}
