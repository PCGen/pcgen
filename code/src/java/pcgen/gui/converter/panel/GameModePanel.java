package pcgen.gui.converter.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstSystemLoader;
import pcgen.util.PropertyFactory;

public class GameModePanel extends ConvertSubPanel
{

	JComboBoxEx gameModeCombo;

	private final LstSystemLoader loader;

	public GameModePanel(LstSystemLoader sl)
	{
		loader = sl;
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		loader.loadPCCFilesInDirectory(pc.get(ObjectKey.DIRECTORY));
		try
		{
			loader.initRecursivePccFiles();
		}
		catch (PersistenceLayerException e)
		{
			e.printStackTrace();
			return false;
		}
		Globals.sortPObjectListByName(Globals.getCampaignList());

		Globals.createEmptyRace();

		SettingsHandler.initGameModes();
		return saveGameMode(pc);
	}

	private boolean saveGameMode(CDOMObject pc)
	{
		boolean advance = pc.get(ObjectKey.GAME_MODE) != null;
		if (advance)
		{
			fireProgressEvent(ProgressEvent.ALLOWED);
		}
		return advance;
	}

	private void getSelection(CDOMObject pc)
	{
		String gameModeKey = (String) gameModeCombo.getSelectedItem();
		if (gameModeKey != null)
		{
			GameMode gamemode = SystemCollections.getGameModeNamed(gameModeKey);
			pc.put(ObjectKey.GAME_MODE, gamemode);
		}
	}

	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		JLabel introLabel = new JLabel(PropertyFactory
				.getString("in_qsrc_gameModeIntro"));
		panel.add(introLabel);

		List<GameMode> games = SystemCollections.getUnmodifiableGameModeList();
		String gameModeNames[] = new String[games.size()];
		for (int i = 0; i < gameModeNames.length; i++)
		{
			gameModeNames[i] = games.get(i).getName();
		}
		gameModeCombo = new JComboBoxEx(gameModeNames);
		gameModeCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				getSelection(pc);
				saveGameMode(pc);
			}
		});
		gameModeCombo.setSelectedItem(SettingsHandler.getGame().getName());

		panel.add(gameModeCombo);
	}
}
