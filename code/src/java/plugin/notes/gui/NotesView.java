/*
 *  NotesView.java - the main view for the Notes plugin for GMGen
 *  Copyright (C) 2003 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.notes.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ProgressMonitor;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.html.HTML;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.gui.ExtendedHTMLDocument;
import gmgen.gui.ExtendedHTMLEditorKit;
import gmgen.gui.FlippingSplitPane;
import gmgen.gui.ImageFileChooserPreview;
import gmgen.util.LogReceiver;
import gmgen.util.LogUtilities;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.CommonMenuText;
import pcgen.gui2.tools.Icons;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import plugin.notes.NotesPlugin;

import org.apache.commons.io.FileUtils;

/**
 *  This class is the main view for the Notes Plugin. Mostof the work is done
 *  here and in the NotesTreeNode Class.
 */
public class NotesView extends JPanel
{

	/**
	 * Extension with a point
	 */
	private static final String EXTENSION = '.' + NotesPlugin.EXTENSION_NOTES;

	private static final String OPTION_NAME_LASTFILE = NotesPlugin.LOG_NAME + ".LastFile"; //$NON-NLS-1$

	/**  Drop Target for the Edit Area */
	private DropTarget editAreaDT;

	/**  Drop Target for the File Bar */
	private DropTarget filesBarDT;

	/**  Drop Target for the Tree */
	private DropTarget treeDT;

	/**  Insert OL Action for JTextPane */
	private ExtendedHTMLEditorKit.InsertListAction actionListOrdered =
			new ExtendedHTMLEditorKit.InsertListAction("InsertOLItem", HTML.Tag.OL);

	/**  Insert UL Action for JTextPane */
	private ExtendedHTMLEditorKit.InsertListAction actionListUnordered =
			new ExtendedHTMLEditorKit.InsertListAction("InsertULItem", HTML.Tag.UL);

	// End of variables declaration//GEN-END:variables
	protected NotesPlugin plugin;

	/**  Root node of tree */
	protected NotesTreeNode root;

	/**  Redo Action for JTextPane */
	private RedoAction redoAction = new RedoAction();

	/**  Data Directory */
	private File dataDir;

	/**  Undo Action for JTextPane */
	private UndoAction undoAction = new UndoAction();

	/**  Undo Manager */
	protected UndoManager undo = new UndoManager();

	/**  Image extensions that this supports */

	// TODO: Move Image extensions to properties
	private final String[] extsIMG = {"gif", "jpg", "jpeg", "png"};
	private JButton boldButton;
	private JButton bulletButton;
	private JButton centerJustifyButton;
	private JButton colorButton;
	private JButton copyButton;
	private JButton cutButton;
	private JButton deleteButton;
	private JButton enumButton;
	private JButton exportButton;
	private JButton fileLeft;
	private JButton fileRight;
	private JButton imageButton;
	private JButton italicButton;
	private JButton leftJustifyButton;
	private JButton newButton;
	private JButton pasteButton;
	private JButton revertButton;
	private JButton rightJustifyButton;
	private JButton saveButton;
	private JButton underlineButton;
	private JComboBox sizeCB;
	private JPanel filePane;
	private JPanel jPanel1;
	private JPanel jPanel2;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private FlippingSplitPane jSplitPane1;
	private JTextPane editor;
	private JToolBar alignmentBar;
	private JToolBar clipboardBar;
	private JToolBar fileBar;
	private JToolBar filesBar;
	private JToolBar formatBar;
	private JTree notesTree;

	/**
	 *  Creates new form NotesView
	 *
	 *@param  dataDir  Data directory where notes will be stored.
	 * @param plugin
	 */
	public NotesView(File dataDir, NotesPlugin plugin)
	{
		this.plugin = plugin;
		this.dataDir = dataDir;
		initComponents();
		initEditingComponents();
		initDnDComponents();
		initTree();
		initFileBar(new ArrayList<>());
		initLogging();
		notesTree.setSelectionRow(0);
	}

	/**
	 *  Searches a text component for a particular action.
	 *
	 *@param  textComponent  Text component to search for the action in
	 *@param  name           name of the action to get
	 *@return                the action
	 */
	private Action getActionByName(JTextComponent textComponent, String name)
	{
		// TODO: This should be static in a GUIUtilities file
		for (Action a : textComponent.getActions())
		{
			if (a.getValue(Action.NAME).equals(name))
			{
				return a;
			}
		}

		return null;
	}

	private static FileFilter getFileType()
	{
		return new FileNameExtensionFilter(LanguageBundle.getString("in_plugin_notes_file"),
			NotesPlugin.EXTENSION_NOTES);

	}

	/**
	 *  {@literal handle File->Open.} Will open any .gmn files, and import them into your
	 *  notes structure
	 */
	public void handleOpen()
	{
		// TODO fix
		String sFile = SettingsHandler.getGMGenOption(OPTION_NAME_LASTFILE, System.getProperty("user.dir"));
		File defaultFile = new File(sFile);
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(defaultFile);
		chooser.addChoosableFileFilter(getFileType());
		chooser.setFileFilter(getFileType());
		chooser.setMultiSelectionEnabled(true);
		Component component = GMGenSystem.inst;
		Cursor originalCursor = component.getCursor();
		component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		int option = chooser.showOpenDialog(GMGenSystem.inst);

		if (option == JFileChooser.APPROVE_OPTION)
		{
			for (File noteFile : chooser.getSelectedFiles())
			{
				SettingsHandler.setGMGenOption(OPTION_NAME_LASTFILE, noteFile.toString());

				if (noteFile.toString().endsWith(EXTENSION))
				{
					openGMN(noteFile);
				}
			}
		}

		GMGenSystem.inst.setCursor(originalCursor);
		refreshTree();
	}

	/**
	 *  fills the 'edit' menu of the main menu
	 *
	 *@param  editMenu  The Edit Menu
	 */
	public void initEditMenu(JMenu editMenu)
	{
		JMenuItem paste = new JMenuItem();
		CommonMenuText.name(paste, "mnuEditPaste"); //$NON-NLS-1$
		paste.addActionListener(this::pasteButtonActionPerformed);
		editMenu.insert(paste, 0);

		JMenuItem copy = new JMenuItem();
		CommonMenuText.name(copy, "mnuEditCopy"); //$NON-NLS-1$
		copy.addActionListener(this::copyButtonActionPerformed);
		editMenu.insert(copy, 0);

		JMenuItem cut = new JMenuItem();
		CommonMenuText.name(cut, "mnuEditCut"); //$NON-NLS-1$
		cut.addActionListener(this::cutButtonActionPerformed);
		editMenu.insert(cut, 0);
		editMenu.insertSeparator(0);
		editMenu.insert(redoAction, 0);
		editMenu.insert(undoAction, 0);
	}

