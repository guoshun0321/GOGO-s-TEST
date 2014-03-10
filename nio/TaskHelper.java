package com.gogo.event;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * User: 刘永健 Date: 12-10-3 Time: 下午5:28 To change this template use File |
 * Settings | File Templates.
 */
public class TaskHelper {

	public static TaskEventEmitter createIOTask(TaskExecutor executor, String fileName) {

		final IOTask task = new IOTask(executor, fileName, "UTF-8");

		task.on("open", new EventHandler() {
			@Override
			public void handle(EventObject event) {
				String fileName = (String) event.getArgs()[0];
				System.out.println(Thread.currentThread() + " - " + fileName + " has been opened.");
			}
		});
		task.on("next", new EventHandler() {
			@Override
			public void handle(EventObject event) {
				BufferedReader reader = (BufferedReader) event.getArgs()[0];
				try {
					String line = reader.readLine();
					if (line != null) {
						task.emit("ready", line);
						task.emit("next", reader);
					} else {
						task.emit("close", task.getFileName());
					}
				} catch (IOException e) {
					task.emit(e.getClass().getName(), e, task.getFileName());
					try {
						reader.close();
						task.emit("close", task.getFileName());
					} catch (IOException e1) {
						e1.printStackTrace(); // To change body of catch
												// statement use File | Settings
												// | File Templates.
					}
				}
			}
		});
		task.on("ready", new EventHandler() {
			@Override
			public void handle(EventObject event) {
				String line = (String) event.getArgs()[0];
				int len = line.length();
				int wordCount = line.split("[\\s+,.]+").length;
				System.out.println(Thread.currentThread() + " - word count: " + wordCount + " length: " + len);
			}
		});
		task.on(IOException.class.getName(), new EventHandler() {
			@Override
			public void handle(EventObject event) {
				Object[] args = event.getArgs();
				IOException e = (IOException) args[0];
				String fileName = (String) args[1];
				System.out.println(Thread.currentThread() + " - An IOException occurred while reading " + fileName + ", error: " + e.getMessage());
			}
		});
		task.on("close", new EventHandler() {
			@Override
			public void handle(EventObject event) {
				String fileName = (String) event.getArgs()[0];
				System.out.println(Thread.currentThread() + " - " + fileName + " has been closed.");
			}
		});
		task.on(FileNotFoundException.class.getName(), new EventHandler() {
			@Override
			public void handle(EventObject event) {
				FileNotFoundException e = (FileNotFoundException) event.getArgs()[0];
				e.printStackTrace();
				System.exit(1);
			}
		});
		return task;
	}

	public static TaskEventEmitter createPiTask(TaskExecutor executor, int n) {
		final PICalcTask task = new PICalcTask(executor, n);
		// 计算下一个级数项
		task.on("next", new EventHandler() {
			@Override
			public void handle(EventObject event) {
				int n = ((Integer) event.getArgs()[0]).intValue();
				double xn = Math.pow(-1, n - 1) / (2 * n - 1);
				task.emit("sum", xn);
			}
		});
		// 将每一个级数项加起来
		task.on("sum", new EventHandler() {
			private int i = 0;
			private double sum = 0;

			@Override
			public void handle(EventObject event) {
				double xn = ((Double) event.getArgs()[0]).doubleValue();
				sum += xn;
				i++;
				System.out.println(Thread.currentThread() + " - sum = " + sum);
				if (i >= task.getN()) {
					task.emit("finish", sum * 4);
				} else {
					task.emit("next", i + 1);
				}
			}
		});
		// 完成PI的近似计算
		task.on("finish", new EventHandler() {
			@Override
			public void handle(EventObject event) {
				Double sum = (Double) event.getArgs()[0];
				System.out.println("pi=" + sum);
			}
		});
		return task;
	}
}
