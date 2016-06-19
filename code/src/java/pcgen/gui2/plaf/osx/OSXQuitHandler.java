package pcgen.gui2.plaf.osx;

import com.apple.eawt.AppEvent;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import pcgen.gui2.PCGenUIManager;

public class OSXQuitHandler implements QuitHandler {
    @Override
    public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
        PCGenUIManager.closePCGen();
    }
}
