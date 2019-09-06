package hrds.b.biz.dataquery;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.ApplyType;
import hrds.commons.codes.AuthType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>类名: DataQueryWebAction</p>
 * <p>类说明: Web服务查询数据界面后台处理类</p>
 * @author BY-HLL
 * @date 2019/9/3 0003 下午 03:26
 * @since JDK1.8
 */
public class DataQueryWebAction extends BaseAction {

    /**
     * <p>方法名: getFileDataSource</p>
     * <p>方法说明: 根据部门id获取该部门的包含文件采集任务的数据源信息</p>
     * <p>编写人员: BY-HLL <p>
     * @param dep_id: 登录用户所在的部门id
     * @return 返回数据源的map
     */
    public Map<String, Object> getFileDataSource(Long dep_id) {
        Result dataSourceRs = Dbo.queryResult(" select ds.source_id,ds.datasource_name" +
                " from "+ Source_relation_dep.TableName +" srd join "+ Data_source.TableName +" ds" +
                " on srd.source_id = ds.source_id" +
                " join "+ Agent_info.TableName +" ai on ds.source_id = ai.source_id" +
                " where srd.dep_id = ? AND ai.agent_type = '"+AgentType.WenJianXiTong.getCode()+"'" +
                " GROUP BY ds.source_id,ds.datasource_name",
                dep_id
        );
        Map<String, Object> result = new HashMap<>(2);
        result.put("fileDataSourceList", dataSourceRs.toList());
        return result;
    }

    /**
     * <p>方法名: getFileCollectionTask</p>
     * <p>方法说明: 根据数据源id获取数据源下所有文件采集任务</p>
     * @param sourceId:数据源id
     * @return 返回文件采集任务的map
     */
    public Map<String, Object> getFileCollectionTask(String sourceId) {
        Result fct = Dbo.queryResult(" select *" +
                " from "+File_collect_set.TableName+" fc join "+Agent_info.TableName+" ai" +
                " on fc.agent_id = ai.agent_id" +
                " where ai.source_id = ?", sourceId +
                " AND ai.agent_type = ?", AgentType.WenJianXiTong.toString()
        );
        Map<String, Object> result = new HashMap<>(2);
        result.put("fileCollectionTaskList", fct.toList());
        return result;
    }

    /**
     * <p>方法名: downloadFileCheck</p>
     * <p>方法说明: 检查文件的下载权限</p>
     * @author BY-HLL
     * @param sysUser:登录的用户对象
     * @param fileId:文件id
     * @return isAuth:(0:是 1:否)
     */
    public String downloadFileCheck(@RequestBean Sys_user sysUser, String fileId) {
        //没有下载权限
        String applyType = ApplyType.XiaZai.toString();
        String isAuth = "1";
        Result authResult = Dbo.queryResult("select *" +
                " from "+Data_auth.TableName+" da join "+Source_file_attribute.TableName+" sfa" +
                " ON sfa.file_id = da.file_id WHERE da.user_id = ?", sysUser.getUser_id().toString() +
                " AND sfa.file_id = ?", fileId +" AND da.apply_type = ?", applyType
        );
        if ( !authResult.isEmpty() ) {
            //检查申请的是否为下载类型
            if ( applyType.equals(authResult.getString(0, "apply_type")) ) {
                String authType = authResult.getString(0, "auth_type");
                //如果是就查看是否有下载权限
                if( AuthType.YunXu.toString().equals(authType) || AuthType.YiCi.toString().equals(authType) ) {
                    isAuth = "0";
                }
            }
        } else {
            throw new BusinessException(String.format("查询文件权限出错!"));
        }
        return isAuth;
    }

