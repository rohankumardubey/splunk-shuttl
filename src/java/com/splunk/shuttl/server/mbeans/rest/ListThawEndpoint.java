// Copyright (C) 2011 Splunk Inc.
//
// Splunk Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.splunk.shuttl.server.mbeans.rest;

import static com.splunk.shuttl.ShuttlConstants.*;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.splunk.shuttl.archiver.flush.ThawedBuckets;
import com.splunk.shuttl.archiver.model.Bucket;
import com.splunk.shuttl.archiver.model.IllegalIndexException;
import com.splunk.shuttl.archiver.thaw.BucketFilter;
import com.splunk.shuttl.archiver.thaw.SplunkSettings;
import com.splunk.shuttl.archiver.thaw.SplunkSettingsFactory;

@Path(ENDPOINT_ARCHIVER + ENDPOINT_THAW_LIST)
public class ListThawEndpoint {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listThawedBuckets(@QueryParam("index") String index,
			@QueryParam("from") String from, @QueryParam("to") String to) {

		Date earliest = RestUtil.getValidFromDate(from);
		Date latest = RestUtil.getValidToDate(to);

		try {
			return filteredBucketsInThaw(index, earliest, latest);
		} catch (IllegalIndexException e) {
			return RestUtil.respondWithIndexError(index);
		}
	}

	private String filteredBucketsInThaw(String index, Date earliest, Date latest)
			throws IllegalIndexException {
		SplunkSettings splunkSettings = SplunkSettingsFactory.create();
		List<Bucket> buckets = ThawedBuckets.getBucketsFromThawLocation(index,
				splunkSettings.getThawLocation(index));
		List<Bucket> filteredBuckets = BucketFilter.filterBuckets(buckets,
				earliest, latest);
		return RestUtil.respondWithBuckets(filteredBuckets);
	}
}
