/*
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import pcgen.gui2.util.event.PopupMouseAdapter;
import pcgen.system.LanguageBundle;

/**
 * {@code FlippingSplitPane} is an improved version of
 * {@code JSplitPane} featuring a popup menu accesses by right-clicking on
 * the divider.
 * 
 * <p>({@code JSplitPane} is used to divide two (and only two)
 * {@code Component}s.  The two {@code Component}s are graphically
 * divided based on the look and feel implementation, and the two
 * {@code Component}s can then be interactively resized by the user.
 * Information on using {@code JSplitPane} is in <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/splitpane.html">How
 * to Use Split Panes</a> in <em>The Java Tutorial</em>.)
 * 
 * <p>In addition to the standard keyboard keys used by {@code JSplitPane},
 * {@code FlippingSplitPane} will flip the panes orientation on
 * {@code SHIFT-BUTTON1}.
 * 
 * <p>(For the keyboard keys used by {@code JSplitPane} in the standard Look
 * and Feel (L&amp;F) renditions, see the <a href="doc-files/Key-Index.html#JSplitPane">{@code JSplitPane}
 * key assignments</a>.)
 * 
 * <p>{@code FlippingSplitPane} treats many of the methods of
 * {@code JSplitPane} recursively, calling the same method on the left and
 * right components (or top and bottom for {@code VERTICAL_ORIENTATION}) if
 * they are also {@code FlippingSplitPane}s.  You can defeat this behavior
 * by using {@code JSplitPane} components instead.
 * 
 * <p>{@code FlippingSplitPane} also supports "locking": a locked pane renders
 * the divider unmovable, and the popup menu only has an "Unlocked" item.
 * Locking is also recursive for {@code FlippingSplitPane} components.
 *
 */
public class FlippingSplitPane extends JSplitPane
{
	private final LockAction lockAction = new LockAction();
	private JPopupMenu popupMenu;

	/**
	 * Creates a new {@code FlippingSplitPane}.  Panes begin as unlocked
	 */
	public FlippingSplitPane()
	{
		setupExtensions();
	}

	/**
	 * Creates a new {@code FlippingSplitPane}.  Panes begin as unlocked, and
	 * otherwise take the defaults of {@link JSplitPane#JSplitPane(int)}.
	 */
	public FlippingSplitPane(int newOrientation)
	{
		super(newOrientation);

		setupExtensions();
	}

