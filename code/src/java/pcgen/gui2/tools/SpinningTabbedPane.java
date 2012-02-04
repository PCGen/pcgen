/*
 * SpinningTabbedPane.java
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
 * Created on August 18th, 2002.
 */
package pcgen.gui2.tools; // hm.binkley.gui;

import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>SpinningTabbedPane</code>.
 *
 * @author <a href="binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 1184 $
 *
 * @see JTabbedPane
 */
public class SpinningTabbedPane extends JTabbedPane
{

    static final long serialVersionUID = 4980035692406423131L;
    private static final int PLACE_OFFSET = 0;
    private static final int MOVE_LEFT_RIGHT_OFFSET = 4;
    private static final int MOVE_UP_DOWN_OFFSET = 8;
    private static final int GROUP_OFFSET = 12;
    private static final int TAB_OFFSET = 16;
    private static final int UNGROUP_CHILD_OFFSET = 20;
    private static final int UNGROUP_SELF_OFFSET = 24;
    private static final int UNGROUP_SINGLE_OFFSET = 28;
    private static final String[] labels =
            {
        LanguageBundle.getString("in_top"), // place
        LanguageBundle.getString("in_left"),
        LanguageBundle.getString("in_bottom"),
        LanguageBundle.getString("in_right"),
        LanguageBundle.getString("in_beginning"), // move left/right
        LanguageBundle.getString("in_left"),
        LanguageBundle.getString("in_end"),
        LanguageBundle.getString("in_right"),
        LanguageBundle.getString("in_top"), // move up/down
        LanguageBundle.getString("in_up"),
        LanguageBundle.getString("in_bottom"),
        LanguageBundle.getString("in_down"),
        LanguageBundle.getString("in_up"), // group
        LanguageBundle.getString("in_left"),
        LanguageBundle.getString("in_down"),
        LanguageBundle.getString("in_right"),
        null, // tab
        null,
        null,
        null,
        LanguageBundle.getString("in_ungroupTop"), // ungroup child
        LanguageBundle.getString("in_ungroupLeft"),
        LanguageBundle.getString("in_ungroupBottom"),
        LanguageBundle.getString("in_ungroupRight"),
        LanguageBundle.getString("in_ungroupTop"), // ungroup self
        LanguageBundle.getString("in_ungroupLeft"),
        LanguageBundle.getString("in_ungroupBottom"),
        LanguageBundle.getString("in_ungroupRight"),
        LanguageBundle.getString("in_ungroupUp"), // ungroup single
        LanguageBundle.getString("in_ungroupLeft"),
        LanguageBundle.getString("in_ungroupDown"),
        LanguageBundle.getString("in_ungroupRight")
    };
    private static final ImageIcon[] icons =
            {
        Utilities.UP_ICON, // place
        Utilities.LEFT_ICON,
        Utilities.DOWN_ICON,
        Utilities.RIGHT_ICON,
        Utilities.BEGINNING_ICON, // move left/right
        Utilities.LEFT_ICON,
        Utilities.END_ICON,
        Utilities.RIGHT_ICON,
        Utilities.TOP_ICON, // move up/down
        Utilities.UP_ICON,
        Utilities.BOTTOM_ICON,
        Utilities.DOWN_ICON,
        Utilities.UP_ICON, // group
        Utilities.LEFT_ICON,
        Utilities.DOWN_ICON,
        Utilities.RIGHT_ICON,
        Utilities.UP_ICON, // tab
        Utilities.LEFT_ICON,
        Utilities.DOWN_ICON,
        Utilities.RIGHT_ICON,
        Utilities.TOP_ICON, // ungroup child
        Utilities.BEGINNING_ICON,
        Utilities.BOTTOM_ICON,
        Utilities.END_ICON,
        Utilities.TOP_ICON, // ungroup self
        Utilities.BEGINNING_ICON,
        Utilities.BOTTOM_ICON,
        Utilities.END_ICON,
        Utilities.UP_ICON, // ungroup single
        Utilities.LEFT_ICON,
        Utilities.DOWN_ICON,
        Utilities.RIGHT_ICON
    };
    private static final String[] tips =
            {
        LanguageBundle.getString("in_spinTips1"),
        LanguageBundle.getString("in_spinTips2"),
        LanguageBundle.getString("in_spinTips3"),
        LanguageBundle.getString("in_spinTips4"),
        LanguageBundle.getString("in_spinTips5"),
        LanguageBundle.getString("in_spinTips6"),
        LanguageBundle.getString("in_spinTips7"),
        LanguageBundle.getString("in_spinTips8"),
        LanguageBundle.getString("in_spinTips9"),
        LanguageBundle.getString("in_spinTips10"),
        LanguageBundle.getString("in_spinTips11"),
        LanguageBundle.getString("in_spinTips12"),
        LanguageBundle.getString("in_spinTips13"),
        LanguageBundle.getString("in_spinTips14"),
        LanguageBundle.getString("in_spinTips15"),
        LanguageBundle.getString("in_spinTips16"),
        LanguageBundle.getString("in_spinTips17"),
        LanguageBundle.getString("in_spinTips18"),
        LanguageBundle.getString("in_spinTips19"),
        LanguageBundle.getString("in_spinTips20"),
        LanguageBundle.getString("in_spinTips21"),
        LanguageBundle.getString("in_spinTips22"),
        LanguageBundle.getString("in_spinTips23"),
        LanguageBundle.getString("in_spinTips24"),
        LanguageBundle.getString("in_spinTips25"),
        LanguageBundle.getString("in_spinTips26"),
        LanguageBundle.getString("in_spinTips27"),
        LanguageBundle.getString("in_spinTips28"),
        LanguageBundle.getString("in_spinTips29"),
        LanguageBundle.getString("in_spinTips30"),
        LanguageBundle.getString("in_spinTips31"),
        LanguageBundle.getString("in_spinTips32")
    };
    private PopupMenuPolicy policy = new DefaultPopupMenuPolicy();
    private Set<Component> locked = new HashSet<Component>();
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
    public final void setTitleAt(int index, String title)
    {
        String extra = getExtraTitleAt(index);

        if (extra != null)
        {
            if ((title == null) || (title.length() == 0))
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
     * @since 1.4
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

    private static void setMenuItem(JMenuItem menuItem, int offset)
    {
        String label = labels[offset];

        menuItem.setText(label);

        if (label != null)
        {
            menuItem.setMnemonic(label.charAt(0));
        }

        menuItem.setIcon(icons[offset]);
        menuItem.setToolTipText(tips[offset]);
    }

    // Need to use action events instead  XXX
    private static final SpinningTabbedPane createPane()
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

    private final String getExtraTitleAt(int index)
    {
        Component c = getComponentAt(index);

        return (c instanceof SpinningTabbedPane) ? ("(" +
                ((SpinningTabbedPane) c).getSpinTabCount() + ")") : null;
    }

    private final int getMovableTabCount()
    {
        int n = 0;

        for (int i = 0,  x = getTabCount(); i < x; ++i)
        {
            if (policy.canMove(i) && !isTabLockedAt(i))
            {
                ++n;
            }
        }

        return n;
    }

    private final int[] getMovableTabIndices()
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

        int[] list2 = new int[n];

        for (int i = 0; i < n; ++i)
        {
            list2[i] = list1[i];
        }

        //TODO:gorm - test this as a replacement, should be more efficient
        //System.arraycopy(list1, 1, list2, 1, n - 1);
        return list2;
    }

    private final String getPlainTitleAt(int index)
    {
        String title = getTitleAt(index);
        Component c = getComponentAt(index);

        if (title == null)
        {
            return "";
        }

        if ((title.length() == 0) || !(c instanceof SpinningTabbedPane))
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

    private final int getSpinTabCount()
    {
        int n = getTabCount();

        for (int i = 0,  x = n; i < x; ++i)
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

    private final int getSpinTabPlacementAt(int index)
    {
        Component c = getComponentAt(index);

        if (c instanceof SpinningTabbedPane)
        {
            return ((SpinningTabbedPane) c).getTabPlacement();
        }

        return -1;
    }

    private final boolean isTabLockedAt(int index)
    {
        return locked.contains(getComponentAt(index));
    }

    /*
    private static final void addPopupMenuItems(JPopupMenu popupMenu, int index, MouseEvent e)
    {
    }
     */
    private final void addNewTab()
    {
        add(new JPanel());
    }

    private final void lockTabAt(int index)
    {
        locked.add(getComponentAt(index));
        setIconAt(index, Utilities.LOCK_ICON);
    }

    /**
     * Spin tabs starting at index to the end, stopping when we find
     * another spun tab.  If the first tab is itself spun, spin it as
     * well; this permits spinning of spun tabs as a special case.
     *
     * @param index start spinning tabs here
     * @param placement direction to place spun tabs
     */
    private final void spinTabsAt(int index, int placement)
    {
        SpinningTabbedPane pane = createPane();

        moveTabAtTo(index, -1, pane);

        for (int i = index,  x = getTabCount(); i < x; ++i)
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

    private final void unlockTabAt(int index)
    {
        locked.remove(getComponentAt(index));
        setIconAt(index, null);
    }

    /**
     * Unspin all tabs in this pane.
     */
    private final void unspinAll()
    {
        int parentIndex = parent.indexOfComponent(this);

        parent.removeTabAt(parentIndex);

        for (int x = getTabCount(); --x >= 0;)
        {
            moveTabAtTo(x, parentIndex, parent);
        }

        parent = null; // help GC
    }

    private final void unspinTabAt(int index)
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

    private void moveTabAtTo(int fromIndex, int toIndex, JTabbedPane to)
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

        int offset = offsetForPlacement(pane.getTabPlacement()) + TAB_OFFSET;

        setTitleAt(index, getPlainTitleAt(index));
        setIconAt(index, icons[offset]);
        setToolTipTextAt(index, tips[offset]);
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

        public boolean canClose(int index)
        {
            return true;
        }

        public boolean canGroup(int index)
        {
            return true;
        }

        public boolean canLock(int index)
        {
            return true;
        }

        public boolean canMove(int index)
        {
            return true;
        }

        public boolean canNew(int index)
        {
            return true;
        }

        public boolean canRename(int index)
        {
            return true;
        }

        public boolean hasGroupMenu(int index, MouseEvent e)
        {
            return true;
        }

        public boolean hasMoveMenu(int index, MouseEvent e)
        {
            return true;
        }

        public boolean hasPlaceMenu(int index, MouseEvent e)
        {
            return true;
        }

    }

    private class CloseAction extends IndexedAction
    {

        public CloseAction(int index)
        {
            super(index, LanguageBundle.getString("in_close"),
                  Utilities.CLOSE_ICON,
                  LanguageBundle.getMnemonic("in_mn_close"));
        }

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
                    this.add(new JMenuItem(new UngroupChildAction(index)));
                    addSeparator();

                    // Add backwards to get clockwise choices; skip
                    // tab's own direction since already spun
                    for (int j = 3; j > 0; --j)
                    {
                        this.add(new JMenuItem(new PlaceAction((SpinningTabbedPane) c,
                                                               placementForSlot(j,
                                                                                placement))));
                    }

                    first = false;
                }
            }
            else
            { // we are spun
                this.add(new JMenuItem(new UngroupSelfAction()));

                if (c instanceof SpinningTabbedPane) // tab is spun
                {
                    this.add(new JMenuItem(new UngroupChildAction(index)));
                }
                else // tab is not spun
                {
                    this.add(new JMenuItem(new UngroupSingleAction(index)));
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
                    this.add(new JMenuItem(new GroupAction(index,
                                                           placementForSlot(j,
                                                                            placement))));
                }
            }
        }

    }

