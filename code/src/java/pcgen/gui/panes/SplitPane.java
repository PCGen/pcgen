/*
 * SplitPane.java
 *
 * Copyright 2002 - 2003 (C) B. K. Oxley (binkley)
 * <binkley@alumni.rice.edu>
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
 * Created on August 18th, 2002.
 */
package pcgen.gui.panes; // hm.binkley.gui;

import pcgen.system.LanguageBundle;

import javax.swing.*;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <code>SplitPane</code> is an improved version of
 * <code>JSplitPane</code> featuring a popup menu accesses by
 * right-clicking on the divider.
 *
 * (<code>JSplitPane</code> is used to divide two (and only two)
 * <code>Component</code>s.  The two <code>Component</code>s are
 * graphically divided based on the look and feel implementation, and
 * the two <code>Component</code>s can then be interactively resized
 * by the user.  Information on using <code>JSplitPane</code> is in <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/splitpane.html">How
 * to Use Split Panes</a> in <em>The Java Tutorial</em>.)
 *
 * In addition to the standard keyboard keys used by
 * <code>JSplitPane</code>, <code>SplitPane</code> will flip the panes
 * orientation on <code>SHIFT-BUTTON1</code>.
 *
 * (For the keyboard keys used by <code>JSplitPane</code> in the
 * standard Look and Feel (L&F) renditions, see the <a
 * href="doc-files/Key-Index.html#JSplitPane"><code>JSplitPane</code>
 * key assignments</a>.)
 *
 * <code>SplitPane</code> treats many of the methods of
 * <code>JSplitPane</code> recursively, calling the same method on the
 * left and right components (or top and bottom for
 * <code>VERTICAL_ORIENTATION</code>) if they are also
 * <code>SplitPane<code>s.  You can defeat this behavior by using
 * <code>JSplitPane</code> components instead.
 *
 * <code>SplitPane</code> also supports "locking": a locked pane
 * renders the divider unmovable, and the popup menu only has an
 * "Unlocked" item.  Locking is recursive for <code>SplitPane</code>
 * components.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 *
 * @see JSplitPane
 */
final class SplitPane extends JSplitPane
{
	static final long serialVersionUID = 7077400429448626205L;

	/**
	 * Creates a new <code>SplitPane</code>.
	 */
	public SplitPane()
	{
		setupExtensions();
	}

	/**
	 * <code>setContinuousLayout</code> recursively calls {@link
	 * JSplitPane#setContinuousLayout(boolean)} on
	 * <code>SplitPane</code> components.
	 *
	 * @param newContinuousLayout <code>boolean</code>, the setting
	 *
	 *        bound: true
	 *  description: Whether the child components are
	 *               continuously redisplayed and laid out during
	 *               user intervention.
	 */
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

	public void setEnabled(boolean enabled)
	{
		maybeSetEnabledComponent(getLeftComponent(), enabled);
		maybeSetEnabledComponent(getRightComponent(), enabled);

		super.setEnabled(enabled);
	}

