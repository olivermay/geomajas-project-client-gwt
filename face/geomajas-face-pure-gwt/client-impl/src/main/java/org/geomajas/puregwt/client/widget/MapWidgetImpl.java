/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.puregwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.geomajas.puregwt.client.map.MapPresenterImpl.MapWidget;
import org.geomajas.puregwt.client.map.gfx.HtmlContainer;
import org.geomajas.puregwt.client.map.gfx.HtmlGroup;
import org.geomajas.puregwt.client.map.gfx.VectorContainer;
import org.geomajas.puregwt.client.map.gfx.VectorGroup;
import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Group;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * <p>
 * Implementation of the MapWidget interface as described by the
 * {@link org.geomajas.puregwt.client.map.MapPresenterImpl}. It represents the MVP 'view' of the map's presenter (aka
 * MapPresenter).
 * </p>
 * <p>
 * This widget is able to render all required objects that the MapPresenter supports, and does this in the following
 * order:
 * <ol>
 * <li>Raster layers & rasterized vector layers.</li>
 * <li>Vector layers (SVG/VML)</li>
 * <li>All screen and world containers</li>
 * <li>All map gadgets</li>
 * </ol>
 * </p>
 * 
 * @author Pieter De Graef
 */
public final class MapWidgetImpl extends AbsolutePanel implements MapWidget {

	// Container for raster layers or rasterized layers:
	private HtmlGroup layerHtmlContainer;

	// Container for vector layers (SVG/VML):
	private VectorGroup layerVectorContainer;

	// Container for user-defined world containers:
	private VectorGroup worldVectorContainer;

	// Container for user-defined screen containers:
	private VectorGroup screenVectorContainer;

	// Container for map gadgets:
	private VectorGroup gadgetVectorContainer;

	// Parent container for all SVG/VML:
	private DrawingArea drawingArea;

	// List of all screen containers and world containers:
	private List<VectorContainer> screenContainers = new ArrayList<VectorContainer>();

	// List of all screen containers and world containers:
	private List<VectorContainer> worldContainers = new ArrayList<VectorContainer>();
	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	@Inject
	private MapWidgetImpl() {
		super();

		// Attach an HtmlContainer inside the clipping area (used for rendering layers):
		layerHtmlContainer = new HtmlGroup();
		add(layerHtmlContainer, 0, 0);

		// Attach a DrawingArea inside the clipping area (used for vector rendering):
		drawingArea = new DrawingArea(100, 100);
		add(drawingArea, 0, 0);

		// First child within the vector drawing area is a group for the map to render it's non-HTML layers:
		layerVectorContainer = new VectorGroup();
		drawingArea.add(layerVectorContainer);

		// Second child within the vector drawing area is a group for user-defined world containers:
		worldVectorContainer = new VectorGroup();
		drawingArea.add(worldVectorContainer);

		// Second child within the vector drawing area is a group for user-defined screen containers:
		screenVectorContainer = new VectorGroup();
		drawingArea.add(screenVectorContainer);

		// Third child within the vector drawing area is a group for map gadget containers:
		gadgetVectorContainer = new VectorGroup();
		drawingArea.add(gadgetVectorContainer);

		// Firefox and Chrome allow for DnD of images. This default behavior is not wanted.
		addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
			}
		});
		addMouseMoveHandler(new MouseMoveHandler() {

			public void onMouseMove(MouseMoveEvent event) {
				event.preventDefault();
			}
		});

		// We don't want scrolling on the page and zooming at the same time.
		// TODO: make this optional. When no zoom on scroll is used, scrolling the page should be possible.
		addMouseWheelHandler(new MouseWheelHandler() {

			public void onMouseWheel(MouseWheelEvent event) {
				event.preventDefault();
			}
		});
	}

	public Widget asWidget() {
		return this;
	}

	public HtmlContainer getMapHtmlContainer() {
		return layerHtmlContainer;
	}

	public VectorContainer getMapVectorContainer() {
		return layerVectorContainer;
	}

	public VectorContainer getWorldVectorContainer() {
		return worldVectorContainer;
	}

	public VectorContainer getNewScreenContainer() {
		VectorGroup container = new VectorGroup();
		screenVectorContainer.add(container);
		screenContainers.add(container);
		return container;
	}

	public VectorContainer getNewWorldContainer() {
		VectorGroup container = new VectorGroup();
		worldVectorContainer.add(container);
		worldContainers.add(container);
		return container;
	}

	public boolean removeVectorContainer(VectorContainer container) {
		if (container instanceof Group) {
			if (worldContainers.contains(container)) {
				worldVectorContainer.remove((Group) container);
				worldContainers.remove(container);
				return true;
			} else if (screenContainers.contains(container)) {
				screenVectorContainer.remove((Group) container);
				screenContainers.remove(container);
				return true;
			}
		}
		return false;
	}

	public boolean bringToFront(VectorContainer container) {
		if (container instanceof Group) {
			if (worldContainers.contains(container)) {
				worldVectorContainer.bringToFront((Group) container);
				return true;
			} else if (screenContainers.contains(container)) {
				screenVectorContainer.bringToFront((Group) container);
				return true;
			}
		}
		return false;
	}

	public VectorContainer getMapGadgetContainer() {
		VectorGroup container = new VectorGroup();
		gadgetVectorContainer.add(container);
		return container;
	}

	public boolean removeMapGadgetContainer(VectorContainer mapGadgetContainer) {
		if (mapGadgetContainer instanceof Group) {
			gadgetVectorContainer.remove((Group) mapGadgetContainer);
			return true;
		}
		return false;
	}

	public void onResize() {
		for (Widget child : getChildren()) {
			if (child instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}
	}

	// ------------------------------------------------------------------------
	// Overriding resize methods:
	// ------------------------------------------------------------------------

	public void setPixelSize(int width, int height) {
		layerHtmlContainer.setPixelSize(width, height);
		drawingArea.setWidth(width);
		drawingArea.setHeight(height);
		super.setPixelSize(width, height);
	}

	public void setSize(String width, String height) {
		layerHtmlContainer.setSize(width, height);
		drawingArea.setWidth(width);
		drawingArea.setHeight(height);
		super.setSize(width, height);
	}

	public void setWidth(String width) {
		layerHtmlContainer.setWidth(width);
		drawingArea.setWidth(width);
		super.setWidth(width);
	}

	public void setHeight(String height) {
		layerHtmlContainer.setHeight(height);
		drawingArea.setHeight(height);
		super.setHeight(height);
	}

	// ------------------------------------------------------------------------
	// Add mouse event catch methods:
	// ------------------------------------------------------------------------

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return addDomHandler(handler, DoubleClickEvent.getType());
	}
}