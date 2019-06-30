/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import javax.swing.SwingUtilities;

import pcgen.base.lang.UnreachableError;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.SimpleHtmlPanelController;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

public class HtmlSheetSupport
{

	private static final ThreadFactory THREAD_FACTORY = r -> {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		thread.setName("html-sheet-thread");
		return thread;
	};
	private ExecutorService executor = Executors.newSingleThreadExecutor(THREAD_FACTORY);

	private WeakReference<CharacterFacade> characterRef;
	private final File templateFile;
	private final JFXPanelFromResource<SimpleHtmlPanelController> htmlPane;
	private FutureTask<String> refresher = null;
	private boolean installed = false;
	private String missingSheetMsg;

	public HtmlSheetSupport(JFXPanelFromResource<SimpleHtmlPanelController> htmlPane, String infoSheetFile)
	{
		if (!StringUtils.isEmpty(infoSheetFile))
		{
			templateFile = new File(infoSheetFile);
		}
		else
		{
			templateFile = null;
		}
		this.htmlPane = htmlPane;
	}

	public HtmlSheetSupport(CharacterFacade character, JFXPanelFromResource<SimpleHtmlPanelController> htmlPane, String infoSheetFile)
	{
		this(htmlPane, infoSheetFile);
		setCharacter(character);
	}

	public void setCharacter(CharacterFacade character)
	{
		this.characterRef = new WeakReference<>(character);
	}

	public void install()
	{
		installed = true;
		refresh();
	}

	public void uninstall()
	{
		installed = false;
	}

	public void refresh()
	{
		if (templateFile == null)
		{
			htmlPane.getController().setHtml(missingSheetMsg);
			return;
		}
		if (characterRef == null || characterRef.get() == null)
		{
			return;
		}
		if (refresher != null && !refresher.isDone())
		{
			refresher.cancel(true);
		}
		refresher = new Refresher();
		executor.execute(refresher);
	}

	public void setMissingSheetMsg(String missingSheetMsg)
	{
		this.missingSheetMsg = missingSheetMsg;
	}

	private final class Refresher extends FutureTask<String>
	{

		private Refresher()
		{
			super(new DocumentBuilder());
		}

		@Override
		protected void done()
		{
			if (!installed || isCancelled())
			{
				return;
			}
			try
			{
				final String doc = get();
				SwingUtilities.invokeAndWait(() -> htmlPane.getController().setHtml(doc));
			}
			catch (InvocationTargetException ex)
			{
				throw new UnreachableError(ex);
			}
			catch (InterruptedException | ExecutionException ex)
			{
				Logging.errorPrint(templateFile.getName(), ex);
			}

		}

	}

	private class DocumentBuilder implements Callable<String>
	{

		@Override
		public String call() throws Exception
		{
			try (StringWriter writer = new StringWriter())
			{
				characterRef.get().export(ExportHandler.createExportHandler(templateFile), new BufferedWriter(writer));
				return writer.toString();
			}
		}

	}

}
