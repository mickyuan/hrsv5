package hrds.b.biz.dataquery;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.ResponseUtil;
import hrds.b.biz.dataquery.tools.FileOperations;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@DocClass(desc = "Web服务查询数据界面后台处理类", author = "BY-HLL", createdate = "2019/9/3 0003 下午 03:26")
public class DataQueryAction extends BaseAction {

    @Method(desc = "获取部门的包含文件采集任务的数据源信息",
            logicStep = "数据可访问权限处理方式: 根据登录用户的 user_id 进行权限检查" +
                    "1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list"
    )
    @Return(desc = "存放包含文件采集任务的数据源的集合", range = "无限制")
    public List<Map<String, Object>> getFileDataSource() {
        //数据可访问权限处理方式: 根据 Agent_info 的 user_id 进行权限检查
        //1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
        return Dbo.queryList(
                " select ds.source_id,ds.datasource_name" +
                        " from " + Source_relation_dep.TableName + " srd" +
                        " join " + Data_source.TableName + " ds on srd.source_id = ds.source_id" +
                        " join " + Agent_info.TableName + " ai on ds.source_id = ai.source_id" +
                        " where srd.dep_id = ? AND ai.agent_type = ? AND ai.user_id = ?" +
                        " GROUP BY ds.source_id,ds.datasource_name",
                getUser().getDepId(), AgentType.WenJianXiTong.getCode(), getUserId()
        );
    }

    @Method(desc = "获取数据源下所有文件采集任务",
            logicStep = "数据可访问权限处理方式: 根据登录用户的 user_id 进行权限检查" +
                    "1.根据数据源id获取该数据源下所有文件采集任务的list"
    )
    @Param(name = "sourceId", desc = "数据源id", range = "long类型值，不为空的数字，通过生成规则自动生成")
    @Return(desc = "存放数据源下文件采集任务的集合", range = "无限制")
    public List<Map<String, Object>> getFileCollectionTask(long sourceId) {
        //数据可访问权限处理方式: 根据 Agent_info 的 user_id 进行权限检查
        //1.根据数据源id获取该数据源下所有文件采集任务的list
        return Dbo.queryList(
                " select * from " + File_collect_set.TableName + " fc" +
                        " join " + Agent_info.TableName + " ai on fc.agent_id = ai.agent_id" +
                        " where ai.source_id = ? AND ai.agent_type = ? and ai.user_id = ?",
                sourceId, AgentType.WenJianXiTong.getCode(), getUserId()
        );
    }

    @Method(desc = "检查文件的下载权限",
            logicStep = "数据可访问权限处理方式: 根据登录用户的 user_id 进行权限检查" +
                    "1.根据登录用户id和文件id获取文件检查后的结果集" +
                    "2.检查申请的操作是否是下载" +
                    "2-1.类型是下载，检查是否具有下载权限"
    )
    @Param(name = "fileId", desc = "文件id", range = "String类型值的唯一id（32位），不包含特殊字符")
    @Return(desc = "boolean类型", range = "true 或者 false")
    private boolean downloadFileCheck(String fileId) {
        //数据可访问权限处理方式: 根据 Data_auth 的 user_id 进行权限检查
        //1.根据登录用户id和文件id获取文件检查后的结果集
        Result authResult = Dbo.queryResult(
                "select * from " + Data_auth.TableName + " da" +
                        " join " + Source_file_attribute.TableName + " sfa" +
                        " ON sfa.file_id = da.file_id WHERE da.user_id = ?" +
                        " AND sfa.file_id = ? AND da.apply_type = ?",
                getUserId(), fileId, ApplyType.XiaZai.getCode()
        );
        if (authResult.isEmpty()) {
            throw new BusinessException("下载文件不存在! fileId=" + fileId);
        }
        //2.检查申请的操作是否是下载
        ApplyType applyType = ApplyType.ofEnumByCode(authResult.getString(0, "apply_type"));
        if (ApplyType.XiaZai == applyType) {
            AuthType authType = AuthType.ofEnumByCode(authResult.getString(0, "auth_type"));
            //2-1.类型是下载，检查是否具有下载权限
            return AuthType.YunXu == authType || AuthType.YiCi == authType;
        }
        return false;
    }