	/**
	 * <code>setOneTouchExpandable</code> recursively calls 
	 * {@link JSplitPane#setOneTouchExpandable(boolean)} on
	 * <code>SplitPane</code> components.
	 *
	 * @param newOneTouchExpandable <code>boolean</code>, the setting
	 *
	 * bound: true
	 * description: UI widget on the divider to quickly
	 *               expand/collapse the divider.
	 */
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
	 * <code>setOrientation</code> recursively calls {@link
	 * JSplitPane#setOrientation(int)} on <code>SplitPane</code>
	 * components, alternating the orietation so as to achieve a
	 * "criss-cross" affect.
	 *
	 * @param newOrientation the orientation
	 * @exception IllegalArgumentException if orientation is not one of:
	 *        HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 * 
	 *        bound: true
	 *  description: The orientation, or how the splitter is divided.
	 *         enum: HORIZONTAL_SPLIT JSplitPane.HORIZONTAL_SPLIT
	 *               VERTICAL_SPLIT   JSplitPane.VERTICAL_SPLIT
	 */
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
	 * <code>resetToPreferredSizes</code> recursively calls {@link
	 * JSplitPane#resetToPreferredSizes} on <code>SplitPane</code>
	 * components.
	 */
	public void resetToPreferredSizes()
	{
		maybeResetToPreferredSizesComponent(getLeftComponent());
		maybeResetToPreferredSizesComponent(getRightComponent());
		fixedResetToPreferredSizes();
	}

	/**
	 * Center <code>SplitPane</code> components; do nothing for other
	 * components.
	 *
	 * @param c <code>Component</code>, the component.
	 */
	private static void maybeCenterDividerLocationsComponent(Component c)
	{
		if (c instanceof SplitPane)
		{
			((SplitPane) c).centerDividerLocations();
		}
	}

	/**
	 * Figure out the divider proportion for a given orientation; it
	 * depends on how many subdivided panes there are facing the same
	 * direction.
	 *
	 * @param anOrientation the orientation
	 *
	 * @return <code>double</code>, the divider proportion
	 *
	 * @see #setDividerLocation(double)
	 */
	private double getDividerProportion(int anOrientation)
	{
		double n1;
		double n2;
		Component c1 = getLeftComponent();
		Component c2 = getRightComponent();

		if (c1 instanceof SplitPane)
		{
			n1 = ((SplitPane) c1).getPaneCount(anOrientation);
		}
		else
		{
			n1 = 1.0;
		}

		if (c2 instanceof SplitPane)
		{
			n2 = ((SplitPane) c2).getPaneCount(anOrientation);
		}
		else
		{
			n2 = 1.0;
		}

		return n1 / (n1 + n2);
	}

	/**
	 * How many recursive components are <code>SplitPane</code> facing
	 * the same direction?
	 *
	 * @param anOrientation the orientation
	 *
	 * @return <code>int</code>, the pane count
	 */
	private int getPaneCount(int anOrientation)
	{
		int n1;
		int n2;
		Component c1 = getLeftComponent();
		Component c2 = getRightComponent();

		if (c1 instanceof SplitPane)
		{
			SplitPane pane = (SplitPane) c1;

			if (pane.getOrientation() == anOrientation)
			{
				n1 = pane.getPaneCount(anOrientation);
			}
			else
			{
				n1 = 1;
			}
		}
		else
		{
			n1 = 1;
		}

		if (c2 instanceof SplitPane)
		{
			SplitPane pane = (SplitPane) c2;

			if (pane.getOrientation() == anOrientation)
			{
				n2 = pane.getPaneCount(anOrientation);
			}
			else
			{
				n2 = 1;
			}
		}
		else
		{
			n2 = 1;
		}

		return n1 + n2;
	}

	/**
	 * <code>centerDividerLocations</code> sets the divider location
	 * in the middle by recursively calling
	 * <code>setDividerLocation(0.5)</code>.
	 *
	 * @see #setDividerLocation(double)
	 */
	private void centerDividerLocations()
	{
		setDividerLocation(getDividerProportion(getOrientation()));
		maybeCenterDividerLocationsComponent(getLeftComponent());
		maybeCenterDividerLocationsComponent(getRightComponent());
	}

	/**
	 * Flip <code>SplitPane</code> components; do nothing for other
	 * components.
	 *
	 * @param c <code>Component</code>, the component.
	 */
	private static void maybeFlipComponent(Component c)
	{
		if (c instanceof SplitPane)
		{
			((SplitPane) c).flipOrientation();
		}
	}

	/**
	 * Reset <code>SplitPane</code> components; do nothing for other
	 * components (not even <code>JSplitPane</code> components).
	 *
	 * @param c <code>Component</code>, the component.
	 */
	private static void maybeResetToPreferredSizesComponent(Component c)
	{
		if (c instanceof SplitPane)
		{
			((SplitPane) c).resetToPreferredSizes();
		}
	}

	/**
	 * <code>fixedResetToPreferredSizes</code> fixes a bug whereby
	 * flipping a pane from vertical to horizontal sets the divider
	 * location to <code>1</code>, thereby hiding the left component.
	 */
	private void fixedResetToPreferredSizes()
	{
		setDividerLocation(((getMinimumDividerLocation() + getMaximumDividerLocation()) / 2));
	}

	/**
	 * <code>invertOrientation</code> is a convenience function to
	 * turn horizontal into vertical orientations and the converse.
	 *
	 * @param orientation <code>int</code>, either
	 * <code>HORIZONTAL_ORIENTATION</code> or
	 * <code>VERTICAL_ORIENTATION</code>
	 *
	 * @return <code>int</code>, the inverse
	 */
	private static int invertOrientation(int orientation)
	{
		return (orientation == HORIZONTAL_SPLIT) ? VERTICAL_SPLIT : HORIZONTAL_SPLIT;
	}

	/**
	 * <code>flipOrientation</code> inverts the current orientation of
	 * the panes, recursively flipping <code>SplitPane</code>
	 * components.
	 */
	private void flipOrientation()
	{
		super.setOrientation(invertOrientation(getOrientation()));
		maybeFlipComponent(getLeftComponent());
		maybeFlipComponent(getRightComponent());

		resetToPreferredSizes(); // gets munched anyway?  XXX
	}

	/**
	 * Set continuous layout for <code>SplitPane</code> components; do
	 * nothing for other components (not even <code>JSplitPane</code>
	 * components).
	 *
	 * @param c <code>Component</code>, the component
	 * @param newContinuousLayout <code>boolean</code>, the setting
	 */
	private static void maybeSetContinuousLayoutComponent(Component c, boolean newContinuousLayout)
	{
		if (c instanceof SplitPane)
		{
			((SplitPane) c).setContinuousLayout(newContinuousLayout);
		}
	}

	/**
	 * Set enabled for <code>SplitPane</code> components; do nothing
	 * for other components.
	 *
	 * @param c <code>Component</code>, the component
	 * @param enabled <code>boolean</code>, the setting
	 */
	private static void maybeSetEnabledComponent(Component c, boolean enabled)
	{
		if (c instanceof SplitPane)
		{
			c.setEnabled(enabled);
		}
	}

	/**
	 * Set one touch expandable for <code>SplitPane</code> components;
	 * do nothing for other components (not even
	 * <code>JSplitPane</code> components).
	 *
	 * @param c <code>Component</code>, the component
	 * @param newOneTouchExpandable <code>boolean</code>, the setting
	 */
	private static void maybeSetOneTouchExpandableComponent(Component c, boolean newOneTouchExpandable)
	{
		if (c instanceof SplitPane)
		{
			((SplitPane) c).setOneTouchExpandable(newOneTouchExpandable);
		}
	}

	/**
	 * Set orientation for <code>SplitPane</code> components; do
	 * nothing for other components (not even <code>JSplitPane</code>
	 * components).
	 *
	 * @param c <code>Component</code>, the component
	 * @param newOrientation <code>int</code>, the orientation
	 */
	private static void maybeSetOrientationComponent(Component c, int newOrientation)
	{
		if (c instanceof SplitPane)
		{
			((SplitPane) c).setOrientation(newOrientation);
		}
	}

//     private class KeyboardShiftHomeAction extends AbstractAction
//     {
//         public void actionPerformed(ActionEvent e)
// 	{
// 	    centerDividerLocations();
//         }
//     }
//     private class KeyboardShiftEndAction extends AbstractAction
//     {
//         public void actionPerformed(ActionEvent e)
// 	{
// 	    resetToPreferredSizes();
//         }
//     }

	/**
	 * <code>setupExtensions</code> installs the mouse listener for
	 * the popup menu, and fixes some egregious defaults in
	 * <code>JSplitPane</code>.
	 */
	private void setupExtensions()
	{
		SplitPaneUI aNUi = getUI();

		if (aNUi instanceof BasicSplitPaneUI)
		{
			((BasicSplitPaneUI) aNUi).getDivider().addMouseListener(new PopupListener());
		}

// 	// See source for JSplitPane for this junk.
// 	ActionMap map = (ActionMap) UIManager.get("SplitPane.actionMap");
// 	map.put("selectCenter", new KeyboardShiftHomeAction()); // XXX
// 	map.put("selectReset", new KeyboardShiftEndAction()); // XXX
// 	SwingUtilities.replaceUIActionMap(this, map);
		// This is *so* much better than squishing the top/left
		// component into oblivion.
		setResizeWeight(0.5);
	}

	/** Action for Center item in popup menu. */
	private class CenterActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			centerDividerLocations();
		}
	}

