package pcgen.gui2.plaf.osx;

import com.apple.eawt.AppEvent;
import com.apple.eawt.PreferencesHandler;
import pcgen.gui2.PCGenUIManager;

public class OSXPreferencesHandler implements PreferencesHandler {
    @Override
    public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
        PCGenUIManager.displayPreferencesDialog();
    }
}
