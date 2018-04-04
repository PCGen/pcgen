/*
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
 */
package pcgen.gui2.filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SizeRequirements;
import javax.swing.UIManager;
import javax.swing.border.Border;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class represents the highest level DisplayableFilter in the filter hierarchy. A FilterBar
 * is a filter which contains a set of other DisplayableFilters. At the bottom of a FilterBar is a
 * region of space with an arrow at the center. When this is clicked all of the children filters will
 * be hidden from view.
 */
public class FilterBar<C, E> extends JPanel implements DisplayableFilter<C, E>
{

	private final JPanel filterPanel = new JPanel(new FilterLayout());
	private final List<DisplayableFilter<? super C, ? super E>> filters = new ArrayList<>();
	private FilterHandler filterHandler;

	public FilterBar()
	{
		this(true);
	}

	public FilterBar(boolean collapsable)
	{
		setLayout(new BorderLayout());
		add(filterPanel, BorderLayout.CENTER);

		final ArrowButton arrowbutton = new ArrowButton();
		arrowbutton.addMouseListener(
				new MouseAdapter()
				{

					@Override
					public void mousePressed(MouseEvent e)
					{
						boolean closed = !arrowbutton.isOpen();
						arrowbutton.setOpen(closed);
						filterPanel.setVisible(closed);
					}

				});
		if (collapsable)
		{
			add(arrowbutton, BorderLayout.SOUTH);
		}
	}

	public void addDisplayableFilter(DisplayableFilter<? super C, ? super E> filter)
	{
		filterPanel.add(filter.getFilterComponent());
		filters.add(filter);
		filter.setFilterHandler(filterHandler);
	}

	public void removeDisplayableFilter(DisplayableFilter<C, E> filter)
	{
		filterPanel.remove(filter.getFilterComponent());
		filters.remove(filter);
		filter.setFilterHandler(null);
	}

	@Override
	public Component getFilterComponent()
	{
		return this;
	}

	@Override
	public void setFilterHandler(FilterHandler handler)
	{
		this.filterHandler = handler;
		for (DisplayableFilter<? super C, ? super E> displayableFilter : filters)
		{
			displayableFilter.setFilterHandler(handler);
		}
	}

	@Override
	public boolean accept(C context, E element)
	{
		for (DisplayableFilter<? super C, ? super E> displayableFilter : filters)
		{
			if (!displayableFilter.accept(context, element))
			{
				return false;
			}
		}
		return true;
	}

	private static class ArrowButton extends JButton
	{

		private boolean entered = false;
		private boolean open = true;

		public ArrowButton()
		{
			setMinimumSize(new Dimension(6, 6));
			setPreferredSize(new Dimension(6, 6));
			setFocusPainted(false);
			setBorderPainted(false);
			setRequestFocusEnabled(false);
			addMouseListener(
					new MouseAdapter()
					{

						@Override
						public void mouseEntered(MouseEvent e)
						{
							entered = true;
							repaint();
						}

						@Override
						public void mouseExited(MouseEvent e)
						{
							entered = false;
							repaint();
						}

					});
		}

		@Override
		public void setBorder(Border border)
		{
		}

		private static final int[] yup =
		{
			1,
			4,
			4
		};
		private static final int[] ydown =
		{
			4,
			1,
			1
		};

		@Override
		public void paint(Graphics g)
		{
			Color b;
			Color f;
			if (entered)
			{
				b = UIManager.getColor("controlDkShadow");
				f = Color.BLACK;
			}
			else
			{
				b = UIManager.getColor("control");
				f = UIManager.getColor("controlDkShadow");
			}
			g.setColor(b);
			g.fillRect(0, 0, getWidth(), getHeight());
			int center = getWidth() / 2;
			int[] xs =
			{
				center,
				center - 3,
				center + 3
			};
			int[] ys;
			if (open)
			{
				ys = yup;

			}
			else
			{
				ys = ydown;
			}
			g.setColor(f);
			g.drawPolygon(xs, ys, 3);
			g.fillPolygon(xs, ys, 3);
		}

		public void setOpen(boolean open)
		{
			this.open = open;
		}

		public boolean isOpen()
		{
			return open;
		}

	}

	/*
	 * this layout functions like left to right FlowLayout with the exception
	 * that it treats the Container's width as absolute and will change the
	 * height of the container to fit container's children.
	 */
	private static class FilterLayout extends FlowLayout
	{

		public FilterLayout()
		{
			super(FlowLayout.LEFT, 5, 2);
		}

