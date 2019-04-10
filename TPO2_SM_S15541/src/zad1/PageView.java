package zad1;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PageView extends Application {

	public static String url;

	public void getCity(String city) {
		url = new StringBuilder().append("https://en.wikipedia.org/wiki/").append(city).toString();
	}

	public void go() {
		Application.launch();
	}

	public void start(Stage stage) {
		WebView webView = new WebView();
		WebEngine engine = webView.getEngine();

		engine.load(url);
		VBox root = new VBox();

		root.getChildren().addAll(webView);

		Scene scene = new Scene(root, 800, 500);
		stage.setScene(scene);

		stage.show();

	}

}
