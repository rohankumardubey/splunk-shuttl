package com.splunk.shep.archiver.model;

import java.io.File;
import java.io.FileNotFoundException;

import com.splunk.shep.archiver.archive.ArchiveFormat;

/**
 * Model representing a Splunk bucket
 */
public class Bucket {

    private final String index;
    private final ArchiveFormat format;

    protected Bucket(String index, ArchiveFormat format) {
	this.index = index;
	this.format = format;
    }

    public ArchiveFormat getFormat() {
	return format;
    }

    public String getIndex() {
	return index;
    }

    public static Bucket createWithAbsolutePath(String path)
	    throws FileNotFoundException, FileNotDirectoryException {
	File directory = new File(path);
	if (!directory.exists()) {
	    throw new FileNotFoundException();
	} else if (!directory.isDirectory()) {
	    throw new FileNotDirectoryException();
	} else {
	    String index = directory.getParentFile().getParentFile().getName();
	    File rawdata = new File(directory, "rawdata");
	    ArchiveFormat format;
	    if (rawdata.exists()) {
		format = ArchiveFormat.SPLUNK_BUCKET;
	    } else {
		format = ArchiveFormat.UNKNOWN;
	    }
	    return new Bucket(index, format);
	}
    }

}
