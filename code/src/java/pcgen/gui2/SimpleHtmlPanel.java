package pcgen.gui2;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

/**
 * Displays HTML content as a "panel".
 */
final class SimpleHtmlPanel extends JFXPanel
{
	private WebView browser;

	SimpleHtmlPanel()
	{
		Platform.runLater(() -> {
			browser = new WebView();
			browser.setContextMenuEnabled(true);
			browser.getEngine().setJavaScriptEnabled(true);
			this.setScene(new Scene(browser));
		});
	}

	public void setHtml(String html)
	{
		Platform.runLater(() -> browser.getEngine().loadContent(html));
	}
}
