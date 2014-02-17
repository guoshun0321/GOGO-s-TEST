package gogo.test.akka;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;

public class FutrueTest
{

    public final static class PrintResult<T> extends OnSuccess<T>
    {
        @Override
        public final void onSuccess(T t)
        {
            System.out.println("success : " + t);
        }
    }

    public static void main(String[] args) throws Exception
    {
//        ActorSystem system = ActorSystem.create("system");
//        Future<String> f = Futures.future(new Callable<String>()
//        {
//            @Override
//            public String call() throws Exception
//            {
//                TimeUnit.SECONDS.sleep(5);
//                return "hello, world";
//            }
//        }, system.dispatcher());
//        f.onSuccess(new PrintResult<String>(), system.dispatcher());
//        System.out.println("end");
        Future<String> future = Futures.successful("Yep!");
        System.out.println(Await.result(future, Duration.create(5, TimeUnit.SECONDS)));
    }

}
