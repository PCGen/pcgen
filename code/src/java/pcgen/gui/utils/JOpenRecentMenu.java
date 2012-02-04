/*
 * JOpenRecentMenu.java
 * Copyright 2001-2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 * Created on February 7th, 2002.
 */
package pcgen.gui.utils;

import pcgen.cdom.base.Constants;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  <code>JOpenRecentMenu</code> extends JMenu with one special coded
 *  for opening recent files
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
public class JOpenRecentMenu extends JMenu
{
	static final long serialVersionUID = -1385714650728604115L;
	private FixedArrayList<OpenRecentEntry> entries = null;
	private OpenRecentCallback cb = null;

	/**
	 * Constructor
	 * @param aCb
	 */
	public JOpenRecentMenu(OpenRecentCallback aCb)
	{
		standardMenuFeatures();
		setEnabled(false);
		cb = aCb;
		entries = new FixedArrayList<OpenRecentEntry>();
	}

	/**
	 * Set the recent entries
	 * @param strings
	 */
	public final void setEntriesAsStrings(String[] strings)
	{
		for (int i = 0; i < strings.length; i += 2)
		{
			try
			{
				add(strings[i], new File(strings[i + 1]));
			}
			catch (Exception e)
			{
				Logging.errorPrint("Error setting old character " + strings[i]
					+ ".", e);
			}
		}
	}

	/**
	 * Get recent entries as strings
	 * @return recent entries as string array
	 */
	public final String[] getEntriesAsStrings()
	{
		List<String> strings = new ArrayList<String>();

		if (entries != null)
		{
			for (int i = entries.size() - 1; i >= 0; --i)
			{
				final OpenRecentEntry entry = entries.get(i);
				strings.add(entry.displayAs);
				strings.add(entry.file.getAbsolutePath());
			}
		}

		return strings.toArray(new String[0]);
	}

	/**
	 * Add a new entry to the open recent menu.  If the entry is a
	 * duplicate, move it to the top.
	 *
	 * @param displayAs String the menu item label
	 * @param file File the <code>File</code> object
	 */
	public final void add(String displayAs, File file)
	{
		doAddUpdateEntry(new OpenRecentEntry(displayAs, file));
	}

	private void doAddEntry(OpenRecentEntry entry)
	{
		doRemoveEntry(entry); // move to top if possible
		entries.add(entry);
	}

	private void doAddUpdateEntry(OpenRecentEntry entry)
	{
		doAddEntry(entry);
		updateMenu();
	}

	private void doRemoveEntry(OpenRecentEntry entry)
	{
		for (int i = 0; i < entries.size(); ++i)
		{
			if (!(entries.get(i)).equals(entry))
			{
				continue;
			}

			entries.remove(i);

			break;
		}
	}

	private void doRemoveUpdateEntry(OpenRecentEntry entry)
	{
		doRemoveEntry(entry);
		updateMenu();
	}

	private void standardMenuFeatures()
	{
		setText(LanguageBundle.getString("in_mnuOpenRecent"));
		setMnemonic(LanguageBundle.getMnemonic("in_mn_mnuOpenRecent"));
		Utility.setDescription(this, LanguageBundle
			.getString("in_mnuOpenRecentTip"));
	}

	private final void updateMenu()
	{
		setEnabled(false);
		removeAll();

		final int x = entries.size();

		// Load in reverse order so most recent is at the top
		for (int i = 0; i < x; ++i)
		{
			add(createMenuItem(entries.get(i)));
		}

		if (x != 0)
		{
			setEnabled(true);
		}
	}

	private JMenuItem createMenuItem(OpenRecentEntry entry)
	{
		return Utility.createMenuItem(entry.displayAs,
			new OpenRecentActionListener(this, entry, cb), null, (char) 0,
			null, entry.file.getAbsolutePath(), null, true);
	}

	/**
	 * <code>OpenRecentCallback</code> is an
	 * <code>ActionListener</code> for menu items in the recently
	 * opened menu.
	 */
	public interface OpenRecentCallback
	{
		/**
		 * Handle the reopening of a recently opened file.
		 *
		 * @param e ActionEvent the menu selection event
		 * @param file File the recently opened file
		 */
		void openRecentPerformed(ActionEvent e, File file);
	}

	private static final class FixedArrayList<T> extends ArrayList<T>
	{
		// A rather incomplement implementation, I admit.  XXX --bko
		private int max = 0;

		FixedArrayList()
		{
			this(Constants.MAX_OPEN_RECENT_ENTRIES);
		}

		FixedArrayList(int max)
		{
			super(max);
			this.max = max;
		}

		public boolean add(T element)
		{
			//
			// Insert to the top and remove excess from the bottom
			//
			super.add(0, element);

			int size = FixedArrayList.this.size();

			while (size > max)
			{
				this.remove(--size);
			}

			return true;
		}
	}

	private static final class OpenRecentActionListener implements
			ActionListener
	{
		private JOpenRecentMenu menu = null;
		private OpenRecentCallback cb = null;
		private OpenRecentEntry entry = null;

		OpenRecentActionListener(JOpenRecentMenu aMenu,
			OpenRecentEntry anEntry, OpenRecentCallback aCb)
		{
			menu = aMenu;
			entry = anEntry;
			cb = aCb;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (entry.file.exists())
			{
				menu.doAddUpdateEntry(entry); // move to top
				cb.openRecentPerformed(e, entry.file);
			}
			else
			{
				menu.doRemoveUpdateEntry(entry);
			}
		}
	}

	private static final class OpenRecentEntry
	{
		private File file;
		private String displayAs;

		OpenRecentEntry(final String displayAs, final File file)
		{
			this.displayAs = displayAs;
			this.file = file;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (obj == null)
			{
				return false;
			}

			if (!(obj instanceof OpenRecentEntry))
			{
				return false;
			}

			final OpenRecentEntry entry = (OpenRecentEntry) obj;

			//if (!displayAs.equals(entry.displayAs))
			//{
			//	return false;
			//}
			return file.equals(entry.file);
		}

		/**
		 * As I have no idea what to do here, I simply return super.hashCode()
		 * TODO: Is that right?
		 * @return super.hashCode()
		 */
		@Override
		public int hashCode()
		{
			return super.hashCode();
		}
	}

	/**
	 * Remove an entry from the open recent menu.
	 *
	 * @param displayAs String the menu item label
	 * @param file File the <code>File</code> object
	 */

	//public final void remove(String displayAs, File file)
	//{
	//	doRemoveUpdateEntry(new OpenRecentEntry(displayAs, file));
	//}
}