    private class GroupAction extends IndexedAction
    {

        private int placement;

        public GroupAction(int index, int placement)
        {
            super(index, offsetForPlacement(placement) + GROUP_OFFSET);
            this.placement = placement;
        }

        public void actionPerformed(ActionEvent e)
        {
            spinTabsAt(getIndex(), placement);
            setSelectedIndex(getIndex());
        }

    }

    private class LockAction extends IndexedAction
    {

        public LockAction(int index)
        {
            super(index, LanguageBundle.getString("in_lock"),
                  Utilities.LOCK_ICON, LanguageBundle.getMnemonic("in_mn_lock"));
        }

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

        public void actionPerformed(ActionEvent e)
        {
            final int[] indices = getMovableTabIndices();
            int i = -1;

            switch (placement)
            {
                case TOP:
                    i = indices[0];

                    break;

                case LEFT:
                    i = previous(index, indices);

                    break;

                case BOTTOM:
                    i = indices[indices.length - 1];

                    break;

                case RIGHT:
                    i = next(index, indices);

                    break;
            }

            moveTabAtTo(index, i, SpinningTabbedPane.this);
            setSelectedIndex(i);
        }

        private int next(int current, int[] indices)
        {
            for (int i = 0,  x = indices.length - 1; i < x; ++i)
            {
                if (current == indices[i])
                {
                    return indices[i + 1];
                }
            }

            return -1;
        }

