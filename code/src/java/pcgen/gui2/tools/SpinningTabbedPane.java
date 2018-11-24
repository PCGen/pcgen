/*
 *
 * Copyright 2002, 2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 */
package pcgen.gui2.tools; // hm.binkley.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.TabbedPaneUI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * {@code SpinningTabbedPane}.
 */
public class SpinningTabbedPane extends JTabbedPane
{

	private static final long serialVersionUID = 4980035692406423131L;
	private static final int PLACE_OFFSET = 0;
	private static final int MOVE_LEFT_RIGHT_OFFSET = 4;
	private static final int MOVE_UP_DOWN_OFFSET = 8;
	private static final int GROUP_OFFSET = 12;
	private static final int TAB_OFFSET = 16;
	private static final int UNGROUP_CHILD_OFFSET = 20;
	private static final int UNGROUP_SELF_OFFSET = 24;
	private static final int UNGROUP_SINGLE_OFFSET = 28;
	private static final String[] LABELS = {LanguageBundle.getString("in_top"), // place
		LanguageBundle.getString("in_left"), LanguageBundle.getString("in_bottom"),
		LanguageBundle.getString("in_right"), LanguageBundle.getString("in_beginning"), // move left/right
		LanguageBundle.getString("in_left"), LanguageBundle.getString("in_end"), LanguageBundle.getString("in_right"),
		LanguageBundle.getString("in_top"), // move up/down
		LanguageBundle.getString("in_up"), LanguageBundle.getString("in_bottom"), LanguageBundle.getString("in_down"),
		LanguageBundle.getString("in_up"), // group
		LanguageBundle.getString("in_left"), LanguageBundle.getString("in_down"), LanguageBundle.getString("in_right"),
		null, // tab
		null, null, null, LanguageBundle.getString("in_ungroupTop"), // ungroup child
		LanguageBundle.getString("in_ungroupLeft"), LanguageBundle.getString("in_ungroupBottom"),
		LanguageBundle.getString("in_ungroupRight"), LanguageBundle.getString("in_ungroupTop"), // ungroup self
		LanguageBundle.getString("in_ungroupLeft"), LanguageBundle.getString("in_ungroupBottom"),
		LanguageBundle.getString("in_ungroupRight"), LanguageBundle.getString("in_ungroupUp"), // ungroup single
		LanguageBundle.getString("in_ungroupLeft"), LanguageBundle.getString("in_ungroupDown"),
		LanguageBundle.getString("in_ungroupRight")};
	private static final ImageIcon[] ICONS = {Utilities.UP_ICON, // place
		Utilities.LEFT_ICON, Utilities.DOWN_ICON, Utilities.RIGHT_ICON, Utilities.BEGINNING_ICON, // move left/right
		Utilities.LEFT_ICON, Utilities.END_ICON, Utilities.RIGHT_ICON, Utilities.TOP_ICON, // move up/down
		Utilities.UP_ICON, Utilities.BOTTOM_ICON, Utilities.DOWN_ICON, Utilities.UP_ICON, // group
		Utilities.LEFT_ICON, Utilities.DOWN_ICON, Utilities.RIGHT_ICON, Utilities.UP_ICON, // tab
		Utilities.LEFT_ICON, Utilities.DOWN_ICON, Utilities.RIGHT_ICON, Utilities.TOP_ICON, // ungroup child
		Utilities.BEGINNING_ICON, Utilities.BOTTOM_ICON, Utilities.END_ICON, Utilities.TOP_ICON, // ungroup self
		Utilities.BEGINNING_ICON, Utilities.BOTTOM_ICON, Utilities.END_ICON, Utilities.UP_ICON, // ungroup single
		Utilities.LEFT_ICON, Utilities.DOWN_ICON, Utilities.RIGHT_ICON};
	private static final String[] TIPS =
			{LanguageBundle.getString("in_spinTips1"), LanguageBundle.getString("in_spinTips2"),
				LanguageBundle.getString("in_spinTips3"), LanguageBundle.getString("in_spinTips4"),
				LanguageBundle.getString("in_spinTips5"), LanguageBundle.getString("in_spinTips6"),
				LanguageBundle.getString("in_spinTips7"), LanguageBundle.getString("in_spinTips8"),
				LanguageBundle.getString("in_spinTips9"), LanguageBundle.getString("in_spinTips10"),
				LanguageBundle.getString("in_spinTips11"), LanguageBundle.getString("in_spinTips12"),
				LanguageBundle.getString("in_spinTips13"), LanguageBundle.getString("in_spinTips14"),
				LanguageBundle.getString("in_spinTips15"), LanguageBundle.getString("in_spinTips16"),
				LanguageBundle.getString("in_spinTips17"), LanguageBundle.getString("in_spinTips18"),
				LanguageBundle.getString("in_spinTips19"), LanguageBundle.getString("in_spinTips20"),
				LanguageBundle.getString("in_spinTips21"), LanguageBundle.getString("in_spinTips22"),
				LanguageBundle.getString("in_spinTips23"), LanguageBundle.getString("in_spinTips24"),
				LanguageBundle.getString("in_spinTips25"), LanguageBundle.getString("in_spinTips26"),
				LanguageBundle.getString("in_spinTips27"), LanguageBundle.getString("in_spinTips28"),
				LanguageBundle.getString("in_spinTips29"), LanguageBundle.getString("in_spinTips30"),
				LanguageBundle.getString("in_spinTips31"), LanguageBundle.getString("in_spinTips32")};
	private final PopupMenuPolicy policy = new DefaultPopupMenuPolicy();
	private final Set<Component> locked = new HashSet<>();
	@Nullable
	private SpinningTabbedPane parent = null;

