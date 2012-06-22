/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.layer.tms.tile;

import org.geomajas.layer.tile.TileCode;
import org.geomajas.layer.tms.xml.TileMap;
import org.geomajas.layer.tms.xml.TileSet;

/**
 * Tile URL builder that builds URLs based upon a {@link TileMap} configuration object. This class uses the
 * tile-level to get the correct {@link TileSet} object, and assumes that these tile-sets are ordered.
 * 
 * @author Pieter De Graef
 */
public class TileMapUrlBuilder implements TileUrlBuilder {

	private final TileMap tileMap;

	private final String baseTmsUrl;

	public TileMapUrlBuilder(TileMap tileMap, String baseTmsUrl) {
		if (tileMap == null || baseTmsUrl == null) {
			throw new IllegalStateException("No null values allowed.");
		}
		this.tileMap = tileMap;
		this.baseTmsUrl = baseTmsUrl;
	}

	/** {@inheritDoc} */
	public String buildUrl(TileCode tileCode) {
		StringBuilder builder;

		// assuming they are ordered:
		TileSet tileSet = tileMap.getTileSets().getTileSets().get(tileCode.getTileLevel());
		String href = tileSet.getHref();
		if (href.startsWith("http://") || href.startsWith("https://")) {
			builder = new StringBuilder(href);
			if (!href.endsWith("/")) {
				builder.append("/");
			}
		} else {
			builder = new StringBuilder(baseTmsUrl);
			if (!baseTmsUrl.endsWith("/")) {
				builder.append("/");
			}
			builder.append(href);
			builder.append("/");
		}

		builder.append(tileCode.getX());
		builder.append("/");
		builder.append(tileCode.getY());
		builder.append(".");
		builder.append(tileMap.getTileFormat().getExtension());
		return builder.toString();
	}
}