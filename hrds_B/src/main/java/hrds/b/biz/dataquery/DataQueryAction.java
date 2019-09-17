package hrds.b.biz.dataquery;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>类名: DataQueryAction</p>
 * <p>类说明: Web服务查询数据界面后台处理类</p>
 *
 * @author BY-HLL
 * @date 2019/9/3 0003 下午 03:26
 * @since JDK1.8
 */
public class DataQueryAction extends BaseAction {
    /**
     * <p>方法名: getFileDataSource</p>
     * <p>方法说明: 获取部门的包含文件采集任务的数据源信息</p>
     * 1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
     *
     * @param depId Long
     *              含义：部门id
     *              取值范围: long类型值
     * @return List<Map < String, Object>>
     * 含义: 数据源查询结果的list集合
     * 取值范围: 不为NULL
     */
    public List<Map<String, Object>> getFileDataSource(long depId) {
        //数据可访问权限处理方式: 根据 Agent_info 的 user_id 进行权限检查
        //1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
        return Dbo.queryList(
                " select ds.source_id,ds.datasource_name" +
                        " from " + Source_relation_dep.TableName + " srd" +
                        " join " + Data_source.TableName + " ds on srd.source_id = ds.source_id" +
                        " join " + Agent_info.TableName + " ai on ds.source_id = ai.source_id" +
                        " where srd.dep_id = ? AND ai.agent_type = ? AND ai.user_id = ?" +
                        " GROUP BY ds.source_id,ds.datasource_name",
                depId, AgentType.WenJianXiTong.getCode(), getUserId()
        );
    }

    /**
     * <p>方法名: getFileCollectionTask</p>
     * <p>方法说明: 根据数据源id获取数据源下所有文件采集任务</p>
     * 1.根据数据源id获取该数据源下所有文件采集任务的list
     *
     * @param sourceId 含义: 数据源id
     *                 取值范围: long类型值
     * @return List<Map < String, Object>>
     * 含义: 返回文件采集任务的map
     * 取值范围: 不为NULL
     */
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

