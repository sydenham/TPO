/**
 *
 *  @author Szarek Marcin S15541
 *
 */

package zad1;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
	  Runnable serverTask = () -> { 
			
			try {
				Server.main(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		new Thread(serverTask).start();

		Runnable clientTask = () -> {
			try {
				Client.main(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		new Thread(clientTask).start();
		new Thread(clientTask).start();
		new Thread(clientTask).start();
  }
}
