package c.biz.jobschedule;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.entity.Etl_sys;
import hrds.commons.entity.Sys_user;
import org.junit.Before;
import org.junit.Test;
import testbase.WebBaseTestCase;

public class JobConfigurationTest extends WebBaseTestCase {

    private static final String

    @Before
    public void before(){
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Etl_sys etl_sys = new Etl_sys();
            etl_sys.setEtl_sys_cd();
        }
    }
}