		@Override
		public void layoutContainer(Container target)
		{
			synchronized (target.getTreeLock())
			{
				Insets insets = target.getInsets();
				int maxwidth = target.getWidth() - (insets.left + insets.right + getHgap() * 2);
				int nmembers = target.getComponentCount();
				int x = 0, y = insets.top + getVgap();
				int rowh = 0, start = 0;

				boolean ltr = target.getComponentOrientation().isLeftToRight();
				SizeRequirements[] xChildren = new SizeRequirements[nmembers];
				SizeRequirements[] yChildren = new SizeRequirements[nmembers];
				for (int i = 0; i < nmembers; i++)
				{
					Component c = target.getComponent(i);
					if (!c.isVisible())
					{
						xChildren[i] = new SizeRequirements(0, 0, 0, c.getAlignmentX());
						yChildren[i] = new SizeRequirements(0, 0, 0, c.getAlignmentY());
					}
					else
					{
						Dimension min = c.getMinimumSize();
						Dimension typ = c.getPreferredSize();
						Dimension max = c.getMaximumSize();
						xChildren[i] = new SizeRequirements(min.width, typ.width,
															max.width,
															c.getAlignmentX());
						yChildren[i] = new SizeRequirements(min.height, typ.height,
															max.height,
															c.getAlignmentY());

						if ((x == 0) || ((x + typ.width) <= maxwidth))
						{
							if (x > 0)
							{
								x += getHgap();
							}
							x += typ.width;
							rowh = Math.max(rowh, typ.height);
						}
						else
						{
							layoutComponents(target, insets.left + getHgap(), y,
											 maxwidth, rowh, xChildren, yChildren, start, i, ltr);

							x = typ.width;
							y += getVgap() + rowh;
							rowh = typ.height;
							start = i;
						}
					}
				}
				layoutComponents(target, insets.left + getHgap(), y,
								 maxwidth, rowh, xChildren, yChildren, start, nmembers, ltr);
			}
		}

		private void layoutComponents(Container target, int xOffset, int yOffset, int maxwidth, int rowheight,
									  SizeRequirements[] xChildren, SizeRequirements[] yChildren,
									  int start, int end, boolean ltr)
		{
			SizeRequirements[] children = ArrayUtils.subarray(xChildren, start, end);
			int[] xOffsets = new int[children.length];
			int[] xSpans = new int[children.length];
			SizeRequirements.calculateTiledPositions(maxwidth, null, children, xOffsets, xSpans, ltr);

			children = ArrayUtils.subarray(yChildren, start, end);
			int[] yOffsets = new int[children.length];
			int[] ySpans = new int[children.length];
			SizeRequirements total = new SizeRequirements(rowheight, rowheight, rowheight, 0.5f);
			SizeRequirements.calculateAlignedPositions(rowheight, total, children, yOffsets, ySpans, ltr);

			for (int i = 0; i < children.length; i++)
			{
				Component c = target.getComponent(i + start);
				c.setBounds((int) Math.min((long) xOffset + (long) xOffsets[i], Integer.MAX_VALUE),
							(int) Math.min((long) yOffset + (long) yOffsets[i], Integer.MAX_VALUE),
							xSpans[i], ySpans[i]);
			}
		}

		@Override
		public Dimension preferredLayoutSize(Container target)
		{
			synchronized (target.getTreeLock())
			{
				Dimension dim = new Dimension(0, 0);
				int nmembers = target.getComponentCount();

				Insets insets = target.getInsets();
				// Provide a default if the panel has not been displayed yet (i.e. in a dialog)
				int targetWidth = target.getWidth() == 0? 400 : target.getWidth(); 
				int maxwidth = targetWidth - (insets.left + insets.right +
						getHgap() * 2);
				int width = 0;
				int height = 0;
				int component = 0;
				for (int i = 0; i < nmembers; i++, component++)
				{
					Component m = target.getComponent(i);
					if (m.isVisible())
					{
						Dimension d = m.getPreferredSize();
						if (component > 0)
						{
							if (width + d.width > maxwidth)
							{
								dim.width = Math.max(dim.width, width);
								dim.height += height + getVgap();
								width = 0;
								height = 0;
								component = 0;
							}
							width += getHgap();
						}
						height = Math.max(height, d.height);
						width += d.width;
					}
				}
				dim.width = Math.max(dim.width, width);
				dim.height += height;

				dim.width += insets.left + insets.right + getHgap() * 2;
				dim.height += insets.top + insets.bottom + getVgap() * 2;
				return dim;
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container target)
		{
			synchronized (target.getTreeLock())
			{
				Dimension dim = new Dimension(0, 0);
				int nmembers = target.getComponentCount();

				Insets insets = target.getInsets();
				int maxwidth = target.getWidth() - (insets.left + insets.right +
						getHgap() * 2);
				int width = 0;
				int height = 0;
				int component = 0;
				for (int i = 0; i < nmembers; i++, component++)
				{
					Component m = target.getComponent(i);
					if (m.isVisible())
					{
						Dimension d = m.getMinimumSize();
						if (component > 0)
						{
							if (width + d.width > maxwidth)
							{
								dim.width = Math.max(dim.width, width);
								dim.height += height + getVgap();
								width = 0;
								height = 0;
								component = 0;
							}
							width += getHgap();
						}
						height = Math.max(height, d.height);
						width += d.width;
					}
				}
				dim.width = Math.max(dim.width, width);
				dim.height += height;

				dim.width += insets.left + insets.right + getHgap() * 2;
				dim.height += insets.top + insets.bottom + getVgap() * 2;
				return dim;
			}
		}

	}

}


