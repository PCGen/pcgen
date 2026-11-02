package pcgen.gui3.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationExtension;

/** Verifies FXML module access and initialization of the hit point preferences. */
@ExtendWith(ApplicationExtension.class)
class HitPointsPreferencesTest
{

	@Test
	void fxmlCanAccessConstantsInTheApplicationModule() throws Exception
	{
		// Unit tests run on the classpath, so also check the compiled application's JPMS boundary.
		Path classes = Path.of(Constants.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		var descriptor = ModuleFinder.of(classes).find("pcgen").orElseThrow().descriptor();
		String packageName = Constants.class.getPackageName();
		String loaderModule = FXMLLoader.class.getModule().getName();
		boolean exported = descriptor.exports().stream().anyMatch(export ->
			export.source().equals(packageName)
				&& (!export.isQualified() || export.targets().contains(loaderModule)));
		boolean opened = descriptor.opens().stream().anyMatch(open ->
			open.source().equals(packageName)
				&& (!open.isQualified() || open.targets().contains(loaderModule)));
		assertTrue(exported || opened, "FXMLLoader must be able to read Constants.HP_*");
	}

	@ParameterizedTest
	@ValueSource(ints = {Constants.HP_AUTO_MAX, Constants.HP_AVERAGE, Constants.HP_PERCENTAGE,
		Constants.HP_STANDARD, Constants.HP_USER_ROLLED, Constants.HP_AVERAGE_ROUNDED_UP})
	void loadsAndRoundTripsPreferences(int method) throws Exception
	{
		LanguageBundle.getString("in_Prefs_hpGenLabel");
		int oldMethod = SettingsHandler.getHPRollMethod();
		int oldPercent = SettingsHandler.getHPPercent();
		boolean oldFirstLevel = SettingsHandler.isHPMaxAtFirstLevel();
		boolean oldFirstClassLevel = SettingsHandler.isHPMaxAtFirstClassLevel();
		try
		{
			SettingsHandler.setHPRollMethod(method);
			SettingsHandler.setHPPercent(73);
			SettingsHandler.setHPMaxAtFirstLevel(true);
			SettingsHandler.setHPMaxAtFirstClassLevel(false);
			asyncFx(() -> {
				FXMLLoader loader = new FXMLLoader(HitPointsPreferencesController.class
					.getResource("HitPointsPreferencesPanel.fxml"), LanguageBundle.getBundle());
				Scene scene = loader.load();
				assertTrue(scene.getRoot().getChildrenUnmodifiable().size() > 0);
				HitPointsPreferencesController controller = loader.getController();
				controller.reset();
				SettingsHandler.setHPRollMethod(-1);
				SettingsHandler.setHPPercent(1);
				SettingsHandler.setHPMaxAtFirstLevel(false);
				SettingsHandler.setHPMaxAtFirstClassLevel(true);
				controller.apply();
				return null;
			}).get(10, TimeUnit.SECONDS);
			assertEquals(method, SettingsHandler.getHPRollMethod());
			assertEquals(73, SettingsHandler.getHPPercent());
			assertTrue(SettingsHandler.isHPMaxAtFirstLevel());
			assertFalse(SettingsHandler.isHPMaxAtFirstClassLevel());
		}
		finally
		{
			SettingsHandler.setHPRollMethod(oldMethod);
			SettingsHandler.setHPPercent(oldPercent);
			SettingsHandler.setHPMaxAtFirstLevel(oldFirstLevel);
			SettingsHandler.setHPMaxAtFirstClassLevel(oldFirstClassLevel);
		}
	}
}
