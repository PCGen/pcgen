/*
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
 */
package pcgen.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.text.DefaultEditorKit;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.TempBonusFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.SortedListFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui2.util.AbstractListMenu;
import pcgen.gui2.util.AbstractRadioListMenu;
import pcgen.system.CharacterManager;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;
import pcgen.util.Logging;

/**
 * The menu bar that is displayed in PCGen's main window.
 */
public final class PCGenMenuBar extends JMenuBar implements CharacterSelectionListener
{

    /**
     * The context indicating what items are currently loaded/being processed in the UI
     */
    private final UIContext uiContext;
    private final PCGenFrame frame;
    private final PCGenActionMap actionMap;
    private final TempBonusMenu tempMenu;
    private CharacterFacade character;

    public PCGenMenuBar(PCGenFrame frame, UIContext uiContext)
    {
        this.uiContext = Objects.requireNonNull(uiContext);
        this.frame = frame;
        this.actionMap = frame.getActionMap();
        this.tempMenu = new TempBonusMenu();
        initComponents();
    }

    private void initComponents()
    {
        add(new FileMenu());
        add(createEditMenu());
        add(createSourcesMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }

    private JMenu createEditMenu()
    {
        JMenu menu = new JMenu();
        menu.setText(LanguageBundle.getString("in_mnuEdit"));
        menu.setMnemonic(KeyEvent.VK_E);
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.ADD_KIT_COMMAND)));
        menu.addSeparator();
        menu.add(tempMenu);
        menu.addSeparator();

        JMenuItem cutMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        cutMenuItem.setText("Cut");
        cutMenuItem.setMnemonic(KeyEvent.VK_T);
        menu.add(cutMenuItem);

        JMenuItem copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMenuItem.setText("Copy");
        copyMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(copyMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setMnemonic(KeyEvent.VK_P);
        menu.add(pasteMenuItem);
        return menu;
    }

    private JMenu createSourcesMenu()
    {
        JMenu menu = new JMenu();
        menu.setText(LanguageBundle.getString("in_mnuSources"));
        menu.setToolTipText(LanguageBundle.getString("in_mnuSourcesTip"));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SOURCES_LOAD_SELECT_COMMAND)));
        menu.addSeparator();
        menu.add(new QuickSourceMenu());
        menu.addSeparator();
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SOURCES_RELOAD_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SOURCES_UNLOAD_COMMAND)));
        menu.addSeparator();
        menu.add(actionMap.get(PCGenActionMap.INSTALL_DATA_COMMAND));

        return menu;
    }

    private JMenu createToolsMenu()
    {
        JMenu menu = new JMenu();
        menu.setText(LanguageBundle.getString("in_mnuTools"));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.PREFERENCES_COMMAND)));
        menu.addSeparator();
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.LOG_COMMAND)));
        menu.add(new LoggingLevelMenu());
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.CALCULATOR_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.COREVIEW_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SOLVERVIEW_COMMAND)));
        return menu;
    }

    private JMenu createHelpMenu()
    {
        JMenu menu = new JMenu();
        menu.setText(LanguageBundle.getString("in_mnuHelp"));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_DOCS_COMMAND)));
        menu.addSeparator();
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_OGL_COMMAND)));
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_TIPOFTHEDAY_COMMAND)));
        menu.addSeparator();
        menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_ABOUT_COMMAND)));
        return menu;
    }

    @Override
    public void setCharacter(CharacterFacade character)
    {
        this.character = character;
        tempMenu.setListModel(character.getAvailableTempBonuses());
    }

    private class FileMenu extends AbstractListMenu<File> implements ActionListener
    {

        public FileMenu()
        {
            super(actionMap.get(PCGenActionMap.FILE_COMMAND));
            add(new JMenuItem(actionMap.get(PCGenActionMap.NEW_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
            addSeparator();
            add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSEALL_COMMAND)));
            addSeparator();

            add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEALL_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.REVERT_COMMAND)));
            addSeparator();
            add(new PartyMenu());
            addSeparator();

            add(new JMenuItem(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_COMMAND)));
            addSeparator();
            setOffset(16);
            setListModel(CharacterManager.getRecentCharacters());
            addSeparator();

            add(new JMenuItem(actionMap.get(PCGenActionMap.EXIT_COMMAND)));
        }

        @Override
        protected JMenuItem createMenuItem(File item, int index)
        {
            JMenuItem menuItem = new JMenuItem();
            menuItem.setText((index + 1) + " " + item.getName()); //$NON-NLS-1$
            menuItem.setToolTipText(
                    LanguageBundle.getFormattedString("in_OpenRecentCharTip", item.getAbsolutePath())); //$NON-NLS-1$
            menuItem.setActionCommand(item.getAbsolutePath());
            menuItem.setMnemonic(String.valueOf(index + 1).charAt(0));
            menuItem.addActionListener(this);
            return menuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            frame.loadCharacterFromFile(new File(e.getActionCommand()));
        }

    }

    private class PartyMenu extends AbstractListMenu<File> implements ActionListener
    {

        public PartyMenu()
        {
            super(actionMap.get(PCGenActionMap.PARTY_COMMAND));
            add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_PARTY_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_PARTY_COMMAND)));
            addSeparator();

            add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_PARTY_COMMAND)));
            add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_PARTY_COMMAND)));
            addSeparator();
            setOffset(6);
            setListModel(CharacterManager.getRecentParties());
        }

        @Override
        protected JMenuItem createMenuItem(File item, int index)
        {
            JMenuItem menuItem = new JMenuItem();
            menuItem.setText((index + 1) + " " + item.getName()); //$NON-NLS-1$
            menuItem.setToolTipText(item.getAbsolutePath());
            menuItem.setActionCommand(item.getAbsolutePath());
            menuItem.setMnemonic(String.valueOf(index + 1).charAt(0));
            menuItem.addActionListener(this);
            return menuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            frame.loadPartyFromFile(new File(e.getActionCommand()));
        }

    }

    private final class QuickSourceMenu extends AbstractRadioListMenu<SourceSelectionFacade>
            implements ReferenceListener<SourceSelectionFacade>
    {

        private QuickSourceMenu()
        {

            super(actionMap.get(PCGenActionMap.SOURCES_LOAD_COMMAND));
            super.setText(LanguageBundle.getString("in_mnuSourcesLoad"));


            ReferenceFacade<SourceSelectionFacade> ref = uiContext.getCurrentSourceSelectionRef();
            setSelectedItem(ref.get());
            ListFacade<SourceSelectionFacade> sources = FacadeFactory.getDisplayedSourceSelections();
            setListModel(new SortedListFacade<>(Comparators.toStringIgnoreCaseCollator(), sources));
            ref.addReferenceListener(this);
        }

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                Object item = e.getItemSelectable().getSelectedObjects()[0];
                if (frame.loadSourceSelection((SourceSelectionFacade) item))
                {
                    setSelectedItem(uiContext.getCurrentSourceSelectionRef().get());
                }
            }
        }

        @Override
        public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
        {
            clearSelection();
            setSelectedItem(e.getNewReference());
        }

    }

    private final class TempBonusMenu extends AbstractListMenu<TempBonusFacade> implements ItemListener
    {

        private TempBonusMenu()
        {
            super(actionMap.get(PCGenActionMap.TEMP_BONUS_COMMAND));
        }

        @Override
        protected JMenuItem createMenuItem(TempBonusFacade item, int index)
        {
            Objects.requireNonNull(item);
            return new CheckBoxMenuItem(item, character.getTempBonuses().containsElement(item), this);
        }

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            TempBonusFacade bonus = (TempBonusFacade) e.getItemSelectable().getSelectedObjects()[0];
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                character.addTempBonus(bonus);
            } else
            {
                character.removeTempBonus(bonus);
            }
        }

    }

    /**
     * The Class {@code LoggingLevelMenu} provides a menu to control the
     * level of logging output.
     */
    private class LoggingLevelMenu extends AbstractRadioListMenu<LoggingLevelWrapper>
    {
        public LoggingLevelMenu()
        {
            super(actionMap.get(PCGenActionMap.LOGGING_LEVEL_COMMAND));
            DefaultListFacade<LoggingLevelWrapper> levels = new DefaultListFacade<>();
            Level currentLvl = Logging.getCurrentLoggingLevel();
            LoggingLevelWrapper current = null;
            for (Level level : Logging.getLoggingLevels())
            {
                LoggingLevelWrapper levelWrapper = new LoggingLevelWrapper(level);
                levels.addElement(levelWrapper);
                if (level == currentLvl)
                {
                    current = levelWrapper;
                }
            }
            setListModel(levels);
            setSelectedItem(current);
        }

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                Object item = e.getItemSelectable().getSelectedObjects()[0];
                Level level = ((LoggingLevelWrapper) item).getLevel();
                Logging.setCurrentLoggingLevel(level);
            }
        }

    }

    /**
     * The Class {@code LoggingLevelWrapper} provides a display wrapper
     * around a Level.
     */
    private static class LoggingLevelWrapper
    {
        private final Level level;

        public LoggingLevelWrapper(Level level)
        {
            this.level = level;
        }

        @Override
        public String toString()
        {
            return LanguageBundle.getString("in_loglvl" + level.getName());
        }

        /**
         * @return the level
         */
        public Level getLevel()
        {
            return level;
        }

    }

}
