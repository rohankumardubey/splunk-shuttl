package com.splunk.shep.archiver.archive;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.splunk.shep.testutil.ShellClassRunner;

@Test(groups = { "fast" })
public class ArchiveBucketTest {

    private ShellClassRunner shellClassRunner;

    @BeforeMethod(groups = { "fast" })
    public void setUp() {
	shellClassRunner = new ShellClassRunner();
    }

    @AfterMethod(groups = { "fast" })
    public void tearDown() {
	deleteDirectory(getSafeLocationDirectory());
	deleteDirectory(getTestDirectory());
    }

    private void deleteDirectory(File dir) {
	try {
	    FileUtils.deleteDirectory(dir);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    public void safeLocationDirectory_shouldNot_exist() {
	assertFalse(getSafeLocationDirectory().exists());
    }

    public void testDirectory_shouldNot_exist() {
	assertFalse(getTestDirectory().exists());
    }

    public void should_returnExitStatusZero_when_runWithOneArgument_where_theArgumentIsAnExistingDirectory() {
	File directory = createTestDirectory();
	assertEquals(0, runArchiveBucketMain(directory.getAbsolutePath()));
    }

    public void should_returnExitStatus_1_when_runWithZeroArguments() {
	assertEquals(1, runArchiveBucketMain());
    }

    public void should_returnExitStatus_2_when_runWithMoreThanOneArgument() {
	assertEquals(2, runArchiveBucketMain("one", "two"));
    }

    private int runArchiveBucketMain(String... args) {
	return shellClassRunner.runClassWithArgs(ArchiveBucket.class, args)
		.getExitCode();
    }

    public void should_returnExitStatus_3_when_runWithArgumentThatIsNotADirectory()
	    throws IOException {
	File file = File.createTempFile("ArchiveTest", ".tmp");
	file.deleteOnExit();
	assertTrue(!file.isDirectory());
	assertEquals(3, runArchiveBucketMain(file.getAbsolutePath()));
    }

    @Test
    public void should_moveDirectory_to_aSafeLocation_when_givenPath() {
	File dirToBeMoved = createTestDirectory();
	File safeLocationDirectory = getSafeLocationDirectory();

	assertEquals(0, runArchiveBucketMain(dirToBeMoved.getAbsolutePath()));
	assertTrue(!dirToBeMoved.exists());
	File dirInSafeLocation = new File(safeLocationDirectory,
		dirToBeMoved.getName());
	assertTrue(dirInSafeLocation.exists());
	assertTrue(dirInSafeLocation.delete());
	assertTrue(safeLocationDirectory.exists());
    }

    private File getSafeLocationDirectory() {
	return new File(ArchiveBucket.SAFE_LOCATION);
    }

    private File getTestDirectory() {
	return new File(getClass().getSimpleName() + "-test-dir");
    }

    private File createTestDirectory() {
	return createDirectory(getTestDirectory());
    }

    private File createDirectory(File dir) {
	dir.mkdir();
	assertTrue(dir.exists());
	return dir;
    }

}