    @Method(desc = "下载文件",
            logicStep = "数据可访问权限处理方式: 无数据库操作不需要权限检查" +
                    "1.根据文件id检查文件是否有下载权限" +
                    "2.通过文件id获取文件的 byte" +
                    "3.写出输出流，返回结果" +
                    "4.下载文件完成后修改文件下载计数信息"
    )
    @Param(name = "fileId", desc = "文件id", range = "String类型值的唯一id（32位），不包含特殊字符")
    @Param(name = "fileName", desc = "文件名", range = "String类型值，无输入限制")
    @Param(name = "queryKeyword", desc = "文件查询关键字", range = "String类型值，无输入限制", nullable = true)
    public void downloadFile(String fileId, String fileName, String queryKeyword) {
        //数据可访问权限处理方式: 无数据库操作不需要权限检查
        //1.根据文件id检查文件是否有下载权限
        if (!downloadFileCheck(fileId)) {
            throw new BusinessException("文件没有下载权限! fileName=" + fileName);
        }
        try (OutputStream out = ResponseUtil.getResponse().getOutputStream()) {
            //2.通过文件id获取文件的 byte
            byte[] bye = FileOperations.getFileBytesFromAvro(fileId);
            if (bye == null) {
                throw new BusinessException("文件已不存在! fileName=" + fileName);
            }
            //3.写入输出流，返回结果
            out.write(bye);
            out.flush();
            //4.下载文件完成后修改文件下载计数信息
            modifySortCount(fileId, queryKeyword);
        } catch (IOException e) {
            throw new AppSystemException("文件下载失败! fileName=" + fileName);
        }
    }

    @Method(desc = "修改文件计数",
            logicStep = "数据可访问权限处理方式: 修改文件计数信息，不需要检查" +
                    "1.通过文件id获取文件计数信息" +
                    "2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息"
    )
    @Param(name = "fileId", desc = "文件id", range = "String类型值的唯一id（32位），不包含特殊字符")
    @Param(name = "queryKeyword", desc = "文件查询关键字", range = "String类型值，无输入限制")
    private void modifySortCount(String fileId, String queryKeyword) {
        //数据可访问权限处理方式: 修改文件计数信息，不需要检查
        //1.通过文件id获取文件计数信息
        Result siResult = Dbo.queryResult(
                "select * from " + Search_info.TableName + " where file_id = ? and word_name = ?",
                fileId, queryKeyword
        );
        Search_info searchInfo = new Search_info();
        searchInfo.setWord_name(queryKeyword);
        //2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息
        if (siResult.isEmpty()) {
            long nextId = PrimayKeyGener.getNextId();
            searchInfo.setFile_id(fileId);
            searchInfo.setSi_id(nextId);
            if (searchInfo.add(Dbo.db()) != 1) {
                throw new BusinessException("添加文件计数信息失败！data=" + searchInfo);
            }
        } else {
            searchInfo.setFile_id(fileId);
            int execute = Dbo.execute("update search_info set si_count = si_count+1 where" +
                            " file_id = ? and word_name = ?",
                    fileId, queryKeyword);
            if (execute != 1) {
                throw new BusinessException("修改文件计数信息失败！data" + searchInfo);
            }
        }
    }

    @Method(desc = "保存文件收藏方法",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id做权限校验" +
                    "1.根据文件id获取文件名" +
                    "2.根据文件id和文件名收藏该文件"
    )
    @Param(name = "fileId", desc = "文件id", range = "String类型值的唯一id（32位），不包含特殊字符")
    public void saveFavoriteFile(String fileId) {
        //数据可访问权限处理方式: 根据 User_fav 表的 user_id做权限校验
        //1.根据文件id获取文件名
        Optional<Source_file_attribute> sourceFileAttribute = Dbo.queryOneObject(Source_file_attribute.class,
                "select original_name from " + Source_file_attribute.TableName + " where" +
                        " file_id = ?", fileId);
        //2.根据文件id和文件名收藏该文件
        if (!sourceFileAttribute.isPresent()) {
            throw new BusinessException("文件不存在！fileId=" + fileId);
        }
        User_fav userFav = new User_fav();
        userFav.setFav_id(PrimayKeyGener.getNextId());
        userFav.setOriginal_name(sourceFileAttribute.get().getOriginal_name());
        userFav.setFile_id(fileId);
        userFav.setUser_id(getUserId());
        userFav.setFav_flag(IsFlag.Shi.getCode());
        if (userFav.add(Dbo.db()) != 1) {
            throw new BusinessException("收藏失败！fileId=" + userFav.getFile_id());
        }
    }

    @Method(desc = "删除已收藏文件方法",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id 做权限校验" +
                    "1.根据收藏文件id删除收藏记录"
    )
    @Param(name = "favId", desc = "收藏文件id", range = "long类型值的唯一id（10位）")
    public void cancelFavoriteFile(Long favId) {
        //数据可访问权限处理方式: 根据表的 user_id做权限校验
        //1.根据收藏文件id删除收藏记录
        User_fav userFav = new User_fav();
        userFav.setFav_id(favId);
        int deleteUserFavNum = Dbo.execute(
                "delete from " + User_fav.TableName + " where fav_id=? and user_id=?", favId, getUserId());
        if (deleteUserFavNum != 1) {
            if (deleteUserFavNum == 0) {
                throw new BusinessException("表中不存在该条记录！favId=" + favId);
            }
            throw new BusinessException("取消收藏失败！favId=" + favId);
        }
    }