	/** Menu item for Center item in popup menu. */
	private class CenterMenuItem extends JMenuItem
	{
		CenterMenuItem()
		{
			super(LanguageBundle.getString("in_center"));

			setMnemonic(LanguageBundle.getMnemonic("in_mn_center"));
			setIcon(Utilities.CENTER_ICON);

			addActionListener(new CenterActionListener());
		}
	}

	/** Action for Continuous layout item in options menu. */
	private class ContinuousLayoutActionListener implements ActionListener
	{
		private boolean aContinuousLayout;

		ContinuousLayoutActionListener(boolean continuousLayout)
		{
			this.aContinuousLayout = continuousLayout;
		}

		public void actionPerformed(ActionEvent e)
		{
			setContinuousLayout(aContinuousLayout);
		}
	}

	/** Menu item for Continuous layout item in options menu. */
	private class ContinuousLayoutMenuItem extends JCheckBoxMenuItem
	{
		ContinuousLayoutMenuItem()
		{
			super(LanguageBundle.getString("in_smothRes"));

			boolean aContinuousLayout = isContinuousLayout();

			setMnemonic(LanguageBundle.getMnemonic("in_mn_smothRes"));
			setSelected(aContinuousLayout);

			addActionListener(new ContinuousLayoutActionListener(!aContinuousLayout));
		}
	}

