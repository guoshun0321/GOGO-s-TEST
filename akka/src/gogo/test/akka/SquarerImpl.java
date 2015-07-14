package gogo.test.akka;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.ConfigFactory;

import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.dispatch.Futures;
import akka.japi.Creator;
import akka.japi.Option;

public class SquarerImpl implements Squarer
{

    private String name;

    public SquarerImpl()
    {
        this.name = "default";
    }

    public SquarerImpl(String name)
    {
        this.name = name;
    }

    @Override
    public void squareDontCare(int i)
    {
        int sq = cal(i);
    }

    @Override
    public Future<Integer> square(int i)
    {
        return Futures.successful(cal(i));
    }

    @Override
    public Option<Integer> squareNowPlease(int i)
    {
        return Option.some(cal(i));
    }

    @Override
    public int squareNow(int i)
    {
        return cal(i);
    }

    public int cal(int i)
    {
        try
        {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (Exception ex)
        {

        }
        return i * i;
    }

    public static void main(String[] args)
    {
        ActorSystem system = ActorSystem.create("typed-system", ConfigFactory.load());
        Squarer mySquarer = TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class, SquarerImpl.class));
        Squarer otherSquarer = TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class, new Creator<SquarerImpl>()
        {
            @Override
            public SquarerImpl create() throws Exception
            {
                return new SquarerImpl("foo");
            }
        }));

        System.out.println("squareDontCare: " + new Date());
        mySquarer.squareDontCare(2);
        System.out.println("squareDontCare: " + new Date());

        System.out.println("square: " + new Date());
        Future<Integer> future = mySquarer.square(2);
        System.out.println("square: " + new Date());

        System.out.println("squareNowPlease: " + new Date());
        mySquarer.squareNowPlease(2);
        System.out.println("squareNowPlease: " + new Date());

        System.out.println("squareNow: " + new Date());
        mySquarer.squareNow(2);
        System.out.println("squareNow: " + new Date());
    }

}
