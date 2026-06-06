package pcgen.gui3;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pcgen.system.LanguageBundle;

import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;
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
 * <p>Unlike {@link JFXPanelFromResource}, this class does <strong>not</strong> extend
 * {@code JFXPanel}, so the loaded {@link Scene} is never wrapped in an embedded
 * scene peer. This avoids the {@code EmbeddedScene.sceneState} NPE that occurs
 * on macOS HiDPI displays when an embedded scene is later re-parented onto a
 * top-level {@link Stage}. Use this class for any dialog that is shown via
 * {@link #showAsStage(String)} or {@link #showAndBlock(String)}.
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
	 * The controller is only available after one of the {@code show*} methods has
	 * been invoked (which is when the FXML file is actually loaded).
	 *
	 * <p>This method may be called from any thread; if invoked off the JavaFX
	 * application thread it will bounce to it and wait for the result.
	 *
	 * @return The controller instance, or {@code null} if no FXML has been loaded yet.
	 */
	@Override
	public T getController()
	{
		if (Platform.isFxApplicationThread())
		{
			return fxmlLoader.getController();
		}
		return GuiUtility.runOnJavaFXThreadNow(fxmlLoader::getController);
	}

	/**
	 * Displays the loaded FXML resource as a new non-modal JavaFX stage.
	 *
	 * <p>This method may be called from any thread. If invoked off the JavaFX
	 * application thread, the stage creation and display are dispatched there
	 * via {@link Platform#runLater(Runnable)} and this call returns immediately.
	 *
	 * @param title The title of the stage to be displayed.
	 */
	public void showAsStage(String title)
	{
		if (Platform.isFxApplicationThread())
		{
			showAsStageOnFxThread(title);
		}
		else
		{
			Platform.runLater(() -> showAsStageOnFxThread(title));
		}
	}

	private void showAsStageOnFxThread(String title)
	{
		try
		{
			Scene scene = fxmlLoader.load();

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

	/**
	 * Displays the loaded FXML resource as a modal JavaFX stage, blocking the
	 * calling thread until the user closes the dialog.
	 *
	 * <p>Must <strong>not</strong> be called from the JavaFX application thread;
	 * doing so would deadlock because {@code Stage.showAndWait()} requires the
	 * FX thread to remain available to pump events.
	 *
	 * @param title The title of the stage to be displayed.
	 * @throws RuntimeException if this method is called on the JavaFX application thread.
	 */
	public void showAndBlock(String title)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		CompletableFuture<Integer> lock = new CompletableFuture<>();
		Platform.runLater(() -> {
			try
			{
				Scene scene = fxmlLoader.load();

				Stage stage = new Stage();
				stage.setTitle(title);
				stage.setScene(scene);
				stage.sizeToScene();
				stage.showAndWait();
			} catch (IOException e)
			{
				LOG.log(Level.SEVERE,
						MessageFormat.format("Failed to load stream fxml from location {0})", fxmlLoader.getLocation()),
						e);
			} finally
			{
				lock.complete(0);
			}
		});
		lock.join();
	}
}
