package com.gogo.event;

/**
 * User: 刘永健
 * Date: 12-10-3
 * Time: 下午5:27
 * To change this template use File | Settings | File Templates.
 */
public class CompoundTaskDemo {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        TaskExecutor executor = manager.getExecutor();
        manager.start();
        TaskEventEmitter ioTask = TaskHelper.createIOTask(executor, "info.txt");
        TaskEventEmitter piTask = TaskHelper.createPiTask(executor, 100);
        final TaskEventEmitter guardTask = new GuardTask(manager, 2);
        EventHandler handler = new EventHandler() {
            @Override
            public void handle(EventObject event) {
                guardTask.emit("end");
            }
        };
        ioTask.on("close",handler);
        piTask.on("finish", handler);
        executor.submit(ioTask);
        executor.submit(piTask);
        executor.submit(guardTask);

    }
}