	/**
	 * Creates a new {@code FlippingSplitPane}.  Panes begin as unlocked, and
	 * otherwise take the defaults of {@link JSplitPane#JSplitPane(int, Component,
	 * Component)}.
	 */
	public FlippingSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);

		setupExtensions();
	}

	/**
	 * Creates a new {@code FlippingSplitPane}.  Panes begin as unlocked, and
	 * otherwise take the defaults of {@link JSplitPane#JSplitPane(int, boolean,
	 * Component, Component)}.
	 */
	public FlippingSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent,
	                         Component newRightComponent)
	{
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);

		setupExtensions();
	}

	/**
	 * {@code setContinuousLayout} recursively calls {@link
	 * JSplitPane#setContinuousLayout(boolean)} on {@code FlippingSplitPane}
	 * components.
	 *
	 * @param newContinuousLayout {@code boolean}, the setting
	 */
	@Override
	public void setContinuousLayout(boolean newContinuousLayout)
	{
		if (newContinuousLayout == isContinuousLayout())
		{
			return;
		}

		super.setContinuousLayout(newContinuousLayout);
		maybeSetContinuousLayoutComponent(getLeftComponent(), newContinuousLayout);
		maybeSetContinuousLayoutComponent(getRightComponent(), newContinuousLayout);
	}

	/**
	 * {@code setDividerLocation} calls {@link JSplitPane#setDividerLocation(int)}
	 * unless the {@code FlippingSplitPane} is locked.
	 *
	 * @param location {@code int}, the location
	 */
	@Override
	public void setDividerLocation(int location)
	{
		if (isLocked())
		{
			super.setDividerLocation(getLastDividerLocation());
		}
		else
		{
			super.setDividerLocation(location);
		}
	}

	@Override
	public void setDividerSize(int newSize)
	{
		if (newSize == getDividerSize())
		{
			return;
		}

		super.setDividerSize(newSize);
		maybeSetDividerSizeComponent(getLeftComponent(), newSize);
		maybeSetDividerSizeComponent(getRightComponent(), newSize);
	}

	/**
	 * {@code setOneTouchExpandable} recursively calls {@link
	 * JSplitPane#setOneTouchExpandable(boolean)} on {@code FlippingSplitPane}
	 * components.
	 *
	 * @param newOneTouchExpandable {@code boolean}, the setting
	 */
	@Override
	public void setOneTouchExpandable(boolean newOneTouchExpandable)
	{
		if (newOneTouchExpandable == isOneTouchExpandable())
		{
			return;
		}

		super.setOneTouchExpandable(newOneTouchExpandable);
		maybeSetOneTouchExpandableComponent(getLeftComponent(), newOneTouchExpandable);
		maybeSetOneTouchExpandableComponent(getRightComponent(), newOneTouchExpandable);
	}

	/**
	 * {@code setOrientation} recursively calls {@link
	 * JSplitPane#setOrientation(int)} on {@code FlippingSplitPane}
	 * components, alternating the orietation so as to achieve a "criss-cross"
	 * affect.
	 *
	 * @param newOrientation {@code int}, the orientation
	 *
	 * @throws IllegalArgumentException if orientation is not one of:
	 * HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 */
	@Override
	public void setOrientation(int newOrientation)
	{
		if (newOrientation == getOrientation())
		{
			return;
		}

		super.setOrientation(newOrientation);

		int subOrientation = invertOrientation(newOrientation);
		maybeSetOrientationComponent(getLeftComponent(), subOrientation);
		maybeSetOrientationComponent(getRightComponent(), subOrientation);
	}

	/**
	 * {@code resetToPreferredSizes} recursively calls  on {@code FlippingSplitPane}
	 * components.
	 */
	@Override
	public void resetToPreferredSizes()
	{
		fixedResetToPreferredSizes();
		maybeResetToPreferredSizesComponent(getLeftComponent());
		maybeResetToPreferredSizesComponent(getRightComponent());
	}

	/**
	 * Center {@code FlippingSplitPane} components; do nothing for other
	 * components.
	 *
	 * @param c {@code Component}, the component.
	 */
	private static void maybeCenterDividerLocationsComponent(Component c)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).centerDividerLocations();
		}
	}

	/**
	 * {@code centerDividerLocations} sets the divider location in the middle
	 * by recursively calling {@code setDividerLocation(0.5)}.
	 *
	 * @see #setDividerLocation(double)
	 */
	private void centerDividerLocations()
	{
		setDividerLocation(0.5);
		maybeCenterDividerLocationsComponent(getLeftComponent());
		maybeCenterDividerLocationsComponent(getRightComponent());
	}

	/**
	 * Reset {@code FlippingSplitPane} components; do nothing for other
	 * components (not even {@code JSplitPane} components).
	 *
	 * @param c {@code Component}, the component.
	 */
	private static void maybeResetToPreferredSizesComponent(Component c)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).resetToPreferredSizes();
		}
	}

	/**
	 * {@code fixedResetToPreferredSizes} fixes a bug whereby flipping a pane
	 * from vertical to horizontal sets the divider location to {@code 1},
	 * thereby hiding the left component.
	 */
	private void fixedResetToPreferredSizes()
	{
		setDividerLocation((getMinimumDividerLocation() + getMaximumDividerLocation()) / 2);
	}

	/**
	 * {@code invertOrientation} is a convenience function to turn horizontal
	 * into vertical orientations and the converse.
	 *
	 * @param orientation {@code int}, either {@code HORIZONTAL_ORIENTATION}
	 * or {@code VERTICAL_ORIENTATION}
	 *
	 * @return {@code int}, the inverse
	 */
	private static int invertOrientation(int orientation)
	{
		return orientation == HORIZONTAL_SPLIT ? VERTICAL_SPLIT : HORIZONTAL_SPLIT;
	}

	/**
	 * Flip {@code FlippingSplitPane} components; do nothing for other
	 * components.
	 *
	 * @param c {@code Component}, the component.
	 */
	private static void maybeFlipComponent(Component c)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).flipOrientation();
		}
	}

	/**
	 * {@code flipOrientation} inverts the current orientation of the panes,
	 * recursively flipping {@code FlippingSplitPane} components.
	 */
	private void flipOrientation()
	{
		super.setOrientation(invertOrientation(getOrientation()));
		maybeFlipComponent(getLeftComponent());
		maybeFlipComponent(getRightComponent());

		resetToPreferredSizes(); // gets munched anyway?  XXX
	}

	private static void maybeSetDividerSizeComponent(Component c, int size)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setDividerSize(size);
		}
	}

	/**
	 * Set continuous layout for {@code FlippingSplitPane} components; do
	 * nothing for other components (not even {@code JSplitPane} components).
	 *
	 * @param c {@code Component}, the component
	 * @param newContinuousLayout {@code boolean}, the setting
	 */
	private static void maybeSetContinuousLayoutComponent(Component c, boolean newContinuousLayout)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setContinuousLayout(newContinuousLayout);
		}
	}

	/**
	 * Set one touch expandable for {@code FlippingSplitPane} components; do
	 * nothing for other components (not even {@code JSplitPane} components).
	 *
	 * @param c {@code Component}, the component
	 * @param newOneTouchExpandable {@code boolean}, the setting
	 */
	private static void maybeSetOneTouchExpandableComponent(Component c, boolean newOneTouchExpandable)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setOneTouchExpandable(newOneTouchExpandable);
		}
	}

	/**
	 * Set orientation for {@code FlippingSplitPane} components; do nothing
	 * for other components (not even {@code JSplitPane} components).
	 *
	 * @param c {@code Component}, the component
	 * @param newOrientation {@code int}, the orientation
	 */
	private static void maybeSetOrientationComponent(Component c, int newOrientation)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setOrientation(newOrientation);
		}
	}

	/**
	 * Gets the {@code locked} property.
	 *
	 * @return the value of the {@code locked} property
	 *
	 * @see #setLocked
	 */
	private boolean isLocked()
	{
		return lockAction.isLocked();
	}

	/**
	 * Set locked for {@code FlippingSplitPane} components; do nothing for
	 * other components.
	 *
	 * @param c {@code Component}, the component
	 * @param locked {@code boolean}, the setting
	 */
	private static void maybeSetLockedComponent(Component c, boolean locked)
	{
		if (c instanceof FlippingSplitPane)
		{
			((FlippingSplitPane) c).setLocked(locked);
		}
	}

	/**
	 * Sets the value of the {@code locked} property, which must be
	 * {@code true} for the child components to be locked against changes. The
	 * default value of this property is {@code false}.
	 *
	 * @param locked {@code int}, the setting
	 *
	 * @see #isLocked
	 */
	private void setLocked(boolean locked)
	{
		lockAction.setLocked(locked);
	}

	/**
	 * {@code setupExtensions} installs the mouse listener for the popup menu,
	 * and fixes some egregious defaults in {@code JSplitPane}.
	 */
	private void setupExtensions()
	{
		SplitPaneUI anUi = getUI();

		if (anUi instanceof BasicSplitPaneUI)
		{
			((BasicSplitPaneUI) anUi).getDivider().addMouseListener(new PopupListener());
		}
		setResizeWeight(0.5);
	}

	private final class LockAction extends AbstractAction
	{

		/**
		 * Workaround for bug with locking panes; this is easier that big surgery on
		 * BasicSplitPaneDivider.
		 */
		private boolean wasContinuousLayout = false;
		/**
		 * Is the split pane locked?
		 */
		private boolean locked = false;

		public LockAction()
		{
			configureProps();
		}

		/**
		 * Configures MenuItem properties based on locked state
		 */
		private void configureProps()
		{
			String prop = locked ? "unlock" : "lock";
			putValue(NAME, LanguageBundle.getString("in_" + prop));
			putValue(MNEMONIC_KEY, LanguageBundle.getMnemonic("in_mn_" + prop));
		}

		/**
		 * Action for Lock/Unlock item in popup menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			setLocked(!locked);
		}

		public boolean isLocked()
		{
			return locked;
		}

		/**
		 * Sets the value of the {@code locked} property, which must be
		 * {@code true} for the child components to be locked against changes. The
		 * default value of this property is {@code false}.
		 *
		 * @param locked {@code int}, the setting
		 *
		 * @see #isLocked
		 */
		public void setLocked(boolean locked)
		{
			if (this.locked == locked)
			{
				return;
			}

			// Workaround so that you can't drag the divider when locked.
			this.locked = locked;
			configureProps();

			if (locked)
			{
				wasContinuousLayout = isContinuousLayout();
				setContinuousLayout(true);
			}
			else
			{
				setContinuousLayout(wasContinuousLayout);
			}

			maybeSetLockedComponent(getLeftComponent(), locked);
			maybeSetLockedComponent(getRightComponent(), locked);
		}

	}

	/**
	 * Menu item for Center item in popup menu.
	 */
	private class CenterMenuItem extends JMenuItem implements ActionListener
	{

		CenterMenuItem()
		{
			super(LanguageBundle.getString("in_center"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_center"));

			addActionListener(this);
		}

		/**
		 * Action for Center item in popup menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			centerDividerLocations();
		}

	}

	/**
	 * Menu item for Continuous layout item in options menu.
	 */
	private final class ContinuousLayoutMenuItem extends JCheckBoxMenuItem implements ActionListener
	{

		ContinuousLayoutMenuItem()
		{
			super(LanguageBundle.getString("in_smothRes"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_smothRes"));
			setSelected(isContinuousLayout());

			addActionListener(this);
		}

		/**
		 * Action for Continuous layout item in options menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			setContinuousLayout(!isContinuousLayout());
		}

	}

	/**
	 * Menu item for Flip item in popup menu.
	 */
	private class FlipMenuItem extends JMenuItem implements ActionListener
	{

		FlipMenuItem()
		{
			super(LanguageBundle.getString("in_flip"));

			setMnemonic(LanguageBundle.getMnemonic("in_mn_flip"));

			addActionListener(this);
		}

		/**
		 * Action for Flip item in popup menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			flipOrientation();
		}

	}

	/**
	 * Menu item for One touch expandable item in options menu.
	 */
	private final class OneTouchExpandableMenuItem extends JCheckBoxMenuItem implements ActionListener
	{

		OneTouchExpandableMenuItem()
		{
			super(LanguageBundle.getString("in_oneTouchExp"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_oneTouchExp"));
			setSelected(isOneTouchExpandable());

			addActionListener(this);
		}

		/**
		 * Action for One touch expandable item in options menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			setOneTouchExpandable(!isOneTouchExpandable());
		}

	}

	/**
	 * Menu for Options item in popup menu.
	 */
	private final class OptionsMenu extends JMenu
	{

		OptionsMenu()
		{
			super(LanguageBundle.getString("in_options"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_options"));

			this.add(new OneTouchExpandableMenuItem());
			this.add(new ContinuousLayoutMenuItem());
		}

	}

	/**
	 * Mouse listener for popup menu.
	 */
	private class PopupListener extends PopupMouseAdapter
	{

		@Override
		protected void maybeShowPopup(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				showPopup(e);
			}
			else if (Utility.isShiftLeftMouseButton(e))
			{
				if (!isLocked())
				{
					flipOrientation();
				}
			}
		}

		@Override
		public void showPopup(MouseEvent e)
		{
			JPopupMenu menu;
			if (!isLocked())
			{
				if (popupMenu == null)
				{
					popupMenu = new JPopupMenu();
					popupMenu.add(new CenterMenuItem());
					popupMenu.add(new FlipMenuItem());
					popupMenu.add(new ResetMenuItem());
					popupMenu.addSeparator();
					popupMenu.add(new JMenuItem(lockAction));
					popupMenu.addSeparator();
					popupMenu.add(new OptionsMenu());
				}
				menu = popupMenu;
			}
			else
			{
				menu = new JPopupMenu();
				menu.add(new JMenuItem(lockAction));
			}
			menu.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	/**
	 * Menu item for Reset item in popup menu.
	 */
	private final class ResetMenuItem extends JMenuItem implements ActionListener
	{

		ResetMenuItem()
		{
			super(LanguageBundle.getString("in_reset"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_reset"));

			addActionListener(this);
		}

		/**
		 * Action for Reset item in popup menu.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			resetToPreferredSizes();
		}

	}

}
