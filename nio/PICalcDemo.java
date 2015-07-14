package com.gogo.event;

/**
 * User: 刘永健
 * Date: 12-10-3
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 * π/4 = 1 - 1/3 + 1/5 - 1/7 + 1/9 - 1/11 + … + (-1)^(n-1)/(2*n-1)
 */
public class PICalcDemo {
    public static void main(String[] args) {
        final TaskManager manager = new TaskManager();
        manager.start();
        TaskExecutor executor = manager.getExecutor();
         Task piTask = TaskHelper.createPiTask(executor, 10000);
        executor.submit(piTask);
    }

}

class PICalcTask extends TaskEventEmitter {
    private final int N;

    PICalcTask(TaskExecutor executor, int n) {
        super(executor);
        if (n < 1) throw new IllegalArgumentException("n must be larger than 0");
        this.N = n;
    }

    public int getN() {
        return N;
    }

    @Override
    protected void run() throws Exception {
        emit("next", 1);
    }
}