    /**
     * <p>方法名: downloadFileCheck</p>
     * <p>方法说明: 检查文件的下载权限</p>
     * 1.根据登录用户id和文件id获取文件检查后的结果集
     * 2.检查申请的操作是否是下载
     * 2-1.类型是下载，检查是否具有下载权限
     *
     * @param fileId 含义: 文件id
     *               取值范围: String类型值的UUID（32位）
     * @return boolean
     * 含义: 文件是否具有下载权限
     * 取值范围: true 或者 false
     */
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
            throw new BusinessException("查询文件权限出错! fileId=" + fileId);
        }
        //2.检查申请的操作是否是下载
        String authResultApplyType = authResult.getString(0, "apply_type");
        if (ApplyType.XiaZai.getCode().equals(authResultApplyType)) {
            String authType = authResult.getString(0, "auth_type");
            //2-1.类型是下载，检查是否具有下载权限
            if (AuthType.YunXu.getCode().equals(authType) || AuthType.YiCi.getCode().equals(authType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>方法名: downloadFile</p>
     * <p>方法说明: 根据文件id下载文件</p>
     * 1.根据文件id检查文件是否有下载权限
     * 2.通过文件id获取文件的 byte
     * 3.写入输出流，返回结果
     * 4.下载文件完成后修改文件下载计数信息
     *
     * @param fileId       含义:文件id
     *                     取值范围: String类型值的UUID（32位）
     * @param fileName     含义: 文件名
     *                     取值范围: String类型值
     * @param queryKeyword 含义:文件查询关键字
     *                     取值范围: 没有限制
     */
    public void downloadFile(String fileId, String fileName, String queryKeyword) {
        //1.根据文件id检查文件是否有下载权限
        if (!downloadFileCheck(fileId)) {
            throw new BusinessException("文件没有下载权限! fileName=" + fileName);
        }
        try (OutputStream out = ResponseUtil.getResponse().getOutputStream()) {
            //2.通过文件id获取文件的 byte
            byte[] bye = getFileBytesFromAvro(fileId);
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

    /**
     * <p>方法名: getFileBytesFromAvro</p>
     * <p>方法说明: 占位方法</p>
     *
     * @param
     */
    private byte[] getFileBytesFromAvro(String fileId) {
        byte[] bye = fileId.getBytes();
        return bye;
    }

    /**
     * <p>方法名: modifySortCount</p>
     * <p>方法说明: 修改文件计数</p>
     * 1.通过文件id获取文件计数信息
     * 2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息
     *
     * @param fileId       含义:文件id
     *                     取值范围: long类型值
     * @param queryKeyword 含义:查询关键字
     *                     取值范围: String类型值
     */
    public void modifySortCount(String fileId, String queryKeyword) {
        //1.通过文件id获取文件计数信息
        Result si = Dbo.queryResult(
                "select * from " + Search_info.TableName + " where file_id = ? and word_name = ?",
                fileId, queryKeyword
        );
        Search_info searchInfo = new Search_info();
        //2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息
        if (si.isEmpty()) {
            String nextId = PrimayKeyGener.getNextId();
            searchInfo.setFile_id(fileId);
            searchInfo.setSi_id(nextId);
            searchInfo.setWord_name(queryKeyword);
            if (searchInfo.add(Dbo.db()) != 1) {
                throw new BusinessException("添加文件计数信息失败！data=" + searchInfo);
            }
        } else {
            searchInfo.setFile_id(fileId);
            searchInfo.setWord_name(queryKeyword);
            Dbo.execute("update search_info set si_count = si_count+1 where" +
                            " file_id = ? and word_name = ?",
                    fileId, queryKeyword);  //FIXME update成功失败的判断哪去了
        }
    }

    /**
     * <p>方法名: getCollectFile</p>
     * <p>方法说明: 根据登录用户获取用户收藏的文件列表,返回结果显示最近9条收藏</p>
     * 1.获取当前登录的用户已经收藏的文件列表
     * 2.添加文件后缀名到返回的结果中
     *
     * @return Result
     * 含义: 用户收藏文件的结果集
     * 取值含义：登录用户收藏文件列表的
     */
    public Result getCollectFile() {
        //1.获取当前用户已经收藏的文件列表
        Result userFavRs = Dbo.queryResult("SELECT * FROM " + User_fav.TableName +
                        " WHERE user_id = ? AND fav_flag = '" + IsFlag.Shi.getCode() + "'" +
                        " ORDER BY fav_id DESC LIMIT 9",  //FIXME 把9作为参数传入，且设置为默认值9
                getUserId()
        );
        if (!userFavRs.isEmpty()) {
            String fileSufix = null;
            for (int i = 0; i < userFavRs.getRowCount(); i++) {
                String fileName = userFavRs.getString(i, "original_name");
                if (fileName.lastIndexOf(".") != -1) {
                    fileSufix = fileName.substring(fileName.lastIndexOf(".") + 1);
                } else {
                    fileSufix = "other";
                }
                //2.添加文件后缀名到返回的结果中
                userFavRs.setObject(i, "fileSufix", fileSufix);
                //FIXME 为什么要添加后缀名到结果集中？
                // 另外：如果不添加的话，可以返回 List<User_fav> 这样更易于下面的操作，直接写 foreach 进行迭代即可
            }
        }
        return userFavRs;
    }

    /**
     * <p>方法名: saveCollectFileInfo</p>
     * <p>方法说明: 文件收藏或者取消收藏处理方法</p>
     *
     * @param fileId       String
     *                     含义: 文件id
     *                     取值范围: String类型值
     * @param originalName String
     *                     含义: 文件名称
     *                     取值范围: String类型值
     * @param favId        long
     *                     含义: 收藏文件的id
     *                     取值范围: long类型值
     */
    public void saveCollectFileInfo(String fileId, String originalName, String favId) {
        User_fav userFav = new User_fav();
        if (StringUtil.isBlank(favId)) {
            // 收藏文件
            userFav.setFav_id(PrimayKeyGener.getNextId());
            userFav.setOriginal_name(originalName);
            userFav.setFile_id(fileId);
            userFav.setUser_id(getUserId());
            userFav.setFav_flag(IsFlag.Shi.toString());
            if (userFav.update(Dbo.db()) != 1) {
                throw new BusinessException("收藏失败！fileId=" + userFav.getFile_id());
            }
        } else {
            // 取消收藏
            userFav.setFav_id(favId);
            if (userFav.delete(Dbo.db()) != 1) {
                throw new BusinessException("取消收藏失败！fileId=" + userFav.getFile_id());
            }
        }
    }

    /**
     * <p>方法名: getFileClassifySum</p>
     * <p>方法说明: 文件采集分类统计</p>
     * 1.根据登录用户的id获取用户文件采集统计的结果
     * 2.根据统计类型设置返回的map结果集
     *
     * @return classificationSumMap
     * 含义 返回的结果集map
     * 取值范围: 不为NULL
     * @author BY-HLL
     */
    public Map<String, Integer> getFileClassifySum() {
        //1.根据登录用户的id获取用户文件采集统计的结果
        List<Map<String, Object>> fcsList = Dbo.queryList("select count(1) sum_num,file_type" +
                        " from source_file_attribute sfa join agent_info ai" +
                        " on sfa.agent_id = ai.agent_id" +
                        " where sfa.collect_type = '" + CollectType.WenJianCaiJi.getCode() + "'" +
                        " AND ai.user_id = ? GROUP BY file_type ORDER BY file_type",
                getUserId()
        );
        //2.根据统计类型设置返回的map结果集
        Map<String, Integer> classificationSumMap = new HashMap<>();
        if (!fcsList.isEmpty()) {
            Integer sumNum = 0;
            for (Map<String, Object> fcsMap : fcsList) {
                sumNum = (Integer) fcsMap.get("sum_num");
                //FIXME 下面这么多，用一句不就行了吗：classificationSumMap.put(FileType.ofValueByCode((String)fcsMap.get("file_type")), sumNum)
                if (FileType.TuPian.getCode().equals(fcsMap.get("file_type"))) {
                    //文件类型为图片
                    classificationSumMap.put(FileType.TuPian.getValue(), sumNum);
                } else if (FileType.ShiPin.getCode().equals(fcsMap.get("file_type"))) {
                    //文件类型为视频
                    classificationSumMap.put(FileType.ShiPin.getValue(), sumNum);
                } else if (FileType.YinPin.getCode().equals(fcsMap.get("file_type"))) {
                    //文件类型为音频
                    classificationSumMap.put(FileType.YinPin.getValue(), sumNum);
                } else if (FileType.Other.getCode().equals(fcsMap.get("file_type"))) {
                    //文件类型为其他
                    classificationSumMap.put(FileType.Other.getValue(), sumNum);
                } else {
                    //文件类型为文档
                    sumNum += sumNum;
                    classificationSumMap.put(FileType.WenDang.getValue(), sumNum);
                }
            }
        }
        return classificationSumMap;
    }

    /**
     * <p>方法名: getSevenDayCollectFileSum</p>
     * <p>方法说明: 7天内文件采集统计</p>
     * 1.根据登录用户的id获取用户最近7天的文件采集信息
     * 2.根据查询结果设置返回的map结果集
     *
     * @return sevenDayCollectFileSumMap
     * 含义 返回的结果集map
     * 取值范围: 不为NULL
     * @author BY-HLL
     */
    public Map<String, List<Object>> getSevenDayCollectFileSum() {
        //1.根据登录用户的id获取用户最近7天的文件采集信息
        List<Map<String, Object>> scfList = Dbo.queryList("select count(1) count,storage_date" +
                        " from source_file_attribute sfa join agent_info ai " +
                        " on sfa.agent_id = ai.agent_id" +
                        " where sfa.collect_type = '" + CollectType.WenJianCaiJi.getCode() + "'" +
                        " AND ai.user_id = ? GROUP BY storage_date ORDER BY storage_date desc" +
                        " LIMIT 7",
                getUserId()
        );
        //2.根据查询结果设置返回的map结果集
        Map<String, List<Object>> sevenDayCollectFileSumMap = new HashMap<>();
        if (!scfList.isEmpty()) {
            String collectDate = null;
            Integer collectSum = 0;
            for (Map<String, Object> scfMap : scfList) {
                //采集日期
                collectDate = (String) scfMap.get("storage_date");
                if (sevenDayCollectFileSumMap.containsKey("collectDate")) {
                    sevenDayCollectFileSumMap.get("collectDate").add(collectDate);
                } else {
                    List<Object> list = new ArrayList<>();
                    list.add(collectDate);
                    sevenDayCollectFileSumMap.put("collectDate", list);
                }
                //采集总数
                collectSum = (Integer) scfMap.get("count");
                if (sevenDayCollectFileSumMap.containsKey("collectSum")) {
                    sevenDayCollectFileSumMap.get("collectSum").add(collectSum);
                } else {
                    List<Object> list = new ArrayList<>();
                    list.add(collectSum);
                    sevenDayCollectFileSumMap.put("collectSum", list);
                }
            }
            Collections.reverse(sevenDayCollectFileSumMap.get("collectDate"));
            Collections.reverse(sevenDayCollectFileSumMap.get("collectSum"));
        }
        return sevenDayCollectFileSumMap;
    }

    /**
     * <p>方法名: getLast3FileCollections</p>
     * <p>方法说明: 获取最近的3次文件采集信息</p>
     * 1.根据登录用户的id获取用户最近3次的文件采集信息
     * 2.根据查询结果设置返回的map结果集
     *
     * @return sevenDayCollectFileSumMap
     * 含义 返回的结果集map
     * 取值范围: 不为NULL
     * @author BY-HLL
     */
    public Map<String, Object> getLast3FileCollections() {
        //1.根据登录用户的id获取用户最近3次的文件采集信息
        List<Map<String, Object>> l3fcList = Dbo.queryList("select storage_date, storage_time," +
                        " max(concat(storage_date,storage_time)) max_date, count(1) count," +
                        " fcs.fcs_name from source_file_attribute sfa join file_collect_set fcs on" +
                        " sfa.collect_set_id = fcs.fcs_id join agent_info ai" +
                        " on ai.agent_id = fcs.agent_id" +
                        " where collect_type = '" + CollectType.WenJianCaiJi.getCode() + "'" +
                        " and ai.user_id = ? GROUP BY storage_date,storage_time,fcs.fcs_name" +
                        " ORDER BY max_date desc limit 3",
                getUserId()
        );
        //2.根据查询结果设置返回的map结果集
        String collectDate = null;
        Integer collectSum = 0;
        String collectName = null;
        Map<String, Object> last3FileCollectionsMap = new HashMap<>();
        for (Map<String, Object> l3fcMap : l3fcList) {
            //采集日期
            collectDate = (String) l3fcMap.get("storage_date");
            //采集总数
            collectSum = (Integer) l3fcMap.get("collectSum");
            //采集任务名称
            collectName = (String) l3fcMap.get("fcs_name");
            last3FileCollectionsMap.put("collectDate", collectDate);
            last3FileCollectionsMap.put("collectSum", collectSum);
            last3FileCollectionsMap.put("collectName", collectName);
        }
        return last3FileCollectionsMap;
    }
}
