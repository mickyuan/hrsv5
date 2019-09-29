package hrds.commons.utils;

import fd.ng.test.junit.TestCaseLog;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ZipUtilsTest {

	@Test
	public void gzip() {
		String gzip = ZipUtils.gzip("天山路8号402-李强");
		TestCaseLog.println(gzip);
		String gunzip = ZipUtils.gunzip(gzip);
		TestCaseLog.println(gunzip);
		assertThat(gunzip,is("天山路8号402-李强"));
	}

	@Test
	public void zip() {
		String zip = ZipUtils.zip("锦业一路宝德云谷11F");
		TestCaseLog.println(zip);
		String unzip = ZipUtils.unzip(zip);
		assertThat(unzip,is("锦业一路宝德云谷11F"));
	}
}