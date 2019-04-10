/**
 *
 *  @author Szarek Marcin S15541
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server {

	private static ServerSocketChannel ssc = null;
	private static Selector selector = null;
	static final int SERVER_PORT = 10000;
	static final String SERVER_URL = "localhost";
	// Strona kodowa do kodowania/dekodowania buforów
	private static Charset charset = Charset.forName("UTF-8");

	// Bufor bajtowy - do niego są wczytywane dane z kanału
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);

	// Tu będzie zlecenie do pezetworzenia
	private static StringBuffer reqString = new StringBuffer();

	public static void main(String[] args) throws IOException {
		new Server();
	}

	public Server() {

		try {
			// Utworzenie kanału dla gniazda serwera
			ssc = ServerSocketChannel.open();

			// Tryb nieblokujący
			ssc.configureBlocking(false);

			// Ustalenie adresu (host+port) gniazda kanału
			ssc.socket().bind(new InetSocketAddress(SERVER_URL, SERVER_PORT));

			// Utworzenie selektora
			selector = Selector.open();

			// Zarejestrowanie kanału do obsługi przez selektor
			// dla tego kanału interesuje nas tylko nawiązywanie połączeń
			// tryb - OP_ACCEPT
			ssc.register(selector, SelectionKey.OP_ACCEPT);

		} catch (Exception exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server started and ready for handling requests");
		try {
			serviceConnections();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void serviceConnections() throws IOException {
		while (true) {
			selector.select();
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();

			while (iter.hasNext()) {
				SelectionKey key = (SelectionKey) iter.next(); // pobranie klucza
				iter.remove(); // usuwamy, bo już
								// go zaraz obsłużymy

				if (key.isAcceptable()) { // jakiś klient chce się połączyć

					// Uzyskanie kanału do komunikacji z klientem
					// accept jest nieblokujące, bo już klient się zgłosił
					SocketChannel cc = ssc.accept();

					// Komunikacja z klientem - nieblokujące we/wy
					cc.configureBlocking(false);

					// rejestrujemy kanał komunikacji z klientem
					// do obsługi przez selektor
					// - typ zdarzenia - dane gotowe do czytania przez serwer
					cc.register(selector, SelectionKey.OP_READ);
					continue;
				}

				if (key.isReadable()) { // któryś z kanałów gotowy do czytania
					// Uzyskanie kanału na którym czekają dane do odczytania
					SocketChannel cc = (SocketChannel) key.channel();
					serviceRequest(cc); // obsluga zlecenia
					broadcast(reqString.toString());
					continue;
				}
			}
		}
	}

	private static void broadcast(String msg) throws IOException {
		ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
		for (SelectionKey key : selector.keys()) {
			if (key.isValid() && key.channel() instanceof SocketChannel) {
				SocketChannel channel = (SocketChannel) key.channel();
				channel.write(messageBuffer);
				messageBuffer.rewind();
			}
		}
	}

	private static void serviceRequest(SocketChannel sc) throws IOException {
		if (!sc.isOpen())
			return;
		reqString.setLength(0);
		sc.read(buffer);
		buffer.flip();
		CharBuffer cbuf = charset.decode(buffer);
		while (cbuf.hasRemaining()) {
			char c = cbuf.get();
			reqString.append(c);
		}
		buffer.clear();
	}
}
