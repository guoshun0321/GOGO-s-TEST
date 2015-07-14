package com.gogo.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: 刘永健
 * Date: 12-10-3
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */

/**
 * 表示一个任务管理器
 */
public class TaskManager implements TaskController {
	private TaskExecutor executor; // 任务执行器
	private Thread executorThread; // 用于执行任务的工作线程
	private int lenOfTaskQueue = Integer.MAX_VALUE; // 任务队列的长度,默认为无限大

	public TaskManager() {
	}

	public TaskManager(int lenOfTaskQueue) {
		this.lenOfTaskQueue = lenOfTaskQueue;
	}

	public TaskManager(TaskExecutor executor) {
		this.executor = executor;
	}

	public TaskExecutor getExecutor() {
		if (executor == null) {
			init();
		}
		return executor;
	}

	private class DefaultTaskExecutor implements TaskExecutor {
		final private BlockingQueue<Task> taskQueue; // 任务队列，所有待执行的任务都会被放入这个队列，等待执行

		public DefaultTaskExecutor() {
			this(Integer.MAX_VALUE);
		}

		public DefaultTaskExecutor(int len) {
			taskQueue = new LinkedBlockingQueue<Task>(len);
		}

		@Override
		public void submit(Task task) {
			if (!isStop) {
				try {
					taskQueue.put(task);
				} catch (InterruptedException e) {
					e.printStackTrace(); // To change body of catch statement
											// use File | Settings | File
											// Templates.
				}
			} else {
				throw new IllegalStateException("This executor is not alive.");
			}
		}

		@Override
		public void execute() {
			isRunning = true;
			taskQueue.clear();
			System.out.println("Start to process task ... ");
			while (!isShutdown && (!isStop || taskQueue.size() > 0)) {
				try {
					Task task = taskQueue.take();
					// System.out.println("Tak a task to execute , now the length of  task queue is "
					// + taskQueue.size());
					task.execute();
				} catch (InterruptedException e) {
					e.printStackTrace(); // To change body of catch statement
											// use File | Settings | File
											// Templates.
				}
			}
			System.out.println("Stop to process task ... ");
			isRunning = false;
		}

	}

	private boolean isStop = true; // 标识执行器是否接收新的任务
	private boolean isShutdown = true; // 标识执行器是否立即结束
	private boolean isRunning = false; // 标识执行器是否正在运行

	public void start() {
		if (executor == null) {
			init();
		}
		if (isStop && !isRunning) {
			isStop = false;
			isShutdown = false;
			executorThread = new Thread() {
				public void run() {
					executor.execute();
				}
			};
			executorThread.start();
		} else {
			throw new IllegalStateException("This executor is still alive or running, please stop or shutdown immediately and try it again later");
		}
	}

	/**
	 * 初始化一个任务执行器
	 */
	private void init() {
		executor = new DefaultTaskExecutor(lenOfTaskQueue);
	}

	public void stop() {
		isStop = true;
		executorThread.interrupt();
	}

	public void shutdown() {
		isStop = true;
		isShutdown = true;
		executorThread.interrupt();
	}

	@Override
	public boolean isStop() {
		return this.isStop;
	}

	@Override
	public boolean isShutdown() {
		return this.isShutdown;
	}

	@Override
	public boolean isRunning() {
		return this.isRunning;
	}

}
