package com.gogo.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * User: 刘永健
 * Date: 12-10-2
 * Time: 下午9:37
 * To change this template use File | Settings | File Templates.
 */

/**
 * 任务执行器
 */
public interface TaskExecutor extends Task {

	/**
	 * 提交一个任务
	 * 
	 * @param task
	 */
	public void submit(Task task);
}