        private int previous(int current, int[] indices)
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
            super(LanguageBundle.getString("in_moveTab"));
            setMnemonic(LanguageBundle.getMnemonic("in_mn_movetab"));

            final int[] indices = getMovableTabIndices();

            // Only you can prevent out of range errors.
            int primum = -1;

            // Only you can prevent out of range errors.
            int secundum = -1;

            // Only you can prevent out of range errors.
            int penultimatum = -1;

            // Only you can prevent out of range errors.
            int ultimatum = -1;

            switch (indices.length)
            {
                case 0:
                    this.setEnabled(false);

                    break;

                case 1:
                    this.setEnabled(false);

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

                default:
                    primum = indices[0];
                    secundum = indices[1];
                    penultimatum = indices[indices.length - 2];
                    ultimatum = indices[indices.length - 1];
            }

            for (int i = 0; i < indices.length; ++i)
            {
                if (index < indices[i])
                {
                    continue;
                }

                if (index > primum)
                {
                    if (index > secundum)
                    {
                        this.add(new MoveTabMenuItem(index, TOP));
                    }

                    this.add(new MoveTabMenuItem(index, LEFT));
                }

                if (index < ultimatum)
                {
                    this.add(new MoveTabMenuItem(index, RIGHT));

                    if (index < penultimatum)
                    {
                        this.add(new MoveTabMenuItem(index, BOTTOM));
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
            int offset = offsetForPlacement(placement);

            switch (getTabPlacement())
            {
                case TOP:
                case BOTTOM:
                    offset += MOVE_LEFT_RIGHT_OFFSET;

                    break;

                case LEFT:
                case RIGHT:
                    offset += MOVE_UP_DOWN_OFFSET;

                    break;
            }

            addActionListener(new MoveActionListener(index, placement));
            setMenuItem(this, offset);
        }

    }

    private class NewAction extends IndexedAction
    {

        public NewAction()
        {
            super(0, LanguageBundle.getString("in_new"), Utilities.NEW_ICON,
                  LanguageBundle.getMnemonic("in_mn_new"));
        }

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
                this.add(new JMenuItem(new PlaceAction(SpinningTabbedPane.this,
                                                       placementForSlot(j,
                                                                        placement))));
            }
        }

    }

