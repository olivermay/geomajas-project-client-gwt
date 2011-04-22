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

package org.geomajas.gwt.client.map.feature;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geomajas.configuration.AssociationAttributeInfo;
import org.geomajas.configuration.AttributeInfo;
import org.geomajas.configuration.PrimitiveAttributeInfo;
import org.geomajas.geometry.Coordinate;
import org.geomajas.global.Api;
import org.geomajas.gwt.client.gfx.Paintable;
import org.geomajas.gwt.client.gfx.PainterVisitor;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.gwt.client.spatial.Bbox;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.geomajas.gwt.client.util.GeometryConverter;
import org.geomajas.layer.feature.Attribute;
import org.geomajas.layer.feature.attribute.AssociationAttribute;
import org.geomajas.layer.feature.attribute.AssociationValue;
import org.geomajas.layer.feature.attribute.BooleanAttribute;
import org.geomajas.layer.feature.attribute.CurrencyAttribute;
import org.geomajas.layer.feature.attribute.DateAttribute;
import org.geomajas.layer.feature.attribute.DoubleAttribute;
import org.geomajas.layer.feature.attribute.FloatAttribute;
import org.geomajas.layer.feature.attribute.ImageUrlAttribute;
import org.geomajas.layer.feature.attribute.IntegerAttribute;
import org.geomajas.layer.feature.attribute.LongAttribute;
import org.geomajas.layer.feature.attribute.ManyToOneAttribute;
import org.geomajas.layer.feature.attribute.OneToManyAttribute;
import org.geomajas.layer.feature.attribute.PrimitiveAttribute;
import org.geomajas.layer.feature.attribute.ShortAttribute;
import org.geomajas.layer.feature.attribute.StringAttribute;
import org.geomajas.layer.feature.attribute.UrlAttribute;

/**
 * <p>
 * Definition of an object belonging to a {@link VectorLayer}. Simply put, a feature is an object with a unique ID
 * within it's layer, a list of alpha-numerical attributes and a geometry.
 * </p>
 * 
 * @author Pieter De Graef
 * @since 1.6.0
 */
@Api
public class Feature implements Paintable, Cloneable {

	/** Unique identifier */
	private String id;

	/** Reference to the layer. */
	private VectorLayer layer;

	/** Map of this feature's attributes. */
	private Map<String, Attribute> attributes;

	/** This feature's geometry. */
	private Geometry geometry;

	/** The identifier of this feature's style. */
	private String styleId;

	/** Coordinate for the label. */
	private Coordinate labelPosition;

	/** is the feature clipped ? */
	private boolean clipped;

	/**
	 * Is it allowed for the user in question to edit this feature?
	 */
	private boolean updatable;

	/**
	 * Is it allowed for the user in question to delete this feature?
	 */
	private boolean deletable;

	private String crs;

	// Constructors:

	public Feature() {
		this(null);
	}

	public Feature(VectorLayer layer) {
		this(null, layer);
	}

	public Feature(org.geomajas.layer.feature.Feature dto, VectorLayer layer) {
		this.layer = layer;
		this.attributes = new HashMap<String, Attribute>();
		this.geometry = null;
		this.styleId = null;
		this.labelPosition = null;
		this.clipped = false;
		if (dto != null) {
			attributes = dto.getAttributes();
			id = dto.getId();
			geometry = GeometryConverter.toGwt(dto.getGeometry());
			styleId = dto.getStyleId();
			crs = dto.getCrs();
			setUpdatable(dto.isUpdatable());
			setDeletable(dto.isDeletable());
		} else {
			if (layer != null) {
				// Create empty attributes:
				for (AttributeInfo attrInfo : layer.getLayerInfo().getFeatureInfo().getAttributes()) {
					attributes.put(attrInfo.getName(), createEmptyAttribute(attrInfo));
				}
			}
			setUpdatable(true);
			setDeletable(true);
		}
	}

	// Paintable implementation:

	public void accept(PainterVisitor visitor, Object group, Bbox bounds, boolean recursive) {
		visitor.visit(this, group);
	}

	public String getId() {
		return id;
	}

	// Class specific functions:

	public Feature clone() {
		Feature feature = new Feature(this.layer);
		if (null != attributes) {
			feature.attributes = new HashMap<String, Attribute>();
			for (Entry<String, Attribute> entry : attributes.entrySet()) {
				feature.attributes.put(entry.getKey(), (Attribute<?>) entry.getValue().clone());
			}
		}
		feature.clipped = clipped;
		feature.labelPosition = labelPosition;
		feature.layer = layer;
		if (null != geometry) {
			feature.geometry = (Geometry) geometry.clone();
		}
		feature.id = id;
		feature.styleId = styleId;
		feature.crs = crs;
		feature.deletable = deletable;
		feature.updatable = updatable;
		return feature;
	}

	public Object getAttributeValue(String attributeName) {
		Attribute attribute = getAttributes().get(attributeName);
		if (attribute != null) {
			return attribute.getValue();
		}
		return null;
	}
	
