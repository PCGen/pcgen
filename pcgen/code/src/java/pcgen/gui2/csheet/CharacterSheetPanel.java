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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.swing.SwingUtilities;

import pcgen.core.Globals;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.PCGenStatusBar;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.io.ExportException;
import pcgen.io.ExportHandler;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class CharacterSheetPanel extends HtmlPanel implements CharacterSelectionListener
{
	private enum CssColor
	{
		BLUE("preview_color_blue.css"),
		LIGHTBLUE("preview_color_light_blue.css"),
		GREEN("preview_color_green.css"),
		LIGHTGREEN("preview_color_light_green.css"),
		RED("preview_color_red.css"),
		LIGHTRED("preview_color_light_red.css"),
		YELLOW("preview_color_yellow.css"),
		LIGHTYELLOW("preview_color_light_yellow.css"),
		GREY("preview_color_grey.css"),
		LIGHTGREY("preview_color_light_grey.css");

		private final String cssfile;

		CssColor(String cssfile) {
			this.cssfile = cssfile;
		}

		public String getCssfile() {
			return this.cssfile;
		}
	}

	private final HtmlRendererContext theRendererContext;
	private final DocumentBuilderImpl theDocBuilder;
	private final CssColor cssColor = CssColor.BLUE;
	private static final String COLOR_TAG = "preview_color.css";
	private final ExecutorService refreshService;
	private ExportHandler handler;
	private CharacterFacade character;
	private FutureTask<Document> refreshTask;



	public CharacterSheetPanel()
	{
		theRendererContext = new SimpleHtmlRendererContext(this, new SimpleUserAgentContext());
		theDocBuilder = new DocumentBuilderImpl(theRendererContext.getUserAgentContext(),
			theRendererContext);

		// KAW TODO rewrite to use StatusWorker and PCGenTask for better progress display

		refreshService = Executors.newSingleThreadExecutor(r ->
		{
            Thread thread = new Thread(r, "Character-Sheet-Refresher-Thread");
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        });
	}

	@Override
	public void setCharacter(CharacterFacade character)
	{
		this.character = character;
	}

	public void setCharacterSheet(File sheet)
	{
		handler = (sheet == null) ? null : new ExportHandler(sheet);
	}

	public void refresh()
	{
		if (handler == null || character == null)
		{
			return;
		}
		final PCGenStatusBar statusBar = ((PCGenFrame) Globals.getRootFrame()).getStatusBar();

		// TODO externalize NLS strings
		final String taskName = "Refreshing character...";
		statusBar.startShowingProgress(taskName, true);

		if (refreshTask != null && !refreshTask.isDone())
		{
			refreshTask.cancel(true);
		}
		refreshTask = new RefreshTask();
		refreshService.execute(refreshTask);
	}

	// KAW TODO maybe rewrite to use PCGenTask instead?
	private class RefreshTask extends FutureTask<Document>
	{
		public RefreshTask()
		{
			super(new DocumentConstructor());
		}

		@Override
		protected void done()
		{
			if (!isCancelled())
			{
				SwingUtilities.invokeLater(() ->
				{
                    Document doc = null;
                    try
                    {
                        doc = get();
                    }
                    catch (Throwable e)
                    {
                        final String errorMsg = String.format("<html><body>Unable to process sheet<br>%s</body></html>", e);
                        try (InputStream instream = new ByteArrayInputStream(errorMsg.getBytes())) {
                            doc = theDocBuilder.parse(instream);
                        } catch (IOException | SAXException e1) {
                            e1.printStackTrace();
                        }
                        Logging.errorPrint("Unable to process sheet: ", e);
                    }
                    if (doc != null)
                    {
                        setDocument(doc, theRendererContext);
                    }

                    // Re-set status bar and end progress bar display
                    final PCGenStatusBar statusBar = ((PCGenFrame) Globals.getRootFrame()).getStatusBar();
                    statusBar.endShowingProgress();
                });
			}
		}
	}

	private class DocumentConstructor implements Callable<Document>
	{
		@Override
		public Document call() throws URISyntaxException, IOException, ExportException, SAXException {
			StringWriter out = new StringWriter();
			BufferedWriter buf = new BufferedWriter(out);
			character.export(handler, buf);

			final String genText = out.toString().replace(COLOR_TAG, cssColor.getCssfile());
			ByteArrayInputStream instream = new ByteArrayInputStream(genText.getBytes());

			URI root = new URI("file", ConfigurationSettings.getPreviewDir().replaceAll("\\\\", "/"), null);
			return theDocBuilder.parse(new InputSourceImpl(instream, root.toString(), "UTF-8"));
		}
	}
}