	public SpinningTabbedPane()
	{
		addMouseListener(new PopupListener());
	}

	@Override
	public final void setTabPlacement(int placement)
	{
		super.setTabPlacement(placement);

		if (parent != null)
		{
			parent.updateTabUIAt(parent.indexOfComponent(this));
		}
	}

	@Override
	public final void setTitleAt(int index, @Nullable String title)
	{
		String extra = getExtraTitleAt(index);

		if (extra != null)
		{
			if ((title == null) || (title.isEmpty()))
			{
				title = extra;
			}
			else
			{
				// Separate extra from the title with one space.
				title += (" " + extra);
			}
		}

		super.setTitleAt(index, title);
	}

	/**
	 * Returns the tab index corresponding to the tab whose bounds
	 * intersect the specified location.  Returns -1 if no tab
	 * intersects the location.
	 * NB: This method provides to JDK1.3 the JTabbedPane.indexAtLocation
	 * method first provided in JDK 1.4. The method interface cannot be
	 * changed.
	 *
	 * Must be public in order to overrride javax.swing.JTabbedPane.
	 *
	 * @param x the x location relative to this tabbedpane
	 * @param y the y location relative to this tabbedpane
	 * @return the tab index which intersects the location, or
	 *         -1 if no tab intersects the location
	 */
	@Override
	public final int indexAtLocation(int x, int y)
	{
		if (ui != null)
		{
			return ((TabbedPaneUI) ui).tabForCoordinate(this, x, y);
		}

		return -1;
	}

	private static void setMenuItem(@NotNull JMenuItem menuItem, int offset)
	{
		String label = LABELS[offset];

		menuItem.setText(label);

		if (label != null)
		{
			menuItem.setMnemonic(label.charAt(0));
		}

		menuItem.setIcon(ICONS[offset]);
		menuItem.setToolTipText(TIPS[offset]);
	}

	// Need to use action events instead  XXX
	@NotNull
	private static SpinningTabbedPane createPane()
	{
		return new SpinningTabbedPane();
	}

	private static int offsetForPlacement(int placement)
	{
		return placement - 1;
	}

	private static int placementForSlot(int slot, int placement)
	{
		return ((placement - 1 + slot) % 4) + 1;
	}

