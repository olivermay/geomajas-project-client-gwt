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

package org.geomajas.puregwt.client.map.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geomajas.geometry.Bbox;
import org.geomajas.geometry.Coordinate;
import org.geomajas.puregwt.client.gfx.HtmlContainer;
import org.geomajas.puregwt.client.gfx.HtmlGroup;
import org.geomajas.puregwt.client.map.ViewPort;
import org.geomajas.puregwt.client.map.layer.Layer;
import org.geomajas.puregwt.client.map.layer.RasterLayer;
import org.geomajas.puregwt.client.map.layer.VectorLayer;

/**
 * <p>
 * MapScalesRenderer implementation that keeps a fixed number of scales in cache. This strategy trades memory for speed.
 * The more scale levels are kept in memory, the less tiles need to be fetched.
 * </p>
 * <p>
 * This implementation keeps the scales for a single layer, not the entire map.
 * </p>
 * 
 * @author Pieter De Graef
 */
public class MapScalesRendererImpl implements MapScalesRenderer {

	private static final int SCALE_CACHE_SIZE = 3; // Let's keep the last 3 scales.

	private final ViewPort viewPort;

	private final Layer<?> layer;

	private final HtmlContainer htmlContainer;

	private final Map<Double, TiledScaleRenderer> scalePresenters;

	private final List<Double> scales;

	private double visibleScale;

	//private double targetScale;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	/**
	 * Create a new instance with the following parameters.
	 * 
	 * @param viewPort
	 *            The viewPort onto which this renderer applies.
	 * @param layer
	 *            The layer to be rendered.
	 * @param htmlContainer
	 *            The container wherein to render all scales.
	 */
	public MapScalesRendererImpl(ViewPort viewPort, Layer<?> layer, HtmlContainer htmlContainer) {
		this.viewPort = viewPort;
		this.layer = layer;
		this.htmlContainer = htmlContainer;
		scalePresenters = new HashMap<Double, TiledScaleRenderer>();
		scales = new ArrayList<Double>(SCALE_CACHE_SIZE + 1);

		visibleScale = viewPort.getScale();
		//targetScale = viewPort.getScale();
	}

	// ------------------------------------------------------------------------
	// MapScalesRenderer implementation:
	// ------------------------------------------------------------------------

	/** {@inheritDoc} */
	public HtmlContainer getHtmlContainer() {
		return htmlContainer;
	}

	/** {@inheritDoc} */
	public void ensureScale(double scale, Bbox bounds) {
		// Get or create the presenter, then turn it invisible and fetch the tiles.
		if (scale > 0) {
			TiledScaleRenderer presenter = getOrCreate(scale);
			presenter.getHtmlContainer().setVisible(false);
			presenter.render(bounds);
			//targetScale = scale;

			// Rearrange the scales:
			if (scales.contains(scale)) {
				scales.remove(scale);
			}
			scales.add(scale);

			// If we have too many scales, remove the last one to be used:
			if (scales.size() > SCALE_CACHE_SIZE) {
				removeScaleLevel(scales.get(0));
			}
		}
	}

	/** {@inheritDoc} */
	public void setScaleVisibility(double scale, boolean visible) {
		TiledScaleRenderer scalePresenter = scalePresenters.get(scale);
		if (scalePresenter != null) {
			if (visible) {
				visibleScale = scale;
				scalePresenter.getHtmlContainer().zoomToLocation(1, 0, 0);
				scalePresenter.getHtmlContainer().setVisible(true);
			} else {
				scalePresenter.getHtmlContainer().setVisible(false);
			}
		}
	}

	/** {@inheritDoc} */
	public void applyScaleTranslation(double scale, Coordinate translation) {
		TiledScaleRenderer scalePresenter = scalePresenters.get(scale);
		if (scalePresenter != null) {
			scalePresenter.getHtmlContainer().setLeft((int) Math.round(translation.getX()));
			scalePresenter.getHtmlContainer().setTop((int) Math.round(translation.getY()));
		}
	}

	/** {@inheritDoc} */
	public void cancel() {
		// removeScaleLevel(targetScale);
	}

	/** {@inheritDoc} */
	public TiledScaleRenderer getVisibleScale() {
		return scalePresenters.get(visibleScale);
	}

	// ------------------------------------------------------------------------
	// Private methods:
	// ------------------------------------------------------------------------

	private TiledScaleRenderer getOrCreate(double scale) {
		if (scalePresenters.containsKey(scale)) {
			return scalePresenters.get(scale);
		}

		final HtmlContainer scaleContainer = new HtmlGroup();
		scaleContainer.setVisible(false);
		htmlContainer.insert(scaleContainer, 0);

		TiledScaleRenderer scalePresenter = null;
		if (layer instanceof RasterLayer) {
			scalePresenter = new RasterLayerScalePresenter(viewPort.getCrs(), (RasterLayer) layer, scaleContainer,
					scale) {

				public void onTilesReceived(HtmlContainer container, double scale) {
				}

				public void onTilesRendered(HtmlContainer container, double scale) {
				}
			};
		} else {
			scalePresenter = new VectorLayerScalePresenter(viewPort, (VectorLayer) layer, scaleContainer, scale) {

				public void onTilesReceived(HtmlContainer container, double scale) {
				}

				public void onTilesRendered(HtmlContainer container, double scale) {
				}
			};
		}
		scalePresenters.put(scale, scalePresenter);
		return scalePresenter;
	}

	private boolean removeScaleLevel(Double scale) {
		// Remove the presenter:
		TiledScaleRenderer removedPresenter = scalePresenters.get(scale);
		if (removedPresenter == null) {
			return false;
		}
		removedPresenter.cancel();
		// TODO let the presenter have it's own destroy() method??
		htmlContainer.remove(removedPresenter.getHtmlContainer());
		scalePresenters.remove(scale);

		// Remove the scale:
		return scales.remove(scale);
	}
}