	/**
	 *  Opens a .gmn file
	 *
	 *@param  notesFile  .gmn file to open
	 */
	private void openGMN(File notesFile)
	{
		try
		{
			Object obj = notesTree.getLastSelectedPathComponent();

			if (obj instanceof NotesTreeNode)
			{
				NotesTreeNode node = (NotesTreeNode) obj;

				if (node != root)
				{
					int choice = JOptionPane.showConfirmDialog(this,
						"Importing note " + notesFile.getName() + " into a node other then root, Continue?",
						"Importing to a node other then root", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (choice == JOptionPane.NO_OPTION)
					{
						return;
					}
				}

				InputStream in = new BufferedInputStream(new FileInputStream(notesFile));
				ZipInputStream zin = new ZipInputStream(in);
				ZipEntry e;

				ProgressMonitor pm = new ProgressMonitor(GMGenSystem.inst, "Reading Notes Export", "Reading", 1, 1000);
				int progress = 1;

				while ((e = zin.getNextEntry()) != null)
				{
					unzip(zin, e.getName(), node.getDir());
					progress++;

					if (progress > 99)
					{
						progress = 99;
					}

					pm.setProgress(progress);
				}

				zin.close();
				pm.close();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, "Error Reading File" + notesFile.getName());
			Logging.errorPrint("Error Reading File" + notesFile.getName());
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**  refreshs the tree, and updates it's UI */
	public void refreshTree()
	{
		root.refresh();
		notesTree.updateUI();
	}

	/**  called when window is closed, saves everything in the tree */
	public void windowClosed()
	{
		if (root.isTreeDirty())
		{
			GMGenSystemView.getTabPane().setSelectedComponent(this);
		}

		root.checkSave();
	}

	//Note Import/Export methods

	/**
	 *  Exports a node out to a gmn file.
	 *
	 *@param  node  node to export to file
	 */
	private void exportFile(NotesTreeNode node)
	{
		JFileChooser fLoad = new JFileChooser();
		String sFile = SettingsHandler.getGMGenOption(OPTION_NAME_LASTFILE, "");
		new File(sFile);

		FileFilter ff = getFileType();
		fLoad.addChoosableFileFilter(ff);
		fLoad.setFileFilter(ff);

		int returnVal = fLoad.showSaveDialog(this);

		try
		{
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fLoad.getSelectedFile().getName();
				String dirName = fLoad.getSelectedFile().getParent();

				String extension = EXTENSION;
				if (!fileName.contains(extension))
				{
					fileName += extension;
				}

				File expFile = new File(dirName + File.separator + fileName);

				if (expFile.exists())
				{
					int choice = JOptionPane.showConfirmDialog(this, "File Exists, Overwrite?", "File Exists",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (choice == JOptionPane.NO_OPTION)
					{
						return;
					}
				}

				SettingsHandler.setGMGenOption(OPTION_NAME_LASTFILE, expFile.toString());
				writeNotesFile(expFile, node);
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Error Writing File");
			Logging.errorPrint("Error Writing to file: " + e.getMessage(), e);
		}
	}

	/**
	 *  gets the number of files in a directory so that you can have a progress
	 *  meter as we zip them into a gmn file. This function is recursive
	 *
	 *@param  count  File to count the children of
	 *@return        count of all files in this dir
	 */
	private int fileCount(File count)
	{
		// TODO: Shouldn't this really be a static method in MiscUtils?
		int num = 0;
		for (File f : count.listFiles())
		{
			if (f.isDirectory())
			{
				num = num + fileCount(f);
			}
			else
			{
				num++;
			}
		}

		return num;
	}

	/**
	 *  Sets a border of an editing button to indicate that the function of the
	 *  button is active according to the text location of the cursor
	 *
	 *@param  button  Button to highlight
	 */
	private void highlightButton(JButton button)
	{
		button.setBorder(new BevelBorder(BevelBorder.LOWERED));
	}

	//Action methods

	/**
	 *  Performs an action of a particular name on the man editor.
	 *
	 *@param  name  name of the action to perform.
	 *@param  evt   ActionEvent that sparked the calling of this function.
	 */
	private void performTextPaneAction(String name, java.awt.event.ActionEvent evt)
	{
		Action action = getActionByName(editor, name);
		action.actionPerformed(evt);
		editor.grabFocus();

		int cp = editor.getCaretPosition();
		updateButtons(editor, cp);
	}

	/**
	 *  Sets a border of an editing button to indicate that the function of the
	 *  button is not active according to the text location of the cursor
	 *
	 *@param  button  button to set in standard mode
	 */
	private void stdButton(JButton button)
	{
		button.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	}

	/**
	 *  Unzips one file from a zipinputstream
	 *
	 *@param  zin              Zip input stream
	 *@param  homeDir          Directory to unzip the file to
	 *@param  entry            Description of the Parameter
	 *@exception  IOException  read or write error
	 */
	private void unzip(ZipInputStream zin, String entry, File homeDir) throws IOException
	{
		// TODO: This function really should be in MiscUtils as a static
		File outFile = new File(homeDir.getPath() + File.separator + entry);
		File parentDir = outFile.getParentFile();
		parentDir.mkdirs();
		outFile.createNewFile();

		FileOutputStream out = new FileOutputStream(outFile);
		byte[] b = new byte[512];
		int len = 0;

		while ((len = zin.read(b)) != -1)
		{
			out.write(b, 0, len);
		}

		out.close();
	}

	//Methods for dealing with button appearance

	/**
	 *  Updates Editing buttons based on the location of the cursor
	 *
	 *@param  textPane  text pane to update buttons base on
	 *@param  pos       current text position
	 */
	private void updateButtons(JTextPane textPane, int pos)
	{
		StyledDocument doc = textPane.getStyledDocument();
		AttributeSet set = doc.getCharacterElement(pos - 1).getAttributes();
		AttributeSet set1 = doc.getCharacterElement(pos).getAttributes();

		if (StyleConstants.isBold(set) && StyleConstants.isBold(set1))
		{
			highlightButton(boldButton);
		}
		else
		{
			stdButton(boldButton);
		}

		if (StyleConstants.isItalic(set) && StyleConstants.isItalic(set1))
		{
			highlightButton(italicButton);
		}
		else
		{
			stdButton(italicButton);
		}

		if (StyleConstants.isUnderline(set) && StyleConstants.isUnderline(set1))
		{
			highlightButton(underlineButton);
		}
		else
		{
			stdButton(underlineButton);
		}

		int align = StyleConstants.getAlignment(set);
		stdButton(leftJustifyButton);
		stdButton(rightJustifyButton);
		stdButton(centerJustifyButton);

		if (align == StyleConstants.ALIGN_LEFT)
		{
			highlightButton(leftJustifyButton);
		}
		else if (align == StyleConstants.ALIGN_RIGHT)
		{
			highlightButton(rightJustifyButton);
		}
		else if (align == StyleConstants.ALIGN_CENTER)
		{
			highlightButton(centerJustifyButton);
		}

		int fontSize = StyleConstants.getFontSize(set);

		for (int i = 0; i < sizeCB.getItemCount(); i++)
		{
			String value = (String) sizeCB.getItemAt(i);

			if (value.equals(Integer.toString(fontSize)))
			{
				sizeCB.setSelectedItem(value);

				break;
			}
		}
	}

	/**
	 *  Writes out a directory to a zipoutputstream
	 *
	 *@param  out              Zip output stream to write to
	 *@param  parentDir        parent dir of whole structure to be written out
	 *@param  currentDir       dir to be zipped up
	 *@param  pm               progress meter that will display the progress
	 *@param  progress         progress up to this dir
	 *@return                  current progress
	 *@exception  IOException  write or read failed for some reason
	 */
	private int writeNotesDir(ZipOutputStream out, File parentDir, File currentDir, ProgressMonitor pm, int progress)
		throws IOException
	{
		byte[] buffer = new byte[4096];
		int bytes_read;
		int returnValue = progress;

		for (File f : currentDir.listFiles())
		{
			if (pm.isCanceled())
			{
				return 0;
			}

			if (f.isDirectory())
			{
				returnValue = writeNotesDir(out, parentDir, f, pm, returnValue);
			}
			else
			{

				try (FileInputStream in = new FileInputStream(f)) {
					String parentPath = parentDir.getParentFile().getAbsolutePath();
					ZipEntry entry = new ZipEntry(f.getAbsolutePath().substring(parentPath.length() + 1));
					out.putNextEntry(entry);

					while ((bytes_read = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytes_read);
					}
				}
				//TODO: Should this really be ignored?

				returnValue++;
			}
		}

		pm.setProgress(returnValue);

		return returnValue;
	}

	/**
	 *  Writes out a GMN file
	 *
	 *@param  exportFile       file to export to
	 *@param  node             node to export
	 *@exception  IOException  file write failed for some reason
	 */
	private void writeNotesFile(File exportFile, NotesTreeNode node) throws IOException
	{
		File dir = node.getDir();

		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(exportFile))) {
			int max = fileCount(dir);
			ProgressMonitor pm = new ProgressMonitor(GMGenSystem.inst, "Writing out Notes Export", "Writing", 0, max);
			writeNotesDir(out, dir, dir, pm, 0);
		}
		//TODO: Should this really be ignored?

		pm.close();
	}

	//CoreUtility methods
	private File getCurrentDir()
	{
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;

			return node.getDir();
		}

		return null;
	}

	/**
	 *  obtains an Image for input using a custom JFileChooser dialog
	 *
	 *@param  startDir  Directory to open JFielChooser to
	 *@param  exts      Extensions to search for
	 *@param  desc      Description for files
	 *@return           File pointing to the selected image
	 */
	private File getImageFromChooser(String startDir, String[] exts, String desc)
	{
		JFileChooser jImageDialog = new JFileChooser();
		jImageDialog.setCurrentDirectory(new File(startDir));
		jImageDialog.setAccessory(new ImageFileChooserPreview(jImageDialog));
		jImageDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
		jImageDialog.setFileFilter(new FileNameExtensionFilter(desc, exts));
		jImageDialog.setDialogTitle("Select an Image to Insert");

		int optionSelected = jImageDialog.showDialog(this, "Insert");

		if (optionSelected == JFileChooser.APPROVE_OPTION)
		{
			return jImageDialog.getSelectedFile();
		}

		return null;
	}

	private void notesTreeNodesChanged()
	{
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;

			try
			{
				node.rename((String) node.getUserObject());
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	//GEN-LAST:event_italicButtonActionPerformed
	private void boldButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_boldButtonActionPerformed
		performTextPaneAction("font-bold", evt);
	}

	//GEN-LAST:event_rightJustifyButtonActionPerformed
	private void centerJustifyButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_centerJustifyButtonActionPerformed
		ActionListener action = new AlignmentAction("Align Centre", StyleConstants.ALIGN_CENTER);
		action.actionPerformed(evt);
		editor.grabFocus();

		int cp = editor.getCaretPosition();
		updateButtons(editor, cp);
	}

	//GEN-LAST:event_leftJustifyButtonActionPerformed
	private void colorButtonActionPerformed()
	{
		//GEN-FIRST:event_colorButtonActionPerformed
		AttributeSet as = editor.getCharacterAttributes();
		SimpleAttributeSet sas = new SimpleAttributeSet(as);
		Color newColor = JColorChooser.showDialog(GMGenSystem.inst, "Choose Text Color",
			editor.getStyledDocument().getForeground(as));

		if (newColor != null)
		{
			StyleConstants.setForeground(sas, newColor);
			editor.setCharacterAttributes(sas, true);
		}

		editor.repaint();
	}

	//GEN-LAST:event_pasteButtonActionPerformed
	private void copyButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_copyButtonActionPerformed
		performTextPaneAction(DefaultEditorKit.copyAction, evt);
	}