    @Method(desc = "文件采集分类统计",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id 做权限校验" +
                    "1.根据登录用户的id获取用户文件采集统计的结果" +
                    "2.根据统计类型,设置返回的map结果集"
    )
    @Return(desc = "存放采集分类统计结果的集合", range = "无限制")
    public List<Map<String, Object>> getFileClassifySum() {
        //数据可访问权限处理方式: 根据表的 user_id做权限校验
        //1.根据登录用户的id获取用户文件采集统计的结果
        List<Map<String, Object>> fcsList = Dbo.queryList("select count(1) sum_num,file_type" +
                        " from " + Source_file_attribute.TableName + " sfa join " + Agent_info.TableName + " ai" +
                        " on sfa.agent_id = ai.agent_id where sfa.collect_type = ? AND ai.user_id = ?" +
                        " GROUP BY file_type ORDER BY file_type",
                AgentType.WenJianXiTong.getCode(), getUserId()
        );
        //2.根据统计类型设置返回的map结果集
        List<Map<String, Object>> classificationSumList = new ArrayList<>();
        if (!fcsList.isEmpty()) {
            Map<String, Object> classificationSumMap = new HashMap<>();
            for (Map<String, Object> fcsMap : fcsList) {
                classificationSumMap.put(FileType.ofValueByCode((String) fcsMap.get("file_type")),
                        fcsMap.get("sum_num"));
                classificationSumMap.put("file_type", FileType.ofValueByCode((String) fcsMap.get("file_type")));
                classificationSumMap.put("sum_num", fcsMap.get("sum_num"));
                classificationSumList.add(classificationSumMap);
            }
        }
        return classificationSumList;
    }

    @Method(desc = "最近7天文件采集统计",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id 做权限校验" +
                    "根据登录用户的id获取用户最近7天的文件采集信息")
    @Return(desc = "存放统计结果的集合", range = "无限制")
    public List<Map<String, Object>> getSevenDayCollectFileSum() {
        //根据登录用户的id获取用户最近7天的文件采集信息
        List<Map<String, Object>> scfList = Dbo.queryList("select count(1) count,storage_date" +
                        " from " + Source_file_attribute.TableName + " sfa join" +
                        " " + Agent_info.TableName + " ai on sfa.agent_id = ai.agent_id" +
                        " where sfa.collect_type = ? AND ai.user_id = ? GROUP BY storage_date" +
                        " ORDER BY storage_date desc LIMIT 7",
                AgentType.WenJianXiTong.getCode(), getUserId());
        //3.根据查询结果设置返回的map结果集
        List<Map<String, Object>> sevenDayCollectFileSumList = new ArrayList<>();
        if (!scfList.isEmpty()) {
            Map<String, Object> sevenDayCollectFileSumMap = new HashMap<>();
            for (Map<String, Object> scfMap : scfList) {
                String collectDate = (String) scfMap.get("storage_date");
                int collectSum = Integer.parseInt(scfMap.get("count").toString());
                if (sevenDayCollectFileSumMap.containsKey("collectDate")) {
                    sevenDayCollectFileSumMap.put("collectDate", collectDate);
                    sevenDayCollectFileSumMap.put("collectSum", collectSum + collectSum);
                } else {
                    sevenDayCollectFileSumMap.put("collectDate", collectDate);
                    sevenDayCollectFileSumMap.put("collectSum", collectSum);
                }
                sevenDayCollectFileSumList.add(sevenDayCollectFileSumMap);
            }
        }
        return sevenDayCollectFileSumList;
    }