	private String getExtraTitleAt(int index)
	{
		Component c = getComponentAt(index);

		return (c instanceof SpinningTabbedPane) ? ("(" + ((SpinningTabbedPane) c).getSpinTabCount() + ")") : null;
	}

	private int getMovableTabCount()
	{
		int n = 0;

		for (int i = 0, x = getTabCount(); i < x; ++i)
		{
			if (policy.canMove(i) && !isTabLockedAt(i))
			{
				++n;
			}
		}

		return n;
	}

	@NotNull
	private int[] getMovableTabIndices()
	{
		int x = getTabCount();
		int[] list1 = new int[x];
		int n = 0;

		for (int i = 0; i < x; ++i)
		{
			if (policy.canMove(i) && !isTabLockedAt(i))
			{
				list1[n++] = i;
			}
		}

		return Arrays.copyOf(list1, n - 1);
	}

	private String getPlainTitleAt(int index)
	{
		String title = getTitleAt(index);
		Component c = getComponentAt(index);

		if (title == null)
		{
			return "";
		}

		if ((title.isEmpty()) || !(c instanceof SpinningTabbedPane))
		{
			return title;
		}

		String extra = getExtraTitleAt(index);

		if (title.length() == extra.length())
		{
			return "";
		}

		// Stip extra and the one space separating it from the title.
		return title.substring(0, title.length() - extra.length() - 1);
	}

	private int getSpinTabCount()
	{
		int n = getTabCount();

		for (int i = 0, x = n; i < x; ++i)
		{
			Component c = getComponentAt(i);

			if (!(c instanceof SpinningTabbedPane))
			{
				continue;
			}

			n -= 1; // don't count the spun tab itself
			n += ((SpinningTabbedPane) c).getSpinTabCount();
		}

		return n;
	}

	private int getSpinTabPlacementAt(int index)
	{
		Component c = getComponentAt(index);

		if (c instanceof SpinningTabbedPane)
		{
			return ((SpinningTabbedPane) c).getTabPlacement();
		}

		return -1;
	}

	private boolean isTabLockedAt(int index)
	{
		return locked.contains(getComponentAt(index));
	}

	/**
	 * Spin tabs starting at index to the end, stopping when we find
	 * another spun tab.  If the first tab is itself spun, spin it as
	 * well; this permits spinning of spun tabs as a special case.
	 *
	 * @param index start spinning tabs here
	 * @param placement direction to place spun tabs
	 */
	private void spinTabsAt(int index, int placement)
	{
		SpinningTabbedPane pane = SpinningTabbedPane.createPane();

		moveTabAtTo(index, -1, pane);

		for (int i = index, x = getTabCount(); i < x; ++i)
		{
			Component c = getComponentAt(index);

			if (c instanceof SpinningTabbedPane)
			{
				break;
			}

			moveTabAtTo(index, -1, pane);
		}

		add(pane, index);
		setTitleAt(index, ""); // get count added

		pane.parent = this;
		pane.setTabPlacement(placement);

		updateTabUIAt(index);
	}

	private void unlockTabAt(int index)
	{
		locked.remove(getComponentAt(index));
		setIconAt(index, null);
	}

	/**
	 * Unspin all tabs in this pane.
	 */
	private void unspinAll()
	{
		int parentIndex = parent.indexOfComponent(this);

		parent.removeTabAt(parentIndex);

		for (int x = getTabCount(); --x >= 0;)
		{
			moveTabAtTo(x, parentIndex, parent);
		}

		parent = null; // help GC
	}

	private void unspinTabAt(int index)
	{
		if (getTabCount() == 1)
		{
			unspinAll();
		}
		else
		{
			int parentIndex = parent.indexOfComponent(this);
			moveTabAtTo(index, parentIndex, parent);
			parent.updateTabUIAt(parentIndex + 1);
		}
	}

