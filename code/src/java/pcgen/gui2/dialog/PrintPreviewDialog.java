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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import pcgen.cdom.base.Constants;
import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.system.BatchExporter;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;
import pcgen.util.fop.FopTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.render.awt.AWTRenderer;
import org.apache.fop.render.awt.viewer.PreviewPanel;
import org.jetbrains.annotations.NotNull;

/**
 * Dialog to allow the preview of character export.
 */
@SuppressWarnings("serial")
public final class PrintPreviewDialog extends JDialog implements ActionListener
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
    private final CharacterFacade character;
    private final JComboBox<Object> sheetBox;
    private final JComboBox<String> pageBox;
    private final JComboBox<Double> zoomBox;
    private final JButton zoomInButton;
    private final JButton zoomOutButton;
    private final JButton printButton;
    private final JButton cancelButton;
    private final JPanel previewPanelParent;
    private PreviewPanel previewPanel;
    private final JProgressBar progressBar;
    private final PCGenFrame frame;
    private Pageable pageable;

    private PrintPreviewDialog(PCGenFrame frame)
    {
        super(frame, true);
        this.frame = frame;
        this.character = frame.getSelectedCharacterRef().get();
        this.previewPanelParent = new JPanel(new GridLayout(1, 1));
        this.sheetBox = new JComboBox<>();
        this.progressBar = new JProgressBar();
        this.pageBox = new JComboBox<>();
        this.zoomBox = new JComboBox<>();
        this.zoomInButton = new JButton();
        this.zoomOutButton = new JButton();
        this.printButton = new JButton();
        this.cancelButton = new JButton();
        initComponents();
        initLayout();
        pack();
        new SheetLoader().execute();
    }

    private <E> void initComponents()
    {
        setTitle("Print Preview");
        sheetBox.setRenderer(new DefaultListCellRenderer()
        {

            @Override
            public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus)
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
        zoomBox.addItem(0.25);
        zoomBox.addItem(0.50);

        zoomBox.addItem(0.75);
        zoomBox.addItem(1.00);
        zoomBox.setSelectedItem(0.75);
        zoomBox.setRenderer(new DefaultListCellRenderer()
        {

            @Override
            public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus)
            {
                NumberFormat format = NumberFormat.getPercentInstance();
                String formattedValue = format.format(value);
                return super.getListCellRendererComponent(list, formattedValue, index, isSelected, cellHasFocus);
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

    private void setPreviewPanel(PreviewPanel previewPanel)
    {
        previewPanelParent.removeAll();
        this.previewPanel = previewPanel;
        previewPanelParent.add(previewPanel);
        previewPanel.reload();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (SHEET_COMMAND.equals(e.getActionCommand()))
        {
            new PreviewLoader((URI) sheetBox.getSelectedItem()).execute();
        } else if (PAGE_COMMAND.equals(e.getActionCommand()))
        {
            previewPanel.setPage(pageBox.getSelectedIndex());
        } else if (ZOOM_COMMAND.equals(e.getActionCommand()))
        {
            Double zoom = (Double) zoomBox.getSelectedItem();
            previewPanel.setScaleFactor(zoom);
        } else if (ZOOM_IN_COMMAND.equals(e.getActionCommand()))
        {
            Double zoom = (Double) zoomBox.getSelectedItem();
            zoomBox.setSelectedItem(zoom * ZOOM_MULTIPLIER);
        } else if (ZOOM_OUT_COMMAND.equals(e.getActionCommand()))
        {
            Double zoom = (Double) zoomBox.getSelectedItem();
            zoomBox.setSelectedItem(zoom / ZOOM_MULTIPLIER);
        } else if (PRINT_COMMAND.equals(e.getActionCommand()))
        {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPageable(pageable);
            if (printerJob.printDialog())
            {
                try
                {
                    printerJob.print();
                    dispose();
                } catch (PrinterException ex)
                {
                    String message = "Could not print " + character.getNameRef().get();
                    Logging.errorPrint(message, ex);
                    frame.showErrorMessage(Constants.APPLICATION_NAME, message);
                }
            }
        } else if (CANCEL_COMMAND.equals(e.getActionCommand()))
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
            previewPanelParent.setPreferredSize(new Dimension(600, 800));
            vbox.add(previewPanelParent);
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

    /**
     * A JFormattedTextField that edits percentages.
     */
    private static class PercentEditor extends JFormattedTextField implements ComboBoxEditor, PropertyChangeListener
    {

        public PercentEditor(JComboBox<Double> comboBox)
        {
            super(NumberFormat.getPercentInstance());
            addPropertyChangeListener("value", this);
            //We steal the border from the LAF's editor
            JComponent oldEditor = (JComponent) comboBox.getEditor().getEditorComponent();
            setBorder(oldEditor.getBorder());
        }

        @Override
        public Component getEditorComponent()
        {
            return this;
        }

        @Override
        public void setItem(Object anObject)
        {
            setValue(anObject);
        }

        @Override
        public Object getItem()
        {
            return getValue();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            fireActionPerformed();
        }

    }

    private class PreviewLoader extends SwingWorker<AWTRenderer, Object>
    {

        private final URI uri;

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

            FOUserAgent userAgent = FopTask.getFactory().newFOUserAgent();
            AWTRenderer renderer = new AWTRenderer(userAgent, null, false, false);

            try (PipedOutputStream out = new PipedOutputStream())
            {
                FopTask task = FopTask.newFopTask(new PipedInputStream(out), xsltFile, renderer);
                Thread thread = new Thread(task, "fop-preview");

                thread.setDaemon(true);
                thread.start();
                BatchExporter.exportCharacter(character, out);
                try
                {
                    thread.join();
                } catch (InterruptedException ex)
                {
                    //pass on the interrupt and hope it stops
                    thread.interrupt();
                }
            }
            return renderer;
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
                pageable = renderer;
                setPreviewPanel(new PreviewPanel(renderer.getUserAgent(), null, renderer));
                pageBox.setModel(createPagesModel(renderer.getNumberOfPages()));
            } catch (InterruptedException | ExecutionException ex)
            {
                Logging.errorPrint("Could not load sheet", ex);
            }
        }

    }

    private static ComboBoxModel<String> createPagesModel(int pages)
    {
        String[] pageNumbers = new String[pages];
        for (int i = 0; i < pages; i++)
        {
            pageNumbers[i] = (i + 1) + " of " + pages;
        }
        return new DefaultComboBoxModel<>(pageNumbers);
    }

    private class SheetLoader extends SwingWorker<Object[], Object> implements FilenameFilter
    {

        @Override
        public boolean accept(@NotNull File dir, @NotNull String name)
        {
            return dir.getName().equalsIgnoreCase("pdf");
        }

        @Override
        protected Object[] doInBackground()
        {
            IOFileFilter pdfFilter = FileFilterUtils.asFileFilter(this);
            IOFileFilter suffixFilter = FileFilterUtils.notFileFilter(new SuffixFileFilter(".fo"));
            IOFileFilter sheetFilter = FileFilterUtils.prefixFileFilter(Constants.CHARACTER_TEMPLATE_PREFIX);
            IOFileFilter fileFilter = FileFilterUtils.and(pdfFilter, suffixFilter, sheetFilter);

            IOFileFilter dirFilter = TrueFileFilter.INSTANCE;
            File dir = new File(ConfigurationSettings.getOutputSheetsDir());
            Collection<File> files = FileUtils.listFiles(dir, fileFilter, dirFilter);
            URI osPath = new File(ConfigurationSettings.getOutputSheetsDir()).toURI();
            return files.stream().map(v -> osPath.relativize(v.toURI())).toArray();
        }

        @Override
        protected void done()
        {
            try
            {
                ComboBoxModel<Object> model = new DefaultComboBoxModel<>(get());
                model.setSelectedItem(null);
                sheetBox.setModel(model);
            } catch (InterruptedException | ExecutionException ex)
            {
                Logging.errorPrint("could not load sheets", ex);
            }
        }

    }

}
