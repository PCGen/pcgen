package pcgen.gui2.tabs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.tabs.CharacterInfoTab.ModelMap;

import org.junit.jupiter.api.Test;

/** Verifies that deferred tab restoration cannot outlive its character selection. */
class TabModelServiceTest
{

	@Test
	void closingBeforeDeferredRestorationDetachesOnlyInstalledTabs() throws Exception
	{
		TabModelService service = new TabModelService();
		RecordingTab first = new RecordingTab();
		RecordingTab deferred = new RecordingTab();
		ModelMap firstModels = new ModelMap();
		Map<CharacterInfoTab, ModelMap> states = new HashMap<>(Map.of(first, firstModels, deferred, new ModelMap()));
		SwingUtilities.invokeAndWait(() -> {
			service.restoreTab(first, firstModels);
			service.restoreModels(states, first);
			service.storeModels(states);
			states.clear();
		});
		drainEvents();
		assertEquals(List.of(firstModels), first.restored);
		assertEquals(List.of(firstModels), first.stored);
		assertTrue(deferred.restored.isEmpty());
		assertTrue(deferred.stored.isEmpty());
	}

	@Test
	void switchingCharactersInvalidatesOldCallbacksButRestoresNewModels() throws Exception
	{
		TabModelService service = new TabModelService();
		RecordingTab first = new RecordingTab();
		RecordingTab deferred = new RecordingTab();
		Map<CharacterInfoTab, ModelMap> oldStates = Map.of(first, new ModelMap(), deferred, new ModelMap());
		Map<CharacterInfoTab, ModelMap> newStates = Map.of(first, new ModelMap(), deferred, new ModelMap());
		SwingUtilities.invokeAndWait(() -> {
			service.restoreTab(first, oldStates.get(first));
			service.restoreModels(oldStates, first);
			service.storeModels(oldStates);
			service.restoreTab(first, newStates.get(first));
			service.restoreModels(newStates, first);
		});
		drainEvents();
		SwingUtilities.invokeAndWait(() -> service.storeModels(newStates));
		assertEquals(List.of(newStates.get(deferred)), deferred.restored);
		assertEquals(List.of(newStates.get(deferred)), deferred.stored);
		assertSame(oldStates.get(first), first.stored.get(0));
		assertSame(newStates.get(first), first.stored.get(1));
	}

	@Test
	void aFailedTabIsReportedWithoutPreventingOtherTabsFromRestoring() throws Exception
	{
		TabModelService service = new TabModelService();
		RecordingTab first = new RecordingTab();
		RecordingTab failing = new RecordingTab();
		RecordingTab last = new RecordingTab();
		RuntimeException failure = new IllegalStateException("Failed tab");
		failing.failure = failure;
		Map<CharacterInfoTab, ModelMap> states = Map.of(
			first, new ModelMap(), failing, new ModelMap(), last, new ModelMap());
		List<Throwable> reported = new ArrayList<>();
		Thread.UncaughtExceptionHandler[] previous = new Thread.UncaughtExceptionHandler[1];
		SwingUtilities.invokeAndWait(() -> {
			previous[0] = Thread.currentThread().getUncaughtExceptionHandler();
			Thread.currentThread().setUncaughtExceptionHandler((thread, error) -> reported.add(error));
			// Measure the last tab so the unmeasured, failing tab is restored ahead of it.
			service.restoreTab(last, states.get(last));
			service.storeModels(states);
			last.restored.clear();
			service.restoreTab(first, states.get(first));
			service.restoreModels(states, first);
		});
		try
		{
			drainEvents();
			assertEquals(List.of(failure), reported);
			assertEquals(List.of(states.get(last)), last.restored);
		}
		finally
		{
			SwingUtilities.invokeAndWait(() -> {
				service.storeModels(states);
				Thread.currentThread().setUncaughtExceptionHandler(previous[0]);
			});
		}
	}

	@Test
	void cancellationBetweenTabsStopsTheRestOfTheBatch() throws Exception
	{
		TabModelService service = new TabModelService();
		RecordingTab first = new RecordingTab();
		RecordingTab second = new RecordingTab();
		RecordingTab third = new RecordingTab();
		Map<CharacterInfoTab, ModelMap> states = Map.of(
			first, new ModelMap(), second, new ModelMap(), third, new ModelMap());
		SwingUtilities.invokeAndWait(() -> {
			service.restoreTab(first, states.get(first));
			service.restoreModels(states, first);
			SwingUtilities.invokeLater(() -> service.storeModels(states));
		});
		drainEvents();
		assertEquals(1, second.restored.size() + third.restored.size());
		assertEquals(1, second.stored.size() + third.stored.size());
	}

	/** Drains both the first callback and the continuation it can enqueue. */
	private static void drainEvents() throws Exception
	{
		for (int i = 0; i < 3; i++)
		{
			SwingUtilities.invokeAndWait(() -> { });
		}
	}

	/** Records the model identity and verifies all model operations stay on Swing's thread. */
	private static final class RecordingTab implements CharacterInfoTab
	{
		private final List<ModelMap> restored = new ArrayList<>();
		private final List<ModelMap> stored = new ArrayList<>();
		private RuntimeException failure;

		@Override
		public ModelMap createModels(CharacterFacade character)
		{
			return new ModelMap();
		}

		@Override
		public void restoreModels(ModelMap models)
		{
			assertTrue(SwingUtilities.isEventDispatchThread());
			if (failure != null)
			{
				throw failure;
			}
			restored.add(models);
		}

		@Override
		public void storeModels(ModelMap models)
		{
			assertTrue(SwingUtilities.isEventDispatchThread());
			stored.add(models);
		}

		@Override
		public TabTitle getTabTitle()
		{
			throw new UnsupportedOperationException();
		}
	}
}
