package jetsennet.orm.executor;

public class Executors
{

    private static final SimpleExecutor simple = new SimpleExecutor();

    private static final BatchExecutor batch = new BatchExecutor();

    public static IExecutor getSimpleExecutor()
    {
        return simple;
    }

    public static IExecutor getBatchExecutor()
    {
        return batch;
    }

}