    @Method(desc = "最近的3次文件采集信息",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id 做权限校验" +
                    "1.查询最近次数小于1则显示默认最近3次,查询最近次数大于30天则显示最近30次,否则取传入的查询次数" +
                    "2.根据登录用户的id获取用户最近3次的文件采集信息" +
                    "3.根据查询结果设置返回的map结果集"
    )
    @Param(name = "timesRecently", desc = "查询最近采集的次数", range = "int类型值 1-30 默认为3", valueIfNull = "3")
    @Return(desc = "存放统计结果的集合", range = "无限制")
    public List<Map<String, Object>> getLast3FileCollections(int timesRecently) {
        //1.如果查询最近次数小于1则显示默认最近3次，查询最近次数大于30天则显示最近30次，否则取传入的查询次数
        timesRecently = Math.max(3, timesRecently);
        timesRecently = Math.min(timesRecently, 3);
        //2.根据登录用户的id获取用户最近3次的文件采集信息
        List<Map<String, Object>> l3fcList = Dbo.queryList("select storage_date, storage_time," +
                        " max(concat(storage_date,storage_time)) max_date, count(1) count," +
                        " fcs.fcs_name from " + Source_file_attribute.TableName + " sfa join" +
                        " " + File_collect_set.TableName + " fcs on sfa.collect_set_id = fcs.fcs_id" +
                        " join agent_info ai on ai.agent_id = fcs.agent_id" +
                        " where collect_type = ? and ai.user_id = ?" +
                        " GROUP BY storage_date,storage_time,fcs.fcs_name" +
                        " ORDER BY max_date desc limit ?",
                AgentType.WenJianXiTong.getCode(), getUserId(), timesRecently
        );
        //3.根据查询结果设置返回的map结果集
        List<Map<String, Object>> last3FileCollectionsMapList = new ArrayList<>();
        for (Map<String, Object> l3fcMap : l3fcList) {
            Map<String, Object> last3FileCollectionsMap = new HashMap<>(30);
            //采集日期
            String collectDate = (String) l3fcMap.get("storage_date");
            //采集时间
            String collectTime = (String) l3fcMap.get("storage_time");
            //采集总数
            Integer collectSum = Integer.valueOf(l3fcMap.get("count").toString());
            //采集任务名称
            String collectName = (String) l3fcMap.get("fcs_name");
            //拼接待返回数据的map
            last3FileCollectionsMap.put("collectDate", collectDate);
            last3FileCollectionsMap.put("collectTime", collectTime);
            last3FileCollectionsMap.put("collectName", collectName);
            last3FileCollectionsMap.put("collectSum", collectSum);
            last3FileCollectionsMapList.add(last3FileCollectionsMap);
        }
        return last3FileCollectionsMapList;
    }

    @Method(desc = "自定义查询条件获取采集文件的信息",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id 做权限校验" +
                    "获取文件的申请和审核信息" +
                    "设置文件各类申请详情汇总" +
                    "设置文件申请统计汇总")
    @Param(name = "sourceId", desc = "数据源id", range = "long类型值，10位长度", nullable = true)
    @Param(name = "fcsId", desc = "采集任务id", range = "long类型值，10位长度", nullable = true)
    @Param(name = "fileType", desc = "文件采集类型", range = "文件类型代码值", nullable = true)
    @Param(name = "startDate", desc = "查询开始日期", range = "日期格式 yyyy-mm-dd", nullable = true)
    @Param(name = "endDate", desc = "查询结束日期", range = "日期格式 yyyy-mm-dd", nullable = true)
    @Return(desc = "存放自定义查询结果的数据集合", range = "无限制")
    public Map<String, Object> getConditionalQuery(String sourceId, String fcsId, String fileType, String startDate,
                                                   String endDate) {
        //获取文件的申请和审核信息
        Result file_rs = conditionalQuery(sourceId, fcsId, fileType, startDate, endDate);
        Map<String, Object> conditionalQueryMap = new HashMap<>();
        conditionalQueryMap.put("file_rs", file_rs.toList());
        Map<String, Object> fadMap = getFileApplicationDetails();
        //设置文件各类申请详情汇总
        int myDownloadRequest = 0;
        int myPostApplication = 0;
        int myApplicationRecord = 0;
        int myRenameRequest = 0;
        int myViewRequest = 0;
        Result applyRequestRs = (Result) fadMap.get("applyRequestRs");
        if (!applyRequestRs.isEmpty()) {
            for (int i = 0; i < applyRequestRs.getRowCount(); i++) {
                ApplyType applyType = ApplyType.ofEnumByCode(applyRequestRs.getString(i, "apply_type"));
                // 我的下载申请
                if (ApplyType.XiaZai == applyType) {
                    myDownloadRequest = applyRequestRs.getIntDefaultZero(i, "apply_type");
                }
                // 我的发布申请
                else if (ApplyType.FaBu == applyType) {
                    myPostApplication = applyRequestRs.getIntDefaultZero(i, "apply_type");
                }
                // 我的查看申请
                else if (ApplyType.ChaKan == applyType) {
                    myViewRequest = applyRequestRs.getIntDefaultZero(i, "apply_type");
                }
                // 重命名申请
                else if (ApplyType.ChongMingMing == applyType) {
                    myRenameRequest = applyRequestRs.getIntDefaultZero(i, "apply_type");
                }
            }
        }
        //设置文件申请统计汇总
        Result countRs = (Result) fadMap.get("countRs");
        if (!countRs.isEmpty()) {
            myApplicationRecord = countRs.getRowCount();
        }
        conditionalQueryMap.put("myDownloadRequest", myDownloadRequest);
        conditionalQueryMap.put("myPostApplication", myPostApplication);
        conditionalQueryMap.put("myApplicationRecord", myApplicationRecord);
        conditionalQueryMap.put("myViewRequest", myViewRequest);
        conditionalQueryMap.put("myRenameRequest", myRenameRequest);
        return conditionalQueryMap;
    }

