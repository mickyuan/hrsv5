package hrds.b.biz.datasource;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AuthType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

@DocClass(desc = "数据源增删改查，导入、下载类", author = "dhw", createdate = "2019-9-20 09:23:06")
public class DataSourceAction extends BaseAction {
    private static final Logger logger = LogManager.getLogger();

    @Method(desc = "查询数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息，首页展示",
            logicStep = "1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查" +
                    "2.查询数据源及Agent数" +
                    "3.数据权限管理，分页查询数据源及部门关系信息" +
                    "4.数据管理列表，查询申请审批信息" +
                    "5.创建存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合并将数据进行封装" +
                    "6.返回存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "5")
    @Return(desc = "存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合", range = "无限制")
    public Map<String, Object> searchDataSourceInfo(int currPage, int pageSize) {
        // 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
        // 2.查询数据源及Agent数
        List<Map<String, Object>> dsAiList = Dbo.queryList("select ds.source_id,ds.datasource_name," +
                "count(ai.Agent_id) sumAgent from " + Data_source.TableName + " ds left join "
                + Agent_info.TableName + " ai on ds.source_id=ai.source_id where ds.create_user_id=? " +
                " GROUP BY ds.source_id,ds.datasource_name", getUserId());
        // 3.数据权限管理，分页查询数据源及部门关系信息
        Result dataSourceRelationDep = searchSourceRelationDepForPage(currPage, pageSize);
        // 4.数据管理列表，查询数据申请审批信息
        List<Map<String, Object>> dataAuditList = getDataAuditInfoForPage(currPage, pageSize);
        // 5.创建存放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合并将数据进行封装
        Map<String, Object> dataSourceInfoMap = new HashMap<>();
        dataSourceInfoMap.put("dataSourceRelationDep", dataSourceRelationDep.toList());
        dataSourceInfoMap.put("dataAudit", dataAuditList);
        dataSourceInfoMap.put("dataSourceAndAgentCount", dsAiList);
        // 6.返回放数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息的集合
        return dataSourceInfoMap;
    }

    @Method(desc = "数据管理列表，分页查询获取数据申请审批信息的集合",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.获取所有的source_id" +
                    "3.查询数据源申请审批信息集合并返回")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "5")
    @Return(desc = "存放数据申请审批信息的集合", range = "无限制")
    public List<Map<String, Object>> getDataAuditInfoForPage(int currPage, int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.获取所有的source_id
        List<Long> sourceIdList = Dbo.queryOneColumnList("select source_id from " + Data_source.TableName
                + " where create_user_id=?", getUserId());
        // 3.查询数据源申请审批信息集合并返回
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.addSql("select da.DA_ID,da.APPLY_DATE,da.APPLY_TIME,da.APPLY_TYPE,da.AUTH_TYPE,da.AUDIT_DATE," +
                "da.AUDIT_TIME,da.AUDIT_USERID,da.AUDIT_NAME,da.FILE_ID,da.USER_ID,da.DEP_ID,sfa.*,su.user_name" +
                " from " + Data_auth.TableName + " da join " + Sys_user.TableName + " su on da.user_id=su.user_id " +
                " join " + Source_file_attribute.TableName + " sfa on da.file_id= sfa.file_id  where " +
                "su.create_id in (select user_id from sys_user where user_type=? or user_id = ?) ")
                .addParam(UserType.XiTongGuanLiYuan.getCode()).addParam(getUserId())
                .addORParam("sfa.source_id", sourceIdList.toArray()).addSql(" ORDER BY  da_id desc");
        return Dbo.queryPagedList(new DefaultPageImpl(currPage, pageSize), asmSql.sql(), asmSql.params());
    }

