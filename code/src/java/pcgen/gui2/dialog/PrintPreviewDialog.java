/*
 * PrintPreviewDialog.java
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
 * Created on Nov 7, 2011, 9:17:28 PM
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.fop.render.awt.AWTRenderer;
import pcgen.cdom.base.Constants;
import pcgen.core.facade.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.io.ExportHandler;
import pcgen.system.ConfigurationSettings;
import pcgen.util.FOPHandler;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PrintPreviewDialog extends JDialog implements ActionListener
{

	public static void showPrintPreviewDialog(PCGenFrame frame)
	{
		JDialog dialog = new PrintPreviewDialog(frame);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private static final String SHEET_COMMAND = "sheet";
	private static final String PAGE_COMMAND = "page";
	private static final String ZOOM_COMMAND = "zoom";
	private static final String ZOOM_IN_COMMAND = "zoomin";
	private static final String ZOOM_OUT_COMMAND = "zoomout";
	private static final String PRINT_COMMAND = "print";
	private static final String CANCEL_COMMAND = "cancel";
	private static final double ZOOM_MULTIPLIER = Math.pow(2, 0.125);
	private final FOPHandler handler;
	private final CharacterFacade character;
	private final JComboBox sheetBox;
	private final JComboBox pageBox;
	private final JComboBox zoomBox;
	private final JButton zoomInButton;
	private final JButton zoomOutButton;
	private final JButton printButton;
	private final JButton cancelButton;
	private final SheetPreview sheetPreview;
	private final JProgressBar progressBar;
	private final PCGenFrame frame;

	private PrintPreviewDialog(PCGenFrame frame)
	{
		super(frame, true);
		this.frame = frame;
		this.character = frame.getSelectedCharacterRef().getReference();
		this.handler = new FOPHandler();
		this.sheetPreview = new SheetPreview();
		this.sheetBox = new JComboBox();
		this.progressBar = new JProgressBar();
		this.pageBox = new JComboBox();
		this.zoomBox = new JComboBox();
		this.zoomInButton = new JButton();
		this.zoomOutButton = new JButton();
		this.printButton = new JButton();
		this.cancelButton = new JButton();
		initComponents();
		initLayout();
		pack();
		new SheetLoader().execute();
	}

	private void initComponents()
	{
		setTitle("Print Preview");
		handler.setMode(FOPHandler.AWT_MODE);
		sheetBox.setRenderer(new DefaultListCellRenderer()
		{

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value != null)
				{
					setToolTipText(value.toString());
				}
				return this;
			}

		});
		sheetBox.setActionCommand(SHEET_COMMAND);
		sheetBox.addActionListener(this);
		pageBox.addItem("0 of 0");
		pageBox.setActionCommand(PAGE_COMMAND);
		pageBox.addActionListener(this);
		zoomBox.addItem(Double.valueOf(0.25));
		zoomBox.addItem(Double.valueOf(0.50));

		zoomBox.addItem(Double.valueOf(0.75));
		zoomBox.addItem(Double.valueOf(1.00));
		zoomBox.setSelectedItem(Double.valueOf(0.75));
		zoomBox.setRenderer(new DefaultListCellRenderer()
		{

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				NumberFormat format = NumberFormat.getPercentInstance();
				value = format.format((Double) value);
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}

		});
		zoomBox.setEditable(true);
		zoomBox.setEditor(new PercentEditor(zoomBox));
		zoomBox.setActionCommand(ZOOM_COMMAND);
		zoomBox.addActionListener(this);
		zoomInButton.setIcon(Icons.ZoomIn16.getImageIcon());
		zoomInButton.setActionCommand(ZOOM_IN_COMMAND);
		zoomInButton.addActionListener(this);
		zoomOutButton.setIcon(Icons.ZoomOut16.getImageIcon());
		zoomOutButton.setActionCommand(ZOOM_OUT_COMMAND);
		zoomOutButton.addActionListener(this);

		printButton.setText("Print");
		printButton.setActionCommand(PRINT_COMMAND);
		printButton.addActionListener(this);

		cancelButton.setText("Cancel");
		cancelButton.setActionCommand(CANCEL_COMMAND);
		cancelButton.addActionListener(this);

		enableEditGroup(false);
		
		Utility.installEscapeCloseOperation(this);
	}

	private void enableEditGroup(boolean enable)
	{
		pageBox.setEnabled(enable);
		zoomBox.setEnabled(enable);
		zoomInButton.setEnabled(enable);
		zoomOutButton.setEnabled(enable);
		printButton.setEnabled(enable);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (SHEET_COMMAND.equals(e.getActionCommand()))
		{
			new PreviewLoader((URI) sheetBox.getSelectedItem()).execute();
		}
		else if (PAGE_COMMAND.equals(e.getActionCommand()))
		{
			sheetPreview.setPageIndex(pageBox.getSelectedIndex());
		}
		else if (ZOOM_COMMAND.equals(e.getActionCommand()))
		{
			Double zoom = (Double) zoomBox.getSelectedItem();
			sheetPreview.setScalingFactor(zoom);
		}
		else if (ZOOM_IN_COMMAND.equals(e.getActionCommand()))
		{
			Double zoom = (Double) zoomBox.getSelectedItem();
			zoomBox.setSelectedItem(zoom * ZOOM_MULTIPLIER);
		}
		else if (ZOOM_OUT_COMMAND.equals(e.getActionCommand()))
		{
			Double zoom = (Double) zoomBox.getSelectedItem();
			zoomBox.setSelectedItem(zoom / ZOOM_MULTIPLIER);
		}
		else if (PRINT_COMMAND.equals(e.getActionCommand()))
		{
			AWTRenderer renderer = sheetPreview.getRenderer();
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			printerJob.setPrintable(renderer);
			printerJob.setPageable(renderer);
			if (printerJob.printDialog())
			{
				try
				{
					printerJob.print();
					dispose();
				}
				catch (PrinterException ex)
				{
					String message = "Could not print " + character.getNameRef().getReference();
					Logging.errorPrint(message, ex);
					frame.showErrorMessage(Constants.APPLICATION_NAME, message);
				}
			}
		}
		else if (CANCEL_COMMAND.equals(e.getActionCommand()))
		{
			dispose();
		}
	}

	private void initLayout()
	{
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		{//layout top bar
			JPanel bar = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.BASELINE;
			gbc.insets = new Insets(8, 6, 8, 2);
			bar.add(new JLabel("Select Template:"), gbc);
			gbc.insets = new Insets(8, 2, 8, 6);
			gbc.weightx = 1;
			bar.add(sheetBox, gbc);
			pane.add(bar, BorderLayout.NORTH);
		}
		{
			Box vbox = Box.createVerticalBox();
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
			panel.add(sheetPreview);
			vbox.add(new JScrollPane(panel));
			vbox.add(progressBar);
			pane.add(vbox, BorderLayout.CENTER);
		}
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(new JLabel("Page:"));
			hbox.add(Box.createHorizontalStrut(4));
			hbox.add(pageBox);
			hbox.add(Box.createHorizontalStrut(10));
			hbox.add(new JLabel("Zoom:"));
			hbox.add(Box.createHorizontalStrut(4));
			hbox.add(zoomBox);
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(zoomInButton);
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(zoomOutButton);
			hbox.add(Box.createHorizontalGlue());
			hbox.add(printButton);
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(cancelButton);
			hbox.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
			pane.add(hbox, BorderLayout.SOUTH);
		}
	}

	private static class PercentEditor extends JFormattedTextField implements ComboBoxEditor, PropertyChangeListener
	{

		public PercentEditor(JComboBox comboBox)
		{
			super(NumberFormat.getPercentInstance());
			addPropertyChangeListener("value", this);
			//We steal the border from the LAF's editor
			//Note: this doesn't work for Nimbus
			JComponent oldEditor = (JComponent) comboBox.getEditor().getEditorComponent();
			setBorder(oldEditor.getBorder());
		}

		public Component getEditorComponent()
		{
			return this;
		}

		public void setItem(Object anObject)
		{
			setValue(anObject);
		}

		public Object getItem()
		{
			return getValue();
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			fireActionPerformed();
		}

	}

	private static class SheetPreview extends JComponent
	{

		private static final PageFormat format = new PageFormat();
		private Image[] pageCache;
		private double scaleFactor = .75;
		private AWTRenderer renderer;
		private Image previewImage;
		private int currentPage;

		public SheetPreview()
		{
			setBorder(BorderFactory.createRaisedBevelBorder());
		}

		@Override
		public Dimension getPreferredSize()
		{
			int width = getPreviewWidth();
			int height = getPreviewHeight();
			Insets insets = getInsets();
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;
			return new Dimension(width, height);
		}

		@Override
		public Dimension getMaximumSize()
		{
			return getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			Insets insets = getInsets();
			g.drawImage(previewImage, insets.left, insets.top, this);
		}

		private int getPreviewWidth()
		{
			return (int) (format.getWidth() * scaleFactor);
		}

		private int getPreviewHeight()
		{
			return (int) (format.getHeight() * scaleFactor);
		}

		public void setScalingFactor(double scaleFactor)
		{
			this.scaleFactor = scaleFactor;
			resetPreviewImage();
			revalidate();
			repaint();
		}

		public AWTRenderer getRenderer()
		{
			return renderer;
		}

		public void setRenderer(AWTRenderer renderer)
		{
			this.renderer = renderer;
			if (renderer.getNumberOfPages() > 0)
			{
				pageCache = new Image[renderer.getNumberOfPages()];
			}
			else
			{
				pageCache = new Image[1];
				pageCache[0] = createBrokenPage();
			}
			setPageIndex(0);
		}

		public void setPageIndex(int page)
		{
			currentPage = page;
			resetPreviewImage();
			repaint();
		}

		private void resetPreviewImage()
		{
			Image pageImage = pageCache[currentPage];
			if (pageImage == null)
			{
				pageImage = createNewPage();
				Graphics g = pageImage.getGraphics();
				try
				{
					renderer.print(g, format, currentPage);
				}
				catch (PrinterException ex)
				{
					Logging.errorPrint(null, ex);
				}
				g.dispose();
				pageCache[currentPage] = pageImage;
			}
			previewImage = pageImage.getScaledInstance(getPreviewWidth(), getPreviewHeight(), Image.SCALE_SMOOTH);
		}

		private static BufferedImage createNewPage()
		{
			int pageWidth = (int) format.getWidth();
			int pageHeight = (int) format.getHeight();
			return new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
		}

		private static Image createBrokenPage()
		{
			BufferedImage image = createNewPage();
			Graphics g = image.getGraphics();
			ImageIcon icon = Icons.stock_broken_image.getImageIcon();
			Rectangle viewRect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
			Rectangle iconRect = new Rectangle();
			SwingUtilities.layoutCompoundLabel(null, null, icon,
											   SwingConstants.CENTER,
											   SwingConstants.CENTER,
											   SwingConstants.CENTER,
											   SwingConstants.CENTER,
											   viewRect,
											   iconRect,
											   new Rectangle(),
											   0);
			icon.paintIcon(null, g, iconRect.x, iconRect.y);
			g.dispose();
			return image;
		}

	}

	private class PreviewLoader extends SwingWorker<AWTRenderer, Object>
	{

		private URI uri;
		private File temp = null;

		public PreviewLoader(URI uri)
		{
			this.uri = uri;
			progressBar.setIndeterminate(true);
			sheetBox.setEnabled(false);
			enableEditGroup(false);
		}

		@Override
		protected AWTRenderer doInBackground() throws Exception
		{
			URI osPath = new File(ConfigurationSettings.getOutputSheetsDir()).toURI();
			File xsltFile = new File(osPath.resolve(uri));
			temp = File.createTempFile("currentPC_", ".xml");
			printToXMLFile(temp, character);
			handler.setInputFile(temp, xsltFile);
			handler.run();
			return (AWTRenderer) handler.getRenderer();
		}

		@Override
		protected void done()
		{
			progressBar.setIndeterminate(false);
			sheetBox.setEnabled(true);
			enableEditGroup(true);
			try
			{
				AWTRenderer renderer = get();
				sheetPreview.setRenderer(renderer);
				pageBox.setModel(createPagesModel(renderer.getNumberOfPages()));
			}
			catch (InterruptedException ex)
			{
				Logging.errorPrint("Could not load sheet", ex);
			}
			catch (ExecutionException ex)
			{
				Logging.errorPrint("Could not load sheet", ex.getCause());
			}
			finally
			{
				if (temp != null)
				{
					temp.delete();
				}
			}
		}

	}

	private static File getXMLTemplate(CharacterFacade character)
	{
		File template = FileUtils.getFile(ConfigurationSettings.getSystemsDir(),
										  "gameModes",
										  character.getDataSet().getGameMode().getName(),
										  "base.xml");
		if (!template.exists())
		{
			template = new File(ConfigurationSettings.getOutputSheetsDir(), "base.xml");
		}
		return template;
	}

	private static void printToXMLFile(File outFile, CharacterFacade character)
			throws IOException
	{
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));

		File template = getXMLTemplate(character);
		character.export(new ExportHandler(template), bw);
		bw.close();
	}

	private static ComboBoxModel createPagesModel(int pages)
	{
		String[] pageNumbers = new String[pages];
		for (int i = 0; i < pages; i++)
		{
			pageNumbers[i] = (i + 1) + " of " + pages;
		}
		return new DefaultComboBoxModel(pageNumbers);
	}

	private class SheetLoader extends SwingWorker<Object[], Object> implements FilenameFilter
	{

		public boolean accept(File dir, String name)
		{
			return dir.getName().equalsIgnoreCase("pdf");
		}

		@Override
		protected Object[] doInBackground() throws Exception
		{
			IOFileFilter pdfFilter = FileFilterUtils.asFileFilter(this);
			IOFileFilter suffixFilter = FileFilterUtils.notFileFilter(new SuffixFileFilter(".fo"));
			IOFileFilter sheetFilter = FileFilterUtils.prefixFileFilter(Constants.CHARACTER_TEMPLATE_PREFIX);
			IOFileFilter fileFilter = FileFilterUtils.and(pdfFilter, suffixFilter, sheetFilter);

			IOFileFilter dirFilter = FileFilterUtils.makeSVNAware(TrueFileFilter.INSTANCE);
			File dir = new File(ConfigurationSettings.getOutputSheetsDir());
			Collection<File> files = FileUtils.listFiles(dir, fileFilter, dirFilter);
			URI osPath = new File(ConfigurationSettings.getOutputSheetsDir()).toURI();
			Object[] uriList = new Object[files.size()];
			int i = 0;
			for (File file : files)
			{
				uriList[i] = osPath.relativize(file.toURI());
				i++;
			}
			return uriList;
		}

		@Override
		protected void done()
		{
			try
			{
				DefaultComboBoxModel model = new DefaultComboBoxModel(get());
				model.setSelectedItem(null);
				sheetBox.setModel(model);
			}
			catch (InterruptedException ex)
			{
				Logging.errorPrint("could not load sheets", ex);
			}
			catch (ExecutionException ex)
			{
				Logging.errorPrint("could not load sheets", ex.getCause());
			}
		}

	}

}
