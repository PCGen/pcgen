/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import pcgen.gui3.sources.SourceSelectionDialogPane.ActiveTabEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

/**
 * The dialog routes Load and the source-memory commit by which tab is in
 * front, so it has to know which that is at all times -- including before the
 * user has touched a tab.
 *
 * <p>This used to be read out of the selection-changed event with
 * {@code (Tab) event.getTarget()}. {@code Event.getTarget()} is typed only as
 * {@code EventTarget}, which {@code Node}, {@code Window}, {@code Scene},
 * {@code MenuItem} and {@code TreeItem} all implement, so the cast rested
 * entirely on an FXML attribute staying attached to a {@code Tab}. The
 * selection model answers the same question without a cast, and answers it
 * once per change rather than twice -- the event fired for the tab being left
 * as well as the one being entered.
 */
@ExtendWith(ApplicationExtension.class)
class ActiveTabTrackingTest
{
	private final List<ActiveTabEnum> reported = new ArrayList<>();

	private TabPane tabs;
	private Tab basic;
	private Tab advanced;

	/** Controls may only be built on the FX thread. */
	@BeforeEach
	void buildTabs() throws Exception
	{
		Callable<TabPane> build = () -> {
			basic = new Tab("Basic");
			advanced = new Tab("Advanced");
			return new TabPane(basic, advanced);
		};
		tabs = WaitForAsyncUtils.asyncFx(build).get(5, TimeUnit.SECONDS);
	}

	private void track() throws Exception
	{
		WaitForAsyncUtils.asyncFx(
				() -> SourceSelectionDialogPane.trackActiveTab(tabs, basic, reported::add))
				.get(5, TimeUnit.SECONDS);
	}

	private void select(Tab tab) throws Exception
	{
		WaitForAsyncUtils.asyncFx(() -> tabs.getSelectionModel().select(tab)).get(5, TimeUnit.SECONDS);
	}

	/**
	 * The dialog must know where it stands before anything happens. Waiting for
	 * the first switch is what left the old code with two different defaults
	 * for one question.
	 */
	@Test
	void tracking_reportsTheTabAlreadyInFront() throws Exception
	{
		track();

		assertEquals(List.of(ActiveTabEnum.BASIC), reported);
	}

	@Test
	void selectingTheAdvancedTab_isReported() throws Exception
	{
		track();

		select(advanced);

		assertEquals(ActiveTabEnum.ADVANCED, reported.getLast());
	}

	@Test
	void returningToTheBasicTab_isReported() throws Exception
	{
		track();
		select(advanced);

		select(basic);

		assertEquals(ActiveTabEnum.BASIC, reported.getLast());
	}

	/**
	 * The selection-changed event fired on both the tab being left and the one
	 * being entered, so the old handler ran twice per switch and filtered with
	 * {@code isSelected()}.
	 */
	@Test
	void eachSwitch_isReportedOnce() throws Exception
	{
		track();
		reported.clear();

		select(advanced);

		assertEquals(1, reported.size(), reported.toString());
	}

	@Test
	void reselectingTheSameTab_reportsNothingNew() throws Exception
	{
		track();
		select(advanced);
		reported.clear();

		select(advanced);

		assertEquals(List.of(), reported, "re-selecting is not a change");
	}

	@Test
	void everySwitch_isReportedInOrder() throws Exception
	{
		track();

		select(advanced);
		select(basic);
		select(advanced);

		assertEquals(
				List.of(ActiveTabEnum.BASIC, ActiveTabEnum.ADVANCED, ActiveTabEnum.BASIC,
						ActiveTabEnum.ADVANCED),
				reported);
	}
}
