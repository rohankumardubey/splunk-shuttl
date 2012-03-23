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
package com.splunk.shep;

import static com.splunk.shep.ShepConstants.*;
import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.splunk.Args;
import com.splunk.EntityCollection;
import com.splunk.Index;
import com.splunk.Service;

/**
 * @author hyan
 *
 */
public class ShepTestBase {
    public static final String PWD = System.getProperty("user.dir");
    // splunk convert your index name to all lowercase, so use lowercase to make
    // sure you can find the index
    protected static final String indexName = "shepTestIndex".toLowerCase();
    private static final Logger log = Logger.getLogger(ShepTestBase.class);

    protected void wait_event_count(Index index, int value, int seconds) {
	while (seconds > 0) {
	    try {
		Thread.sleep(1000); // 1000ms (1 second sleep)
		seconds = seconds - 1;
		if (index.getTotalEventCount() == value) {
		    return;
		}
		index.refresh();
	    } catch (InterruptedException e) {
		return;
	    } catch (Exception e) {
		return;
	    }
	}
    }

    protected void addOneShot(String... lines) throws IOException {
	File file = FileUtils.getFile(PWD, "build-cache", "splunk");
	if (System.getProperty(SPLUNK_HOME_PROPERTY) == null) {
	    System.setProperty(SPLUNK_HOME_PROPERTY, file.getAbsolutePath());
	}

	File translog = new File(TRANSLOG_FILE_PATH);
	if (translog.exists()) {
	    FileUtils.forceDelete(translog);
	}

	// create test index
	Args args = new Args();
	args.put("username", "admin");
	args.put("password", "changeme");
	args.put("host", "localhost");
	args.put("port", 8089);
	Service service = Service.connect(args);
	EntityCollection<Index> indexes = service.getIndexes();
	if (!indexes.containsKey(indexName)) {
	    indexes.create(indexName);
	    indexes.refresh();
	}

	assertTrue(indexes.containsKey(indexName));
	Index index = indexes.get(indexName);
	index.clean(60);
	assertEquals(index.getTotalEventCount(), 0);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	String date = sdf.format(new Date());
	for (String line : lines) {
	    index.submit(date + line);
	}

	wait_event_count(index, lines.length, 30);
	assertEquals(index.getTotalEventCount(), lines.length);
    }

    public void verifyJson(InputStream is, int expectedSize) throws IOException {
	String result = IOUtils.toString(is);
	log.debug("result: " + result);
	ObjectMapper m = new ObjectMapper();
	JsonNode node = m.readTree(result);
	assertNotNull(node);
	assertEquals(expectedSize, node.size());
    }

    public void sleep(long millis) {
	try {
	    Thread.sleep(millis);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
