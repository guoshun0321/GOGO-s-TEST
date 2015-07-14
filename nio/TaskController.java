package com.gogo.event;

/**
 * User: 刘永健
 * Date: 12-10-3
 * Time: 下午1:48
 * To change this template use File | Settings | File Templates.
 */

/**
 * 任务控制器
 */
public interface TaskController {
	/**
	 * 启动任务管理器
	 */
	public void start();

	/**
	 * 停止任务管理器。当调用这个方法后，任务管理器不再接收新提交的任务，但会继续执行已提交的任务
	 */
	public void stop();

	/**
	 * 立即关闭任务管理，已提交且未开始执行的任务将会被丢弃
	 */
	public void shutdown();

	/**
	 * 任务管理器是否停止
	 * 
	 * @return
	 */
	public boolean isStop();

	/**
	 * 任务管理器
	 * 
	 * @return
	 */
	public boolean isShutdown();

	public boolean isRunning();
}
