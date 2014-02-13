package gogo.test.akka;

import akka.japi.Option;
import scala.concurrent.Future;

public interface Squarer
{

    public void squareDontCare(int i);

    public Future<Integer> square(int i);

    public Option<Integer> squareNowPlease(int i);

    public int squareNow(int i);
}
