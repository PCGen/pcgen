package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.npcgen.EquipmentTable;
import pcgen.core.npcgen.TreasureGenerator;

public class TreasureGeneratorDlg extends JDialog
{
	private JButton theGenerateButton = new JButton();
	private JComboBox theTableCombo = new JComboBox();
	private JButton theAddButton = new JButton();
	private JList theResultsList = new JList();

	public TreasureGeneratorDlg( final Frame anOwner )
	{
		super( anOwner, "Treasure Generator", false );
		createControls();

		pack();
		setLocationRelativeTo( anOwner );

		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				populateControls();
			}
		});
	}

	private void createControls()
	{
		this.getContentPane().setLayout(new BorderLayout());
		
		final JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.orange);
		topPanel.setLayout( new GridLayout(2,1) );

			final JPanel instructionPanel = new JPanel();
			
				final JLabel instructionLabel = new JLabel( "Select a table and click generate" );
				instructionPanel.add( instructionLabel );
				
			topPanel.add(instructionPanel);
			
			final JPanel actionPanel = new JPanel();
			
				final JLabel comboLabel = new JLabel( "Table:" );
				actionPanel.add( comboLabel );
				
				theTableCombo.setPreferredSize( new Dimension(200, 19) );
				actionPanel.add( theTableCombo );
				
				theGenerateButton.setText("Generate");
				theGenerateButton.addActionListener( new ActionListener()
				{
					public void actionPerformed( final ActionEvent anEvent )
					{
						generateTreasure();
					}
				});
				actionPanel.add( theGenerateButton );
				
			topPanel.add( actionPanel );
			
		this.getContentPane().add(topPanel, BorderLayout.NORTH);

		final JPanel centerPanel = new JPanel();
			
			final DefaultListModel model = new DefaultListModel();
			theResultsList.setModel(model);
			theResultsList.updateUI();
			theResultsList.setVisibleRowCount(8);

			JScrollPane scrollPane = new JScrollPane(theResultsList);
			scrollPane.setPreferredSize( new Dimension(300, 80) );
			scrollPane.setAlignmentX(LEFT_ALIGNMENT);
			
			centerPanel.add( scrollPane );
			
		this.getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

		final JPanel bottomPanel = new JPanel();
		
			theAddButton.setText("Add to PC");
			theAddButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(final ActionEvent evt)
				{
					final PCGen_Frame1 mainFrame = PCGen_Frame1.getInst();
					PlayerCharacter pc = null;
					if (mainFrame != null)
					{
						pc = mainFrame.getCurrentPC();
					}
					if ( pc != null )
					{
						for ( final Object obj : theResultsList.getSelectedValues() )
						{
							final Equipment eq = (Equipment)obj;
							
							pc.addEquipment(eq);
							Globals.getContext().ref.importObject(eq);
						}
					}
				}
			});
			bottomPanel.add( theAddButton );

			final JButton closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					setVisible(false);
					dispose();
				}
			});
			bottomPanel.add( closeButton );
			
		this.getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);
	}
	
	private void populateControls()
	{
		final TreasureGenerator generator = TreasureGenerator.getInstance();
		
		final List<EquipmentTable> treasureTables = generator.getTables( SettingsHandler.getGame() );
		
		for ( final EquipmentTable table : treasureTables )
		{
			theTableCombo.addItem( table );
		}
		
		setVisible( true );
	}
	
	private void generateTreasure()
	{
		final EquipmentTable table = (EquipmentTable)theTableCombo.getSelectedItem();
		final List<Equipment> equipList = table.getEquipment();
		final DefaultListModel model = (DefaultListModel)theResultsList.getModel();
		for ( final Equipment eq : equipList )
		{
			// add to listbox
			model.addElement(eq);
		}
		theResultsList.setSelectionInterval( 0, equipList.size() - 1 );
		theResultsList.updateUI();
	}
}

