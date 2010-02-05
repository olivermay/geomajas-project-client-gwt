/*
 * This file is part of Geomajas, a component framework for building
 * rich Internet applications (RIA) with sophisticated capabilities for the
 * display, analysis and management of geographic information.
 * It is a building block that allows developers to add maps
 * and other geographic data capabilities to their web applications.
 *
 * Copyright 2008-2010 Geosparc, http://www.geosparc.com, Belgium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geomajas.command.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geomajas.command.CommandDispatcher;
import org.geomajas.command.dto.SearchFeatureRequest;
import org.geomajas.command.dto.SearchFeatureResponse;
import org.geomajas.geometry.Coordinate;
import org.geomajas.geometry.Geometry;
import org.geomajas.layer.feature.Feature;
import org.geomajas.layer.feature.SearchCriterion;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test for the SearchFeatureCommand class.
 *
 * @author Joachim Van der Auwera
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/geomajas/spring/geomajasContext.xml",
		"/org/geomajas/testdata/layerCountries.xml", "/org/geomajas/testdata/simplevectorsContext.xml" })
public class SearchFeatureCommandTest {

	private static final double DOUBLE_TOLERANCE = .00000001;
	private static final String LAYER_ID = "countries";
	private static final String REGION_ATTRIBUTE = "region";
	private static final String NAME_ATTRIBUTE = "name";

	@Autowired
	private CommandDispatcher dispatcher;

	@Test
	public void testSearchOneCriterionNoLimit() throws Exception {
		// prepare command
		SearchFeatureRequest request = new SearchFeatureRequest();
		request.setLayerId(LAYER_ID);
		request.setCrs("EPSG:4326");
		request.setMax(SearchFeatureRequest.MAX_UNLIMITED);
		SearchCriterion searchCriterion = new SearchCriterion();
		searchCriterion.setAttributeName(REGION_ATTRIBUTE);
		searchCriterion.setOperator("like");
		searchCriterion.setValue("'%1'");
		request.setCriteria(new SearchCriterion[] {searchCriterion});

		// execute
		SearchFeatureResponse response = (SearchFeatureResponse) dispatcher.execute(
				"command.feature.Search", request, null, "en");

		// test
		Assert.assertFalse(response.isError());
		Assert.assertEquals(LAYER_ID, response.getLayerId());
		List<Feature> features = Arrays.asList(response.getFeatures());
		Assert.assertNotNull(features);
		List<String> actual = new ArrayList<String>();
		for (Feature feature : features) {
			actual.add(feature.getLabel());
		}
		List<String> expected = new ArrayList<String>();
		// reverse order
		expected.add("Country 2");
		expected.add("Country 1");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSearchOneCriterionLimit() throws Exception {
		// prepare command
		SearchFeatureRequest request = new SearchFeatureRequest();
		request.setLayerId(LAYER_ID);
		request.setCrs("EPSG:4326");
		request.setMax(3);
		SearchCriterion searchCriterion = new SearchCriterion();
		searchCriterion.setAttributeName(REGION_ATTRIBUTE);
		searchCriterion.setOperator("like");
		searchCriterion.setValue("'R%'");
		request.setCriteria(new SearchCriterion[] {searchCriterion});

		// execute
		SearchFeatureResponse response = (SearchFeatureResponse) dispatcher.execute(
				"command.feature.Search", request, null, "en");

		// test
		Assert.assertFalse(response.isError());
		Assert.assertEquals(LAYER_ID, response.getLayerId());
		List<Feature> features = Arrays.asList(response.getFeatures());
		Assert.assertNotNull(features);
		List<String> actual = new ArrayList<String>();
		for (Feature feature : features) {
			actual.add(feature.getLabel());
		}
		List<String> expected = new ArrayList<String>();
		// reverse order
		expected.add("Country 4");
		expected.add("Country 3");
		expected.add("Country 2");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSearchMultipleCriteria() throws Exception {
		// prepare command
		SearchFeatureRequest request = new SearchFeatureRequest();
		request.setLayerId(LAYER_ID);
		request.setCrs("EPSG:4326");
		SearchCriterion searchCriterion1 = new SearchCriterion();
		searchCriterion1.setAttributeName(REGION_ATTRIBUTE);
		searchCriterion1.setOperator("like");
		searchCriterion1.setValue("'%egion 1'");
		SearchCriterion searchCriterion2 = new SearchCriterion();
		searchCriterion2.setAttributeName(REGION_ATTRIBUTE);
		searchCriterion2.setOperator("like");
		searchCriterion2.setValue("'%egion 2'");
		request.setCriteria(new SearchCriterion[] {searchCriterion1, searchCriterion2});
		request.setBooleanOperator("or");

		// execute
		SearchFeatureResponse response = (SearchFeatureResponse) dispatcher.execute(
				"command.feature.Search", request, null, "en");

		// test
		Assert.assertFalse(response.isError());
		Assert.assertEquals(LAYER_ID, response.getLayerId());
		List<Feature> features = Arrays.asList(response.getFeatures());
		Assert.assertNotNull(features);
		List<String> actual = new ArrayList<String>();
		for (Feature feature : features) {
			actual.add(feature.getLabel());
		}
		List<String> expected = new ArrayList<String>();
		// reverse order
		expected.add("Country 4");
		expected.add("Country 3");
		expected.add("Country 2");
		expected.add("Country 1");
		Assert.assertEquals(expected, actual);
	}

	// @todo need to test filter
	// @todo need to test filter + limit + multiple criteria
	// @todo need to test no filter, no criteria (return all?)

	@Test
	public void testSearchTransform() throws Exception {
		// prepare command, verify original coordinates first
		SearchFeatureRequest request = new SearchFeatureRequest();
		request.setLayerId(LAYER_ID);
		request.setCrs("EPSG:4326");
		request.setMax(3); // this immediately tests whether returning less than the maximum works
		SearchCriterion searchCriterion = new SearchCriterion();
		searchCriterion.setAttributeName(NAME_ATTRIBUTE);
		searchCriterion.setOperator("like");
		searchCriterion.setValue("'%3'");
		request.setCriteria(new SearchCriterion[] {searchCriterion});

		// execute
		SearchFeatureResponse response = (SearchFeatureResponse) dispatcher.execute(
				"command.feature.Search", request, null, "en");

		// test
		Assert.assertFalse(response.isError());
		Assert.assertEquals(LAYER_ID, response.getLayerId());
		Feature[] features = response.getFeatures();
		Assert.assertNotNull(features);
		Assert.assertEquals(1, features.length);
		Assert.assertEquals("Country 3", features[0].getLabel());
		Geometry geometry = features[0].getGeometry();
		Assert.assertNotNull(geometry);
		Coordinate coor = geometry.getGeometries()[0].getGeometries()[0].getCoordinates()[0];
		Assert.assertEquals(-1, coor.getX(), DOUBLE_TOLERANCE);
		Assert.assertEquals(0, coor.getY(), DOUBLE_TOLERANCE);

		// try again using mercator
		request.setCrs("EPSG:900913");
		response = (SearchFeatureResponse) dispatcher.execute( "command.feature.Search", request, null, "en");
		if (response.isError()) response.getErrors().get(0).printStackTrace();
		Assert.assertFalse(response.isError());
		Assert.assertEquals(LAYER_ID, response.getLayerId());
		features = response.getFeatures();
		Assert.assertNotNull(features);
		Assert.assertEquals(1, features.length);
		Assert.assertEquals("Country 3", features[0].getLabel());
		geometry = features[0].getGeometry();
		Assert.assertNotNull(geometry);
		coor = geometry.getGeometries()[0].getGeometries()[0].getCoordinates()[0];
		// remark, this value is obtained using a test run, not externally verified
		Assert.assertEquals(-111319.49079327357, coor.getX(), DOUBLE_TOLERANCE);
		Assert.assertEquals(0, coor.getY(), DOUBLE_TOLERANCE);
	}
}
