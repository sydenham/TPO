/**
 *
 *  @author Szarek Marcin S15541
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

	private SocketChannel channel;
	static final int SERVER_PORT = 10000;
	static final String SERVER_URL = "localhost";
	private static Charset charset = Charset.forName("UTF-8");
	private String login;
	ByteBuffer buffer = ByteBuffer.allocate(1024);
	CharBuffer charBuffer;
	boolean userLogged = false;
	boolean serverRunning = true;
	Window window;

	public static void main(String[] args) throws IOException {
		new Client();
	}

	public Client() {
		getConnection();
		clientRunning();
	}

	private void getConnection() {
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(SERVER_URL, SERVER_PORT));
			System.out.print("Łącze się ...");
			while (!channel.finishConnect()) {
			}
		} catch (UnknownHostException exc) {
			System.err.println("Uknown host " + SERVER_URL);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		System.out.println("\nPołaczony");
		LoginWindow lw = new LoginWindow();
		lw.start(this);
	}

	protected void clientRunning() {
		while (true) {
			int count = 0;
			String message = "";
			boolean msgReady = false;
			try {
				while (serverRunning && channel.isOpen() && (count = channel.read(buffer)) > 0) {
					// flip the buffer to start reading
					buffer.flip();
					message += Charset.defaultCharset().decode(buffer);
					msgReady = true;
					buffer.clear();
				}
				if (userLogged && msgReady) {
					window.addText(message + '\n');
					// msgReady = false;
				}
			} catch (IOException e) {
				serverRunning = false;
				if (userLogged) {
					window.addText("Polaczenie zerwane. Zamknij czat i polacz sie ponownie");
				}
				System.exit(0);
			}
		}
	}

	protected void writeToServer(String text) {
		charBuffer = CharBuffer.wrap(login + ": " + text);
		buffer = charset.encode(charBuffer);
		try {
			channel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.clear();
	}

	protected void setLogin(String loginField) {
		window = new Window();
		window.start(loginField, this);
		login = loginField;
		userLogged = true;
	}
}
