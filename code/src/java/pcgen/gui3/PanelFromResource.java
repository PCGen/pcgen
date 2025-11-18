package pcgen.gui3;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pcgen.system.LanguageBundle;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * A utility class for loading and displaying JavaFX scenes from FXML resources.
 * This class provides functionality to load FXML files, assign their controllers,
 * and display the loaded scenes in a new JavaFX stage.
 *
 * @param <T> The type of the controller associated with the FXML resource.
 */
public class PanelFromResource<T> implements Controllable<T>
{
	private static final Logger LOG = Logger.getLogger(PanelFromResource.class.getName());

	private final FXMLLoader fxmlLoader = new FXMLLoader();

	/**
	 * Constructs a new PanelFromResource instance.
	 *
	 * @param klass        The class relative to which the FXML resource is located.
	 * @param resourceName The name of the FXML resource file to load.
	 * @throws NullPointerException if the resource cannot be found.
	 */
	public PanelFromResource(Class<? extends T> klass, String resourceName)
	{
		URL resource = klass.getResource(resourceName);
		Objects.requireNonNull(resource,
				() -> MessageFormat.format("Resource {0} not found relative to class {1}", resourceName, klass));
		LOG.log(Level.FINE, () -> MessageFormat.format(
				"Loading a scene for resource name {0} (a class {1}). The final location is {2}", resourceName, klass,
				resource));
		fxmlLoader.setLocation(resource);
		fxmlLoader.setResources(LanguageBundle.getBundle());
	}

	/**
	 * Retrieves the controller associated with the loaded FXML resource.
	 *
	 * @return The controller instance.
	 * @throws IllegalStateException if this method is called outside the JavaFX application thread.
	 */
	@Override
	public T getController()
	{
		GuiAssertions.assertIsJavaFXThread();
		return fxmlLoader.getController();
	}

	/**
	 * Displays the loaded FXML resource as a new JavaFX stage.
	 *
	 * @param title The title of the stage to be displayed.
	 * @throws IllegalStateException if this method is called outside the JavaFX application thread.
	 */
	public void showAsStage(String title)
	{
		GuiAssertions.assertIsJavaFXThread();

		try
		{
			// Load the scene from the FXML resource.
			Scene scene = fxmlLoader.load();

			// Create and configure a new stage.
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(scene);
			stage.sizeToScene();
			stage.show();
		} catch (IOException e)
		{
			LOG.log(Level.SEVERE,
					MessageFormat.format("Failed to load stream fxml from location {0})", fxmlLoader.getLocation()),
					e);
		}
	}
}