    @Method(desc = "申请信息处理",
            logicStep = "1.根据文件id获取该文件信息" +
                    "2.根据文件id先清除该文件的数据权限信息,然后保存改文件的认证信息")
    @Param(name = "fileId", desc = "文件id", range = "String类型字符,长度最长40,该值唯一",
            example = "12f48c4f-bebd-4f19-b38d-a161929fb350")
    @Param(name = "applyType", desc = "申请类型", range = "1:查看,2:下载,3,发布,4:重命名")
    @Return(desc = "申请是否成功", range = "boolean类型")
    public boolean applicationProcessing(String fileId, String applyType) {
        boolean applyState = true;
        //1.根据文件id获取该文件信息
        Optional<Source_file_attribute> fileRs = Dbo.queryOneObject(Source_file_attribute.class,
                "SELECT * FROM source_file_attribute WHERE file_id=?", fileId);
        if (!fileRs.isPresent()) {
            throw new BusinessException("申请的文件不存在！fileId=" + fileId);
        }
        //2.根据文件id先清除该文件的数据权限信息,然后保存改文件的认证信息
        DboExecute.deletesOrThrow("删除文件权限信息失败!",
                "DELETE FROM DATA_AUTH WHERE file_id = ? AND apply_type = ? AND user_id = ? AND dep_id = ?",
                fileId, applyType, getUserId(), getUser().getDepId());
        Data_auth dataAuth = new Data_auth();
        dataAuth.setDa_id(PrimayKeyGener.getNextId());
        dataAuth.setApply_date(DateUtil.getSysDate());
        dataAuth.setApply_time(DateUtil.getSysTime());
        dataAuth.setAuth_type(AuthType.ShenQing.toString());
        dataAuth.setAgent_id(fileRs.get().getAgent_id());
        dataAuth.setSource_id(fileRs.get().getSource_id());
        dataAuth.setCollect_set_id(fileRs.get().getCollect_set_id());
        if ((dataAuth.add(Dbo.db()) != 1)) {
            throw new BusinessException("申请文件失败！fileId=" + fileId);
        }
        return applyState;
    }

    @Method(desc = "查看文件",
            logicStep = "1.根据文件id获取该文件信息" +
                    "2.如果文件查看权限是一次,看过之后取消权限")
    @Param(name = "fileId", desc = "文件id", range = "String类型字符,长度最长40,该值唯一",
            example = "12f48c4f-bebd-4f19-b38d-a161929fb350")
    @Param(name = "fileType", desc = "文件类型", range = "查看的文件类型,FileType")
    @Return(desc = "文件信息集合", range = "无限制")
    public Map<String, Object> viewFile(String fileId, String fileType) {
        //1.根据文件id获取该文件信息
        Map<String, Object> viewFileMap = new HashMap<>();
        viewFileMap.put("viewFileMap", FileOperations.getFileInfoByFileId(fileId));
        viewFileMap.put("fileType", fileType);
        //2.如果文件查看权限是一次,看过之后取消权限
        FileOperations.updateViewFilePermissions(fileId);
        return viewFileMap;
    }

