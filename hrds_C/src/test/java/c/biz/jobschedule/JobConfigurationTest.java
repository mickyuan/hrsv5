package c.biz.jobschedule;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import org.junit.Before;
import testbase.WebBaseTestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JobConfigurationTest extends WebBaseTestCase {

    // 初始化工程编号
    private static final String etlSysCd = "-10000";
    // 初始化任务编号
    private static final String subSysCd = "-10000";
    // 初始化登录用户ID
    private static final long userId = 8888L;
    // 初始化创建用户ID
    private static final long createId = 1000L;
    // 测试部门ID dep_id,测试作业调度部门
    private static final long depId = -1000011L;

    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.构造sys_user表测试数据
            Sys_user sysUser = new Sys_user();
            sysUser.setUser_id(userId);
            sysUser.setCreate_id(createId);
            sysUser.setDep_id(depId);
            sysUser.setCreate_date(DateUtil.getSysDate());
            sysUser.setCreate_time(DateUtil.getSysTime());
            sysUser.setRole_id("1001");
            sysUser.setUser_name("数据源功能测试");
            sysUser.setUser_password("1");
            sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
            sysUser.setUseris_admin("1");
            sysUser.setUsertype_group("02,03,04,08");
            sysUser.setUser_state(IsFlag.Shi.getCode());
            sysUser.add(db);

            // 2.构造department_info部门表测试数据
            // 创建department_info表实体对象
            Department_info department_info = new Department_info();
            department_info.setDep_id(depId);
            department_info.setDep_name("测试作业调度部门");
            department_info.setCreate_date(DateUtil.getSysDate());
            department_info.setCreate_time(DateUtil.getSysTime());
            department_info.setDep_remark("测试");
            int diNum = department_info.add(db);
            assertThat("测试数据department_info初始化", diNum, is(1));
            // 3.构造etl_sys表测试数据
            Etl_sys etl_sys = new Etl_sys();
            etl_sys.setEtl_sys_cd(etlSysCd);
            etl_sys.setEtl_sys_name("dhwcs");
            etl_sys.setUser_id(userId);
            int etlSysNum = etl_sys.add(db);
            assertThat("测试数据data_source初始化", etlSysNum, is(1));
            // 4.构造etl_sub_sys_list表测试数据
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setEtl_sys_cd(etlSysCd);
            etl_sub_sys_list.setSub_sys_cd(subSysCd);
            int subSysNum = etl_sub_sys_list.add(db);
            assertThat("测试数据data_source初始化", subSysNum, is(1));
            // 5.构造etl_job_def表测试数据
            for (int i = 0; i < 6; i++) {
                Etl_job_def etl_job_def = new Etl_job_def();
                etl_job_def.setEtl_sys_cd(etlSysCd);
                etl_job_def.setSub_sys_cd(subSysCd);
                etl_job_def.setEtl_job("测试作业" + i);
                etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
                etl_job_def.setPro_name(i + "aaa.shell");
                etl_job_def.setPro_dic("/home/hyshf/dhw");
                etl_job_def.setEtl_job_desc("测试作业");
                etl_job_def.setPro_para("1");
                etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
                etl_job_def.setToday_disp(IsFlag.Shi.getCode());
                switch (i) {
                    case 0:
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS0.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
                        etl_job_def.setExe_frequency(1);
                        etl_job_def.setExe_num(1);
                        etl_job_def.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
                                + " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
                        etl_job_def.setEnd_time("2020-12-31 10:30:30");
                        break;
                    case 1:
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        break;
                    case 2:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 3:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 4:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.TENDAYS.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 5:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                }
                int jobNum = etl_job_def.add(db);
                assertThat("测试数据data_source初始化", jobNum, is(1));
            }
            // 6.构造etl_resource表测试数据
            Etl_resource etl_resource = new Etl_resource();
            etl_resource.setEtl_sys_cd(etlSysCd);
            etl_resource.setResource_type("resource");
        }
    }
}

