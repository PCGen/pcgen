/*
 * CharacterSheetPanel.java
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
 * Created on Aug 19, 2008, 3:06:38 PM
 */
package pcgen.gui2.csheet;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import javax.swing.SwingUtilities;

import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;

import pcgen.core.Globals;
import pcgen.core.facade.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.PCGenStatusBar;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.io.ExportHandler;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterSheetPanel extends HtmlPanel implements CharacterSelectionListener
{

	private static final String COLOR_TAG = "preview_color.css";

	private enum CssColor
	{

		BLUE,
		LIGHTBLUE,
		GREEN,
		LIGHTGREEN,
		RED,
		LIGHTRED,
		YELLOW,
		LIGHTYELLOW,
		GREY,
		LIGHTGREY;

		public String getCssText()
		{
			switch (this)
			{
				case BLUE:
					return "preview_color_blue.css";
				case LIGHTBLUE:
					return "preview_color_light_blue.css";
				case GREEN:
					return "preview_color_green.css";
				case LIGHTGREEN:
					return "preview_color_light_green.css";
				case RED:
					return "preview_color_red.css";
				case LIGHTRED:
					return "preview_color_light_red.css";
				case YELLOW:
					return "preview_color_yellow.css";
				case LIGHTYELLOW:
					return "preview_color_light_yellow.css";
				case GREY:
					return "preview_color_grey.css";
				case LIGHTGREY:
					return "preview_color_light_grey.css";
				default:
					throw new InternalError();
			}
		}

	}

	private final HtmlRendererContext theRendererContext;
	private final DocumentBuilderImpl theDocBuilder;
	private ExportHandler handler = null;
	private CharacterFacade character = null;
	private ExecutorService refreshService = null;
	private FutureTask<Document> refreshTask = null;
	private CssColor cssColor = CssColor.BLUE;

	public CharacterSheetPanel()
	{
		theRendererContext = new SimpleHtmlRendererContext(this, new SimpleUserAgentContext());
		theDocBuilder = new DocumentBuilderImpl(theRendererContext.getUserAgentContext(),
			theRendererContext);

		// KAW TODO rewrite to use StatusWorker and PCGenTask for better progress display

		refreshService = Executors.newSingleThreadExecutor(new ThreadFactory()
		{
			@Override
			public Thread newThread(Runnable r)
			{
				Thread thread = new Thread(r, "Character-Sheet-Refresher-Thread");
				thread.setDaemon(true);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		});
	}

	@Override
	public void setCharacter(CharacterFacade character)
	{
		this.character = character;
		//refresh();
	}

	public void setCharacterSheet(File sheet)
	{
		handler = sheet == null ? null : new ExportHandler(sheet);
	}

	/**
	 * 
	 */
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

	/**
	 *
	 */
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
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						Document doc = null;
						try
						{
							doc = get();
						}
						catch (Throwable e)
						{
							final String errorMsg = "<html><body>Unable to process sheet<br>" +
									e + "</body></html>";
							ByteArrayInputStream instream = new ByteArrayInputStream(errorMsg.getBytes());
							try
							{
								doc = theDocBuilder.parse(instream);
							}
							catch (Exception ex)
							{
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
					}
				});
			}
		}
	}

	private class DocumentConstructor implements Callable<Document>
	{
		@Override
		public Document call() throws Exception
		{
			StringWriter out = new StringWriter();
			BufferedWriter buf = new BufferedWriter(out);
			character.export(handler, buf);

			String genText = out.toString().replace(COLOR_TAG,
				cssColor.getCssText());
			ByteArrayInputStream instream = new ByteArrayInputStream(genText.getBytes());
			Document doc = null;

			URI root = new URI("file", ConfigurationSettings.getPreviewDir().replaceAll("\\\\", "/"), null);
			doc = theDocBuilder.parse(new InputSourceImpl(instream,
				root.toString(),
					"UTF-8"));
			return doc;
		}
	}
}
