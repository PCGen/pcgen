/*
 * PCGenTaskExecutor.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Apr 10, 2010, 5:21:21 PM
 */
package pcgen.system;

import java.util.LinkedList;
import org.apache.commons.lang.math.Fraction;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenTaskExecutor extends PCGenTask implements PCGenTaskListener
{

	private LinkedList<PCGenTask> tasks = new LinkedList<PCGenTask>();
	private PCGenTask currentTask = null;
	private Fraction progressMultiplier = null;
	private Fraction baseProgress = Fraction.ZERO;

	public void addPCGenTask(PCGenTask task)
	{
		tasks.add(task);
	}

	public void execute()
	{
		progressMultiplier = Fraction.getFraction(1, tasks.size());
		while (!tasks.isEmpty())
		{
			currentTask = tasks.poll();
			setValues(currentTask.getMessage(), baseProgress.getNumerator(), baseProgress.getDenominator());
			currentTask.addPCGenTaskListener(this);
			currentTask.execute();
			currentTask.removePCGenTaskListener(this);
			baseProgress = baseProgress.add(progressMultiplier);
		}
	}

	public void progressChanged(PCGenTaskEvent event)
	{
		if (currentTask.getMaximum() == 0)
		{
			return;
		}
		Fraction progress = Fraction.getFraction(currentTask.getProgress(), currentTask.getMaximum());
		progress = progress.multiplyBy(progressMultiplier);
		progress = baseProgress.add(progress);
		setValues(currentTask.getMessage(), progress.getNumerator(), progress.getDenominator());
	}

	public void errorOccurred(PCGenTaskEvent event)
	{
		sendErrorMessage(event.getErrorRecord());
	}

}