    @Method(desc = "获取用户申请文件信息",
            logicStep = "1.根据申请类型获取用户申请文件的详细信息")
    @Param(name = "apply_type", desc = "申请类型", range = "1:查看,2:下载,3,发布,4:重命名")
    @Return(desc = "申请文件信息的Map数组", range = "无限制")
    public Map<String, Object> getApplyData(String apply_type) {
        //1.根据申请类型获取用户申请文件的详细信息
        Map<String, Object> applyDataMap = new HashMap<>();
        Object[] sourceIdsObj = Dbo.queryOneColumnList("select source_id from data_source").toArray();
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select da.*,sfa.original_name,sfa.file_size,sfa.file_type,sfa.file_suffix from data_auth da" +
                " left join source_file_attribute sfa on da.file_id = sfa.file_id where da.user_id = ? and" +
                " da.auth_type=? and da.apply_type = ?");
        asmSql.addParam(getUserId());
        asmSql.addParam(AuthType.ShenQing.getCode());
        asmSql.addParam(apply_type);
        asmSql.addORParam("sfa.source_id", sourceIdsObj);
        List<Map<String, Object>> apply_rs = Dbo.queryList(asmSql.sql(), asmSql.params());
        applyDataMap.put("apply_rs", apply_rs);
        applyDataMap.put("apply_type", apply_type);
        return applyDataMap;
    }

    @Method(desc = "取消用户申请文件",
            logicStep = "1.根据申请id获取用户申请文件的详细信息" +
                    "2.根据申请id删除数据申请信息")
    @Param(name = "da_id", desc = "文件申请信息id", range = "long,主键唯一")
    public void cancelApply(long da_id) {
        //1.根据申请id获取用户申请文件的详细信息
        Optional<Data_auth> daRs = Dbo.queryOneObject(Data_auth.class,
                "select * from " + Data_auth.TableName + " where da_id = ? and auth_type = ?",
                da_id, AuthType.ShenQing.getCode());
        if (!daRs.isPresent()) {
            throw new BusinessException("取消申请的文件已不存在！da_id=" + da_id);
        }
        //2.根据申请id删除数据申请信息
        DboExecute.deletesOrThrow("取消申请的文件失败!",
                "DELETE from " + Data_auth.TableName + " where da_id = ?", da_id);
    }

    @Method(desc = "获取用户申请记录数据",
            logicStep = "1.自定义条件获取用户申请记录" +
                    "2.根据申请id删除数据申请信息")
    @Param(name = "original_name", desc = "文件名", range = "String字符串,无限制", nullable = true)
    @Param(name = "apply_date", desc = "文件申请日期", range = "日期格式 yyyyMMdd", nullable = true)
    @Param(name = "apply_type", desc = "文件申请类型", range = "1:查看,2:下载,3,发布,4:重命名", nullable = true)
    @Param(name = "auth_type", desc = "文件申请状态", range = "1:允许,2不允许,3:一次,0:不允许", nullable = true)
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "用户申请记录Map", range = "无限制")
    public Map<String, Object> myApplyRecord(String original_name, String apply_date, String apply_type,
                                             String auth_type, int currPage, int pageSize) {
        //1.获取用户申请记录信息
        Map<String, Object> myApplyRecordMap = new HashMap<>();
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql(" select da.*,sfa.* from source_file_attribute sfa LEFT JOIN (" +
                " select MAX(concat(apply_date,apply_time)) applytime,file_id,apply_type" +
                " from data_auth where user_id=?");
        asmSql.addParam(getUserId());
        asmSql.addLikeParam("apply_date", apply_date);
        if (StringUtil.isNotBlank(apply_type)) {
            asmSql.addSql(" and apply_type = ?").addParam(apply_type);
        }
        if (StringUtil.isNotBlank(auth_type)) {
            asmSql.addSql(" and auth_type = ?").addParam(auth_type);
        }
        asmSql.addSql(" GROUP BY file_id,apply_type) a JOIN data_auth da on a.applytime = concat(da.apply_date," +
                " da.apply_time) ON da.file_id = sfa.file_id where da.apply_type !=''");
        asmSql.addLikeParam("original_name", original_name);
        asmSql.addSql(" ORDER BY da.apply_date DESC,da.apply_time DESC");
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Map<String, Object>> myApplyRecordRs = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
        myApplyRecordMap.put("myApplyRecordRs", myApplyRecordRs);
        myApplyRecordMap.put("totalSize", page.getTotalSize());
        return myApplyRecordMap;
    }

    @Method(desc = "自定义查询条件获取采集文件的信息",
            logicStep = "数据可访问权限处理方式: 根据表的 user_id 做权限校验" +
                    "1.查看待查询的数据源是否属于登录用户所在部门" +
                    "2.是当前登录用户所属部门的数据源" +
                    "2-1.根据查询条件返回查询结果" +
                    "2-1-1.根据选择的数据源查询" +
                    "2-1-2.根据选择的文件采集任务查询" +
                    "2-1-3.根据选择的开始日期查询" +
                    "2-1-4.根据选择的结束日期查询" +
                    "2-2.如果没选择任何查询条件或者所有参数为空,则查询最近的文件采集信息" +
                    "3.非本部门发布的数据" +
                    "3-1.获取文件采集的数据列表" +
                    "3-2.获取数据的访问权限和审核信息"
    )
    @Param(name = "sourceId", desc = "数据源id", range = "long类型值，10位长度")
    @Param(name = "fcsId", desc = "采集任务id", range = "long类型值，10位长度")
    @Param(name = "fileType", desc = "文件采集类型", range = "文件类型代码值")
    @Param(name = "startDate", desc = "查询开始日期", range = "日期格式 yyyyMMdd")
    @Param(name = "endDate", desc = "查询结束日期", range = "日期格式 yyyyMMdd")
    @Return(desc = "存放自定义查询结果的Result", range = "无限制")
    private Result conditionalQuery(String sourceId, String fcsId, String fileType, String startDate, String endDate) {
        //1.查看待查询的数据源是否属于登录用户所在部门
        Source_file_attribute sourceFileAttribute = new Source_file_attribute();
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select dep_id from " + Source_relation_dep.TableName + " srd left join " +
                Collect_case.TableName + " cc on srd.source_id = cc.source_id where srd.dep_id = ?")
                .addParam(getUser().getDepId());
        if (StringUtils.isNotBlank(sourceId)) {
            sourceFileAttribute.setSource_id(sourceId);
            asmSql.addSql(" AND srd.source_id = ?").addParam(sourceFileAttribute.getSource_id());
        }
        Result queryResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
        //2.是当前登录用户所属部门的数据源
        Result searchResult = new Result();
        if (!queryResult.isEmpty()) {
            //2-1.根据查询条件返回查询结果
            asmSql.clean();
            sourceFileAttribute.setCollect_type(AgentType.WenJianXiTong.getCode());
            asmSql.addSql("select * from " + Source_file_attribute.TableName + " WHERE collect_type = ?")
                    .addParam(sourceFileAttribute.getCollect_type());
            //是否是第一次执行，所有参数为空，代表是第一次执行，则走2-2.如果没选择任何查询条件,则查询最近的文件采集信息
            boolean isFirst = true;
            //2-1-1.根据选择的数据源查询
            if (StringUtils.isNotBlank(sourceId)) {
                asmSql.addSql(" AND source_id = ?").addParam(sourceFileAttribute.getSource_id());
                //2-1-2.根据选择的文件采集任务查询
                if (StringUtils.isNotBlank(fcsId)) {
                    sourceFileAttribute.setCollect_set_id(fcsId);
                    asmSql.addSql(" AND collect_set_id = ?").addParam(sourceFileAttribute.getCollect_set_id());
                }
                isFirst = false;
            }
            //2-1-3.根据选择的开始日期查询
            if (StringUtils.isNotBlank(startDate)) {
                sourceFileAttribute.setStorage_date(startDate);
                asmSql.addSql(" AND storage_date >= ?").addParam(sourceFileAttribute.getStorage_date());
                isFirst = false;
            }
            //2-1-4.根据选择的结束日期查询
            if (StringUtil.isNotBlank(endDate)) {
                sourceFileAttribute.setStorage_date(endDate);
                asmSql.addSql(" AND storage_date <= ?").addParam(sourceFileAttribute.getStorage_date());
                isFirst = false;
            }
            //2-2.如果没选择任何查询条件或者所有参数为空,则查询最近的文件采集信息
            if (isFirst) {
                asmSql.addSql(" AND storage_date = (SELECT max(storage_date) FROM source_file_attribute sfa JOIN " +
                        "agent_info ai ON sfa.agent_id = ai.agent_id WHERE ai.user_id = ? )")
                        .addParam(getUserId());
            }
            asmSql.addSql(" order by file_id");
            searchResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
            if (!searchResult.isEmpty()) {
                for (int i = 0; i < searchResult.getRowCount(); i++) {
                    searchResult.setObject(i, "file_size", FileUtil.fileSizeConversion(
                            searchResult.getLongDefaultZero(i, "file_size")));
                    searchResult.setObject(i, "storage_date",
                            DateUtil.parseStr2DateWith8Char(searchResult.getString(i, "storage_date")));
                    searchResult.setObject(i, "storage_time",
                            DateUtil.parseStr2TimeWith6Char(searchResult.getString(i, "storage_time")));
                    searchResult.setObject(i, "title",
                            searchResult.getString(i, "original_name"));
                    searchResult.setObject(i, "original_name",
                            searchResult.getString(i, "original_name"));
                    searchResult.setObject(i, "is_others_apply", IsFlag.Fou.getCode());
                    Result daResult = Dbo.queryResult(
                            "select * from data_auth WHERE file_id = ? and user_id = ? and " +
                                    " auth_type != ?", searchResult.getString(i, "file_id"), getUserId(),
                            AuthType.BuYunXu.getCode());
                    if (!daResult.isEmpty()) {
                        StringBuilder authType = new StringBuilder();
                        StringBuilder applyType = new StringBuilder();
                        for (int j = 0; j < daResult.getRowCount(); j++) {
                            authType.append(daResult.getString(j, "auth_type")).append(',');
                            applyType.append(daResult.getString(j, "apply_type")).append(',');
                        }
                        authType.delete(authType.length() - 1, authType.length());
                        applyType.delete(applyType.length() - 1, applyType.length());
                        searchResult.setObject(i, "auth_type", authType.toString());
                        searchResult.setObject(i, "apply_type", applyType.toString());
                    }
                }
            }
        }
        //3.非本部门发布的数据
        else {
            //3-1.获取文件采集的数据列表
            asmSql.clean();
            asmSql.addSql("select * from source_file_attribute where collect_type = ?")
                    .addParam(AgentType.WenJianXiTong.getCode());
            if (!StringUtil.isEmpty(fileType) && FileType.All != FileType.ofEnumByCode(fileType)) {
                asmSql.addSql("AND file_type = ?").addParam(fileType);
            }
            asmSql.addSql(" order by file_id");
            Result sfaRsAll = Dbo.queryResult(asmSql.sql(), asmSql.params());
            //3-2.获取数据的访问权限和审核信息
            if (!sfaRsAll.isEmpty()) {
                String[] authTypes = {AuthType.YunXu.getCode(), AuthType.YiCi.getCode()};
                for (int i = 0; i < sfaRsAll.getRowCount(); i++) {
                    asmSql.clean();
                    asmSql.addSql("select * from data_auth WHERE file_id = ? and  apply_type = ? ")
                            .addParam(sfaRsAll.getString(i, "file_id"))
                            .addParam(ApplyType.FaBu.getCode())
                            .addORParam("auth_type", authTypes);
                    Result daResult = Dbo.queryResult(asmSql.sql(), asmSql.params());
                    if (!daResult.isEmpty()) {
                        StringBuilder authType = new StringBuilder();
                        StringBuilder applyType = new StringBuilder();
                        for (int j = 0; j < daResult.getRowCount(); j++) {
                            authType.append(daResult.getString(i, "auth_type")).append(',');
                            applyType.append(daResult.getString(i, "apply_type")).append(',');
                            authType.delete(authType.length() - 1, authType.length());
                            applyType.delete(applyType.length() - 1, applyType.length());
                            daResult.setObject(j, "auth_type", authType.toString());
                            daResult.setObject(j, "apply_type", applyType.toString());
                            daResult.setObject(j, "file_id",
                                    sfaRsAll.getString(i, "file_id"));
                            daResult.setObject(j, "collect_type",
                                    sfaRsAll.getString(i, "collect_type"));
                            daResult.setObject(j, "hbase_name",
                                    sfaRsAll.getString(i, "hbase_name"));
                            daResult.setObject(j, "title",
                                    sfaRsAll.getString(i, "original_name"));
                            daResult.setObject(j, "original_name",
                                    sfaRsAll.getString(i, "original_name"));
                            daResult.setObject(j, "storage_date", DateUtil.parseStr2DateWith8Char(
                                    sfaRsAll.getString(i, "storage_date")));
                            daResult.setObject(j, "storage_time", DateUtil.parseStr2TimeWith6Char(
                                    sfaRsAll.getString(i, "storage_time")));
                            daResult.setObject(j, "file_size", FileUtil.fileSizeConversion(
                                    sfaRsAll.getLongDefaultZero(i, "file_size")));
                            daResult.setObject(j, "file_suffix",
                                    sfaRsAll.getString(i, "file_suffix"));
                            daResult.setObject(j, "is_others_apply", IsFlag.Shi.getCode());
                            searchResult.add(daResult);
                        }
                    }
                }
            }
        }
        return searchResult;
    }

    @Method(desc = "获取文件申请详情",
            logicStep = "数据可访问权限处理方式: 根据登录用户的 user_id 校验数据访问权限" +
                    "1.初始化待返回数据的Map" +
                    "1.不同类型文件的申请汇总" +
                    "1.文件的申请汇总"
    )
    @Return(desc = "文件申请类型统计信息", range = "无限制")
    private Map<String, Object> getFileApplicationDetails() {
        //1.初始化待返回数据的Map
        Map<String, Object> fileApplicationDetails = new HashMap<>();
        Object[] sourceIdsObj = Dbo.queryOneColumnList("select source_id from data_source").toArray();
        //2.各类型文件的申请汇总
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT apply_type,count(apply_type) count from data_auth da JOIN source_file_attribute sfa" +
                " ON da.file_id = sfa.file_id where USER_ID = ? and auth_type = ?");
        asmSql.addParam(getUserId());
        asmSql.addParam(AuthType.ShenQing.getCode());
        asmSql.addORParam("sfa.source_id", sourceIdsObj);
        asmSql.addSql(" GROUP BY apply_type");
        Result applyRequestRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
        fileApplicationDetails.put("applyRequestRs", applyRequestRs);
        //3.文件的申请汇总
        asmSql.clean();
        asmSql.addSql("select  MAX(apply_date || apply_time) applytime,da.file_id,apply_type from data_auth da JOIN " +
                "source_file_attribute sfa ON da.file_id = sfa.file_id where user_id=? ");
        asmSql.addParam(getUserId());
        asmSql.addORParam("sfa.source_id", sourceIdsObj);
        asmSql.addSql(" GROUP BY da.file_id,apply_type");
        Result countRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
        fileApplicationDetails.put("countRs", countRs);
        return fileApplicationDetails;
    }
}