	//GEN-LAST:event_copyButtonActionPerformed
	private void cutButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_cutButtonActionPerformed
		performTextPaneAction(DefaultEditorKit.cutAction, evt);
	}

	//GEN-LAST:event_cutButtonActionPerformed
	private void deleteButtonActionPerformed()
	{
		//GEN-FIRST:event_deleteButtonActionPerformed
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;
			node.delete();
		}

		notesTree.updateUI();
	}

	//GEN-LAST:event_newButtonActionPerformed
	private void editorCaretUpdate(CaretEvent evt)
	{
		//GEN-FIRST:event_editorCaretUpdate
		int dot = evt.getDot();
		updateButtons(editor, dot);

		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;

			if (node.isDirty())
			{
				revertButton.setEnabled(true);
			}
			else
			{
				revertButton.setEnabled(false);
			}
		}
	}

	// Key Events
	private void editorKeyTyped(KeyEvent evt)
	{
		editor.getCaretPosition();
		editor.getStyledDocument();

		if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
		{
			handleBackspace();
		}
		else if (evt.getKeyChar() == KeyEvent.VK_ENTER)
		{
			handleEnter();
		}
	}

	//GEN-LAST:event_editorCaretUpdate
	// Edit Events
	private void editorUndoableEditHappened(UndoableEditEvent e)
	{
		undo.addEdit(e.getEdit());
		undoAction.updateUndoState();
		redoAction.updateRedoState();
	}

	//GEN-END:initComponents
	private void exportButtonActionPerformed()
	{
		//GEN-FIRST:event_exportButtonActionPerformed
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;
			exportFile(node);
		}
	}

	//GEN-LAST:event_fileRightActionPerformed
	private void fileLeftActionPerformed()
	{
		//GEN-FIRST:event_fileLeftActionPerformed
		if (filesBar.getComponentCount() > 1)
		{
			Component c = filesBar.getComponent(filesBar.getComponentCount() - 1);
			filesBar.remove(c);
			filesBar.add(c, 0);
		}

		filesBar.updateUI();
	}

	//GEN-LAST:event_imageButtonActionPerformed
	private void fileRightActionPerformed()
	{
		//GEN-FIRST:event_fileRightActionPerformed
		if (filesBar.getComponentCount() > 1)
		{
			Component c = filesBar.getComponent(0);
			filesBar.remove(c);
			filesBar.add(c);
		}

		filesBar.updateUI();
	}

	//methods dealing with Key Events
	private void handleBackspace()
	{
		// TODO: This sucks, clean it up
		Element elem;
		int pos = editor.getCaretPosition();
		StyledDocument htmlDoc = editor.getStyledDocument();

		try
		{
			if (pos > 0)
			{
				if ((editor.getSelectedText()) != null)
				{
					ExtendedHTMLEditorKit.delete(editor);
					return;
				}

				int sOffset = htmlDoc.getParagraphElement(pos).getStartOffset();

				if (sOffset == editor.getSelectionStart())
				{

					if (ExtendedHTMLEditorKit.checkParentsTag(htmlDoc.getParagraphElement(editor.getCaretPosition()),
						HTML.Tag.LI))
					{
						elem = ExtendedHTMLEditorKit
							.getListItemParent(htmlDoc.getCharacterElement(editor.getCaretPosition()));
						boolean content = false;
						int so = elem.getStartOffset();
						int eo = elem.getEndOffset();

						if ((so + 1) < eo)
						{
							char[] temp = editor.getText(so, eo - so).toCharArray();
							for (char aTemp : temp)
							{
								if (!Character.isWhitespace(aTemp))
								{
									content = true;
								}
							}
						}

						if (!content)
						{
							elem.getParentElement();
							ExtendedHTMLEditorKit.removeTag(editor, elem);
							editor.setCaretPosition(sOffset - 1);
							return;
						}
						editor.setCaretPosition(editor.getCaretPosition() - 1);
						editor.moveCaretPosition(editor.getCaretPosition() - 2);
						editor.replaceSelection("");
						return;
					}
				}

				editor.replaceSelection("");
			}
		}
		catch (BadLocationException ble)
		{
			Logging.errorPrint(ble.getMessage(), ble);
		}
	}

	private void handleEnter()
	{
		// TODO: this sucks.  clean it up
		Element elem;
		int pos = editor.getCaretPosition();
		ExtendedHTMLDocument htmlDoc = (ExtendedHTMLDocument) editor.getStyledDocument();

		try
		{
			if (ExtendedHTMLEditorKit.checkParentsTag(htmlDoc.getParagraphElement(editor.getCaretPosition()),
				HTML.Tag.UL)
				|| ExtendedHTMLEditorKit.checkParentsTag(htmlDoc.getParagraphElement(editor.getCaretPosition()),
					HTML.Tag.OL))
			{
				elem = ExtendedHTMLEditorKit.getListItemParent(htmlDoc.getCharacterElement(editor.getCaretPosition()));

				int so = elem.getStartOffset();
				int eo = elem.getEndOffset();
				char[] temp = editor.getText(so, eo - so).toCharArray();
				boolean content = false;

				for (char aTemp : temp)
				{
					if (!Character.isWhitespace(aTemp))
					{
						content = true;
					}
				}

				int repos = -1;
				if (content)
				{
					int end = -1;
					int j = temp.length;

					do
					{
						j--;

						if (Character.isLetterOrDigit(temp[j]))
						{
							end = j;
						}
					}
					while ((end == -1) && (j >= 0));

					j = end;

					do
					{
						j++;

						if (!Character.isSpaceChar(temp[j]))
						{
							repos = j - end - 1;
						}
					}
					while ((repos == -1) && (j < temp.length));

					if (repos == -1)
					{
						repos = 0;
					}
				}

				if ((elem.getStartOffset() == elem.getEndOffset()) || !content)
				{
					manageListElement(htmlDoc);
				}
				else
				{
					if ((editor.getCaretPosition() + 1) == elem.getEndOffset())
					{
						ExtendedHTMLEditorKit.insertListElement(editor, "");
						editor.setCaretPosition(pos - repos);
					}
					else
					{
						int caret = editor.getCaretPosition();
						String tempString = editor.getText(caret, eo - caret);
						editor.select(caret, eo - 1);
						editor.replaceSelection("");
						ExtendedHTMLEditorKit.insertListElement(editor, tempString);

						Element newLi = ExtendedHTMLEditorKit
							.getListItemParent(htmlDoc.getCharacterElement(editor.getCaretPosition()));
						editor.setCaretPosition(newLi.getEndOffset());
					}
				}
			}
		}
		catch (BadLocationException ble)
		{
			Logging.errorPrint(ble.getMessage(), ble);
		}
	}

	//GEN-LAST:event_revertButtonActionPerformed
	private void imageButtonActionPerformed()
	{
		//GEN-FIRST:event_imageButtonActionPerformed
		try
		{
			insertLocalImage(null);
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**
	 *  This method is called from within the constructor to initialize the form.
	 *  WARNING: Do NOT modify this code. The content of this method is always
	 *  regenerated by the Form Editor.
	 */
	private void initComponents()
	{
		//GEN-BEGIN:initComponents
		jSplitPane1 = new FlippingSplitPane();
		jScrollPane1 = new JScrollPane();
		notesTree = new JTree();
		jPanel1 = new JPanel();
		jScrollPane2 = new JScrollPane();
		editor = new JTextPane();
		jPanel2 = new JPanel();
		fileBar = new JToolBar();
		newButton = new JButton();
		saveButton = new JButton();
		exportButton = new JButton();
		revertButton = new JButton();
		deleteButton = new JButton();
		clipboardBar = new JToolBar();
		cutButton = new JButton();
		copyButton = new JButton();
		pasteButton = new JButton();
		formatBar = new JToolBar();
		sizeCB = new JComboBox();
		boldButton = new JButton();
		italicButton = new JButton();
		underlineButton = new JButton();
		colorButton = new JButton();
		bulletButton = new JButton();
		enumButton = new JButton();
		imageButton = new JButton();
		alignmentBar = new JToolBar();
		leftJustifyButton = new JButton();
		centerJustifyButton = new JButton();
		rightJustifyButton = new JButton();
		filePane = new JPanel();
		fileLeft = new JButton();
		fileRight = new JButton();
		filesBar = new JToolBar();

		setLayout(new java.awt.BorderLayout());

		jSplitPane1.setDividerLocation(175);
		jSplitPane1.setDividerSize(5);
		jScrollPane1.setViewportView(notesTree);

		jSplitPane1.setLeftComponent(jScrollPane1);

		jPanel1.setLayout(new java.awt.BorderLayout());

		editor.addCaretListener(this::editorCaretUpdate);

		jScrollPane2.setViewportView(editor);

		jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

		jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

		newButton.setIcon(Icons.stock_new.getImageIcon());
		newButton.setToolTipText("New Node");
		newButton.setBorder(new EtchedBorder());
		newButton.setEnabled(false);
		newButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				newButtonActionPerformed();
			}
		});

		fileBar.add(newButton);

		saveButton.setIcon(Icons.stock_save.getImageIcon());
		saveButton.setToolTipText("Save Node");
		saveButton.setBorder(new EtchedBorder());
		saveButton.setEnabled(false);
		saveButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				saveButtonActionPerformed();
			}
		});

		fileBar.add(saveButton);

		exportButton.setIcon(Icons.stock_export.getImageIcon());
		exportButton.setToolTipText("Export");
		exportButton.setBorder(new EtchedBorder());
		exportButton.setEnabled(false);
		exportButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				exportButtonActionPerformed();
			}
		});

		fileBar.add(exportButton);

		revertButton.setIcon(Icons.stock_revert.getImageIcon());
		revertButton.setToolTipText("Revert to Saved");
		revertButton.setBorder(new EtchedBorder());
		revertButton.setEnabled(false);
		revertButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				revertButtonActionPerformed();
			}
		});

		fileBar.add(revertButton);

		deleteButton.setIcon(Icons.stock_broken_image.getImageIcon());
		deleteButton.setToolTipText("Delete Node");
		deleteButton.setBorder(new EtchedBorder());
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				deleteButtonActionPerformed();
			}
		});

		fileBar.add(deleteButton);

		jPanel2.add(fileBar);

		cutButton.setIcon(Icons.stock_cut.getImageIcon());
		cutButton.setToolTipText("Cut");
		cutButton.setBorder(new EtchedBorder());
		cutButton.addActionListener(this::cutButtonActionPerformed);

		clipboardBar.add(cutButton);

		copyButton.setIcon(Icons.stock_copy.getImageIcon());
		copyButton.setToolTipText("Copy");
		copyButton.setBorder(new EtchedBorder());
		copyButton.addActionListener(this::copyButtonActionPerformed);

		clipboardBar.add(copyButton);

		pasteButton.setIcon(Icons.stock_paste.getImageIcon());
		pasteButton.setToolTipText("Paste");
		pasteButton.setBorder(new EtchedBorder());
		pasteButton.addActionListener(this::pasteButtonActionPerformed);

		clipboardBar.add(pasteButton);

		jPanel2.add(clipboardBar);

		sizeCB.setToolTipText("Size");
		sizeCB.setBorder(new EtchedBorder());
		sizeCB.addActionListener(this::sizeCBActionPerformed);

		formatBar.add(sizeCB);

		boldButton.setIcon(Icons.stock_text_bold.getImageIcon());
		boldButton.setToolTipText("Bold");
		boldButton.setBorder(new EtchedBorder());
		boldButton.addActionListener(this::boldButtonActionPerformed);

		formatBar.add(boldButton);

		italicButton.setIcon(Icons.stock_text_italic.getImageIcon());
		italicButton.setToolTipText("Italic");
		italicButton.setBorder(new EtchedBorder());
		italicButton.addActionListener(this::italicButtonActionPerformed);

		formatBar.add(italicButton);

		underlineButton.setIcon(Icons.stock_text_underline.getImageIcon());
		underlineButton.setToolTipText("Underline");
		underlineButton.setBorder(new EtchedBorder());
		underlineButton.addActionListener(this::underlineButtonActionPerformed);

		formatBar.add(underlineButton);

		colorButton.setForeground(java.awt.SystemColor.text);
		colorButton.setIcon(Icons.menu_mode_rgb.getImageIcon());
		colorButton.setToolTipText("Color");
		colorButton.setBorder(new EtchedBorder());
		colorButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				colorButtonActionPerformed();
			}
		});

		formatBar.add(colorButton);

		bulletButton.setIcon(Icons.stock_list_bulet.getImageIcon());
		bulletButton.setToolTipText("Bulleted List");
		bulletButton.setAction(actionListUnordered);
		bulletButton.setBorder(new EtchedBorder());
		formatBar.add(bulletButton);

		enumButton.setIcon(Icons.stock_list_enum.getImageIcon());
		enumButton.setToolTipText("Numbered List");
		enumButton.setAction(actionListOrdered);
		enumButton.setBorder(new EtchedBorder());
		formatBar.add(enumButton);

		imageButton.setIcon(Icons.stock_insert_graphic.getImageIcon());
		imageButton.setBorder(new EtchedBorder());
		imageButton.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				imageButtonActionPerformed();
			}
		});

		formatBar.add(imageButton);

		jPanel2.add(formatBar);

		leftJustifyButton.setIcon(Icons.stock_text_align_left.getImageIcon());
		leftJustifyButton.setToolTipText("Left Justify");
		leftJustifyButton.setBorder(new EtchedBorder());
		leftJustifyButton.addActionListener(this::leftJustifyButtonActionPerformed);

		alignmentBar.add(leftJustifyButton);

		centerJustifyButton.setIcon(Icons.stock_text_align_center.getImageIcon());
		centerJustifyButton.setToolTipText("Center");
		centerJustifyButton.setBorder(new EtchedBorder());
		centerJustifyButton.addActionListener(this::centerJustifyButtonActionPerformed);

		alignmentBar.add(centerJustifyButton);

		rightJustifyButton.setIcon(Icons.stock_text_align_right.getImageIcon());
		rightJustifyButton.setToolTipText("Right Justify");
		rightJustifyButton.setBorder(new EtchedBorder());
		rightJustifyButton.addActionListener(this::rightJustifyButtonActionPerformed);

		alignmentBar.add(rightJustifyButton);

		jPanel2.add(alignmentBar);

		jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

		filePane.setLayout(new BoxLayout(filePane, BoxLayout.X_AXIS));

		fileLeft.setText("<");
		fileLeft.setBorder(new EtchedBorder());
		fileLeft.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				fileLeftActionPerformed();
			}
		});

		filePane.add(fileLeft);

		fileRight.setText(">");
		fileRight.setBorder(new EtchedBorder());
		fileRight.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				fileRightActionPerformed();
			}
		});

		filePane.add(fileRight);

		filePane.add(filesBar);

		jPanel1.add(filePane, java.awt.BorderLayout.SOUTH);

		jSplitPane1.setRightComponent(jPanel1);

		add(jSplitPane1, java.awt.BorderLayout.CENTER);
	}

	private void initDnDComponents()
	{
		filesBarDT = new DropTarget(filesBar, new DropBarListener());
		treeDT = new DropTarget(notesTree, new DropTreeListener());
	}

	private void initEditingComponents()
	{
		bulletButton.setIcon(Icons.stock_list_bulet.getImageIcon());
		bulletButton.setToolTipText("Bulleted List");
		enumButton.setIcon(Icons.stock_list_enum.getImageIcon());
		enumButton.setToolTipText("Numbered List");
		enumButton.setText("");
		bulletButton.setText("");

		Vector<String> fontVector = new Vector<>();
		fontVector.add("8");
		fontVector.add("10");
		fontVector.add("12");
		fontVector.add("14");
		fontVector.add("16");
		fontVector.add("18");
		fontVector.add("24");
		fontVector.add("36");
		fontVector.add("48");

		DefaultComboBoxModel cbModel = new DefaultComboBoxModel(fontVector);
		sizeCB.setModel(cbModel);
		sizeCB.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		stdButton(boldButton);
		stdButton(italicButton);
		stdButton(underlineButton);
		stdButton(colorButton);

		stdButton(leftJustifyButton);
		stdButton(centerJustifyButton);
		stdButton(rightJustifyButton);

		stdButton(newButton);
		stdButton(saveButton);
		stdButton(deleteButton);

		stdButton(cutButton);
		stdButton(copyButton);
		stdButton(pasteButton);
	}

	private void initFileBar(List<File> files)
	{
		filePane.removeAll();
		filesBar.removeAll();

		if (!files.isEmpty())
		{
			filePane.add(fileLeft);
			filePane.add(fileRight);
			filePane.add(filesBar);

			for (File f : files)
			{
				filesBar.add(new JIcon(f, plugin));
			}
		}

		filePane.updateUI();
	}

	private void initLogging()
	{
		LogUtilities.inst().addReceiver(new NotesLogReciever());
	}

	//Initialization methods
	private void initTree()
	{
		dataDir.listFiles();
		root = new NotesTreeNode(dataDir.getName(), dataDir, notesTree);

		TreeModel model = new DefaultTreeModel(root);
		notesTree.setModel(model);
		notesTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent evt)
			{
				notesTreeActionPerformed();
			}
		});
		notesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		notesTree.setEditable(true);
		model.addTreeModelListener(new TreeModelListener()
		{
			@Override
			public void treeNodesChanged(TreeModelEvent e)
			{
				notesTreeNodesChanged();
			}

			@Override
			public void treeNodesInserted(TreeModelEvent e)
			{
				// TODO:  Method does nothing?
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e)
			{
				// TODO:  Method does nothing?
			}

			@Override
			public void treeStructureChanged(TreeModelEvent e)
			{
				// TODO:  Method does nothing?
			}
		});
	}

	//Image insertion methods

	/**
	 *  Method for inserting an image from a file
	 *
	 *@param  whatImage                 pointer to file
	 *@exception  IOException           if the file can't be read
	 *@exception  BadLocationException  if the file does not exist
	 *@exception  RuntimeException      cause
	 */
	private void insertLocalImage(File whatImage) throws IOException, BadLocationException, RuntimeException
	{
		File image = whatImage;
		if (whatImage == null)
		{
			File dir = getCurrentDir();
			File newImage = getImageFromChooser(dir.getPath(), extsIMG, "Image File");

			//null possible if user cancelled
			if (newImage != null && newImage.exists())
			{
				image = new File(dir.getAbsolutePath() + File.separator + newImage.getName());

				if (!image.exists())
				{
					FileUtils.copyFile(newImage, image);
				}
			}
		}

		if (image != null)
		{
			int caretPos = editor.getCaretPosition();
			ExtendedHTMLEditorKit htmlKit = (ExtendedHTMLEditorKit) editor.getEditorKit();
			ExtendedHTMLDocument htmlDoc = (ExtendedHTMLDocument) editor.getStyledDocument();
			htmlKit.insertHTML(htmlDoc, caretPos, "<IMG SRC=\"" + image + "\">", 0, 0, HTML.Tag.IMG);
			editor.setCaretPosition(caretPos + 1);
		}
	}

	//GEN-LAST:event_underlineButtonActionPerformed
	private void italicButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_italicButtonActionPerformed
		performTextPaneAction("font-italic", evt);
	}

	//GEN-LAST:event_centerJustifyButtonActionPerformed
	private void leftJustifyButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_leftJustifyButtonActionPerformed
		Action action = new StyledEditorKit.AlignmentAction("Left Justify", StyleConstants.ALIGN_LEFT);
		action.actionPerformed(evt);
		editor.grabFocus();

		int cp = editor.getCaretPosition();
		updateButtons(editor, cp);
	}

	private void manageListElement(ExtendedHTMLDocument htmlDoc)
	{
		Element h = ExtendedHTMLEditorKit.getListItemParent(htmlDoc.getCharacterElement(editor.getCaretPosition()));
		h.getParentElement();
		ExtendedHTMLEditorKit.removeTag(editor, h);
	}

	//GEN-LAST:event_saveButtonActionPerformed
	private void newButtonActionPerformed()
	{
		//GEN-FIRST:event_newButtonActionPerformed
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;
			node.createChild();
		}

		refreshTree();
	}

	//Tree Events
	private void notesTreeActionPerformed()
	{
		// Add your handling code here:
		refreshTreeNodes();
	}

	//GEN-LAST:event_boldButtonActionPerformed
	private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_pasteButtonActionPerformed
		performTextPaneAction(DefaultEditorKit.pasteAction, evt);
	}

	//Gui methods
	private void refreshTreeNodes()
	{
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;
			editor = node.getTextPane();
			root.checkCache();
			// TODO: Uh-oh -- never call gc manually without strong reason
			//			Runtime.getRuntime().gc();

			JViewport vp = new JViewport();
			vp.setView(editor);
			jScrollPane2.setViewport(vp);
			editAreaDT = new DropTarget(editor, new DropEditorListener());
			editor.addCaretListener(this::editorCaretUpdate);
			editor.addKeyListener(new java.awt.event.KeyListener()
			{
				@Override
				public void keyTyped(KeyEvent e)
				{
					editorKeyTyped(e);
				}

				@Override
				public void keyPressed(KeyEvent e)
				{
					// TODO:  Method does nothing?
				}

				@Override
				public void keyReleased(KeyEvent e)
				{
					// TODO:  Method does nothing?
				}
			});

			editor.getStyledDocument().addUndoableEditListener(this::editorUndoableEditHappened);

			if (node.isLeaf())
			{
				deleteButton.setEnabled(true);
			}
			else
			{
				deleteButton.setEnabled(false);
			}

			if (node == root)
			{
				exportButton.setEnabled(false);
			}
			else
			{
				exportButton.setEnabled(true);
			}

			if (node.isDirty())
			{
				revertButton.setEnabled(true);
			}
			else
			{
				revertButton.setEnabled(false);
			}

			initFileBar(node.getFiles());
			saveButton.setEnabled(true);
			newButton.setEnabled(true);
		}
		else if (obj == null)
		{
			deleteButton.setEnabled(false);
			saveButton.setEnabled(false);
			revertButton.setEnabled(false);
			newButton.setEnabled(false);
		}
	}

	//GEN-LAST:event_exportButtonActionPerformed
	private void revertButtonActionPerformed()
	{
		//GEN-FIRST:event_revertButtonActionPerformed
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;
			node.revert();
			refreshTreeNodes();
			notesTree.updateUI();
		}
	}

	//GEN-LAST:event_sizeCBActionPerformed
	private void rightJustifyButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_rightJustifyButtonActionPerformed
		Action action = new StyledEditorKit.AlignmentAction("Right Justify", StyleConstants.ALIGN_RIGHT);
		action.actionPerformed(evt);
		editor.grabFocus();

		int cp = editor.getCaretPosition();
		updateButtons(editor, cp);
	}

	//GEN-LAST:event_deleteButtonActionPerformed
	private void saveButtonActionPerformed()
	{
		//GEN-FIRST:event_saveButtonActionPerformed
		Object obj = notesTree.getLastSelectedPathComponent();

		if (obj instanceof NotesTreeNode)
		{
			NotesTreeNode node = (NotesTreeNode) obj;
			node.save();
		}

		revertButton.setEnabled(false);
		notesTree.updateUI();
	}

	//GEN-LAST:event_fileLeftActionPerformed
	private void sizeCBActionPerformed(final ActionEvent evt)
	{
		//GEN-FIRST:event_sizeCBActionPerformed
		if (sizeCB.hasFocus())
		{
			String fontS = (String) sizeCB.getSelectedItem();
			performTextPaneAction("font-size-" + fontS, evt);
		}
	}

	//GEN-LAST:event_colorButtonActionPerformed
	private void underlineButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//GEN-FIRST:event_underlineButtonActionPerformed
		performTextPaneAction("font-underline", evt);
	}

	/**
	 *  This is an abstract drop listener. Extend this to listen for drop events
	 *  for a particular Component
	 */
	public abstract class DropListener extends DropTargetAdapter
	{
		/**
		 *  Checks to see if dragEnter is supported for the actions on this event
		 *  Accepts only javaFileListFlavor data flavors
		 *
		 *@param  dtde  DropTargetDragEvent
		 */
		@Override
		public void dragEnter(DropTargetDragEvent dtde)
		{
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrag(dtde.getDropAction());
			}
			else
			{
				dtde.rejectDrag();
			}
		}

		/**
		 *  Accepts a drag over if the data flavor is javaFileListFlavor, otherwise
		 *  rejects it.
		 *
		 *@param  dtde  DropTargetDragEvent
		 */
		@Override
		public void dragOver(DropTargetDragEvent dtde)
		{
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrag(dtde.getDropAction());
			}
			else
			{
				dtde.rejectDrag();
			}
		}

		/**
		 *  implements a drop. you need to implements this in your class.
		 *
		 *@param  dtde  DropTargetDropEvent
		 */
		@Override
		public abstract void drop(DropTargetDropEvent dtde);

	}

	/**
	 *  Drop listener for the File bar on the bottom of the Notes screen
	 */
	public class DropBarListener extends DropListener
	{
		/**
		 *  implements drop.if we accept it, pass the event to the currently selected
		 *  node
		 *
		 *@param  dtde  DropTargetDropEvent
		 */
		@Override
		public void drop(DropTargetDropEvent dtde)
		{
			Object obj = notesTree.getLastSelectedPathComponent();

			if (obj instanceof NotesTreeNode)
			{
				NotesTreeNode node = (NotesTreeNode) obj;

				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.dropComplete(node.handleDropJavaFileList(dtde));
					refreshTreeNodes();
				}
				else
				{
					dtde.rejectDrop();
				}
			}
			else
			{
				dtde.rejectDrop();
			}
		}
	}

	/**
	 *  Drop listener for the Editor pane on the notes screen
	 */
	public class DropEditorListener extends DropListener
	{
		/**
		 *  Determines if a file passed in is an image or not (based on extension
		 *
		 *@param  image  File to check
		 *@return        true if image, false if not
		 */
		boolean isImageFile(File image)
		{
			for (String anExtsIMG : extsIMG)
			{
				if (image.getName().endsWith(anExtsIMG))
				{
					return true;
				}
			}

			return false;
		}

		/**
		 *  implements drop. if we accept it, pass the event to the handler
		 *
		 *@param  dtde  Description of the Parameter
		 */
		@Override
		public void drop(DropTargetDropEvent dtde)
		{
			Object obj = notesTree.getLastSelectedPathComponent();

			if (obj instanceof NotesTreeNode)
			{
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.dropComplete(handleDropJavaFileListAsImage(dtde));
					refreshTreeNodes();
				}
				else
				{
					dtde.rejectDrop();
				}
			}
			else
			{
				dtde.rejectDrop();
			}
		}

		/**
		 *  handles a drop. if the drop is an image, it will insert the image to the
		 *  proper place in the editor window.
		 *
		 *@param  dtde  DropTargetDropEvent
		 *@return       drop successful or not
		 */
		boolean handleDropJavaFileListAsImage(DropTargetDropEvent dtde)
		{
			dtde.acceptDrop(dtde.getDropAction());

			Transferable t = dtde.getTransferable();

			try
			{
				List<File> fileList = ((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
				File dir = getCurrentDir();

				for (File newFile : fileList)
				{
					if (newFile.exists())
					{
						File destFile = new File(dir.getAbsolutePath() + File.separator + newFile.getName());

						if (!isImageFile(destFile) || !destFile.exists())
						{
							FileUtils.copyFile(newFile, destFile);
						}

						editor.setCaretPosition(editor.viewToModel(dtde.getLocation()));
						handleImageDropInsertion(destFile);
					}
				}
			}
			catch (Exception e)
			{
				Logging.errorPrint(e.getMessage(), e);

				return false;
			}

			return true;
		}

		/**
		 *  Inserts a dropped image into the editor pane
		 *
		 *@param  image  File to insert
		 */
		void handleImageDropInsertion(File image)
		{
			for (String s : extsIMG)
			{
				if (image.getName().endsWith(s))
				{
					try
					{
						insertLocalImage(image);
					}
					catch (Exception e)
					{
						Logging.errorPrint(e.getMessage(), e);
					}

					break;
				}
			}
		}
	}

	/**
	 *  Drop listener for the Tree
	 */
	public class DropTreeListener extends DropListener
	{
		/**
		 *  implements drop.if we accept it, pass the event to the currently selected
		 *  node
		 *
		 *@param  dtde  Description of the Parameter
		 */
		@Override
		public void drop(DropTargetDropEvent dtde)
		{
			Point p = dtde.getLocation();
			TreePath path = notesTree.getPathForLocation(p.x, p.y);

			if (path == null)
			{
				dtde.rejectDrop();

				return;
			}

			Object obj = path.getLastPathComponent();

			if (obj instanceof NotesTreeNode)
			{
				NotesTreeNode node = (NotesTreeNode) obj;

				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.dropComplete(node.handleDropJavaFileList(dtde));
					refreshTreeNodes();
				}
				else
				{
					dtde.rejectDrop();
				}
			}
			else
			{
				dtde.rejectDrop();
			}
		}
	}

	public class NotesLogReciever implements LogReceiver
	{
		NotesTreeNode log;

		NotesLogReciever()
		{
			// Empty Constructor
		}

		/**
		 * Logs a message associated with a specific owner.
		 *
		 * @param owner the owner of the message being logged.
		 * @param message the message to log.
		 */
		@Override
		public void logMessage(String owner, String message)
		{
			if (log == null)
			{
				log = getChildNode("Logs", root);
			}

			NotesTreeNode node = getChildNode(owner, log);

			// TODO add option
			DateFormat dateFmt =
					//					new SimpleDateFormat("MM-dd-yyyy hh.mm.ss a z");
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
			node.appendText("<br>" + Constants.LINE_SEPARATOR + "<b>" + dateFmt.format(Calendar.getInstance().getTime())
				+ "</b> " + message);
		}

		private NotesTreeNode getChildNode(String name, NotesTreeNode parentNode)
		{
			Enumeration<MutableTreeNode> newNodes = parentNode.children();

			while (newNodes.hasMoreElements())
			{
				NotesTreeNode node = (NotesTreeNode) newNodes.nextElement();

				if (node.getUserObject().equals(NotesTreeNode.checkName(name)))
				{
					return node;
				}
			}

			return parentNode.createChild(name);
		}
	}

	/**
	 *  Action implementing Redo for editor
	 */
	protected class RedoAction extends AbstractAction
	{
		/**  Constructor for the RedoAction object */
		RedoAction()
		{
			super(getLocalizedRedo());
			setEnabled(false);
		}

		/**
		 *  Redo Action is preformed, run undo on the undo manager
		 *
		 *@param  e  Action Event
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undo.redo();
			}
			catch (CannotRedoException ex)
			{
				Logging.errorPrint("Unable to redo: " + ex);
			}

			updateRedoState();
			undoAction.updateUndoState();
		}

		/**  Update the current state of the redo labe */
		void updateRedoState()
		{
			if (undo.canRedo())
			{
				setEnabled(true);
				putValue(Action.NAME, undo.getRedoPresentationName());
			}
			else
			{
				setEnabled(false);
				putValue(Action.NAME, getLocalizedRedo());
			}
		}
	}

	private static String getLocalizedRedo()
	{
		return LanguageBundle.getString("in_mnuEditRedo"); //$NON-NLS-1$
	}

	//Internal Classes

	/**
	 *  Action implementing Undo for editor
	 */
	protected class UndoAction extends AbstractAction
	{
		/**  Constructor for the UndoAction object */
		UndoAction()
		{
			super(getLocalizedUndo());
			setEnabled(false);
		}

		/**
		 *  Undo Action is preformed, run undo on the undo manager.
		 *
		 *@param  e  Action Event
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undo.undo();
			}
			catch (CannotUndoException ex)
			{
				Logging.errorPrint("Unable to undo: " + ex.getMessage(), ex);
			}

			updateUndoState();
			redoAction.updateRedoState();
		}

		/**  Update the current state of the undo label */
		void updateUndoState()
		{
			if (undo.canUndo())
			{
				setEnabled(true);
				putValue(Action.NAME, undo.getUndoPresentationName());
			}
			else
			{
				setEnabled(false);
				putValue(Action.NAME, getLocalizedUndo());
			}
		}
	}

	private static String getLocalizedUndo()
	{
		return LanguageBundle.getString("in_mnuEditUndo"); //$NON-NLS-1$
	}
}
