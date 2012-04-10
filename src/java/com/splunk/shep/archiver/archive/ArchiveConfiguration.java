package com.splunk.shep.archiver.archive;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.splunk.shep.server.mbeans.ShepArchiver;
import com.splunk.shep.server.mbeans.ShepArchiverMBean;

public class ArchiveConfiguration {

    private final ShepArchiverMBean mBean;

    public ArchiveConfiguration(ShepArchiverMBean mBean) {
	this.mBean = mBean;
    }

    /**
     * Soft link so the memory can be used if needed. (Soft links are
     * GarbageCollected only if there is really need for the memory)
     */
    private static SoftReference<ArchiveConfiguration> sharedInstanceRef;

    public static ArchiveConfiguration getSharedInstance() {
	ArchiveConfiguration sharedInstance = null;
	if (sharedInstanceRef != null) {
	    sharedInstance = sharedInstanceRef.get();
	}
	if (sharedInstance == null) {
	    sharedInstance = new ArchiveConfiguration(
		    ShepArchiver.getMBeanProxy());
	    sharedInstanceRef = new SoftReference<ArchiveConfiguration>(
		    sharedInstance);
	}
	return sharedInstance;
    }

    public BucketFormat getArchiveFormat() {
	return BucketFormat.valueOf(mBean.getArchiveFormat());
    }

    public URI getArchivingRoot() {
	return URI.create(mBean.getArchiverRootURI());
    }

    public String getClusterName() {
	return mBean.getClusterName();
    }

    public String getServerName() {
	return mBean.getServerName();
    }

    /**
     * List of bucket formats, where lower index means it has higher priority. <br/>
     * {@link ArchiveConfiguration#getBucketFormatPriority()}.get(0) has the
     * highest priority, while .get(length-1) has the least priority.
     */
    public List<BucketFormat> getBucketFormatPriority() {
	List<BucketFormat> tempList = new ArrayList<BucketFormat>();
	for (String format : mBean.getBucketFormatPriority()) {
	    tempList.add(BucketFormat.valueOf(format));
	}
	return tempList;
    }

    /**
     * @return The Path on hadoop filesystem that is used as a temp directory
     */
    public URI getTmpDirectory() {
	URI archivingRoot = getArchivingRoot();
	String tmpDirectoryPath = mBean.getTmpDirectory();
	return archivingRoot.resolve(tmpDirectoryPath);
    }

}