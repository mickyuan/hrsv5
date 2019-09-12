package hrds.b.biz.dataquery;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>类名: DataQueryWebAction</p>
 * <p>类说明: Web服务查询数据界面后台处理类</p>
 *
 * @author BY-HLL
 * @date 2019/9/3 0003 下午 03:26
 * @since JDK1.8
 */
public class DataQueryWebAction extends BaseAction {

    /**
     * <p>方法名: getFileDataSource</p>
     * <p>方法说明: 获取部门的包含文件采集任务的数据源信息</p>
     * 1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
     *
     * @param depId: 部门id
     * @return 返回数据源的map
     */
    public List<Map<String, Object>> getFileDataSource(long depId) {
        //1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
        return Dbo.queryList(" select ds.source_id,ds.datasource_name" +
                        " from " + Source_relation_dep.TableName + " srd" +
                        " join " + Data_source.TableName + " ds on srd.source_id = ds.source_id" +
                        " join " + Agent_info.TableName + " ai on ds.source_id = ai.source_id" +
                        " where srd.dep_id = ?" +
                        " AND ai.agent_type = '" + AgentType.WenJianXiTong.getCode() + "'" +
                        " GROUP BY ds.source_id,ds.datasource_name",
                depId
        );
    }

    /**
     * <p>方法名: getFileCollectionTask</p>
     * <p>方法说明: 根据数据源id获取数据源下所有文件采集任务</p>
     * 1.根据数据源id获取该数据源下所有文件采集任务的list
     *
     * @param sourceId:数据源id
     * @return 返回文件采集任务的map
     * @author BY-HLL
     */
    public List<Map<String, Object>> getFileCollectionTask(long sourceId) {
        //1.根据数据源id获取该数据源下所有文件采集任务的list
        return Dbo.queryList(" select * from " + File_collect_set.TableName + " fc" +
                        " join " + Agent_info.TableName + " ai on fc.agent_id = ai.agent_id" +
                        " where ai.source_id = ?" +
                        " AND ai.agent_type = '" + AgentType.WenJianXiTong.getCode() + "'",
                sourceId
        );
    }