	private void moveTabAtTo(int fromIndex, int toIndex, @NotNull JTabbedPane to)
	{
		Component c = getComponentAt(fromIndex);

		// Reparent incase we are unspinning a grandchild
		if (c instanceof SpinningTabbedPane)
		{
			((SpinningTabbedPane) c).parent = this;
		}

		Color background = getBackgroundAt(fromIndex);
		Icon disabledIcon = getDisabledIconAt(fromIndex);
		Color foreground = getForegroundAt(fromIndex);
		Icon icon = getIconAt(fromIndex);
		String title = getTitleAt(fromIndex);
		String tip = getToolTipTextAt(fromIndex);

		removeTabAt(fromIndex);

		if (toIndex == -1)
		{
			toIndex = to.getTabCount();
		}

		to.add(c, toIndex);

		to.setBackgroundAt(toIndex, background);
		to.setDisabledIconAt(toIndex, disabledIcon);
		to.setForegroundAt(toIndex, foreground);
		to.setIconAt(toIndex, icon);
		to.setTitleAt(toIndex, title);
		to.setToolTipTextAt(toIndex, tip);

		//	int displayedMnemonicIndex = getDisplayedMnemonicIndexAt(fromIndex);
		//	int mnemonic = getMnemonicAt(fromIndex);
		//	if (mnemonic != -1)
		//	    to.setMnemonicAt(toIndex, mnemonic);
		//	if (displayedMnemonicIndex != -1)
		//	    to.setDisplayedMnemonicIndexAt(toIndex, displayedMnemonicIndex);
	}

	private void updateTabUIAt(int index)
	{
		SpinningTabbedPane pane = (SpinningTabbedPane) getComponentAt(index);

		int offset = SpinningTabbedPane.offsetForPlacement(pane.getTabPlacement()) + SpinningTabbedPane.TAB_OFFSET;

		setTitleAt(index, getPlainTitleAt(index));
		setIconAt(index, SpinningTabbedPane.ICONS[offset]);
		setToolTipTextAt(index, SpinningTabbedPane.TIPS[offset]);
	}

	public interface PopupMenuPolicy
	{

		boolean canClose(int index);

		boolean canGroup(int index);

		boolean canLock(int index);

		boolean canMove(int index);

		boolean canNew(int index);

		boolean canRename(int index);

		boolean hasGroupMenu(int index, MouseEvent e);

		boolean hasMoveMenu(int index, MouseEvent e);

		boolean hasPlaceMenu(int index, MouseEvent e);

	}

	private final class DefaultPopupMenuPolicy implements PopupMenuPolicy
	{

		@Override
		public boolean canClose(int index)
		{
			return true;
		}

		@Override
		public boolean canGroup(int index)
		{
			return true;
		}

		@Override
		public boolean canLock(int index)
		{
			return true;
		}

		@Override
		public boolean canMove(int index)
		{
			return true;
		}

		@Override
		public boolean canNew(int index)
		{
			return true;
		}

		@Override
		public boolean canRename(int index)
		{
			return true;
		}

		@Override
		public boolean hasGroupMenu(int index, MouseEvent e)
		{
			return true;
		}

		@Override
		public boolean hasMoveMenu(int index, MouseEvent e)
		{
			return true;
		}

		@Override
		public boolean hasPlaceMenu(int index, MouseEvent e)
		{
			return true;
		}

	}

	private final class CloseAction extends IndexedAction
	{

