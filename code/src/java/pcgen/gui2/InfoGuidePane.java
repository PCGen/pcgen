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
package pcgen.gui2;

import java.awt.BorderLayout;
import java.util.Objects;
import java.util.stream.StreamSupport;

import javax.swing.JComponent;
import javax.swing.plaf.UIResource;

import pcgen.core.Campaign;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui3.InfoGuideController;
import pcgen.gui3.JFXPanelFromResource;

/**
 * A first-time-user guide shown on the main frame when no character is loaded:
 * the loaded game mode and sources, what to do next, and a rotating "Did you
 * know?" tip. Renders as a native JavaFX panel ({@link InfoGuideController})
 * embedded in Swing. Implements {@link UIResource} so it can be a child of the
 * character {@code JTabbedPane} without becoming a tab.
 */
class InfoGuidePane extends JComponent implements UIResource
{
	private final UIContext uiContext;
	private final PCGenFrame frame;
	private final JFXPanelFromResource<InfoGuideController> panel;

	InfoGuidePane(PCGenFrame frame, UIContext uiContext)
	{
		this.uiContext = Objects.requireNonNull(uiContext);
		this.frame = frame;
		this.panel = new JFXPanelFromResource<>(InfoGuideController.class, "InfoGuidePanel.fxml");
		TipOfTheDayHandler.getInstance().loadTips();
		initComponents();
		initListeners();
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		refreshDisplayedSources(null);
		refreshTip();
	}

	private void initListeners()
	{
		frame.getSelectedCharacterRef().addReferenceListener(e -> {
			boolean noCharacter = e.getNewReference() == null;
			setVisible(noCharacter);
			if (noCharacter)
			{
				refreshTip();
			}
		});
		uiContext.getCurrentSourceSelectionRef()
			.addReferenceListener(e -> refreshDisplayedSources(e.getNewReference()));
	}

	private void refreshTip()
	{
		panel.getController().setTip(TipOfTheDayHandler.getInstance().getNextTip());
	}

	private void refreshDisplayedSources(SourceSelectionFacade sources)
	{
		InfoGuideController controller = panel.getController();
		if (sources == null)
		{
			controller.setGameMode(null);
			controller.setNoSources();
			return;
		}
		controller.setGameMode(sources.getGameMode().get().getDisplayName());
		if (sources.getCampaigns().isEmpty())
		{
			controller.setNoSources();
		}
		else
		{
			controller.setSources(
					StreamSupport.stream(sources.getCampaigns().spliterator(), false)
							.map(Campaign::getKeyName).toList());
		}
	}
}
