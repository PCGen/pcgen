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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;

import pcgen.base.lang.UnreachableError;
import pcgen.facade.core.CharacterFacade;
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
	private final JEditorPane htmlPane;
	private ImageCache cache = new ImageCache();
	private FutureTask<HTMLDocument> refresher = null;
	private boolean installed = false;
	private String missingSheetMsg;

	public HtmlSheetSupport(JEditorPane htmlPane, String infoSheetFile)
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

	public HtmlSheetSupport(CharacterFacade character, JEditorPane htmlPane, String infoSheetFile)
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
			htmlPane.setText(missingSheetMsg);
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

	private class Refresher extends FutureTask<HTMLDocument>
	{

		public Refresher()
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
				final HTMLDocument doc = get();
				SwingUtilities.invokeAndWait(() -> htmlPane.setDocument(doc));
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

	private class DocumentBuilder implements Callable<HTMLDocument>
	{

		@Override
		public HTMLDocument call() throws Exception
		{
			StringWriter writer = new StringWriter();
			characterRef.get().export(new ExportHandler(templateFile), new BufferedWriter(writer));
			StringReader reader = new StringReader(writer.toString());
			EditorKit kit = htmlPane.getEditorKit();
			HTMLDocument doc = new HTMLDocument();

			doc.setBase(templateFile.getParentFile().toURI().toURL());
			doc.putProperty("IgnoreCharsetDirective", true);
			// XXX - This is a hack specific to Sun's JDK 5.0 and in no
			// way should be trusted to work in future java releases
			// (though it still might) - Connor Petty
			doc.putProperty("imageCache", cache);
			kit.read(reader, doc, 0);
			return doc;
		}

	}

	/**
	 * A cache for images loaded onto the info pane.
	 */
	private static class ImageCache extends Dictionary<URL, Image>
	{

		private HashMap<URL, Image> cache = new HashMap<>();

		@Override
		public int size()
		{
			return cache.size();
		}

		@Override
		public boolean isEmpty()
		{
			return cache.isEmpty();
		}

		@Override
		public Enumeration<URL> keys()
		{
			return Collections.enumeration(cache.keySet());
		}

		@Override
		public Enumeration<Image> elements()
		{
			return Collections.enumeration(cache.values());
		}

		@Override
		public Image get(Object key)
		{
			if (!(key instanceof URL))
			{
				return null;
			}
			URL src = (URL) key;
			if (!cache.containsKey(src))
			{
				Image newImage = Toolkit.getDefaultToolkit().createImage(src);
				if (newImage != null)
				{
					// Force the image to be loaded by using an ImageIcon.
					ImageIcon ii = new ImageIcon();
					ii.setImage(newImage);
				}
				cache.put(src, newImage);
			}
			return cache.get(src);
		}

		@Override
		public Image put(URL key, Image value)
		{
			return cache.put(key, value);
		}

		@Override
		public Image remove(Object key)
		{
			return cache.remove(key);
		}

	}

}