    /**
     * <p>方法名: downloadFileCheck</p>
     * <p>方法说明: 检查文件的下载权限</p>
     * 1.根据登录用户id和文件id获取文件检查后的结果集
     * 2.检查申请的操作是否是下载
     * 2-1.类型是下载，检查是否具有下载权限
     *
     * @param fileId:文件id
     * @return 文件是否具有下载权限
     * @author BY-HLL
     */
    private boolean downloadFileCheck(String fileId) {
        //1.根据登录用户id和文件id获取文件检查后的结果集
        Result authResult = Dbo.queryResult("select * from " + Data_auth.TableName + " da" +
                        " join " + Source_file_attribute.TableName + " sfa" +
                        " ON sfa.file_id = da.file_id WHERE da.user_id = '" + getUserId() + "'" +
                        " AND sfa.file_id = ?" +
                        " AND da.apply_type = '" + ApplyType.XiaZai.getCode() + "'",
                fileId
        );
        if (authResult.isEmpty()) {
            throw new BusinessException(String.format("查询文件权限出错!"));
        } else {
            //2.检查申请的操作是否是下载
            if (ApplyType.XiaZai.getCode().equals(authResult.getString(0, "apply_type"))) {
                String authType = authResult.getString(0, "auth_type");
                //2-1.类型是下载，检查是否具有下载权限
                if (AuthType.YunXu.getCode().equals(authType) || AuthType.YiCi.getCode().equals(authType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>方法名: downloadFile</p>
     * <p>方法说明: 根据文件id下载文件</p>
     * 1.根据文件id检查文件是否有下载权限
     * 2.通过文件id获取文件的 byte
     * 3.拼接返回结果的 response
     * 4.创建输出流，返回结果
     * 5.下载文件完成后修改文件下载计数信息
     *
     * @param fileId:文件id
     * @param fileName:     文件名
     * @param queryKeyword: 文件关键字
     * @return 文件byte
     * @author BY-HLL
     */
    public HttpServletResponse downloadFile(String fileId, String fileName, String queryKeyword) {
        //1.根据文件id检查文件是否有下载权限
        if (!downloadFileCheck(fileId)) {
            throw new BusinessException(String.format("文件没有下载权限! name=%s", fileName));
        }
        HttpServletResponse response = null;
        OutputStream out = null;
        try {
            //2.通过文件id获取文件的 byte
            /* byte[] bye = Hyren_explorer.fileBytesFromAvro(fileId);*/
            byte[] bye = null;
            if (bye == null) {
                throw new BusinessException(String.format("文件已不存在! name=%s", fileName));
            }
            //3.拼接返回结果的 response
            response.reset();
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("APPLICATION/OCTET-STREAM");
            //4.创建输出流，返回结果
            out = response.getOutputStream();
            out.write(bye);
            out.flush();
            //5.下载文件完成后修改文件下载计数信息
            modifySortCount(fileId, queryKeyword);
        } catch (IOException e) {
            throw new AppSystemException(String.format("文件下载失败! name=%s", fileName));
        }
        return response;
    }

    /**
     * <p>方法名: modifySortCount</p>
     * <p>方法说明: 修改文件计数</p>
     * 1.通过文件id获取文件计数信息
     * 2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息
     *
     * @param fileId:文件id
     * @param queryKeyword:文件关键字
     * @author BY-HLL
     */
    public void modifySortCount(String fileId, String queryKeyword) {
        //1.通过文件id获取文件计数信息
        Result si = Dbo.queryResult("select *" +
                        " from " + Search_info.TableName + " where file_id = ? and word_name = ?",
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
                    fileId, queryKeyword);
        }
    }

    /**
     * <p>方法名: getCollectFile</p>
     * <p>方法说明: 根据登录用户获取用户收藏的文件列表</p>
     * 1.获取当前用户已经收藏的文件列表
     * 2.添加文件后缀名到返回的结果中
     *
     * @return 登录用户收藏文件列表的map
     * @author BY-HLL
     */
    public Result getCollectFile() {
        //1.获取当前用户已经收藏的文件列表
        Result userFavRs = Dbo.queryResult("SELECT * FROM " + User_fav.TableName +
                        " WHERE user_id = ? AND fav_flag = ?", IsFlag.Shi.getCode() +
                        " ORDER BY fav_id DESC LIMIT 9"
                , getUserId()
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
            }
        }
        return userFavRs;
    }

    /**
     * <p>方法名: saveCollectFileInfo</p>
     * <p>方法说明: 文件收藏或者取消收藏处理方法</p>
     *
     * @param fileId:文件id
     * @param fileName:文件名称
     * @param favId:收藏ID
     * @author BY-HLL
     */
    public void saveCollectFileInfo(String fileId, String fileName, String favId) {
        User_fav fav = new User_fav();
        fav.setUser_id(getUserId());
        fav.setOriginal_name(fileName);
        if (StringUtil.isBlank(favId)) {
            // 收藏文件
            fav.setFav_id(PrimayKeyGener.getNextId());
            fav.setFav_flag(IsFlag.Shi.toString());
            if (fav.update(Dbo.db()) != 1) {
                throw new BusinessException("收藏失败！fileId=" + fav.getFile_id());
            }
        } else {
            // 取消收藏
            fav.setFav_id(favId);
            if (fav.delete(Dbo.db()) != 1) {
                throw new BusinessException("取消收藏失败！fileId=" + fav.getFile_id());
            }
        }
    }

    /**
     * <p>方法名: fileClassifySum</p>
     * <p>方法说明: 文件采集分类总计</p>
     * 1.根据登录用户的id获取用户文件采集统计的结果
     * 2.根据统计类型设置返回的map结果集
     *
     * @return classificationSumMap: 返回的结果集map
     * @author BY-HLL
     */
    public Map<String, Integer> fileClassifySum() {
        //1.根据登录用户的id获取用户文件采集统计的结果
        List<Map<String, Object>> fcsList = Dbo.queryList("select count(1) sum_num,file_type" +
                        " from source_file_attribute sfa join agent_info ai on" +
                        " sfa.agent_id = ai.agent_id where sfa.collect_type = ? AND ai.user_id =?" +
                        " GROUP BY file_type ORDER BY file_type"
                , CollectType.WenJianCaiJi.getCode(), getUserId()
        );
        //2.根据统计类型设置返回的map结果集
        Map<String, Integer> classificationSumMap = new HashMap<>();
        if (!fcsList.isEmpty()) {
            Integer sumNum = 0;
            for (Map<String, Object> fcsMap : fcsList) {
                sumNum = (Integer) fcsMap.get("sum_num");
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
}
