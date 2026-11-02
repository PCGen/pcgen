package pcgen.gui2.tabs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;

import org.junit.jupiter.api.Test;

/** Exercises queued skill-selection updates across character model detachment. */
class SkillLevelSelectionTest
{
	/** Rejects the queued selection update after its character has been detached and closed. */
	@Test
	void queuedSelectionDoesNotReadAClosedCharacter() throws Exception
	{
		verifySelection(true);
	}

	/** Still selects the level with unspent points when the character remains installed. */
	@Test
	void installedSelectionFindsTheLevelWithUnspentPoints() throws Exception
	{
		verifySelection(false);
	}

	/** Discards an old installation's callback while allowing the current installation's update to run. */
	@Test
	void queuedSelectionFromPreviousInstallationIsIgnored() throws Exception
	{
		LevelsFixture fixture = new LevelsFixture();
		DefaultListSelectionModel selection = new DefaultListSelectionModel();
		SkillInfoTab.LevelSelectionHandler[] handler = new SkillInfoTab.LevelSelectionHandler[1];
		int[] selectionBeforeCurrentUpdate = {-1};
		SwingUtilities.invokeAndWait(() -> {
			selection.setSelectionInterval(0, 0);
			handler[0] = new SkillInfoTab.LevelSelectionHandler(fixture.levels, selection,
				new JTable(2, 1), () -> fixture.refreshes++);
			handler[0].install();
			handler[0].uninstall();
			// Observe the selection between the obsolete callback and the new installation's callback.
			SwingUtilities.invokeLater(() -> selectionBeforeCurrentUpdate[0] = selection.getMinSelectionIndex());
			handler[0].install();
		});
		try
		{
			SwingUtilities.invokeAndWait(() -> {
				assertEquals(0, selectionBeforeCurrentUpdate[0], "The obsolete callback must not change selection");
				assertEquals(1, selection.getMinSelectionIndex(), "The current callback must still select unspent points");
				assertEquals(1, fixture.refreshes);
			});
		}
		finally
		{
			SwingUtilities.invokeAndWait(() -> handler[0].uninstall());
		}
	}

	/** Queues selection on the EDT, optionally closes the character, then waits for that callback. */
	private void verifySelection(boolean closeBeforeCallback) throws Exception
	{
		LevelsFixture fixture = new LevelsFixture();
		DefaultListSelectionModel selection = new DefaultListSelectionModel();
		SkillInfoTab.LevelSelectionHandler[] handler = new SkillInfoTab.LevelSelectionHandler[1];
		SwingUtilities.invokeAndWait(() -> {
			selection.setSelectionInterval(0, 0);
			handler[0] = new SkillInfoTab.LevelSelectionHandler(fixture.levels, selection,
				new JTable(2, 1), () -> fixture.refreshes++);
			handler[0].install();
			if (closeBeforeCallback)
			{
				handler[0].uninstall();
				fixture.closed = true;
			}
		});
		try
		{
			SwingUtilities.invokeAndWait(() -> { });
			assertEquals(0, fixture.readsAfterClose);
			assertEquals(closeBeforeCallback ? 0 : 1, selection.getMinSelectionIndex());
			assertEquals(closeBeforeCallback ? 0 : 1, fixture.refreshes);
		}
		finally
		{
			if (!closeBeforeCallback)
			{
				SwingUtilities.invokeAndWait(() -> handler[0].uninstall());
			}
		}
	}

	/** The first level has spent its points; the second has points left. */
	private static final class LevelsFixture
	{
		private final CharacterLevelFacade first = new CharacterLevelFacade() { };
		private final CharacterLevelFacade second = new CharacterLevelFacade() { };
		private boolean closed;
		private int readsAfterClose;
		private int refreshes;
		private final CharacterLevelsFacade levels = (CharacterLevelsFacade) Proxy.newProxyInstance(
			CharacterLevelsFacade.class.getClassLoader(), new Class<?>[]{CharacterLevelsFacade.class},
			(proxy, method, args) -> {
				if (closed && method.getName().startsWith("get"))
				{
					readsAfterClose++;
				}
				return switch (method.getName())
				{
					case "isEmpty" -> false;
					case "getSize" -> 2;
					case "getElementAt" -> (int) args[0] == 0 ? first : second;
					case "getSpentSkillPoints" -> args[0] == first ? 1 : 0;
					case "getGainedSkillPoints" -> 1;
					case "addSkillPointListener", "removeSkillPointListener", "addListListener",
						"removeListListener" -> null;
					default -> throw new UnsupportedOperationException(method.getName());
				};
			});
	}
}
