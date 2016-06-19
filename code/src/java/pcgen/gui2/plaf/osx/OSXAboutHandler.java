package pcgen.gui2.plaf.osx;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import pcgen.gui2.PCGenUIManager;

public class OSXAboutHandler implements AboutHandler {
    @Override
    public void handleAbout(AppEvent.AboutEvent aboutEvent) {
        PCGenUIManager.displayAboutDialog();
    }
}