    private class PlaceAction extends IndexedAction
    {

        private SpinningTabbedPane pane;
        private int placement;

        public PlaceAction(SpinningTabbedPane pane, int placement)
        {
            super(0, offsetForPlacement(placement) + PLACE_OFFSET);
            this.pane = pane;
            this.placement = placement;
        }

        public void actionPerformed(ActionEvent e)
        {
            pane.setTabPlacement(placement);
        }

    }

    private class PopupListener extends MouseAdapter
    {

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (Utilities.isRightMouseButton(e))
            {
                final int x = e.getX();
                final int y = e.getY();
                final int index = indexAtLocation(x, y);
                final int aTabPlacement = getTabPlacement();

                JPopupMenu popupMenu = new JPopupMenu();

                JMenuItem newMenuItem = null;
                JMenuItem closeMenuItem = null;
                JMenuItem lockMenuItem = null;
                JMenuItem renameMenuItem = null;
                JMenu groupMenu = null;
                JMenu moveMenu = null;
                JMenu placeMenu = null;

                if (policy.canNew(index))
                {
                    newMenuItem = new JMenuItem(new NewAction());
                }

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
                        groupMenu = new GroupMenu(index,
                                                  (spinTabPlacement == -1) ? aTabPlacement
                                                  : spinTabPlacement);
                    }

                    if (policy.hasMoveMenu(index, e) && (getMovableTabCount() >
                            1) && !isTabLockedAt(index))
                    {
                        moveMenu = new MoveMenu(index);
                    }
                }

                if (policy.hasPlaceMenu(index, e))
                {
                    placeMenu = new PlaceMenu(aTabPlacement);
                }

                final boolean useNewMenuItem = newMenuItem != null;
                final boolean useCloseMenuItem = closeMenuItem != null;
                final boolean useLockMenuItem = lockMenuItem != null;
                final boolean useRenameMenuItem = renameMenuItem != null;
                boolean useGroupMenu = (groupMenu != null) &&
                        (groupMenu.getMenuComponentCount() > 0);
                final boolean useMoveMenu = (moveMenu != null) &&
                        (moveMenu.getMenuComponentCount() > 0);
                final boolean usePlaceMenu = (placeMenu != null) &&
                        (placeMenu.getMenuComponentCount() > 0);

                if ((popupMenu.getComponentCount() > 0) && (useNewMenuItem ||
                        useCloseMenuItem))
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

                if ((popupMenu.getComponentCount() > 0) && (useLockMenuItem ||
                        useRenameMenuItem))
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