	public void setBooleanAttribute(String name, Boolean value) {
		((BooleanAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setCurrencyAttribute(String name, String value) {
		((CurrencyAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setDateAttribute(String name, Date value) {
		((DateAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setDoubleAttribute(String name, Double value) {
		((DoubleAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setFloatAttribute(String name, Float value) {
		((FloatAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setImageUrlAttribute(String name, String value) {
		((ImageUrlAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setIntegerAttribute(String name, Integer value) {
		((IntegerAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setLongAttribute(String name, Long value) {
		((LongAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setShortAttribute(String name, Short value) {
		((ShortAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setStringAttribute(String name, String value) {
		((StringAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setUrlAttribute(String name, String value) {
		((UrlAttribute) getAttributes().get(name)).setValue(value);
	}

	public void setManyToOneAttribute(String name, AssociationValue value) {
		((ManyToOneAttribute) getAttributes().get(name)).setValue(value);
	}

	/**
	 * Transform this object into a DTO feature.
	 * 
	 * @return dto for this feature
	 */
	public org.geomajas.layer.feature.Feature toDto() {
		org.geomajas.layer.feature.Feature dto = new org.geomajas.layer.feature.Feature();
		dto.setAttributes(attributes);
		dto.setClipped(clipped);
		dto.setId(id);
		dto.setGeometry(GeometryConverter.toDto(geometry));
		dto.setCrs(crs);
		return dto;
	}

	public String getLabel() {
		String attributeName = layer.getLayerInfo().getNamedStyleInfo().getLabelStyle().getLabelAttributeName();
		Object attributeValue = getAttributeValue(attributeName);
		return attributeValue == null ? "null" : attributeValue.toString();
	}

	// Getters and setters:

	/**
	 * Crs as (optionally) set by the backend.
	 * 
	 * @return crs for this feature
	 */
	public String getCrs() {
		return crs;
	}

	/**
	 * Get the attributes map, null when it needs to be lazy loaded.
	 * 
	 * @return attributes map
	 * @throws IllegalStateException
	 *             attributes not present because of lazy loading
	 */
	public Map<String, Attribute> getAttributes() throws IllegalStateException {
		if (null == attributes) {
			throw new IllegalStateException("Attributes not available, use LazyLoader.");
		}
		return attributes;
	}

	/**
	 * Set the attributes map.
	 * 
	 * @param attributes
	 *            attributes map
	 */
	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Check whether the attributes are already available or should be lazy loaded.
	 * 
	 * @return true when attributes are available
	 */
	public boolean isAttributesLoaded() {
		return attributes != null;
	}

	/**
	 * Get the feature's geometry, , null when it needs to be lazy loaded.
	 * 
	 * @return geometry
	 * @throws IllegalStateException
	 *             attributes not present because of lazy loading
	 */
	public Geometry getGeometry() throws IllegalStateException {
		if (null == geometry) {
			throw new IllegalStateException("Geometry not available, use LazyLoader.");
		}
		return geometry;
	}

	/**
	 * Set the geometry.
	 * 
	 * @param geometry
	 *            geometry
	 */
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	/**
	 * Check whether the geometry is already available or should be lazy loaded.
	 * 
	 * @return true when geometry are available
	 */
	public boolean isGeometryLoaded() {
		return geometry != null;
	}

	public boolean isSelected() {
		return layer.isFeatureSelected(getId());
	}

	public VectorLayer getLayer() {
		return layer;
	}

	/**
	 * Is the logged in user allowed to edit this feature?
	 * 
	 * @return true when edit/update is allowed for this feature
	 */
	public boolean isUpdatable() {
		return updatable;
	}

	/**
	 * Set whether the logged in user is allowed to edit/update this feature.
	 * 
	 * @param editable
	 *            true when edit/update is allowed for this feature
	 */
	public void setUpdatable(boolean editable) {
		this.updatable = editable;
	}

	/**
	 * Is the logged in user allowed to delete this feature?
	 * 
	 * @return true when delete is allowed for this feature
	 */
	public boolean isDeletable() {
		return deletable;
	}

	/**
	 * Set whether the logged in user is allowed to delete this feature.
	 * 
	 * @param deletable
	 *            true when deleting this feature is allowed
	 */
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	// -------------------------------------------------------------------------
	// Private methods:
	// -------------------------------------------------------------------------

	private Attribute<?> createEmptyAttribute(AttributeInfo attrInfo) {
		if (attrInfo instanceof PrimitiveAttributeInfo) {
			return createEmptyPrimitiveAttribute((PrimitiveAttributeInfo) attrInfo);
		} else if (attrInfo instanceof AssociationAttributeInfo) {
			return createEmptyAssociationAttribute((AssociationAttributeInfo) attrInfo);
		}
		return null;
	}

	private PrimitiveAttribute<?> createEmptyPrimitiveAttribute(PrimitiveAttributeInfo info) {
		PrimitiveAttribute<?> attribute = null;
		switch (info.getType()) {
			case BOOLEAN:
				attribute = new BooleanAttribute();
				break;
			case SHORT:
				attribute = new ShortAttribute();
				break;
			case INTEGER:
				attribute = new IntegerAttribute();
				break;
			case LONG:
				attribute = new LongAttribute();
				break;
			case FLOAT:
				attribute = new FloatAttribute();
				break;
			case DOUBLE:
				attribute = new DoubleAttribute();
				break;
			case CURRENCY:
				attribute = new CurrencyAttribute();
				break;
			case STRING:
				attribute = new StringAttribute();
				break;
			case DATE:
				attribute = new DateAttribute();
				break;
			case URL:
				attribute = new UrlAttribute();
				break;
			case IMGURL:
				attribute = new ImageUrlAttribute();
		}
		attribute.setEditable(info.isEditable());
		return attribute;
	}

	private AssociationAttribute<?> createEmptyAssociationAttribute(AssociationAttributeInfo info) {
		AssociationAttribute<?> association = null;
		switch (info.getType()) {
			case MANY_TO_ONE:
				association = new ManyToOneAttribute();
				break;
			case ONE_TO_MANY:
				association = new OneToManyAttribute();
		}
		association.setEditable(info.isEditable());
		return association;
	}
}
