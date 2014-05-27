package com.gogo.test.MyTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class NIOClient1 {
	/* 标识数字 */
	private static int flag = 0;
	/* 缓冲区大小 */
	private static int BLOCK = 4096;
	/* 接受数据缓冲区 */
	private static ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK);
	/* 发送数据缓冲区 */
	private static ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);
	/* 服务器端地址 */
	private final static InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("localhost", 8888);

	public static void main(String[] args) throws IOException {
		// 打开socket通道
		SocketChannel clientChannel = SocketChannel.open();
		// 设置为非阻塞方式
		clientChannel.configureBlocking(false);
		// 打开选择器
		Selector selector = Selector.open();
		// 注册连接服务端socket动作
		clientChannel.register(selector, SelectionKey.OP_CONNECT);
		// 连接
		clientChannel.connect(SERVER_ADDRESS);

		SocketChannel socketChannel;
		Set<SelectionKey> selectionKeys;
		String receiveText;
		String sendText;
		int count = 0;

		while (true) {
			// 选择一组键，其相应的通道已为 I/O 操作准备就绪。
			// 监控所有注册的 channel ，当其中有注册的 IO 操作可以进行时，该函数返回，并将对应的 SelectionKey 加入
			// selected-key set
			selector.select();
			// 返回此选择器的已选择键集。
			selectionKeys = selector.selectedKeys();
			// System.out.println(selectionKeys.size());
			for (SelectionKey selectionKey : selectionKeys) {
				// 判断是否为建立连接的事件
				if (selectionKey.isConnectable()) {
					System.out.println("client connect");
					socketChannel = (SocketChannel) selectionKey.channel(); //
					// 判断此通道上是否正在进行连接操作。
					// 完成套接字通道的连接过程。
					if (socketChannel.isConnectionPending()) {
						// 完成连接的建立（TCP三次握手）
						socketChannel.finishConnect();
						System.out.println("完成连接!");
						sendBuffer.clear();
						sendBuffer.put("Hello,Server".getBytes());
						sendBuffer.flip();
//						socketChannel.write(sendBuffer);
						socketChannel.register(selector, SelectionKey.OP_WRITE);
					}
//					socketChannel.register(selector, SelectionKey.OP_READ);
				} else if (selectionKey.isReadable()) {
					socketChannel = (SocketChannel) selectionKey.channel();
					// 将缓冲区清空以备下次读取
					receiveBuffer.clear();
					// 读取服务器发送来的数据到缓冲区中
					count = socketChannel.read(receiveBuffer);
					if (count > 0) {
						receiveText = new String(receiveBuffer.array(), 0, count);
						System.out.println("客户端接受服务器端数据--:" + receiveText);
						socketChannel.register(selector, SelectionKey.OP_WRITE);
					}
				} else if (selectionKey.isWritable()) {
					// isWritable 表示是否可写，一般不注册OP_WRITE
					sendBuffer.clear();
					socketChannel = (SocketChannel) selectionKey.channel();
					sendText = "message from client--" + (flag++);
					sendBuffer.put(sendText.getBytes());
					// 将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
					sendBuffer.flip();
					socketChannel.write(sendBuffer);
					System.out.println("客户端向服务器端发送数据--：" + sendText);
					socketChannel.register(selector, SelectionKey.OP_READ);
				}
			}
			selectionKeys.clear();
		}
	}
}