/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.summary;

import javax.swing.JEditorPane;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.GameModeFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tabs.models.HtmlSheetSupport;
import pcgen.system.LanguageBundle;

/**
 * Manages the information pane of the summary tab. This is an output sheet 
 * that is displayed in the summary tab to advise the user of important 
 * stats for their character. The output sheet to be displayed is specified in 
 * the game mode miscinfo.lst file using the INFOSHEET tag.
 *    
 */
public class InfoPaneHandler implements ReferenceListener<Object>,
		ListListener<CharacterLevelFacade>
{

	private boolean installed = false;
	private HtmlSheetSupport support;
	private String currentInfoTemplateFile;
	private CharacterFacade character;

	/**
	 * Create a new info pane handler instance for a character.
	 * @param character The character the pane is to display information for.
	 * @param htmlPane the pane that displays the information
	 */
	public InfoPaneHandler(CharacterFacade character, JEditorPane htmlPane)
	{
		this.character = character;
		GameModeFacade game = character.getDataSet().getGameMode();
		support = new HtmlSheetSupport(character, htmlPane, game.getInfoSheet());
		support.setMissingSheetMsg(LanguageBundle.getFormattedString("in_sumNoInfoSheet", //$NON-NLS-1$
																	 character.getDataSet().getGameMode().getName()));
		registerListeners();
	}

	/**
	 * Initialise our display component. Any expected UI behaviour/
	 * configuration is enforced here. Note that this is a utility function for
	 * use by SummaryInfoTab. While there is a handler for each character 
	 * displayed, there is only a single instance of each display component. 
	 * 
	 * @param htmlPane The editor panel that will display the sheet.
	 */
	public static void initializeEditorPane(JEditorPane htmlPane)
	{
		htmlPane.setOpaque(false);
		htmlPane.setEditable(false);
		htmlPane.setFocusable(false);
		htmlPane.setContentType("text/html"); //$NON-NLS-1$
	}

	/**
	 * Link this handler with our display component and schedule a refresh of 
	 * the contents for the character. 
	 */
	public void install()
	{
		support.install();
		installed = true;
		scheduleRefresh();
	}

	/**
	 * Register with the things we want to be notified of changes about. 
	 */
	private void registerListeners()
	{
		character.getRaceRef().addReferenceListener(this);
		character.getGenderRef().addReferenceListener(this);
		if (!character.getDataSet().getAlignments().isEmpty())
		{
			character.getAlignmentRef().addReferenceListener(this);
		}
		for (StatFacade stat : character.getDataSet().getStats())
		{
			character.getScoreBaseRef(stat).addReferenceListener(this);
		}
		character.getCharacterLevelsFacade().addListListener(this);
		character.getHandedRef().addReferenceListener(this);
		character.getAgeRef().addReferenceListener(this);
	}

	/**
	 * Start an update of the contents of the info pane for this character. The
	 * update will happen in a new thread and will not be started if one is 
	 * already running.  
	 */
	public void scheduleRefresh()
	{
		support.refresh();
	}

	/**
	 * Register that we are no longer the active character. 
	 */
	public void uninstall()
	{
		support.uninstall();
		installed = false;
	}

	/**
	 * @see pcgen.core.facade.event.ReferenceListener#referenceChanged(pcgen.core.facade.event.ReferenceEvent)
	 */
	@Override
	public void referenceChanged(ReferenceEvent<Object> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementAdded(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementAdded(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementRemoved(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementRemoved(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementModified(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementModified(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.core.facade.event.ListListener#elementsChanged(pcgen.core.facade.event.ListEvent)
	 */
	@Override
	public void elementsChanged(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}
//
//	/**
//	 * A cache for images loaded onto the info pane.
//	 */
//	private static class ImageCache extends Dictionary<URL, Image>
//	{
//
//		private HashMap<URL, Image> cache = new HashMap<URL, Image>();
//
//		@Override
//		public int size()
//		{
//			return cache.size();
//		}
//
//		@Override
//		public boolean isEmpty()
//		{
//			return cache.isEmpty();
//		}
//
//		@Override
//		public Enumeration<URL> keys()
//		{
//			return Collections.enumeration(cache.keySet());
//		}
//
//		@Override
//		public Enumeration<Image> elements()
//		{
//			return Collections.enumeration(cache.values());
//		}
//
//		@Override
//		public Image get(Object key)
//		{
//			if (!(key instanceof URL))
//			{
//				return null;
//			}
//			URL src = (URL) key;
//			if (!cache.containsKey(src))
//			{
//				Image newImage = Toolkit.getDefaultToolkit().createImage(src);
//				if (newImage != null)
//				{
//					// Force the image to be loaded by using an ImageIcon.
//					ImageIcon ii = new ImageIcon();
//					ii.setImage(newImage);
//				}
//				cache.put(src, newImage);
//			}
//			return cache.get(src);
//		}
//
//		@Override
//		public Image put(URL key, Image value)
//		{
//			return cache.put(key, value);
//		}
//
//		@Override
//		public Image remove(Object key)
//		{
//			return cache.remove(key);
//		}
//
//	}
//
//	private class TempInfoPaneRefresher implements Runnable
//	{
//
//		private File templateFile = new File(currentInfoTemplateFile);
//
//		public void run()
//		{
//			try
//			{
//				SwingUtilities.invokeAndWait(new Runnable()
//				{
//
//					public void run()
//					{
//						StringWriter writer = new StringWriter();
//						character.export(new ExportHandler(templateFile), new BufferedWriter(writer));
//						StringReader reader = new StringReader(writer.toString());
//						EditorKit kit = htmlPane.getEditorKit();
//						HTMLDocument doc = new HTMLDocument();
//						try
//						{
//							doc.setBase(templateFile.getParentFile().toURL());
//							// XXX - This is a hack specific to Sun's JDK 5.0 and in no
//							// way should be trusted to work in future java releases
//							// (though it still might) - Connor Petty
//							doc.putProperty("imageCache", cache);
//							kit.read(reader, doc, 0);
//						}
//						catch (IOException ex)
//						{
//							Logging.errorPrint("Could not get parent of load template file " +
//									"for info panel.", ex);
//						}
//						catch (BadLocationException ex)
//						{
//							//This should never happen
//						}
//						if (installed)
//						{
//							htmlPane.setDocument(doc);
//						}
//					}
//
//				});
//			}
//			catch (InterruptedException ex)
//			{
//				//do nothing
//			}
//			catch (InvocationTargetException ex)
//			{
//				//do nothing
//			}
//		}
//
//	}
//
//	private class InfoRefreshTask extends FutureTask<HTMLDocument>
//	{
//
//		public InfoRefreshTask()
//		{
//			super(new DocumentBuilder());
//		}
//
//		@Override
//		protected void done()
//		{
//			try
//			{
//				final HTMLDocument doc = get();
//				if (!isCancelled())
//				{
//					SwingUtilities.invokeLater(new Runnable()
//					{
//
//						public void run()
//						{
//							if (installed)
//							{
//								htmlPane.setDocument(doc);
//							}
//						}
//
//					});
//				}
//			}
//			catch (InterruptedException ex)
//			{
//				//This can't happen
//			}
//			catch (ExecutionException ex)
//			{
//				try
//				{
//					throw ex.getCause();
//				}
//				catch (InterruptedIOException e)
//				{
//					//this is normal ignore it
//				}
//				catch (IOException e)
//				{
//				}
//				catch (Throwable e)
//				{
//					Logging.errorPrint("Unexpected error occurred", e);
//				}
//
//			}
//		}
//
//	}
//
//	private class DocumentBuilder implements Callable<HTMLDocument>, Runnable
//	{
//
//		private File templateFile = new File(currentInfoTemplateFile);
//		private PipedWriter writer = new PipedWriter();
//
//		public HTMLDocument call() throws Exception
//		{
//			PipedReader reader = new PipedReader(writer);
//			new Thread(this).start();
//
//			EditorKit kit = htmlPane.getEditorKit();
//			HTMLDocument doc = new HTMLDocument();
//			doc.setBase(templateFile.getParentFile().toURL());
//			// XXX - This is a hack specific to Sun's JDK 5.0 and in no
//			// way should be trusted to work in future java releases
//			// (though it still might) - Connor Petty
//			doc.putProperty("imageCache", cache);
//			kit.read(reader, doc, 0);
//			reader.close();
//			return doc;
//		}
//
//		public void run()
//		{
//			BufferedWriter bw = new BufferedWriter(writer, 1);
//			try
//			{
//				character.export(new ExportHandler(templateFile), bw);
//			}
//			finally
//			{
//				try
//				{
//					bw.close();
//				}
//				catch (IOException ex)
//				{
//					Logging.errorPrint("Unable to close PipedWriter", ex);
//				}
//			}
//		}
//
//	}

}