                if ((popupMenu.getComponentCount() > 0) && (useGroupMenu ||
                        useMoveMenu || usePlaceMenu))
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
                spinTabsAt(index, placementForSlot(3, getTabPlacement()));
                setSelectedIndex(index);
            }
        }

    }

    private class RenameAction extends IndexedAction
    {

        private MouseEvent evt;

        public RenameAction(int index, MouseEvent e)
        {
            super(index, LanguageBundle.getString("in_rename") + "...", null,
                  LanguageBundle.getMnemonic("in_mn_rename"));
            this.evt = e;
        }

        public void actionPerformed(ActionEvent e)
        {
            int x = evt.getX();
            int y = evt.getY();
            JPopupMenu popupMenu = new JPopupMenu();
            String title = getPlainTitleAt(getIndex());
            JTextField textField = new JTextField(title);

            Logging.errorPrint("document? " + textField.getDocument());
            textField.addActionListener(new RenameTextFieldActionListener(getIndex(),
                                                                          textField,
                                                                          popupMenu));
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

            if ((popupLocation.x + popupSize.width) >= (paneLocation.x +
                    paneSize.width))
            {
                reshow = true;
                x = (paneLocation.x + paneSize.width) - popupSize.width - 1;
            }

            if ((popupLocation.y + popupSize.height) >= (paneLocation.y +
                    paneSize.height))
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

        private JPopupMenu popupMenu;
        private JTextField textField;
        private int anIndex;

        RenameTextFieldActionListener(int index, JTextField textField,
                                      JPopupMenu popupMenu)
        {
            this.anIndex = index;
            this.textField = textField;
            this.popupMenu = popupMenu;
        }

        public void actionPerformed(ActionEvent e)
        {
            SpinningTabbedPane.this.setTitleAt(anIndex, textField.getText());
            popupMenu.setVisible(false); // why? XXX
        }

    }

    private class UngroupChildAction extends IndexedAction
    {

        public UngroupChildAction(int index)
        {
            super(index, offsetForPlacement(getTabPlacement()) +
                  UNGROUP_CHILD_OFFSET);
        }

        public void actionPerformed(ActionEvent e)
        {
            SpinningTabbedPane pane = (SpinningTabbedPane) getComponentAt(getIndex());
            final int newIndex = pane.getSelectedIndex();

            pane.unspinAll();

            setSelectedIndex(getIndex() + newIndex);
        }

    }

    private class UngroupSelfAction extends IndexedAction
    {

        public UngroupSelfAction()
        {
            super(0, offsetForPlacement(parent.getTabPlacement()) +
                  UNGROUP_SELF_OFFSET);

        }

        public void actionPerformed(ActionEvent e)
        {
            final int index = parent.indexOfComponent(SpinningTabbedPane.this);
            final int newIndex = getSelectedIndex();
            SpinningTabbedPane aParent = SpinningTabbedPane.this.parent;

            unspinAll();
            aParent.setSelectedIndex(index + newIndex);
        }

    }

    private class UngroupSingleAction extends IndexedAction
    {

        public UngroupSingleAction(int index)
        {
            super(index, offsetForPlacement(parent.getTabPlacement()) +
                  UNGROUP_SINGLE_OFFSET);
        }

        public void actionPerformed(ActionEvent e)
        {
            unspinTabAt(getIndex());
            parent.setSelectedIndex(parent.indexOfComponent(SpinningTabbedPane.this) -
                                    1);
        }

    }

    private class UnlockAction extends IndexedAction
    {

        public UnlockAction(int index)
        {
            super(index, LanguageBundle.getString("in_unlock"),
                  Utilities.LOCK_ICON,
                  LanguageBundle.getMnemonic("in_mn_unlock"));
        }

        public void actionPerformed(ActionEvent e)
        {
            unlockTabAt(getIndex());
        }

    }

    private static abstract class IndexedAction extends AbstractAction
    {

        private int index;

        public IndexedAction(int index, int offset)
        {
            this(index, labels[offset], icons[offset]);
            String label = labels[offset];
            if (label != null)
            {
                putValue(MNEMONIC_KEY, (int) label.charAt(0));
            }
            putValue(SHORT_DESCRIPTION, tips[index]);
        }

        public IndexedAction(int index, String name, ImageIcon icon)
        {
            super(name, icon);
            this.index = index;
        }

        public IndexedAction(int index, String name, ImageIcon icon,
                              int mnemonic)
        {
            this(index, name, icon);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public int getIndex()
        {
            return index;
        }

    }
}
