package com.gogo.event;

import java.io.*;

/**
 * User: 刘永健 Date: 12-10-3 Time: 下午3:54 To change this template use File |
 * Settings | File Templates.
 */
public class IODemo {
	public static void main(String[] args) throws InterruptedException {
		String fileName = "info.txt";
		final TaskManager manager = new TaskManager();
		manager.start();
		TaskExecutor executor = manager.getExecutor();
		Task ioTask = TaskHelper.createIOTask(executor, fileName);
		executor.submit(ioTask);
		Thread.sleep(5000L);
		manager.stop();
	}
}

class IOTask extends TaskEventEmitter {
	final private String fileName;
	final private String encoding;

	public IOTask(TaskExecutor executor, String fileName, String encoding) {
		super(executor);
		this.fileName = fileName;
		this.encoding = encoding;
	}

	public String getFileName() {
		return fileName;
	}

	public String getEncoding() {
		return encoding;
	}

	@Override
	protected void run() throws Exception {
		InputStream fis = this.getClass().getResourceAsStream("/" + fileName);

		if (fis != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, encoding));
			emit("open", getFileName());
			emit("next", reader);
		} else {
			throw new FileNotFoundException(fileName);
		}
	}
}