	/** Action for Flip item in popup menu. */
	private class FlipActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			flipOrientation();
		}
	}

	/** Menu item for Flip item in popup menu. */
	private class FlipMenuItem extends JMenuItem
	{
		FlipMenuItem()
		{
			super(LanguageBundle.getString("in_flip"));

			setMnemonic(LanguageBundle.getMnemonic("in_mn_flip"));
			setIcon(Utilities.FLIP_ICON);

			addActionListener(new FlipActionListener());
		}
	}

	/** Action for Lock/Unlock item in popup menu. */
	private class LockActionListener implements ActionListener
	{
		private boolean enabled;

		LockActionListener(boolean enabled)
		{
			this.enabled = enabled;
		}

		public void actionPerformed(ActionEvent e)
		{
			setEnabled(enabled);
		}
	}

	/** Menu item for Lock/Unlock item in popup menu. */
	private class LockMenuItem extends JMenuItem
	{
		LockMenuItem()
		{
			boolean enabled = !SplitPane.this.isEnabled();

			setText(enabled ? LanguageBundle.getString("in_unlock") : LanguageBundle.getString("in_lock"));
			setMnemonic(LanguageBundle.getMnemonic("in_mn_lock"));
			setIcon(Utilities.LOCK_ICON);

			addActionListener(new LockActionListener(enabled));
		}
	}

	/** Action for One touch expandable item in options menu. */
	private class OneTouchExpandableActionListener implements ActionListener
	{
		private boolean aOneTouchExpandable;

		OneTouchExpandableActionListener(boolean oneTouchExpandable)
		{
			this.aOneTouchExpandable = oneTouchExpandable;
		}

		public void actionPerformed(ActionEvent e)
		{
			setOneTouchExpandable(aOneTouchExpandable);
		}
	}

	/** Menu item for One touch expandable item in options menu. */
	private class OneTouchExpandableMenuItem extends JCheckBoxMenuItem
	{
		OneTouchExpandableMenuItem()
		{
			super(LanguageBundle.getString("in_oneTouchExp"));

			boolean isOneTouchExpandable = isOneTouchExpandable();

			setMnemonic(LanguageBundle.getMnemonic("in_mn_oneTouchExp"));
			setSelected(isOneTouchExpandable);

			addActionListener(new OneTouchExpandableActionListener(!isOneTouchExpandable));
		}
	}

	/** Menu for Options item in popup menu. */
	private class OptionsMenu extends JMenu
	{
		OptionsMenu()
		{
			super(LanguageBundle.getString("in_options"));

			setMnemonic(LanguageBundle.getMnemonic("in_mn_options"));

			this.add(new OneTouchExpandableMenuItem());
			this.add(new ContinuousLayoutMenuItem());
		}
	}

	/* *
	 * After <code>SplitPane</code> builds the basic popup menu,
	 * subclasses may modify it here before <code>SplitPane</code>
	 * displays it.
	 * /
	   private static void addPopupMenuItems(JPopupMenu popupMenu, MouseEvent e)
	   {
	   }
	 */

	/**
	 * Mouse listener for popup menu.  Central to this entire class!
	 */
	private class PopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if (Utilities.isRightMouseButton(e))
			{
				int x = e.getX();
				int y = e.getY();

				JPopupMenu popupMenu = new JPopupMenu();

				if (isEnabled())
				{
					popupMenu.add(new CenterMenuItem());
					popupMenu.add(new FlipMenuItem());
					popupMenu.add(new ResetMenuItem());
					popupMenu.addSeparator();
				}

				popupMenu.add(new LockMenuItem());

				if (isEnabled())
				{
					popupMenu.addSeparator();
					popupMenu.add(new OptionsMenu());
				}

				//Commented out as it's an unused empty function.
				//addPopupMenuItems(popupMenu, e);
				popupMenu.show(e.getComponent(), x, y);
			}

			// A handy shortcut
			else if (Utilities.isShiftLeftMouseButton(e))
			{
				if (isEnabled())
				{
					flipOrientation();
				}
			}
		}
	}

	/** Action for Reset item in popup menu. */
	private class ResetActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			resetToPreferredSizes();
		}
	}

	/** Menu item for Reset item in popup menu. */
	private class ResetMenuItem extends JMenuItem
	{
		ResetMenuItem()
		{
			super(LanguageBundle.getString("in_reset"));

			setMnemonic(LanguageBundle.getMnemonic("in_mn_reset"));
			setIcon(Utilities.RESET_ICON);

			addActionListener(new ResetActionListener());
		}
	}
}