    @Method(desc = "数据权限管理，分页查询数据源及部门关系信息",
            logicStep = "1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查" +
                    "2.分页查询数据源及部门关系" +
                    "3.判断数据源是否为空" +
                    "4.循环数据源" +
                    "5.创建存放数据源对应部门集合" +
                    "6.查询获取数据源对应部门结果集" +
                    "7.判断数据源对应的部门结果集是否为空" +
                    "8.循环部门获取部门名称" +
                    "9.将各个数据源对应的部门名称加入list" +
                    "10.封装部门名称到结果集" +
                    "11.返回结果集")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "5")
    @Return(desc = "返回分页查询数据源及部门关系", range = "无限制")
    public Result searchSourceRelationDepForPage(int currPage, int pageSize) {

        // 1.数据可访问权限处理方式，以下sql通过user_id关联进行权限检查
        // 2.分页查询数据源及部门关系
        Result dsResult = Dbo.queryPagedResult(new DefaultPageImpl(currPage, pageSize), " SELECT * from " +
                        Data_source.TableName + " where create_user_id=? order by create_date desc,create_time desc",
                getUserId());
        // 3.判断数据源是否为空
        if (!dsResult.isEmpty()) {
            // 4.循环数据源
            for (int i = 0; i < dsResult.getRowCount(); i++) {
                // 5.创建存放数据源对应部门集合
                List<String> depList = new ArrayList<>();
                // 6.查询获取数据源对应部门结果集
                Result depResult = Dbo.queryResult(" select di.* from " + Department_info.TableName +
                        " di left join " + Source_relation_dep.TableName + " srd on di.dep_id = srd.dep_id" +
                        " where srd.source_id=?", dsResult.getLong(i, "source_id"));
                // 7.判断数据源对应的部门结果集是否为空
                if (!depResult.isEmpty()) {
                    // 8.循环部门获取部门名称
                    for (int j = 0; j < depResult.getRowCount(); j++) {
                        // 9.将各个数据源对应的部门名称加入list
                        depList.add(depResult.getString(j, "dep_name"));
                    }
                    // 10.封装部门名称到结果集
                    StringBuilder sb = new StringBuilder();
                    for (int n = 0; n < depList.size(); n++) {
                        if (n != depList.size() - 1) {
                            sb.append(depList.get(n)).append(",");
                        } else {
                            sb.append(depList.get(n));
                        }
                    }
                    dsResult.setObject(i, "dep_name", sb.toString());
                }
            }
        }
        // 11.返回结果集
        return dsResult;
    }

    @Method(desc = "数据权限管理，更新数据源关系部门信息",
            logicStep = "1.数据可访问权限处理方式，通过sourceId与user_id关联检查" +
                    "2.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute" +
                    "3.建立新关系，保存source_relation_dep表信息")
    @Param(name = "source_id", desc = "data_source表主键ID", range = "不为空的十位数字，新增时通过主键生成规则自动生成")
    @Param(name = "dep_id", desc = "存储source_relation_dep表主键ID，可能是一个也可能是多个拼接的字符串",
            range = "不为空以及不为空格")
    public void updateAuditSourceRelationDep(long source_id, String dep_id) {
        // 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
        if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + " ds left join " +
                Source_relation_dep.TableName + " srd on ds.source_id=srd.source_id where ds.source_id=?" +
                " and ds.create_user_id=?", source_id, getUserId())
                .orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
            throw new BusinessException("数据权限校验失败，数据不可访问！");
        }
        // 2.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
        int num = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?",
                source_id);
        if (num < 1) {
            throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
                    "sourceId=" + source_id);
        }
        // 3.建立新关系，保存source_relation_dep表信息
        saveSourceRelationDep(source_id, dep_id);
    }

    @Method(desc = "数据管理列表，数据申请审批并返回最新数据申请审批数据信息",
            logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
                    "2.根据数据权限设置ID查询数据申请审批信息" +
                    "3.判断查询信息是否不存在" +
                    "4.根据数据权限设置ID以及权限类型进行审批" +
                    "5.查询审批后的最新数据申请审批信息并返回")
    @Param(name = "da_id", desc = "数据权限设置ID，表data_auth表主键", range = "不为空的十位数字，新增时通过主键生成规则自动生成")
    @Param(name = "auth_type", desc = "权限类型", range = "0-申请<ShenQing>，1-允许<YunXu>，2-不允许<BuYunXu>，3-一次<YiCi>")
    @Return(desc = "存放数据申请审批信息的集合", range = "无限制")
    public List<Map<String, Object>> dataAudit(long da_id, String auth_type) {
        // 1.数据可访问权限处理方式，根据user_id进行权限控制
        // authType代码项合法性验证，如果不存在该方法直接会抛异常
        AuthType.ofEnumByCode(auth_type);
        // 2.根据数据权限设置ID查询数据申请审批信息
        Optional<Data_auth> dataAuth = Dbo.queryOneObject(Data_auth.class, "select * from "
                + Data_auth.TableName + " where da_id=? and user_id=?", da_id, getUserId());
        // 3.判断查询信息是否不存在
        if (!dataAuth.isPresent()) {
            // 不存在值
            throw new BusinessException("此申请已取消或不存在！");
        }
        // 4.根据数据权限设置ID以及权限类型进行审批
        dataAuth.get().setAudit_date(DateUtil.getSysDate());
        dataAuth.get().setAudit_time(DateUtil.getSysTime());
        dataAuth.get().setAudit_userid(getUserId());
        dataAuth.get().setAudit_name(getUserName());
        dataAuth.get().setAuth_type(auth_type);
        dataAuth.get().setDa_id(da_id);
        dataAuth.get().update(Dbo.db());
        // 5.查询审批后的最新数据申请审批信息并返回
        return getDataAuditInfoForPage(1, 5);

    }

    @Method(desc = "根据权限设置ID进行权限回收并将最新数据申请审批信息返回",
            logicStep = "1.数据可访问权限处理方式，根据user_id进行权限控制" +
                    "2.权限回收" +
                    "3.查询审批后的最新数据申请审批信息并返回")
    @Param(name = "da_id", desc = "数据权限设置ID，表data_auth表主键", range = "不为空的十位数字，新增时通过主键生成规则自动生成")
    @Return(desc = "存放数据申请审批信息的集合", range = "无限制")
    public List<Map<String, Object>> deleteAudit(long da_id) {
        // 1.数据可访问权限处理方式，根据user_id进行权限控制
        // 2.权限回收
        DboExecute.deletesOrThrow("权限回收成功!", "delete from " + Data_auth.TableName +
                " where da_id = ? and user_id=?", da_id, getUserId());
        // 3.查询审批后的最新数据申请审批信息并返回
        return getDataAuditInfoForPage(1, 5);
    }

    @Method(desc = "新增数据源",
            logicStep = "1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证" +
                    "2.字段合法性检查" +
                    "3.对data_source初始化一些非页面传值" +
                    "4.保存data_source信息" +
                    "5.保存source_relation_dep表信息")
    @Param(name = "dataSource", desc = "data_source表实体对象", range = "与data_source表字段规则一致", isBean = true)
    @Param(name = "dep_id", desc = "存储source_relation_dep表主键ID，可能是一个也可能是多个拼接的字符串",
            range = "不为空以及不为空格")
    public void saveDataSource(Data_source dataSource, String dep_id) {
        // 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
        // 2.字段做合法性检查
        fieldLegalityValidation(dataSource.getDatasource_name(), dataSource.getDatasource_number(),
                dep_id);
        // 3.对data_source初始化一些非页面传值
        // 数据源主键ID
        dataSource.setSource_id(PrimayKeyGener.getNextId());
        // 数据源创建用户
        dataSource.setCreate_user_id(getUserId());
        // 数据源创建日期
        dataSource.setCreate_date(DateUtil.getSysDate());
        // 数据源创建时间
        dataSource.setCreate_time(DateUtil.getSysTime());
        // 4.保存data_source信息
        dataSource.add(Dbo.db());
        // 5.保存source_relation_dep表信息
        saveSourceRelationDep(dataSource.getSource_id(), dep_id);
    }

    @Method(desc = "更新数据源信息",
            logicStep = "1.数据可访问权限处理方式，通过sourceId与user_id关联检查" +
                    "2.验证sourceId是否合法" +
                    "3.字段合法性检查" +
                    "4.将data_source实体数据封装" +
                    "5.更新数据源信息" +
                    "6.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute" +
                    "7.保存source_relation_dep表信息")
    @Param(name = "source_id", desc = "data_source表主键，source_relation_dep表外键", range = "10位数字,新增时生成")
    @Param(name = "source_remark", desc = "备注，source_relation_dep表外键", range = "无限制")
    @Param(name = "datasource_name", desc = "数据源名称", range = "不为空且不为空格")
    @Param(name = "datasource_number", desc = "数据源编号", range = "不为空且不为空格，长度不超过四位")
    @Param(name = "dep_id", desc = "存储source_relation_dep表主键ID，可能是一个也可能是多个拼接的字符串",
            range = "不为空以及不为空格")
    public void updateDataSource(Long source_id, String source_remark, String datasource_name,
                                 String datasource_number, String dep_id) {
        // 1.数据可访问权限处理方式，通过source_id与user_id关联检查
        if (Dbo.queryNumber("select count(1) from " + Data_source.TableName +
                " where source_id=? and create_user_id=?", source_id, getUserId())
                .orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
            throw new BusinessException("数据权限校验失败，数据不可访问！");
        }
        //sourceId长度
        // 2.验证sourceId是否合法
        if (source_id == null) {
            throw new BusinessException("sourceId不为空以及不为空格，新增时自动生成");
        }
        // 3.字段合法性检查
        fieldLegalityValidation(datasource_name, datasource_number, dep_id);
        // 4.将data_source实体数据封装
        Data_source dataSource = new Data_source();
        dataSource.setSource_id(source_id);
        dataSource.setDatasource_name(datasource_name);
        dataSource.setDatasource_number(datasource_number);
        dataSource.setSource_remark(source_remark);
        // 5.更新数据源信息
        dataSource.update(Dbo.db());
        // 6.先删除数据源与部门关系信息,删除几条数据不确定，一个数据源对应多个部门，所以不能用DboExecute
        int num = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?",
                dataSource.getSource_id());
        if (num < 1) {
            throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
                    "sourceId=" + dataSource.getSource_id());
        }
        // 7.保存source_relation_dep表信息
        saveSourceRelationDep(source_id, dep_id);
    }

    @Method(desc = "数据源表字段合法性验证",
            logicStep = "1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证" +
                    "2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性" +
                    "3.验证datasource_name是否合法" +
                    "4.datasource_number是否合法" +
                    "5.更新前查询数据源编号是否已存在" +
                    "7.保存source_relation_dep表信息")
    @Param(name = "datasource_name", desc = "数据源名称", range = "不为空且不为空格")
    @Param(name = "datasource_umber", desc = "数据源编号", range = "不为空且不为空格，长度不超过四位")
    @Param(name = "dep_id", desc = "存储source_relation_dep表主键ID，可能是一个也可能是多个拼接的字符串",
            range = "不为空以及不为空格")
    private void fieldLegalityValidation(String datasource_name, String datasource_umber, String dep_id) {
        // 1.数据可访问权限处理方式，通过create_user_id检查
        // 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
        String[] depIds = dep_id.split(",");
        for (String depId : depIds) {
            if (StringUtil.isBlank(depId)) {
                throw new BusinessException("部门不能为空或者空格，新增部门时通过主键生成!");
            }
        }
        // 3.验证datasource_name是否合法
        if (StringUtil.isBlank(datasource_name)) {
            throw new BusinessException("数据源名称不能为空以及不能为空格，datasource_name=" + datasource_name);
        }
        // 4.datasource_number是否为空且长度是否超过4位
        if (StringUtil.isBlank(datasource_umber) || datasource_umber.length() > 4) {
            throw new BusinessException("数据源编号不能为空以及不能为空格或数据源编号长度不能超过四位，" +
                    "datasource_number=" + datasource_umber);
        }
        // 5.更新前查询数据源编号是否已存在
        if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + " where datasource_number=? "
                + " and create_user_id=?", datasource_umber, getUserId()).orElseThrow(() ->
                new BusinessException("sql查询错误！")) > 0) {
            // 判断数据源编号是否重复
            throw new BusinessException("数据源编号重复,datasource_number=" + datasource_umber);
        }
    }

    @Method(desc = "保存数据源与部门关系表信息",
            logicStep = "1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证" +
                    "2.验证传递的部门ID对应的部门信息是否存在" +
                    "3.创建source_relation_dep对象，并封装数据" +
                    "4.循环遍历存储部门ID的数组并保存source_relation_dep表信息")
    @Param(name = "source_id", desc = "source_relation_dep表外键ID", range = "不能为空以及不能为空格")
    @Param(name = "dep_id", desc = "存储source_relation_dep表主键ID，可能是一个也可能是多个拼接的字符串",
            range = "不为空以及不为空格")
    private void saveSourceRelationDep(long source_id, String dep_id) {
        // 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
        // 2.验证传递的部门ID对应的部门信息是否存在
        String[] depIds = dep_id.split(",");
        for (String depId : depIds) {
            if (Dbo.queryNumber("select count(*) from " + Department_info.TableName + " where dep_id=?",
                    Long.valueOf(depId)).orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
                throw new BusinessException("该部门ID对应的部门不存在，请检查！");
            }
        }
        // 3.创建source_relation_dep对象，并封装数据
        Source_relation_dep sourceRelationDep = new Source_relation_dep();
        sourceRelationDep.setSource_id(source_id);
        // 4.循环遍历存储部门ID的数组并保存source_relation_dep表信息
        for (String depId : depIds) {
            sourceRelationDep.setDep_id(Long.valueOf(depId));
            sourceRelationDep.add(Dbo.db());
        }
    }

    @Method(desc = "根据数据源编号查询数据源及数据源与部门关系信息以及部门信息",
            logicStep = "1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查" +
                    "2.创建并封装数据源与部门关联信息以及部门信息集合" +
                    "3.判断是新增还是编辑时查询回显数据，如果是新增，只查询部门信息，如果是编辑，还需查询数据源信息" +
                    "3.1关联查询data_source表信息" +
                    "3.2获取数据源对应部门ID所有值，不需要权限控制" +
                    "3.3.封装部门到结果集" +
                    "3.4将部门ID封装入数据源信息中" +
                    "4.查询部门信息，不需要用户权限控制" +
                    "5.将部门信息封装入Map" +
                    "6.返回封装数据源与部门关联信息以及部门信息集合")
    @Param(name = "source_id", desc = "source_relation_dep表外键ID", range = "不能为空以及不能为空格", nullable = true)
    @Return(desc = "返回关联查询data_source表与source_relation_dep表信息结果以及部门信息", range = "无限制")
    public Map<String, Object> searchDataSource(Long source_id) {
        // 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
        // 2.创建并封装数据源与部门关联信息以及部门信息集合
        Map<String, Object> datasourceMap = new HashMap<>();
        // 3.判断是新增还是更新，如果是新增，只查询部门信息，如果是更新，还需查询回显数据源数据
        if (source_id != null) {
            // 编辑时查询
            // 3.1关联查询data_source表信息
            datasourceMap = Dbo.queryOneObject("select * from " + Data_source.TableName +
                    " where source_id=? and create_user_id=?", source_id, getUserId());
            // 3.2获取数据源对应部门ID所有值,不需要权限控制
            List<Long> depIdList = Dbo.queryOneColumnList("select dep_id from " + Source_relation_dep.TableName +
                    " where source_id=?", source_id);
            if (!depIdList.isEmpty()) {
                // 3.3.封装部门到结果集
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < depIdList.size(); i++) {
                    if (i != depIdList.size() - 1) {
                        sb.append(depIdList.get(i)).append(",");
                    } else {
                        sb.append(depIdList.get(i));
                    }
                }
                // 3.4将部门ID封装入数据源信息中
                datasourceMap.put("dep_id", sb.toString());
            }
        }
        // 4.查询部门信息，不需要用户权限控制
        List<Department_info> departmentInfoList = Dbo.queryList(Department_info.class,
                "select * from " + Department_info.TableName);
        // 5.将部门信息封装入Map
        datasourceMap.put("departmentInfo", departmentInfoList);
        // 6.返回封装数据源与部门关联信息以及部门信息集合
        return datasourceMap;
    }

    @Method(desc = "删除数据源信息",
            logicStep = "1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查" +
                    "2.先查询该datasource下是否还有agent,有不能删除，没有，可以删除" +
                    "3.删除data_source表信息" +
                    "4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法" +
                    "5.删除source_relation_dep信息")
    @Param(name = "source_id", desc = "source_relation_dep表外键ID", range = "不能为空以及不能为空格")
    public void deleteDataSource(long source_id) {

        // 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
        // 2.先查询该datasource下是否还有agent
        // FIXME: orElse用法有误，逻辑有问题，用orElseThrow  已解决
        if (Dbo.queryNumber("SELECT count(1) FROM " + Agent_info.TableName + " WHERE source_id=? and user_id=?",
                source_id, getUserId()).orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
            throw new BusinessException("此数据源下还有agent，不能删除,sourceId=" + source_id);
        }
        // 3.删除data_source表信息
        DboExecute.deletesOrThrow("删除数据源信息表data_source失败，sourceId=" + source_id,
                "delete from " + Data_source.TableName + " where source_id=? and create_user_id=?", source_id,
                getUserId());
        // 4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法
        int srdNum = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?", source_id);
        if (srdNum < 1) {
            // 如果数据源存在，那么部门一定存在，所以这里不需要判断等于0的情况
            throw new BusinessException("删除该数据源下source_relation_dep表数据错误，sourceId="
                    + source_id);
        }
    }

    @Method(desc = "查询数据采集用户信息",
            logicStep = "1.数据可访问权限处理方式，此方法不需要权限验证" +
                    "2.查询数据采集用户信息并返回查询结果")
    @Return(desc = "存放数据采集用户信息的集合", range = "无限制")
    public List<Sys_user> searchDataCollectUser() {
        // 1.数据可访问权限处理方式，此方法不需要权限验证，没有用户访问限制
        // 2.查询数据采集用户信息并返回查询结果
        // FIXME: 为什么用union all以及为什么用like,注释说明      已解决
        //我们需要的是当前用户类型是数据采集（前半句sql查询结果）以及数据类型组包含数据采集的用户信息（后半句sql查询结果），所以用union,
        // 用户类型组中数据可能是多个用户类型组成的也可能是单个的，所以用like,我们这里不需要重复数据所以不用union all
        return Dbo.queryList(Sys_user.class, "select * from " + Sys_user.TableName + " where user_type=? " +
                        " and dep_id=? union select * from " + Sys_user.TableName + " where " +
                        " usertype_group like ?", UserType.CaiJiYongHu.getCode(), getUser().getDepId(),
                "%" + UserType.CaiJiYongHu.getCode() + "%");
    }

    @Method(desc = "导入数据源，数据源下载文件提供的文件中涉及到的所有表的数据导入数据库中对应的表中",
            logicStep = "1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限" +
                    "2.判断agent_ip是否是一个合法的ip" +
                    "3.判断agent_port是否是一个有效的端口" +
                    "4.验证userCollectId是否为null" +
                    "5.通过文件名称获取文件" +
                    "6.使用base64对数据进行解码" +
                    "7.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中")
    @Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
    @Param(name = "user_id", desc = "数据采集用户ID，指定谁可以查看该用户对应表信息", range = "不能为空以及空格，页面传值")
    @Param(name = "file", desc = "上传文件名称（全路径），上传要导入的数据源", range = "不能为空以及空格")
    public void uploadFile(String agent_ip, String agent_port, Long user_id, String file) {
        try {
            // 1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限
            // 2.判断agent_ip是否是一个合法的ip
            String[] split = agent_ip.split("\\.");
            for (int i = 0; i < split.length; i++) {
                int temp = Integer.parseInt(split[i]);
                if (temp < 0 || temp > 255) {
                    throw new BusinessException("agent_ip不是一个为空或空格的ip地址," +
                            "agent_ip=" + agent_ip);
                }
            }
            // 3.判断agent_port是否是一个有效的端口
            // 端口范围最小值
            int min = 1024;
            // 端口范围最大值
            int max = 65535;
            if (Integer.parseInt(agent_port) < min || Integer.parseInt(agent_port) > max) {
                throw new BusinessException("agent_port端口不是有效的端口，不在取值范围内，" +
                        "agent_port=" + agent_port);
            }
            // 4.验证userCollectId是否为null
            if (user_id == null) {
                throw new BusinessException("userCollectId不为空且不为空格");
            }
            // 5.通过文件名称获取文件
            File uploadedFile = FileUploadUtil.getUploadedFile(file);
            // 6.使用base64解码
            String strTemp = new String(Base64.getDecoder().decode(Files.readAllBytes(uploadedFile.toPath())));
            // 7.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中
            importDataSource(strTemp, agent_ip, agent_port, user_id, getUserId());
        } catch (Exception e) {
            throw new AppSystemException(e);
        }
    }

    @Method(desc = "导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中",
            logicStep = "1.获取文件对应所有表信息的map" +
                    "2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息" +
                    "3.获取数据源data_source信息并插入数据库" +
                    "4.将department_info表数据插入数据库" +
                    "5.将source_relation_dep数据插入数据库" +
                    "6.将agent_info表数据插入数据库" +
                    "7.将Agent_down_info表数据插入数据库" +
                    "8.将collect_job_classify表数据插入数据库" +
                    "9.将ftp采集设置ftp_collect表数据插入数据库" +
                    "10.将ftp已传输表ftp_transfered表数据插入数据库" +
                    "11.将ftp目录表ftp_folder表数据插入数据库" +
                    "12.将对象采集设置object_collect表数据插入数据库" +
                    "13.将对象采集对应信息object_collect_task表数据插入数据库" +
                    "14.将对象采集存储设置object_storage表数据插入数据库" +
                    "15.将对象采集结构信息object_collect_struct表数据插入数据库" +
                    "16.将数据库设置database_set表数据插入数据库" +
                    "17.将文件系统设置file_collect_set表数据插入数据库" +
                    "18.将文件源设置file_source表数据插入数据库" +
                    "19.将信号文件入库信息signal_file表数据插入数据库" +
                    "20.将数据库对应的表table_info表数据插入数据库" +
                    "21.将列合并信息column_merge表数据插入数据库" +
                    "22.将表存储信息table_storage_info表数据插入数据库" +
                    "23.将表清洗参数信息table_clean表数据插入数据库" +
                    "24.将表对应的字段table_column表数据插入数据库" +
                    "25.将列清洗参数信息column_clean表数据插入数据库" +
                    "26.将列拆分信息表column_split表数据插入数据库")
    @Param(name = "strTemp", desc = "涉及数据源文件下载相关的所有表进行base64编码后的信息", range = "不能为空")
    @Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
    @Param(name = "user_id", desc = "数据采集用户ID，指定谁可以查看该用户对应表信息", range = "不能为空以及空格，页面传值,新增时生成")
    @Param(name = "create_user_id", desc = "data_source表数据源创建用户ID，代表数据是由谁创建的", range = "4位数字，新增用户时生成")
    private void importDataSource(String strTemp, String agent_ip, String agent_port, long
            user_id, long create_user_id) {
        Type type = new TypeReference<Map<String, Object>>() {
        }.getType();
        // 1.获取文件对应所有表信息的map
        Map<String, Object> collectMap = JsonUtil.toObject(strTemp, type);
        // 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
        // 3.获取数据源data_source信息并插入数据库
        Data_source dataSource = getDataSource(create_user_id, collectMap);
        // 4.将department_info表数据插入数据库
        List<Department_info> departmentInfoList = addDepartmentInfo(collectMap);
        // 5.将source_relation_dep数据插入数据库
        addSourceRelationDep(collectMap, dataSource, departmentInfoList);
        // 6.将agent_info表数据插入数据库
        addAgentInfo(agent_ip, agent_port, user_id, collectMap);
        // 7.将Agent_down_info表数据插入数据库
        addAgentDownInfo(agent_ip, agent_port, user_id, collectMap);
        // 8.将collect_job_classify表数据插入数据库
        addCollectJobClassify(create_user_id, collectMap);
        // 9.将ftp采集设置ftp_collect表数据插入数据库
        addFtpCollect(collectMap);
        // 10.将ftp已传输表ftp_transfered表数据插入数据库
        addFtpTransfered(collectMap);
        // 11.将ftp目录表ftp_folder表数据插入数据库
        addFtpFolder(collectMap);
        // 12.将对象采集设置object_collect表数据插入数据库
        addObjectCollect(collectMap);
        // 13.将对象采集对应信息object_collect_task表数据插入数据库
        addObjectCollectTask(collectMap);
        // 14.将对象采集存储设置object_storage表数据插入数据库
        addObjectStorage(collectMap);
        // 15.将对象采集结构信息object_collect_struct表数据插入数据库
        addObjectCollectStruct(collectMap);
        // 16.将数据库设置database_set表数据插入数据库
        addDatabaseSet(collectMap);
        // 17.将文件系统设置file_collect_set表数据插入数据库
        addFileCollectSet(collectMap);
        // 18.将文件源设置file_source表数据插入数据库
        addFileSource(collectMap);
        // 19.将信号文件入库信息signal_file表数据插入数据库
        addSignalFile(collectMap);
        // 20.将数据库对应的表table_info表数据插入数据库
        addTableInfo(collectMap);
        // 21.将列合并信息column_merge表数据插入数据库
        addColumnMerge(collectMap);
        // 22.将表存储信息table_storage_info表数据插入数据库
        addTableStorageInfo(collectMap);
        // 23.将表清洗参数信息table_clean表数据插入数据库
        addTableClean(collectMap);
        // 24.将表对应的字段table_column表数据插入数据库
        addTableColumn(collectMap);
        // 25.将列清洗参数信息column_clean表数据插入数据库
        addColumnClean(collectMap);
        // 26.将列拆分信息表column_split表数据插入数据库
        addColumnSplit(collectMap);
    }

    @Method(desc = "将table_storage_info表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为table_storage_info对应表数据" +
                    "3.获取表存储信息table_storage_info信息" +
                    "4.将table_storage_info表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addTableStorageInfo(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为table_storage_info对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String tsi = "tableStorageInfo";
            if (tsi.equals(entry.getKey())) {
                Type tsiType = new TypeReference<List<Table_storage_info>>() {
                }.getType();
                // 3.获取表存储信息table_storage_info信息
                List<Table_storage_info> tsiList = JsonUtil.toObject(entry.getValue().toString(), tsiType);
                // 4.将table_storage_info表数据循环入数据库
                for (Table_storage_info tableStorageInfo : tsiList) {
                    tableStorageInfo.setStorage_id(PrimayKeyGener.getNextId());
                    tableStorageInfo.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将column_merge表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为column_merge对应表数据" +
                    "3.获取列合并信息column_merge信息" +
                    "4.将column_merge表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addColumnMerge(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为column_merge对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String cm = "columnMerge";
            if (cm.equals(entry.getKey())) {
                Type cmType = new TypeReference<List<Column_merge>>() {
                }.getType();
                // 3.获取列合并信息column_merge信息
                List<Column_merge> columnMergeList = JsonUtil.toObject(entry.getValue().toString(), cmType);
                // 4.将column_merge表数据循环入数据库
                for (Column_merge columnMerge : columnMergeList) {
                    columnMerge.setCol_merge_id(PrimayKeyGener.getNextId());
                    columnMerge.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将column_split表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为column_split对应表数据" +
                    "3.获取列拆分信息表column_split信息" +
                    "4.将column_split表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addColumnSplit(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为column_split对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String cs = "columnSplit";
            if (cs.equals(entry.getKey())) {
                Type csType = new TypeReference<List<Column_split>>() {
                }.getType();
                // 3.获取列拆分信息表column_split信息
                List<Column_split> columnSplitList = JsonUtil.toObject(entry.getValue().toString(), csType);
                // 4.将column_split表数据循环入数据库
                for (Column_split columnSplit : columnSplitList) {
                    columnSplit.setCol_split_id(PrimayKeyGener.getNextId());
                    columnSplit.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将column_clean表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为column_clean对应表数据" +
                    "3.获取列清洗参数信息column_clean信息" +
                    "4.将column_clean表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addColumnClean(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为column_clean对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String cc = "columnClean";
            if (cc.equals(entry.getKey())) {
                Type ccType = new TypeReference<List<Column_clean>>() {
                }.getType();
                // 3.获取列清洗参数信息column_clean信息
                List<Column_clean> columnCleanList = JsonUtil.toObject(entry.getValue().toString(), ccType);
                // 4.将column_clean表数据循环入数据库
                for (Column_clean columnClean : columnCleanList) {
                    columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
                    columnClean.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将table_column表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为table_column对应表数据" +
                    "3.获取表对应的字段table_column信息" +
                    "4.将table_column表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addTableColumn(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为table_column对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String tc = "tableColumn";
            if (tc.equals(entry.getKey())) {
                Type tcnType = new TypeReference<List<Table_column>>() {
                }.getType();
                // 3.获取表对应的字段table_column信息
                List<Table_column> tableColumnList = JsonUtil.toObject(entry.getValue().toString(), tcnType);
                // 4.将table_column表数据循环入数据库
                for (Table_column tableColumn : tableColumnList) {
                    tableColumn.setColumn_id(PrimayKeyGener.getNextId());
                    tableColumn.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将table_clean表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为table_column对应表数据" +
                    "3.获取表对应的字段table_column信息" +
                    "4.将table_column表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addTableClean(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为table_clean对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String tc = "tableClean";
            if (tc.equals(entry.getKey())) {
                Type tcType = new TypeReference<List<Table_clean>>() {
                }.getType();
                // 3.获取表清洗参数信息table_clean信息
                List<Table_clean> tableCleanList = JsonUtil.toObject(entry.getValue().toString(), tcType);
                // 4.将table_clean表数据循环入数据库
                for (Table_clean tableClean : tableCleanList) {
                    tableClean.setTable_clean_id(PrimayKeyGener.getNextId());
                    tableClean.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将table_info表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为table_info对应表数据" +
                    "3.获取数据库对应的表table_info信息" +
                    "4.将table_info表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addTableInfo(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为table_info对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ti = "tableInfo";
            if (ti.equals(entry.getKey())) {
                Type tiType = new TypeReference<List<Table_info>>() {
                }.getType();
                // 3.获取数据库对应的表table_info信息
                List<Table_info> tableInfoList = JsonUtil.toObject(entry.getValue().toString(), tiType);
                // 4.将table_info表数据循环入数据库
                for (Table_info tableInfo : tableInfoList) {
                    tableInfo.setTable_id(PrimayKeyGener.getNextId());
                    tableInfo.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将signal_file表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为signal_file对应表数据" +
                    "3.获取信号文件入库信息signal_file信息" +
                    "4.将signal_file表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addSignalFile(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为signal_file对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String sf = "signalFile";
            if (sf.equals(entry.getKey())) {
                Type sfType = new TypeReference<List<Signal_file>>() {
                }.getType();
                // 3.获取信号文件入库信息signal_file信息
                List<Signal_file> signalFileList = JsonUtil.toObject(entry.getValue().toString(), sfType);
                // 4.将signal_file表数据循环入数据库
                for (Signal_file signalFile : signalFileList) {
                    signalFile.setSignal_id(PrimayKeyGener.getNextId());
                    signalFile.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将file_source表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为file_source对应表数据" +
                    "3.获取文件源设置file_source信息" +
                    "4.将file_source表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addFileSource(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为file_source对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String fs = "fileSource";
            if (fs.equals(entry.getKey())) {
                Type fsType = new TypeReference<List<File_source>>() {
                }.getType();
                // 3.获取文件源设置file_source信息
                List<File_source> fileSourceList = JsonUtil.toObject(entry.getValue().toString(), fsType);
                // 4.将file_source表数据循环入数据库
                for (File_source fileSource : fileSourceList) {
                    fileSource.setFile_source_id(PrimayKeyGener.getNextId());
                    fileSource.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将file_collect_set表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为file_collect_set对应表数据" +
                    "3.获取文件系统设置file_collect_set信息" +
                    "4.将file_collect_set表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addFileCollectSet(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为file_collect_set对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String fcs = "fileCollectSet";
            if (fcs.equals(entry.getKey())) {
                Type fcsType = new TypeReference<List<File_collect_set>>() {
                }.getType();
                // 3.获取文件系统设置file_collect_set信息
                List<File_collect_set> fcsList = JsonUtil.toObject(entry.getValue().toString(), fcsType);
                // 4.将file_collect_set表数据循环入数据库
                for (File_collect_set fileCollectSet : fcsList) {
                    fileCollectSet.setFcs_id(PrimayKeyGener.getNextId());
                    fileCollectSet.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将database_set表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为database_set对应表数据" +
                    "3.获取数据库设置database_set信息" +
                    "4.将database_set表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addDatabaseSet(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为database_set对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ds = "databaseSet";
            if (ds.equals(entry.getKey())) {
                Type dsType = new TypeReference<List<Database_set>>() {
                }.getType();
                // 3.获取数据库设置database_set信息
                List<Database_set> databaseSetList = JsonUtil.toObject(entry.getValue().toString(), dsType);
                // 4.将database_set表数据循环入数据库
                for (Database_set databaseSet : databaseSetList) {
                    databaseSet.setDatabase_id(PrimayKeyGener.getNextId());
                    databaseSet.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将object_collect_struct表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为object_collect_struct对应表数据" +
                    "3.获取对象采集结构信息object_collect_struct信息" +
                    "4.将object_collect_struct表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addObjectCollectStruct(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为object_collect_struct对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ocs = "objectCollectStruct";
            if (ocs.equals(entry.getKey())) {
                Type ocsType = new TypeReference<List<Object_collect_struct>>() {
                }.getType();
                // 3.获取对象采集结构信息object_collect_struct信息
                List<Object_collect_struct> ocsList = JsonUtil.toObject(entry.getValue().toString(), ocsType);
                // 4.将object_collect_struct表数据循环入数据库
                for (Object_collect_struct objectCollectStruct : ocsList) {
                    objectCollectStruct.setStruct_id(PrimayKeyGener.getNextId());
                    objectCollectStruct.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将object_storage表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为object_storage对应表数据" +
                    "3.获取tp采集设置object_storage信息" +
                    "4.将object_storage表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addObjectStorage(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为object_storage对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String os = "objectStorage";
            if (os.equals(entry.getKey())) {
                Type osType = new TypeReference<List<Object_storage>>() {
                }.getType();
                // 3.获取对象采集存储设置object_storage信息
                List<Object_storage> objectStorageList = JsonUtil.toObject(entry.getValue().toString(), osType);
                // 4.将object_storage表数据循环入数据库
                for (Object_storage objectStorage : objectStorageList) {
                    objectStorage.setObj_stid(PrimayKeyGener.getNextId());
                    objectStorage.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将object_collect_task表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为object_collect_task对应表数据" +
                    "3.获取对象采集对应信息object_collect_task信息" +
                    "4.将object_collect_task表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addObjectCollectTask(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为object_collect_task对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String oct = "objectCollectTask";
            if (oct.equals(entry.getKey())) {
                Type octType = new TypeReference<List<Object_collect_task>>() {
                }.getType();
                // 3.获取对象采集对应信息object_collect_task信息
                List<Object_collect_task> octList = JsonUtil.toObject(entry.getValue().toString(), octType);
                // 4.将object_collect_task表数据循环入数据库
                for (Object_collect_task objectCollectTask : octList) {
                    objectCollectTask.setOcs_id(PrimayKeyGener.getNextId());
                    objectCollectTask.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将object_collect表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为object_collect对应表数据" +
                    "3.获取对象采集设置object_collect信息" +
                    "4.将object_collect表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addObjectCollect(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为object_collect对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String oc = "objectCollect";
            if (oc.equals(entry.getKey())) {
                Type ocType = new TypeReference<List<Object_collect>>() {
                }.getType();
                // 3.获取对象采集设置object_collect信息
                List<Object_collect> objectCollectList = JsonUtil.toObject(entry.getValue().toString(), ocType);
                // 4 .将object_collect表数据循环入数据库
                for (Object_collect objectCollect : objectCollectList) {
                    objectCollect.setOdc_id(PrimayKeyGener.getNextId());
                    objectCollect.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将ftp_folder表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为ftp_folder对应表数据" +
                    "3.获取ftp目录表ftp_folder信息" +
                    "4.将ftp_folder表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addFtpFolder(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为ftp_folder对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ff = "ftpFolder";
            if (ff.equals(entry.getKey())) {
                Type ffType = new TypeReference<List<Ftp_folder>>() {
                }.getType();
                // 3.获取ftp目录表ftp_folder信息
                List<Ftp_folder> ftpFolderList = JsonUtil.toObject(entry.getValue().toString(), ffType);
                // 4.将ftp_folder表数据循环入数据库
                for (Ftp_folder ftpFolder : ftpFolderList) {
                    ftpFolder.setFtp_folder_id(PrimayKeyGener.getNextId());
                    ftpFolder.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将ftp_transfered表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为ftp_transfered对应表数据" +
                    "3.获取tp采集设置ftp_transfered信息" +
                    "4.将ftp_transfered表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addFtpTransfered(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为ftp_transfered对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ft = "ftpTransfered";
            if (ft.equals(entry.getKey())) {
                Type ftType = new TypeReference<List<Ftp_transfered>>() {
                }.getType();
                // 3.获取ftp已传输表ftp_transfered信息
                List<Ftp_transfered> transferList = JsonUtil.toObject(entry.getValue().toString(), ftType);
                // 4.将ftp_transfered表数据循环入数据库
                for (Ftp_transfered ftpTransfered : transferList) {
                    ftpTransfered.setFtp_transfered_id(PrimayKeyGener.getNextId());
                    ftpTransfered.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将ftp_collect表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为ftp_collect对应表数据" +
                    "3.获取tp采集设置ftp_collect信息" +
                    "4.将ftp_collect表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addFtpCollect(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为ftp_collect对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String fc = "ftpCollect";
            if (fc.equals(entry.getKey())) {
                Type fcType = new TypeReference<List<Ftp_collect>>() {
                }.getType();
                // 3.获取tp采集设置ftp_collect信息
                List<Ftp_collect> ftpCollectList = JsonUtil.toObject(entry.getValue().toString(), fcType);
                // 4.将ftp_collect表数据循环入数据库
                for (Ftp_collect ftpCollect : ftpCollectList) {
                    ftpCollect.setFtp_id(PrimayKeyGener.getNextId());
                    ftpCollect.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将collect_job_classify表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为collect_job_classify对应表数据" +
                    "3.获取采集任务分类表collect_job_classify信息" +
                    "4.将collect_job_classify表数据循环入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addCollectJobClassify(long userCollectId, Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为collect_job_classify对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String cjc = "collectJobClassify";
            if (cjc.equals(entry.getKey())) {
                Type cjcType = new TypeReference<List<Collect_job_classify>>() {
                }.getType();
                // 3.获取采集任务分类表collect_job_classify信息
                List<Collect_job_classify> cjcList = JsonUtil.toObject(entry.getValue().toString(), cjcType);
                // 4.将collect_job_classify表数据循环入数据库
                for (Collect_job_classify collectJobClassify : cjcList) {
                    collectJobClassify.setClassify_id(PrimayKeyGener.getNextId());
                    collectJobClassify.setUser_id(userCollectId);
                    collectJobClassify.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将agent_down_info表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为agent_down_info对应表数据" +
                    "3.获取agent_down_info表数据" +
                    "4.将agent_down_info表数据循环入数据库")
    @Param(name = "agent_ip", desc = "agent地址", range = "不能为空，服务器ip地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent端口", range = "1024-65535")
    @Param(name = "userCollectId", desc = "数据采集用户，代表此数据属于哪个用户", range = "10位数字")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addAgentDownInfo(String agent_ip, String agent_port, long userCollectId,
                                  Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为agent_down_info对应表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String adi = "agentDownInfo";
            if (adi.equals(entry.getKey())) {
                Type adiType = new TypeReference<List<Agent_down_info>>() {
                }.getType();
                // 3.获取agent_down_info表数据
                List<Agent_down_info> agentDownInfoList = JsonUtil.toObject(entry.getValue().toString(), adiType);
                // 4.将agent_down_info表数据循环入数据库
                for (Agent_down_info agentDownInfo : agentDownInfoList) {
                    agentDownInfo.setDown_id(PrimayKeyGener.getNextId());
                    agentDownInfo.setUser_id(userCollectId);
                    agentDownInfo.setAgent_ip(agent_ip);
                    agentDownInfo.setAgent_port(agent_port);
                    agentDownInfo.add(Dbo.db());
                }
            }
        }
    }

    @Method(desc = "将department_info表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.创建新的department_info表数据集合" +
                    "3.判断map中的key值是否为对应department_info表数据" +
                    "4.获取部门表department_info表数据" +
                    "5.将department_info表数据循环插入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    @Return(desc = "所有表数据的map的实体", range = "不为空")
    private List<Department_info> addDepartmentInfo(Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        List<Department_info> departmentInfoListNew = new ArrayList<>();
        // 2.创建新的department_info表数据集合
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 3.判断map中的key值是否为对应department_info表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String di = "departmentInfo";
            if (di.equals(entry.getKey())) {
                Type srdType = new TypeReference<List<Department_info>>() {
                }.getType();
                // 4.获取部门表department_info表数据
                List<Department_info> departmentInfoList = JsonUtil.toObject(entry.getValue().toString(), srdType);
                for (Department_info departmentInfo : departmentInfoList) {
                    departmentInfo.setDep_id(PrimayKeyGener.getNextId());
                    // 4.将department_info表数据循环插入数据库
                    departmentInfo.add(Dbo.db());
                }
            }
        }
        // 5.返回新的department_info表数据集合
        return departmentInfoListNew;
    }

    @Method(desc = "将source_relation_dep表数据插入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为对应source_relation_dep表数据" +
                    "3.获取数据源和部门关系表source_relation_dep表数据" +
                    "4.遍历department_info表数据，目的是获取最新的部门ID，dep_id" +
                    "5.将source_relation_dep表数据循环插入数据库")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    @Param(name = "departmentInfoList", desc = "存放department_info表数据的集合", range = "不为空")
    private void addSourceRelationDep(Map<String, Object> collectMap, Data_source dataSource,
                                      List<Department_info> departmentInfoList) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为对应source_relation_dep表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String srd = "sourceRelationDep";
            if (srd.equals(entry.getKey())) {
                Type srdType = new TypeReference<List<Source_relation_dep>>() {
                }.getType();
                // 3.获取数据源和部门关系表source_relation_dep表数据
                List<Source_relation_dep> srdList = JsonUtil.toObject(entry.getValue().toString(), srdType);
                for (Source_relation_dep sourceRelationDep : srdList) {
                    sourceRelationDep.setSource_id(dataSource.getSource_id());
                    // 4.遍历department_info表数据，目的是获取最新的部门ID，dep_id
                    for (Department_info departmentInfo : departmentInfoList) {
                        sourceRelationDep.setDep_id(departmentInfo.getDep_id());
                        // 5.将source_relation_dep表数据循环插入数据库
                        sourceRelationDep.add(Dbo.db());
                    }
                }
            }
        }
    }

    @Method(desc = "将data_source表数据入库并返回data_source实体对象",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.创建data_source表实体对象" +
                    "3.判断map中的key值是否为对应data_source表数据" +
                    "4.获取数据源data_source表信息" +
                    "5.将data_source表数据插入数据库" +
                    "6.返回data_source表数据对应实体对象")
    @Param(name = "userId", desc = "创建用户ID，代表此数据源由哪个用户创建", range = "不为空，创建用户时自动生成")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    @Return(desc = "data_source表实体对象", range = "data_source表实体对象", isBean = true)
    private Data_source getDataSource(long userId, Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        // 2.创建data_source表实体对象
        Data_source dataSource = new Data_source();
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 3.判断map中的key值是否为对应data_source表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ds = "dataSource";
            if (ds.equals(entry.getKey())) {
                // 4.获取数据源data_source表信息
                dataSource = JsonUtil.toObjectSafety(entry.getValue().toString(), Data_source.class).get();
                // 5.将data_source表数据插入数据库
                dataSource.setSource_id(PrimayKeyGener.getNextId());
                dataSource.setCreate_user_id(userId);
                dataSource.add(Dbo.db());
            }
        }
        return dataSource;
        // 7.返回data_source表数据对应实体对象
    }

    @Method(desc = "将agent_info表信息入数据库",
            logicStep = "1.数据权限处理方式，此方法是私有方法，不需要做权限验证" +
                    "2.判断map中的key值是否为对应agent_info表数据" +
                    "3.获取agent_info表数据" +
                    "4.循环入库agent_info")
    @Param(name = "agent_ip", desc = "agent地址", range = "不为空，服务器ip地址", example = "127.0.0.1")
    @Param(name = "agent_port", desc = "agent端口", range = "不为空，1024-65535")
    @Param(name = "userCollectId", desc = "数据采集用户，代表此数据属于哪个用户", range = "4位数字，不为空,页面传值")
    @Param(name = "collectMap", desc = "所有表数据的map的实体", range = "不能为空")
    private void addAgentInfo(String agent_ip, String agent_port, long userCollectId,
                              Map<String, Object> collectMap) {
        // 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
        for (Map.Entry<String, Object> entry : collectMap.entrySet()) {
            // 2.判断map中的key值是否为对应agent_info表数据
            // map中key的值，也是下载数据源时对应表信息封装入map的key值
            String ai = "agentInfo";
            if (ai.equals(entry.getKey())) {
                // 获取agent信息表信息
                Type aiType = new TypeReference<List<Agent_info>>() {
                }.getType();
                // 3.获取agent_info表数据
                List<Agent_info> agentInfoList = JsonUtil.toObject(entry.getValue().toString(), aiType);
                // 4.循环入库agent_info
                for (Agent_info agentInfo : agentInfoList) {
                    agentInfo.setAgent_id(PrimayKeyGener.getNextId());
                    agentInfo.setUser_id(userCollectId);
                    agentInfo.setAgent_ip(agent_ip);
                    agentInfo.setAgent_port(agent_port);
                    if (agentInfo.add(Dbo.db()) != 1) {
                        throw new BusinessException("添加agent_info表数据入库失败");
                    }
                }
            }
        }
    }

    @Method(desc = "下载文件（数据源下载功能使用，下载数据源给数据源导入提供上传文件）",
            logicStep = "1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证" +
                    "2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合" +
                    "3.获取data_source表信息集合，将data_source表信息封装入map" +
                    "4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map" +
                    "5.获取department_info表信息集合，将department_info表数据封装入map" +
                    "6.获取agent_info表信息集合，将agent_info表信息封装入map" +
                    "7.获取Agent_down_info表信息集合封装入map" +
                    "8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map" +
                    "9.ftp采集设置ftp_collect,获取ftp_collect表信息集合" +
                    "10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map" +
                    "11.ftp目录表ftp_folder,获取ftp_folder表信息集合入map" +
                    "12.对象采集设置object_collect,获取object_collect表信息集合入map" +
                    "13.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map" +
                    "14.对象采集存储设置object_storage,获取object_storage表信息集合入map" +
                    "15.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map" +
                    "16.数据库设置database_set,获取database_set表信息集合入map" +
                    "17.文件系统设置file_collect_set,获取file_collect_set表信息集合入map" +
                    "18.文件源设置file_source,获取file_source表信息集合入map" +
                    "19.信号文件入库信息signal_file,获取signal_file表信息集合入map" +
                    "20.数据库对应的表table_info,获取table_info表信息集合入map" +
                    "21.列合并信息表column_merge,获取column_merge表信息集合入map" +
                    "22.表存储信息table_storage_info,获取table_storage_info表信息集合入map" +
                    "23.表清洗参数信息table_clean,获取table_clean表信息集合入map" +
                    "24.表对应的字段table_column,获取table_column表信息集合入map" +
                    "25.列清洗参数信息column_clean,获取column_clean表信息集合入map" +
                    "26.列拆分信息表column_split,获取column_split表信息集合入map" +
                    "27.使用base64编码" +
                    "28.判断文件是否存在" +
                    "29.清空response，设置响应头，响应编码格式，控制浏览器下载该文件" +
                    "30.通过流的方式写入文件")
    @Param(name = "source_id", desc = "data_source表主键", range = "不为空以及不为空格，10位数字，新增数据源时生成")
    public void downloadFile(long source_id) {
        // 1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证
        HttpServletResponse response = ResponseUtil.getResponse();
        try (OutputStream out = response.getOutputStream()) {
            // 2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合
            Map<String, Object> collectionMap = new HashMap<>();
            // 3.获取data_source表信息集合，将data_source表信息封装入map
            addDataSourceToMap(source_id, collectionMap);
            // 4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map
            List<Source_relation_dep> sourceRelationDepList = addSourceRelationDepToMap(source_id, collectionMap);
            // 5.获取department_info表信息集合，将department_info表数据封装入map
            addDepartmentInfoToMap(collectionMap, sourceRelationDepList);
            // 6.获取agent_info表信息集合，将agent_info表信息封装入map
            List<Agent_info> agentInfoList = getAgentInfoList(source_id, collectionMap);
            // 7.获取Agent_down_info表信息集合封装入map
            addAgentDownInfoToMap(collectionMap, agentInfoList);
            // 8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
            addCollectJobClassifyToMap(collectionMap, agentInfoList);
            // 9.ftp采集设置ftp_collect,获取ftp_collect表信息集合
            Result ftpCollectResult = getFtpCollectResult(collectionMap, agentInfoList);
            // 10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
            addFtpTransferedToMap(collectionMap, ftpCollectResult);
            // 11.ftp目录表ftp_folder,获取ftp_folder表信息集合入map
            addFtpFolderToMap(collectionMap, ftpCollectResult);
            // 12.对象采集设置object_collect,获取object_collect表信息集合入map
            Result objectCollectResult = getObjectCollectResult(collectionMap, agentInfoList);
            // 13.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map
            Result objectCollectTaskResult = getObjectCollectTaskResult(collectionMap, objectCollectResult);
            // 14.对象采集存储设置object_storage,获取object_storage表信息集合入map
            addObjectStorageToMap(collectionMap, objectCollectTaskResult);
            // 15.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map
            addObjectCollectStructResultToMap(collectionMap, objectCollectTaskResult);
            // 16.数据库设置database_set,获取database_set表信息集合入map
            Result databaseSetResult = getDatabaseSetResult(collectionMap, agentInfoList);
            // 17.文件系统设置file_collect_set,获取file_collect_set表信息集合入map
            Result fileCollectSetResult = getFileCollectSetResult(collectionMap, agentInfoList);
            // 18.文件源设置file_source,获取file_source表信息集合入map
            addFileSourceToMap(collectionMap, fileCollectSetResult);
            // 19.信号文件入库信息signal_file,获取signal_file表信息集合入map
            addSignalFileToMap(collectionMap, databaseSetResult);
            // 20.数据库对应的表table_info,获取table_info表信息集合入map
            Result tableInfoResult = getTableInfoResult(collectionMap, databaseSetResult);
            // 21.列合并信息表column_merge,获取column_merge表信息集合入map
            addColumnMergeToMap(collectionMap, tableInfoResult);
            // 22.表存储信息table_storage_info,获取table_storage_info表信息集合入map
            addTableStorageInfoToMap(collectionMap, tableInfoResult);
            // 23.表清洗参数信息table_clean,获取table_clean表信息集合入map
            addTableCleanToMap(collectionMap, tableInfoResult);
            // 24.表对应的字段table_column,获取table_column表信息集合入map
            Result tableColumnResult = getTableColumnResult(collectionMap, tableInfoResult);
            // 25.列清洗参数信息 column_clean,获取column_clean表信息集合入map
            addColumnCleanToMap(collectionMap, tableColumnResult);
            // 26.列拆分信息表column_split,获取column_split表信息集合入map
            addColumnSplitToMap(collectionMap, tableColumnResult);
            // 27.使用base64编码
            byte[] bytes = Base64.getEncoder().encode(JsonUtil.toJson(collectionMap).
                    getBytes(CodecUtil.UTF8_CHARSET));
            // 28.判断文件是否存在
            if (bytes == null) {
                throw new BusinessException("此文件不存在");
            }
            // 29.清空response，设置响应编码格式,响应头，控制浏览器下载该文件
            response.reset();
            response.setCharacterEncoding(CodecUtil.UTF8_STRING);
            response.setContentType("APPLICATION/OCTET-STREAM");
            // 30.通过流的方式写入文件
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new AppSystemException(e);
        }
    }

    @Method(desc = "获取agent_info表数据集合",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.将agent_info表数据集合存入map" +
                    "3.将agent_info表数据集合返回")
    @Param(name = "sourceId", desc = "data_source表主键，source_relation_dep表外键", range = "不为空及空格，10位数字，" +
            "新增data_source表时自动生成")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是source_relation_dep表数据集合）",
            range = "key值唯一，不为空")
    @Return(desc = "返回source_relation_dep表数据的集合", range = "不为空")
    private List<Agent_info> getAgentInfoList(long source_id, Map<String, Object> collectionMap) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        List<Agent_info> agentInfoList = Dbo.queryList(Agent_info.class, "select * from "
                + Agent_info.TableName + " where  source_id = ?", source_id);
        // 2.将agent_info表数据集合存入map
        collectionMap.put("agentInfo", agentInfoList);
        // 3.将agent_info表数据集合返回
        return agentInfoList;
    }

    @Method(desc = "将department_info表数据加入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放department_info表数据的集合" +
                    "3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据" +
                    "4.将department_info表信息加入集合" +
                    "5.将department_info表数据集合入map")
    @Param(name = "collectionMap", desc = "所有表数据的map的实体", range = "不能为空")
    @Param(name = "sourceRelationDepList", desc = "存放source_relation_dep表数据集合", range = "key值唯一，不为空")
    private void addDepartmentInfoToMap(Map<String, Object> collectionMap,
                                        List<Source_relation_dep> sourceRelationDepList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放department_info表数据的集合
        List<Optional<Department_info>> departmentInfoList = new ArrayList<>();
        // 3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据
        for (Source_relation_dep sourceRelationDep : sourceRelationDepList) {
            Optional<Department_info> departmentInfo = Dbo.queryOneObject(Department_info.class
                    , "select * " + Department_info.TableName + " where dep_id=?",
                    sourceRelationDep.getDep_id());
            // 4.将department_info表信息加入集合
            departmentInfoList.add(departmentInfo);
        }
        // 5.将department_info表数据集合入map
        collectionMap.put("departmentInfo", departmentInfoList);
    }

    @Method(desc = "将source_relation_dep表数据入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.查询数据源与部门关系表信息" +
                    "3.将source_relation_dep表数据入map" +
                    "4.返回source_relation_dep表数据集合")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是source_relation_dep表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "sourceId", desc = "data_source表主键，source_relation_dep表外键",
            range = "不为空及空格，10位数字，新增data_source表时自动生成")
    @Return(desc = "返回source_relation_dep表数据的集合", range = "不为空")
    private List<Source_relation_dep> addSourceRelationDepToMap(long source_id, Map<String, Object> collectionMap) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.查询数据源与部门关系表信息
        List<Source_relation_dep> sourceRelationDepList = Dbo.queryList(Source_relation_dep.class,
                "select * from " + Source_relation_dep.TableName + " where source_id=?", source_id);
        // 3.将source_relation_dep表数据入map
        collectionMap.put("sourceRelationDep", sourceRelationDepList);
        // 4.返回source_relation_dep表数据集合
        return sourceRelationDepList;
    }

    @Method(desc = "将column_split表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放column_split表信息的集合" +
                    "3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将column_split表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_split表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
    private void addColumnSplitToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放column_split表信息的集合
        Result columnSplitResult = new Result();
        // 3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息
        for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Column_split.TableName + " where column_id = ?",
                    tableColumnResult.getLong(i, "column_id"));
            // 4.将查询到的信息封装入集合
            columnSplitResult.add(result);
        }
        // 5.将column_split表集合信息存入map
        collectionMap.put("columnSplit", columnSplitResult.toList());
    }

    @Method(desc = "将column_clean表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放column_clean表信息的集合" +
                    "3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将column_clean表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_clean表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
    private void addColumnCleanToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放column_clean表信息的集合
        Result columnCleanResult = new Result();
        // 3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息
        for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Column_clean.TableName + " where column_id=?",
                    tableColumnResult.getLong(i, "column_id"));
            // 4.将查询到的信息封装入集合
            columnCleanResult.add(result);
        }
        // 5.将column_clean表集合信息存入map
        collectionMap.put("columnClean", columnCleanResult.toList());
    }

    @Method(desc = "将table_column表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放table_column表信息的集合" +
                    "3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将table_column表集合信息存入map" +
                    "6.将table_column表结果集返回")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是table_column表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
    @Return(desc = "将table_column表结果集返回", range = "不为空")
    private Result getTableColumnResult(Map<String, Object> collectionMap, Result tableInfoResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放table_column表信息的集合
        Result tableColumnResult = new Result();
        // 3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息
        for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Table_column.TableName + " where table_id=?",
                    tableInfoResult.getLong(i, "table_id"));
            // 4.将查询到的信息封装入结果集
            tableColumnResult.add(result);
        }
        // 5.将table_column表集合信息存入map
        collectionMap.put("tableColumn", tableColumnResult.toList());
        // 6.将table_column表结果集返回
        return tableColumnResult;
    }

    @Method(desc = "将table_clean表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放table_clean表信息的集合" +
                    "3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将table_clean表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是table_clean表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "tableColumnResult", desc = "table_column表数据结果集", range = "不为空")
    private void addTableCleanToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放table_clean表信息的集合
        Result tableCleanResult = new Result();
        // 3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息
        for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Table_clean.TableName + " where table_id = ?",
                    tableInfoResult.getLong(i, "table_id"));
            // 4.将查询到的信息封装入集合
            tableCleanResult.add(result);
        }
        // 5.将table_clean表集合信息存入map
        collectionMap.put("tableClean", tableCleanResult.toList());
    }

    @Method(desc = "将object_collect_struct表数据集合存入map",
            logicStep = " 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "1.创建存放object_collect_struct表信息的集合" +
                    "2.遍历object_collect_task结果集获取table_id,通过table_id查询object_collect_struct表信息" +
                    "3.将查询到的信息封装入集合" +
                    "4.将object_collect_struct表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect_struct表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "tableInfoResult", desc = "table_info表数据结果集", range = "不为空")
    private void addTableStorageInfoToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
        Result tableStorageInfoResult = new Result();
        for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Table_storage_info.TableName +
                    " where table_id = ?", tableInfoResult.getLong(i, "table_id"));
            tableStorageInfoResult.add(result);
        }
        collectionMap.put("tableStorageInfo", tableStorageInfoResult.toList());
    }

    @Method(desc = "将column_merge表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "1.创建存放column_merge表信息的集合" +
                    "2.遍历table_info结果集获取table_id,通过table_id查询column_merge表信息" +
                    "3.将查询到的信息封装入集合" +
                    "4.将column_merge表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_merge表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "tableInfoResult", desc = "table_info表数据结果集", range = "不为空")
    private void addColumnMergeToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
        Result columnMergeResult = new Result();
        for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Column_merge.TableName + " where table_id = ?",
                    tableInfoResult.getLong(i, "table_id"));
            columnMergeResult.add(result);
        }
        collectionMap.put("columnMerge", columnMergeResult.toList());
    }

    @Method(desc = "将table_info表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放table_info表信息的集合" +
                    "3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将table_info表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是column_merge表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "databaseSetResult", desc = "database_set表数据结果集", range = "不为空")
    private Result getTableInfoResult(Map<String, Object> collectionMap, Result databaseSetResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放table_info表信息的集合
        Result tableInfoResult = new Result();
        // 3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息
        for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Table_info.TableName + " where database_id = ?",
                    databaseSetResult.getLong(i, "database_id"));
            tableInfoResult.add(result);
        }
        // 4.将查询到的信息封装入集合
        collectionMap.put("tableInfo", tableInfoResult.toList());
        // 5.将table_info表集合信息存入map
        return tableInfoResult;
    }

    @Method(desc = "将signal_file表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放signal_file表信息的集合" +
                    "3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将signal_file表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是signal_file表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "databaseSetResult", desc = "database_set表数据结果集", range = "不为空")
    private void addSignalFileToMap(Map<String, Object> collectionMap, Result databaseSetResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放signal_file表信息的集合
        Result signalFileResult = new Result();
        // 3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息
        for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Signal_file.TableName + " where database_id=?",
                    databaseSetResult.getLong(i, "database_id"));
            // 4.将查询到的信息封装入集合
            signalFileResult.add(result);
        }
        // 5.将signal_file表集合信息存入map
        collectionMap.put("signalFile", signalFileResult.toList());
    }

    @Method(desc = "将file_source表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放file_source表信息的集合" +
                    "3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将file_source表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是file_source表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "databaseSetResult", desc = "database_set表数据结果集", range = "不为空")
    private void addFileSourceToMap(Map<String, Object> collectionMap, Result file_collect_setResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放file_source表信息的集合
        Result fileSourceResult = new Result();
        // 3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息
        for (int i = 0; i < file_collect_setResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + File_source.TableName + " where fcs_id=?",
                    file_collect_setResult.getLong(i, "fcs_id"));
            // 4.将查询到的信息封装入集合
            fileSourceResult.add(result);
        }
        // 5.将file_source表集合信息存入map
        collectionMap.put("fileSource", fileSourceResult.toList());
    }

    @Method(desc = "将file_collect_set表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放file_collect_set表信息的集合" +
                    "3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将file_collect_set表集合信息存入map" +
                    "6.返回file_collect_set数据结果集")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是file_collect_set表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "agentInfoList", desc = "agent_info表数据结果集合", range = "不为空")
    private Result getFileCollectSetResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放file_collect_set表信息的集合
        Result fileCollectSetResult = new Result();
        // 3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息
        for (int i = 0; i < agentInfoList.size(); i++) {
            Result result = Dbo.queryResult("select * from " + File_collect_set.TableName + " where agent_id=?",
                    agentInfoList.get(i).getAgent_id());
            // 4.将查询到的信息封装入集合
            fileCollectSetResult.add(result);
        }
        // 5.将file_collect_set表集合信息存入map
        collectionMap.put("fileCollectSet", fileCollectSetResult.toList());
        // 6.返回file_collect_set数据结果集
        return fileCollectSetResult;
    }

    @Method(desc = "将database_set表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放database_set表信息的集合" +
                    "3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将database_set表集合信息存入map" +
                    "6.返回database_result表数据结果集")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是database_set表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "agentInfoList", desc = "agent_info表数据结果集合", range = "不为空")
    private Result getDatabaseSetResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放database_set表信息的集合
        Result databaseSetResult = new Result();
        // 3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息
        for (int i = 0; i < agentInfoList.size(); i++) {
            Result result = Dbo.queryResult("select * from " + Database_set.TableName + " where agent_id = ?",
                    agentInfoList.get(i).getAgent_id());
            // 4.将查询到的信息封装入集合
            databaseSetResult.add(result);
        }
        //5.将database_set表集合信息存入map
        collectionMap.put("databaseSet", databaseSetResult.toList());
        // 6.返回database_result表数据结果集
        return databaseSetResult;
    }

    @Method(desc = "将ftp_folder表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放ftp_folder表信息的集合" +
                    "3.遍历ftp_collect结果集获取ftp_id(ftp采集任务编号),通过ftp_id查询ftp_folder表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将ftp_folder表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是ftp_folder表数据结果集）",
            range = "不能为空,key唯一")
    @Param(name = "ftpCollectResult", desc = "ftp_collect表数据结果集", range = "不为空")
    private void addFtpFolderToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放ftp_folder表信息的集合
        Result ftpFolderResult = new Result();
        // 3.遍历ftp_collect结果集获取ftp_id(ftp采集任务编号),通过ftp_id查询ftp_folder表信息
        for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Ftp_folder.TableName + " where ftp_id = ?",
                    ftpCollectResult.getLong(i, "ftp_id"));
            // 4.将查询到的信息封装入集合
            ftpFolderResult.add(result);
        }
        // 5.将ftp_folder表集合信息存入map
        collectionMap.put("ftpFolder", ftpFolderResult.toList());
    }

    @Method(desc = "将object_collect_struct表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放object_collect_struct表信息的集合" +
                    "3.遍历object_collect_task结果集获取ocs_id(对象采集任务id),通过ocs_id查询object_collect_struct表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将object_collect_struct表集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect_struct表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "objectCollectTaskResult", desc = "object_collect_task表数据结果集", range = "不为空")
    private void addObjectCollectStructResultToMap(Map<String, Object> collectionMap,
                                                   Result objectCollectTaskResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放object_collect_struct表信息的集合
        Result objectCollectStructResult = new Result();
        // 3.遍历object_collect_task结果集获取ocs_id,通过ocs_id查询object_collect_struct表信息
        for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Object_collect_struct.TableName + " where " +
                    " ocs_id =?", objectCollectTaskResult.getLong(i, "ocs_id"));
            // 4.将查询到的信息封装入集合
            objectCollectStructResult.add(result);
        }
        // 5.将object_collect_struct表集合信息存入map
        collectionMap.put("objectCollectStruct", objectCollectStructResult.toList());
    }

    @Method(desc = "将object_storage表数据集合存入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放object_storage信息结果集" +
                    "3.遍历object_collect_task结果集获取ocs_id（对象采集任务id），通过ocs_id查询object_storage表信息" +
                    "4.将查询到的信息封装入集合" +
                    "5.将object_storage集合信息存入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_storage表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "objectCollectTaskResult", desc = "object_collect_task表数据结果集", range = "不为空")
    private void addObjectStorageToMap(Map<String, Object> collectionMap, Result objectCollectTaskResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放object_storage信息的结果集
        Result objectStorageResult = new Result();
        // 3.遍历object_collect_task结果集获取ocs_id（对象采集任务编号），通过ocs_id查询object_storage表信息
        for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Object_storage.TableName + " where ocs_id =?",
                    objectCollectTaskResult.getLong(i, "ocs_id"));
            // 4.将查询到的信息封装入集合
            objectStorageResult.add(result);
        }
        // 5.将object_storage集合信息存入map
        collectionMap.put("objectStorage", objectStorageResult.toList());
    }

    @Method(desc = "封装object_collect_task表信息入map并返回object_collect_task表信息",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建存放object_collect_task表数据的结果集对象" +
                    "3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装" +
                    "4.将object_collect_task表结果集封装入map" +
                    "5.返回object_collect_task表结果集")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect_task表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "objectCollectResult", desc = "object_collect表数据结果集", range = "不为空")
    @Return(desc = "返回object_collect_task表数据集合信息", range = "不为空")
    private Result getObjectCollectTaskResult(Map<String, Object> collectionMap, Result objectCollectResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建存放object_collect_task表数据的结果集对象
        Result objectCollectTaskResult = new Result();
        // 3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装
        for (int i = 0; i < objectCollectResult.getRowCount(); i++) {
            Result result = Dbo.queryResult("select * from " + Object_collect_task.TableName + " where " +
                    " odc_id=?", objectCollectResult.getLong(i, "odc_id"));
            objectCollectTaskResult.add(result);
        }
        // 4.将object_collect_task表结果集封装入map
        collectionMap.put("objectCollectTask", objectCollectTaskResult.toList());
        // 5.返回object_collect_task表结果集
        return objectCollectTaskResult;
    }

    @Method(desc = "封装object_collect表信息入map并返回object_collect表信息",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建封装object_collect信息的结果集对象" +
                    "3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）" +
                    "4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中" +
                    "5.将object_collect结果集封装入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是object_collect表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
    @Return(desc = "返回object_collect表数据集合信息", range = "不为空")
    private Result getObjectCollectResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建封装object_collect信息的结果集对象
        Result objectCollectResult = new Result();
        // 3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）
        for (int i = 0; i < agentInfoList.size(); i++) {
            // 4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中
            Result object_collect = Dbo.queryResult("select * from " + Object_collect.TableName + " where " +
                    "agent_id=?", agentInfoList.get(i).getAgent_id());
            objectCollectResult.add(object_collect);
        }
        // 5.将object_collect结果集封装入map
        collectionMap.put("objectCollect", objectCollectResult.toList());
        // 6.返回object_collect结果集
        return objectCollectResult;
    }

    @Method(desc = "封装agent_down_info表数据集合到map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建封装agent_down_info表信息集合" +
                    "3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）" +
                    "4.通过agent_id查询agent_down_info表信息" +
                    "5.将agent_down_info表信息放入list" +
                    "6.将agent_down_info表信息入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是agent_down_info表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
    private void addAgentDownInfoToMap(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建封装agent_down_info表信息集合
        List<Optional<Agent_down_info>> agentDownInfoList =
                new ArrayList<>();
        // 3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
        for (int i = 0; i < agentInfoList.size(); i++) {
            // 4.通过agent_id查询agent_down_info表信息
            Optional<Agent_down_info> agent_down_info = Dbo.queryOneObject(Agent_down_info.class,
                    "select * from  " + Agent_down_info.TableName + " where  agent_id = ?",
                    agentInfoList.get(i).getAgent_id());
            // 5.将agent_down_info表信息放入list
            if (agent_down_info.isPresent()) {
                agentDownInfoList.add(agent_down_info);
            }
        }
        // 6.将agent_down_info表信息入map
        collectionMap.put("agentDownInfo", agentDownInfoList);
    }

    @Method(desc = "封装data_source表数据集合到map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.根据数据源ID查询数据源data_source集合" +
                    "3.判断获取到的集合是否有数据，没有抛异常，有返回数据" +
                    "4.将data_source数据入map")
    @Param(name = "sourceId", desc = "data_source主键ID", range = "不为空,10位数字，新增数据源时生成")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是agent_down_info表数据集合）",
            range = "不能为空,key唯一")
    private void addDataSourceToMap(long source_id, Map<String, Object> collectionMap) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.根据数据源ID查询数据源data_source集合
        Optional<Data_source> dataSource = Dbo.queryOneObject(Data_source.class, "select * from "
                + Data_source.TableName + " where source_id = ?", source_id);
        // 3.判断获取到的集合是否有数据，没有抛异常，有返回数据
        if (!dataSource.isPresent()) {
            throw new BusinessException("此数据源下没有数据，sourceId = ?" + source_id);
        }
        // 4.将data_source数据入map
        collectionMap.put("dataSource", dataSource);
    }

    @Method(desc = "封装ftp_transfered表数据集合到map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建封装ftp_transfered信息的集合" +
                    "3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）" +
                    "4. 根据ftp_id查询ftp_transfered信息" +
                    "5.将ftp_transfered表数据结果集信息入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是ftp_transfered表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "ftpCollectResult", desc = "ftp_collect表数据集", range = "不为空")
    private void addFtpTransferedToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建封装ftp_transfered信息的集合
        Result ftpTransferedResult = new Result();
        // 3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
        for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
            // 4. 根据ftp_id查询ftp_transfered信息
            Result result = Dbo.queryResult("select * from " + Ftp_transfered.TableName + " where ftp_id=?",
                    ftpCollectResult.getLong(i, "ftp_id"));
            ftpTransferedResult.add(result);
        }
        // 5.将ftp_transfered表数据结果集信息入map
        collectionMap.put("ftpTransfered", ftpTransferedResult.toList());
    }

    @Method(desc = "将ftp_collect表信息封装入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建封装ftp_collect信息的集合" +
                    "3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）" +
                    "4. 根据agent_id查询ftp_collect信息" +
                    "5.将ftp_collect表信息入map" +
                    "6.返回ftp_collect表结果集信息")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是ftp_collect表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
    @Return(desc = "返回ftp_collect集合信息", range = "不为空")
    private Result getFtpCollectResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建封装ftp_collect信息的集合
        Result ftpCollectResult = new Result();
        // 3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
        for (int i = 0; i < agentInfoList.size(); i++) {
            // 4. 根据agent_id查询ftp_collect信息
            Result result = Dbo.queryResult("select * from " + Ftp_collect.TableName + " where agent_id = ?",
                    agentInfoList.get(i).getAgent_id());
            ftpCollectResult.add(result);
        }
        // 5.将ftp_collect表信息入map
        collectionMap.put("ftpCollect", ftpCollectResult.toList());
        // 6.返回ftp_collect表结果集信息
        return ftpCollectResult;
    }

    @Method(desc = "封装collect_job_classify表信息入map",
            logicStep = "1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证" +
                    "2.创建封装Collect_job_classify信息的集合" +
                    "3.遍历agent_info信息，获取agent_id(agent_info主键，Collect_job_classify外键）" +
                    "4. 根据agent_id查询Collect_job_classify信息" +
                    "5.将collect_job_classify表数据结果集入map")
    @Param(name = "collectionMap", desc = "封装数据源下载信息（这里封装的是collect_job_classify表数据集合）",
            range = "不能为空,key唯一")
    @Param(name = "agentInfoList", desc = "agent_info表数据集合", range = "不为空")
    private void addCollectJobClassifyToMap(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
        // 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
        // 2.创建封装collect_job_classify信息的集合
        Result collectJobClassifyResult = new Result();
        // 3.遍历agent_info信息，获取agent_id(agent_info主键，collect_job_classify外键）
        for (int i = 0; i < agentInfoList.size(); i++) {
            // 4.根据agent_id查询Collect_job_classify信息
            Result result = Dbo.queryResult("select * from " + Collect_job_classify.TableName + " where " +
                    " agent_id=?", agentInfoList.get(i).getAgent_id());
            collectJobClassifyResult.add(result);
        }
        // 5.将collect_job_classify表数据结果集入map
        collectionMap.put("collectJobClassify", collectJobClassifyResult.toList());
    }

}