    /**
     * <p>方法名: downloadFile</p>
     * <p>方法说明: 根据文件id下载文件</p>
     * @author BY-HLL
     * @param fileId:文件id
     * @param fileName: 文件名
     * @param queryKeyword: 文件关键字
     * @return 文件byte
     */
    public HttpServletResponse downloadFile(String fileId, String fileName, String queryKeyword) {
        HttpServletResponse response = null ;
        OutputStream out = null;
        try {
            /* byte[] bye = Hyren_explorer.fileBytesFromAvro(fileId);*/
            byte[] bye = null ;
            if (bye == null) {
                throw new BusinessException(String.format("文件已不存在! name=%s", fileName));
            }
            // 清空response
            response.reset();
            // 设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("APPLICATION/OCTET-STREAM");
            // 创建输出流
            out = response.getOutputStream();
            out.write(bye);
            out.flush();
            // 下载文件完成后修改文件下载计数
            modifySortCount(fileId,queryKeyword);
        }catch(IOException e) {
            throw new BusinessException(String.format("文件下载失败!"));
        }
        return response;
    }

    /**
     * <p>方法名: modifySortCount</p>
     * <p>方法说明: 修改文件计数</p>
     * @author BY-HLL
     * @param fileId:文件id
     * @param queryKeyword:文件关键字
     */
    public void modifySortCount(String fileId, String queryKeyword) {
        Result si = Dbo.queryResult("select *" +
                " from "+Search_info.TableName+" where file_id = ?", fileId +
                " and word_name = ?", queryKeyword
        );
        Search_info searchInfo = new Search_info();
        if ( si.isEmpty() ) {
            String nextId = PrimayKeyGener.getNextId();
            searchInfo.setFile_id(fileId);
            searchInfo.setSi_id(nextId);
            searchInfo.setWord_name(queryKeyword);
            if(searchInfo.add(Dbo.db())!=1){
                throw new BusinessException("添加文件计数信息失败！data="+searchInfo);
            }
        } else {
            searchInfo.setFile_id(fileId);
            searchInfo.setWord_name(queryKeyword);
            if(searchInfo.update(Dbo.db())!=1){
                throw new BusinessException("更新文件计数失败！data="+searchInfo);
            }
        }
    }

    /**
     * <p>方法名: getCollectFile</p>
     * <p>方法说明: 根据登录用户获取用户收藏的文件列表</p>
     * @author BY-HLL
     * @param sysUser:登录用户对象
     * @return 登录用户收藏文件列表的map
     */
    public Map<String, Object> getCollectFile(Sys_user sysUser) {
        Result userFavRs ;
        userFavRs = Dbo.queryResult("SELECT *" +
                " FROM "+User_fav.TableName+" WHERE user_id = ?", sysUser.getUser_id() +
                " AND fav_flag = ?", IsFlag.Shi.toString() +
                " ORDER BY fav_id DESC LIMIT 9"
        );
        if( !userFavRs.isEmpty() ) {
            String fileSufix = null;
            for(int i = 0; i < userFavRs.getRowCount(); i++) {
                String fileName = userFavRs.getString(i, "original_name");
                if( fileName.lastIndexOf(".") != -1 ) {
                    fileSufix = fileName.substring(fileName.lastIndexOf(".") + 1);
                }else {
                    fileSufix = "other";
                }
                userFavRs.setObject(i, "fileSufix", fileSufix);
            }
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("fileCollectionTaskList", userFavRs.toList());
        return result;
    }

    /**
     * <p>方法名: saveCollectFileInfo</p>
     * <p>方法说明: 文件收藏或者取消收藏处理方法</p>
     * @author BY-HLL
     * @param userId:登录用户id
     * @param fileId:文件id
     * @param fileName:文件名称
     * @param favId:收藏ID
     */
    public void saveCollectFileInfo(String userId, String fileId, String fileName, String favId) {
        User_fav fav = new User_fav();
        fav.setUser_id(userId);
        fav.setFile_id(fileId);
        fav.setOriginal_name(fileName);
        if( StringUtil.isBlank(favId) ) {
            // 收藏文件
            fav.setFav_id(PrimayKeyGener.getNextId());
            fav.setFav_flag(IsFlag.Shi.toString());
            if ( fav.update(Dbo.db())!=1 ) {
                throw new BusinessException("收藏失败！fileId="+fav.getFile_id());
            }
        }
        else {
            // 取消收藏
            fav.setFav_id(favId);
            if ( fav.update(Dbo.db())!=1 ) {
                throw new BusinessException("取消收藏失败！fileId="+fav.getFile_id());
            }
        }
    }
}
