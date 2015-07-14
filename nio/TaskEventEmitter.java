package com.gogo.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: 刘永健 Date: 12-10-2 Time: 下午11:34 To change this template use File |
 * Settings | File Templates.
 */
abstract public class TaskEventEmitter implements Task, EventEmitter {
	private Map<String, List<EventHandler>> eventHandlerMap = new HashMap<String, List<EventHandler>>();
	final private TaskExecutor executor;

	public TaskEventEmitter(TaskExecutor executor) {
		this.executor = executor;
		addDefaultExceptionHandlers();
	}

	protected void addDefaultExceptionHandlers() {
		EventHandler eh = new EventHandler() {
			@Override
			public void handle(EventObject event) {
				((Exception) event.getSource()).printStackTrace();
			}
		};
		on(Exception.class.getName(), eh);
		on(IOException.class.getName(), eh);
		on(RuntimeException.class.getName(), eh);
		on(NullPointerException.class.getName(), eh);
		on(IndexOutOfBoundsException.class.getName(), eh);
	}

	/**
	 * 注册状态的处理方式
	 */
	@Override
	public void on(String eventName, EventHandler handler) {
		if (!eventHandlerMap.containsKey(eventName)) {
			List<EventHandler> eventHandlerList = new LinkedList<EventHandler>();
			eventHandlerMap.put(eventName, eventHandlerList);
		}
		eventHandlerMap.get(eventName).add(handler);
	}

	/**
	 * 移除对某状态的处理
	 */
	@Override
	public void remove(String eventName) {
		eventHandlerMap.remove(eventName);
	}

	@Override
	public void emit(final String eventName, final Object... args) {
		if (eventHandlerMap.containsKey(eventName)) {
			List<EventHandler> eventHandlerList = eventHandlerMap.get(eventName);
			for (final EventHandler handler : eventHandlerList) {
				executor.submit(new Task() {
					@Override
					public void execute() {
						handler.handle(new EventObject(eventName, TaskEventEmitter.this, args));
					}
				});
			}
		} else {
			System.out.println("No event handler listen this event: " + eventName);
			if (args[0] instanceof Exception) {
				((Exception) args[0]).printStackTrace();
			}
		}
	}

	@Override
	public void execute() {
		try {
			run();
		} catch (Exception e) {
			emit(e.getClass().getName(), e);
		}
	}

	abstract protected void run() throws Exception;
}
