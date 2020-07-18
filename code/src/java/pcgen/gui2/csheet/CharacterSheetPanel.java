/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.gui2.csheet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import pcgen.core.Globals;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.PCGenStatusBar;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui3.GuiAssertions;
import pcgen.io.ExportException;
import pcgen.io.ExportHandler;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

/**
 * This class is confusing because it is a model, view, and controller.
 * It also has a load of weird ideas about how handle threads,
 * for example calling the swing worker thread from the javafx thread.
 * We'll need a more developed sense of how to correctly handle this,
 * but it exists for now to force us to deal with the dual-platform tech.
 */
public final class CharacterSheetPanel extends JFXPanel implements CharacterSelectionListener
{
    private PreviewVariablesHandler previewVariableHandler = new PreviewVariablesHandler();
    private WebView browser;
    private CharacterFacade character;
    private ExportHandler handler;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public CharacterSheetPanel()
    {
        GuiAssertions.assertIsNotJavaFXThread();
        Platform.runLater(() -> {
            browser = new WebView();
            previewVariableHandler = new PreviewVariablesHandler();
            browser.setContextMenuEnabled(true);
            browser.getEngine().setJavaScriptEnabled(true);
            browser.getEngine().documentProperty().addListener(previewVariableHandler);
            this.setScene(new Scene(browser));
        });
    }

    public void setCharacterSheet(File sheet)
    {
        handler = (sheet == null) ? null : ExportHandler.createExportHandler(sheet);
    }

    /**
     * TODO: This is pseudo-async and can be strucutured much better.
     * TODO: handle progress reporting from the webview
     */
    public void refresh()
    {
        executor.execute(() -> {
            // loading of the output sheet is much faster than in the past (lobo-browser).
            // do we still really need a statusbar/progress bar?
            final PCGenStatusBar statusBar = ((PCGenFrame) Globals.getRootFrame()).getStatusBar();
            SwingUtilities.invokeLater(() ->
                    statusBar.startShowingProgress(LanguageBundle.getString("in_loadingCharacterPreview"), true)
            );

            String content;
            if (handler == null || character == null)
            {
                Logging.debugPrint("no character found");
                content = "<html><body>No Character Found.</body></html>";
            } else
                {
                try
                {
                    StringWriter out = new StringWriter();
                    BufferedWriter buf = new BufferedWriter(out);
                    Logging.debugPrint("ready to export");
                    character.export(handler, buf);
                    Logging.debugPrint("export complete");
                    content = out.toString();
                }
                catch (ExportException e)
                {
                    content = "<html><body>Exception when exporting</body></html>";
                    Logging.errorPrint("failed to export", e);
                }
            }

            final String finalContent = content;
            Platform.runLater(() -> {
                try
                {
                    Logging.debugPrint("loading character content");
                    browser.getEngine().loadContent(finalContent);
                }
                catch (Throwable e)
                {
                    Logging.errorPrint("Exception in GUI update", e);
                }
                SwingUtilities.invokeLater(statusBar::endShowingProgress);
            });
        });
    }

    @Override
    public void setCharacter(CharacterFacade character)
    {
        this.character = character;
        previewVariableHandler.setCharacter(character);
        refresh();
    }
}