		private CloseAction(int index)
		{
			super(index, LanguageBundle.getString("in_close"), Utilities.CLOSE_ICON,
				LanguageBundle.getMnemonic("in_mn_close"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			removeTabAt(getIndex());
		}

	}

	private class GroupMenu extends JMenu
	{

		GroupMenu(int index, int placement)
		{
			super(LanguageBundle.getString("in_groupTabs"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_groupTabs"));

			Component c = SpinningTabbedPane.this.getComponentAt(index);
			boolean first = true;

			if (parent == null)
			{ // we are not spun

				if (c instanceof SpinningTabbedPane)
				{ // tab is spun
					add(new JMenuItem(new UngroupChildAction(index)));
					addSeparator();

					// Add backwards to get clockwise choices; skip
					// tab's own direction since already spun
					for (int j = 3; j > 0; --j)
					{
						add(new JMenuItem(new PlaceAction((SpinningTabbedPane) c,
							SpinningTabbedPane.placementForSlot(j, placement))));
					}

					first = false;
				}
			}
			else
			{ // we are spun
				add(new JMenuItem(new UngroupSelfAction()));

				if (c instanceof SpinningTabbedPane) // tab is spun
				{
					add(new JMenuItem(new UngroupChildAction(index)));
				}
				else // tab is not spun
				{
					add(new JMenuItem(new UngroupSingleAction(index)));
				}

				first = false;
			}

			if (policy.canGroup(index))
			{
				if (!first)
				{
					addSeparator();
				}

				// Add backwards to get clockwise choices
				for (int j = 4; j > 0; --j)
				{
					add(new JMenuItem(new GroupAction(index, SpinningTabbedPane.placementForSlot(j, placement))));
				}
			}
		}

	}

	private final class GroupAction extends IndexedAction
	{

		private final int placement;

		private GroupAction(int index, int placement)
		{
			super(index, SpinningTabbedPane.offsetForPlacement(placement) + SpinningTabbedPane.GROUP_OFFSET);
			this.placement = placement;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			spinTabsAt(getIndex(), placement);
			setSelectedIndex(getIndex());
		}

	}

	private final class LockAction extends IndexedAction
	{

		private void lockTabAt(int index)
		{
			locked.add(getComponentAt(index));
			setIconAt(index, Utilities.LOCK_ICON);
		}

		private LockAction(int index)
		{
			super(index, LanguageBundle.getString("in_lock"), Utilities.LOCK_ICON,
				LanguageBundle.getMnemonic("in_mn_lock"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			lockTabAt(getIndex());
		}

	}

	private class MoveActionListener implements ActionListener
	{

		int index;
		int placement;

		MoveActionListener(int index, int placement)
		{
			this.index = index;
			this.placement = placement;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			final int[] indices = getMovableTabIndices();
			int i = -1;

			switch (placement)
			{
				case SwingConstants.TOP:
					i = indices[0];
					break;
				case SwingConstants.LEFT:
					i = previous(index, indices);
					break;
				case SwingConstants.BOTTOM:
					i = indices[indices.length - 1];
					break;
				case SwingConstants.RIGHT:
					i = next(index, indices);
					break;
				default:
					//Case not caught, should this cause an error?
					break;
			}

			moveTabAtTo(index, i, SpinningTabbedPane.this);
			setSelectedIndex(i);
		}

		private int next(int current, @NotNull int[] indices)
		{
			for (int i = 0, x = indices.length - 1; i < x; ++i)
			{
				if (current == indices[i])
				{
					return indices[i + 1];
				}
			}

			return -1;
		}

		private int previous(int current, @NotNull int[] indices)
		{
			for (int i = 1; i < indices.length; ++i)
			{
				if (current == indices[i])
				{
					return indices[i - 1];
				}
			}

			return -1;
		}

	}

	private class MoveMenu extends JMenu
	{

		MoveMenu(int index)
		{
			super(LanguageBundle.getString("in_moveTab")); //$NON-NLS-1$
			setMnemonic(LanguageBundle.getMnemonic("in_mn_moveTab")); //$NON-NLS-1$

			final int[] indices = getMovableTabIndices();

			// Only you can prevent out of range errors.
			int primum = -1;
			int secundum = -1;
			int penultimatum = -1;
			int ultimatum = -1;

			switch (indices.length)
			{
				case 0:
					setEnabled(false);

					break;

				case 1:
					setEnabled(false);

					break;

				case 2:
					primum = indices[0];
					secundum = Integer.MAX_VALUE;
					penultimatum = Integer.MIN_VALUE;
					ultimatum = indices[1];

					break;

				case 3:
					primum = indices[0];
					secundum = penultimatum = indices[1];
					ultimatum = indices[2];

					break;

				default:
					primum = indices[0];
					secundum = indices[1];
					penultimatum = indices[indices.length - 2];
					ultimatum = indices[indices.length - 1];
					break;
			}

			for (int indice : indices)
			{
				if (index < indice)
				{
					continue;
				}

				if (index > primum)
				{
					if (index > secundum)
					{
						add(new MoveTabMenuItem(index, SwingConstants.TOP));
					}

					add(new MoveTabMenuItem(index, SwingConstants.LEFT));
				}

				if (index < ultimatum)
				{
					add(new MoveTabMenuItem(index, SwingConstants.RIGHT));

					if (index < penultimatum)
					{
						add(new MoveTabMenuItem(index, SwingConstants.BOTTOM));
					}
				}

				break;
			}
		}

	}

	private class MoveTabMenuItem extends JMenuItem
	{

		MoveTabMenuItem(int index, int placement)
		{
			int offset = SpinningTabbedPane.offsetForPlacement(placement);

			switch (getTabPlacement())
			{
				case SwingConstants.TOP:
				case SwingConstants.BOTTOM:
					offset += SpinningTabbedPane.MOVE_LEFT_RIGHT_OFFSET;
					break;
				case SwingConstants.LEFT:
				case SwingConstants.RIGHT:
					offset += SpinningTabbedPane.MOVE_UP_DOWN_OFFSET;
					break;
				default:
					//Case not caught, should this cause an error?
					break;
			}

			addActionListener(new MoveActionListener(index, placement));
			SpinningTabbedPane.setMenuItem(this, offset);
		}

	}

	private final class NewAction extends IndexedAction
	{

		private void addNewTab()
		{
			add(new JPanel());
		}

		private NewAction()
		{
			super(0, LanguageBundle.getString("in_new"), Utilities.NEW_ICON, LanguageBundle.getMnemonic("in_mn_new"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			addNewTab();
		}

	}

	private class PlaceMenu extends JMenu
	{

		PlaceMenu(int placement)
		{
			super(LanguageBundle.getString("in_placeTabs"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_placeTabs"));

			// Add backwards to get clockwise choices
			for (int j = 3; j > 0; --j)
			{
				add(new JMenuItem(
					new PlaceAction(SpinningTabbedPane.this, SpinningTabbedPane.placementForSlot(j, placement))));
			}
		}

	}

	private final class PlaceAction extends IndexedAction
	{

		private final SpinningTabbedPane pane;
		private final int placement;

		private PlaceAction(SpinningTabbedPane pane, int placement)
		{
			super(0, SpinningTabbedPane.offsetForPlacement(placement) + SpinningTabbedPane.PLACE_OFFSET);
			this.pane = pane;
			this.placement = placement;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			pane.setTabPlacement(placement);
		}

	}

	private class PopupListener extends MouseAdapter
	{

		@Override
		public void mousePressed(@NotNull MouseEvent e)
		{
			if (Utilities.isRightMouseButton(e))
			{
				final int x = e.getX();
				final int y = e.getY();
				final int index = indexAtLocation(x, y);
				final int aTabPlacement = getTabPlacement();

				JMenuItem newMenuItem = null;

				if (policy.canNew(index))
				{
					newMenuItem = new JMenuItem(new NewAction());
				}

				JMenu moveMenu = null;
				JMenu groupMenu = null;
				JMenuItem renameMenuItem = null;
				JMenuItem lockMenuItem = null;
				JMenuItem closeMenuItem = null;
				if (index >= 0)
				{
					final int spinTabPlacement = getSpinTabPlacementAt(index);

					if (policy.canClose(index) && !isTabLockedAt(index))
					{
						closeMenuItem = new JMenuItem(new CloseAction(index));
					}

					if (policy.canLock(index))
					{
						lockMenuItem = isTabLockedAt(index) ? new JMenuItem(new UnlockAction(index))
							: new JMenuItem(new LockAction(index));
					}

					if (policy.canRename(index))
					{
						renameMenuItem = new JMenuItem(new RenameAction(index, e));
					}

					if (policy.hasGroupMenu(index, e) && !isTabLockedAt(index))
					{
						groupMenu = new GroupMenu(index, (spinTabPlacement == -1) ? aTabPlacement : spinTabPlacement);
					}

					if (policy.hasMoveMenu(index, e) && (getMovableTabCount() > 1) && !isTabLockedAt(index))
					{
						moveMenu = new MoveMenu(index);
					}
				}

				JMenu placeMenu = null;
				if (policy.hasPlaceMenu(index, e))
				{
					placeMenu = new PlaceMenu(aTabPlacement);
				}

				final boolean useNewMenuItem = newMenuItem != null;
				final boolean useCloseMenuItem = closeMenuItem != null;
				final boolean useLockMenuItem = lockMenuItem != null;
				final boolean useRenameMenuItem = renameMenuItem != null;
				boolean useGroupMenu = (groupMenu != null) && (groupMenu.getMenuComponentCount() > 0);
				final boolean useMoveMenu = (moveMenu != null) && (moveMenu.getMenuComponentCount() > 0);
				final boolean usePlaceMenu = (placeMenu != null) && (placeMenu.getMenuComponentCount() > 0);

				JPopupMenu popupMenu = new JPopupMenu();
				if ((popupMenu.getComponentCount() > 0) && (useNewMenuItem || useCloseMenuItem))
				{
					popupMenu.addSeparator();
				}

				if (useNewMenuItem)
				{
					popupMenu.add(newMenuItem);
				}

				if (useCloseMenuItem)
				{
					popupMenu.add(closeMenuItem);
				}

				if ((popupMenu.getComponentCount() > 0) && (useLockMenuItem || useRenameMenuItem))
				{
					popupMenu.addSeparator();
				}

				if (useLockMenuItem)
				{
					popupMenu.add(lockMenuItem);
				}

				if (useRenameMenuItem)
				{
					popupMenu.add(renameMenuItem);
				}

				if ((popupMenu.getComponentCount() > 0) && (useGroupMenu || useMoveMenu || usePlaceMenu))
				{
					popupMenu.addSeparator();
				}

				if (useGroupMenu)
				{
					popupMenu.add(groupMenu);
				}

				if (useMoveMenu)
				{
					popupMenu.add(moveMenu);
				}

				if (usePlaceMenu)
				{
					popupMenu.add(placeMenu);
				}

				//Commented out as the method contains no code.
				//addPopupMenuItems(popupMenu, index, e);
				popupMenu.show(e.getComponent(), x, y);
			}

			// As a shortcut, spin clockwise.
			else if (Utilities.isShiftLeftMouseButton(e))
			{
				final int index = indexAtLocation(e.getX(), e.getY());

				// 3 is magic; it's the next clock position. XXX
				spinTabsAt(index, SpinningTabbedPane.placementForSlot(3, getTabPlacement()));
				setSelectedIndex(index);
			}
		}

	}

	private final class RenameAction extends IndexedAction
	{

		private final MouseEvent evt;

		private RenameAction(int index, MouseEvent e)
		{
			super(index, LanguageBundle.getString("in_rename") + "...", null,
				LanguageBundle.getMnemonic("in_mn_rename"));
			this.evt = e;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int x = evt.getX();
			int y = evt.getY();
			String title = getPlainTitleAt(getIndex());
			JTextField textField = new JTextField(title);

			Logging.errorPrint("document? " + textField.getDocument());
			JPopupMenu popupMenu = new JPopupMenu();
			textField.addActionListener(new RenameTextFieldActionListener(getIndex(), textField, popupMenu));
			popupMenu.add(textField);

			Component c = evt.getComponent();

			// Because this doesn't have a width/height before being
			// shown, need to show it them move it.
			popupMenu.show(c, x, y);

			// These don't seem to work. ?? XXX
			textField.selectAll();
			textField.setCaretPosition(title.length());

			// Workaround bug in JDK1.4 (and earlier?): if JTextField
			// slops past edge of the pane window, you can't stick the
			// cursor in it.  XXX
			Component pane = getComponentAt(getIndex());
			Point paneLocation = pane.getLocationOnScreen();
			Point popupLocation = popupMenu.getLocationOnScreen();
			Dimension paneSize = pane.getSize();
			Dimension popupSize = popupMenu.getSize();
			boolean reshow = false;

			if ((popupLocation.x + popupSize.width) >= (paneLocation.x + paneSize.width))
			{
				reshow = true;
				x = (paneLocation.x + paneSize.width) - popupSize.width - 1;
			}

			if ((popupLocation.y + popupSize.height) >= (paneLocation.y + paneSize.height))
			{
				reshow = true;
				y = (paneLocation.y + paneSize.height) - popupSize.height - 1;
			}

			if (reshow)
			{
				popupMenu.show(c, x, y);
			}
		}

	}

	private class RenameTextFieldActionListener implements ActionListener
	{

		private final JPopupMenu popupMenu;
		private final JTextField textField;
		private final int anIndex;

		RenameTextFieldActionListener(int index, JTextField textField, JPopupMenu popupMenu)
		{
			this.anIndex = index;
			this.textField = textField;
			this.popupMenu = popupMenu;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setTitleAt(anIndex, textField.getText());
			popupMenu.setVisible(false); // why? XXX
		}

	}

	private final class UngroupChildAction extends IndexedAction
	{

		private UngroupChildAction(int index)
		{
			super(index,
				SpinningTabbedPane.offsetForPlacement(getTabPlacement()) + SpinningTabbedPane.UNGROUP_CHILD_OFFSET);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SpinningTabbedPane pane = (SpinningTabbedPane) getComponentAt(getIndex());
			final int newIndex = pane.getSelectedIndex();

			pane.unspinAll();

			setSelectedIndex(getIndex() + newIndex);
		}

	}

	private final class UngroupSelfAction extends IndexedAction
	{

		private UngroupSelfAction()
		{
			super(0, SpinningTabbedPane.offsetForPlacement(parent.getTabPlacement())
				+ SpinningTabbedPane.UNGROUP_SELF_OFFSET);

		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			final int index = parent.indexOfComponent(SpinningTabbedPane.this);
			final int newIndex = getSelectedIndex();
			SpinningTabbedPane aParent = parent;

			unspinAll();
			aParent.setSelectedIndex(index + newIndex);
		}

	}

	private final class UngroupSingleAction extends IndexedAction
	{

		private UngroupSingleAction(int index)
		{
			super(index, SpinningTabbedPane.offsetForPlacement(parent.getTabPlacement())
				+ SpinningTabbedPane.UNGROUP_SINGLE_OFFSET);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			unspinTabAt(getIndex());
			parent.setSelectedIndex(parent.indexOfComponent(SpinningTabbedPane.this) - 1);
		}

	}

	private final class UnlockAction extends IndexedAction
	{

		private UnlockAction(int index)
		{
			super(index, LanguageBundle.getString("in_unlock"), Utilities.LOCK_ICON,
				LanguageBundle.getMnemonic("in_mn_unlock"));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			unlockTabAt(getIndex());
		}

	}

	private abstract static class IndexedAction extends AbstractAction
	{

		private final int index;

		private IndexedAction(int index, int offset)
		{
			this(index, SpinningTabbedPane.LABELS[offset], SpinningTabbedPane.ICONS[offset]);
			String label = SpinningTabbedPane.LABELS[offset];
			if (label != null)
			{
				putValue(Action.MNEMONIC_KEY, (int) label.charAt(0));
			}
			putValue(Action.SHORT_DESCRIPTION, SpinningTabbedPane.TIPS[index]);
		}

		private IndexedAction(int index, String name, ImageIcon icon)
		{
			super(name, icon);
			this.index = index;
		}

		private IndexedAction(int index, String name, ImageIcon icon, int mnemonic)
		{
			this(index, name, icon);
			putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		public int getIndex()
		{
			return index;
		}

	}
}
