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
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
import org.xml.sax.InputSource;
import pcgen.core.facade.CharacterFacade;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui2.util.SwingWorker;
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
	private FutureTask refreshTask = null;
	private CssColor cssColor = CssColor.BLUE;

	public CharacterSheetPanel()
	{
		theRendererContext = new SimpleHtmlRendererContext(this, new SimpleUserAgentContext());
		theDocBuilder = new DocumentBuilderImpl(theRendererContext.getUserAgentContext(),
												theRendererContext);
		refreshService = Executors.newSingleThreadExecutor(new ThreadFactory()
		{

			public Thread newThread(Runnable r)
			{
				Thread thread = new Thread(r, "Charater-Sheet-Refresher-Thread");
				thread.setDaemon(true);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}

		});
	}

	public void setCharacter(CharacterFacade character)
	{
		this.character = character;
		refresh();
	}

	public void setCharacterSheet(File sheet)
	{
		handler = new ExportHandler(sheet);
		refresh();
	}

	public void refresh()
	{
		if (handler == null || character == null)
		{
			return;
		}
		if (refreshTask != null && !refreshTask.isDone())
		{
			refreshTask.cancel(true);
		}
		refreshTask = new RefreshTask();
		//SwingUtilities.invokeLater(refreshTask);
		refreshService.execute(refreshTask);
	}

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

					public void run()
					{
						Document doc = null;
						try
						{
							doc = get();
						}
						catch (Throwable e)
						{
							// TODO Auto-generated catch block
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
					}

				});
			}
		}

	}

	private class DocumentConstructor implements Callable<Document>
	{

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

	private class PipedRefreshWorker extends SwingWorker<Document> implements Runnable
	{

		private boolean interupted = false;
		private PipedReader reader = new PipedReader();

		public void run()
		{
			BufferedWriter bufWriter = null;
			try
			{
				PipedWriter writer = new PipedWriter(reader);
				bufWriter = new BufferedWriter(new ColorFilterWriter(writer));
				start();
				character.export(handler, bufWriter);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Unable to construct piped writer", ex);
			}
			finally
			{
				try
				{
					if (bufWriter != null)
					{
						bufWriter.close();
					}
				}
				catch (IOException ex)
				{
					Logging.errorPrint("Unable to close PipedWriter", ex);
				}
			}
			interupted = Thread.interrupted();
		}

		@Override
		public Document construct()
		{
			Document doc = null;
			try
			{
				URI root = new URI("file", ConfigurationSettings.getPreviewDir().replaceAll("\\\\", "/"), null);
				InputSource inputSource = new InputSourceImpl(reader, root.toString());

				return theDocBuilder.parse(inputSource);
			}
			catch (Throwable e)
			{
				// TODO Auto-generated catch block
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
			finally
			{
				try
				{
					reader.close();
				}
				catch (IOException ex)
				{
					Logging.errorPrint("Unable to close PipedReader", ex);
				}
			}
			return doc;
		}

		@Override
		public void finished()
		{
			if (!interupted)
			{
				Document doc = get();
				if (doc != null)
				{
					setDocument(doc, theRendererContext);
				}
			}
		}

	}

	private class ColorFilterWriter extends Writer
	{

		private final StringBuilder buffer = new StringBuilder();
		private Writer writer;

		public ColorFilterWriter(Writer writer)
		{
			this.writer = writer;
		}

		@Override
		public synchronized void write(char[] cbuf, int off, int len) throws IOException
		{
			buffer.append(cbuf, off, len);
			int index = buffer.indexOf(COLOR_TAG);
			if (index != -1)
			{
				buffer.replace(index, COLOR_TAG.length(), cssColor.getCssText());
			}
			int length = buffer.length() - COLOR_TAG.length();
			if (length > 0)
			{
				writer.write(buffer.substring(0, length));
				buffer.delete(0, length);
			}
		}

		@Override
		public void flush() throws IOException
		{
			writer.flush();
		}

		@Override
		public void close() throws IOException
		{
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		}

	}

}
