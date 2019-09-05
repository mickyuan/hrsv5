package hrds.b.biz.datasource;

import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Source_relation_dep;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 数据源增删改，导入、下载类
 *
 * @author mine
 * @date 2019-09-03 16:44:25
 */
public class DataSourceAction extends BaseAction {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 新增/编辑数据源
     * <p>
     * 1.判断数据源编号是否为空，为空则为新增，不为空则为编辑
     * 2.新增前查询数据源编号是否已存在，存在则抛异常，不存在就新增
     * 3.保存或更新数据源信息
     * 4.如果是编辑先删除数据源与部门关系
     * 5.保存或更新数据源与部门关系信息
     *
     * @param dataSource 数据源编号
     */
    public void saveDataSource(@RequestBean Data_source dataSource, String dep_id) {

        // 1.判断数据源编号是否为空
        if (dataSource.getSource_id() == null) {
            // 新增
            dataSource.setSource_id(PrimayKeyGener.getNextId());
            dataSource.setUser_id(ActionUtil.getUser().getUserId());
            // 2.新增前查询数据源编号是否已存在
            Result result = Dbo.queryResult("select datasource_number from " + Data_source.TableName +
                    "  where datasource_number=?", dataSource.getDatasource_number());
            if (!result.isEmpty()) {
                // 数据源编号重复
                throw new BusinessException("数据源编号重复");
            }
            // 3.保存数据源信息
            if (dataSource.add(Dbo.db()) != 1) {
                // 新增保存失败
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }

        } else {
            // 编辑
            // 3.更新数据源信息
            if (dataSource.update(Dbo.db()) != 1) {
                // 编辑保存失败
                throw new BusinessException(ExceptionEnum.DATA_UPDATE_ERROR);
            }

            // 4.先删除数据源与部门关系信息
            int num = Dbo.execute("delete from " + Source_relation_dep.TableName +
                    " where source_id=?", dataSource.getSource_id());
            if (num != 1) {
                throw new BusinessException(ExceptionEnum.DATA_DELETE_ERROR);
            }
        }
        // 5.保存或更新数据源与部门关系信息
        saveSourceRelationDep(dataSource.getSource_id(), dep_id);
    }

    /**
     * 保存数据源与部门关系表信息
     * <p>
     * 1.循环保存或更新数据源与部门关系信息
     *
     * @param source_id 数据源编号
     * @param dep_id    部门编号
     * @return
     */
    public void saveSourceRelationDep(Long source_id, String dep_id) {
        // 建立数据源与部门关系信息
        Source_relation_dep srd = new Source_relation_dep();
        srd.setSource_id(source_id);
        String[] depIds = dep_id.split(",");
        // 1.循环保存或更新数据源与部门关系信息
        for (String depId : depIds) {
            srd.setDep_id(Long.parseLong(depId));
            if (srd.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
    }

    /**
     * 编辑前根据数据源编号查询数据源及数据源与部门关系信息
     * <p>
     * 1.判断该数据源下是否有数据，没有抛异常，有则返回查询结果
     *
     * @param source_id 数据源编号
     * @return
     */
    public Result searchDataSource(Long source_id) {
        // 1.判断该数据源下是否有数据，没有抛异常，有则返回查询结果
        Result result = Dbo.queryResult("select ds.*,srd.dep_id from data_source ds " +
                "join source_relation_dep srd on ds.source_id=srd.source_id where ds.source_id = ?", source_id);
        if (result.isEmpty()) {
            // 该数据源下数据为空(此为编辑情况下数据不能为空）
            throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
        }
        // 不为空，返回查询结果
        return result;

    }

    /**
     * 删除数据源与部门关系表信息
     * <p>
     * 1.删除数据源与部门关系表信息，失败就抛异常，否则就正常删除
     *
     * @param source_id 数据源编号
     */
    public void deleteSourceRelationDep(Long source_id) {
        // 1.删除数据源与部门关系表信息，
        int num = Dbo.execute("delete from " + Source_relation_dep.TableName + " where source_id=?", source_id);
        if (num != 1) {
            // 删除失败
            throw new BusinessException(ExceptionEnum.DATA_DELETE_ERROR);
        }
    }

    /**
     * 删除数据源信息
     * <p>
     * 1.先查询该datasource下是否还有agent,有不能删除，没有，可以删除
     * 2.删除data_source表信息，删除失败就抛异常，否则正常删除
     *
     * @param source_id 数据源编号
     */
    public void deleteDataSource(Long source_id) {

        // 1。先查询该datasource下是否还有agent
        Result result = Dbo.queryResult("SELECT * FROM agent_info WHERE source_id=? ", source_id);
        if (!result.isEmpty()) {
            // 此数据源下还有agent，不能删除
            throw new BusinessException("此数据源下还有agent，不能删除");
        }

        // 2.删除data_source表信息
        int num = Dbo.execute("delete from " + Data_source.TableName + " where source_id=?", source_id);
        if (num != 1) {
            // 删除失败
            throw new BusinessException(ExceptionEnum.DATA_DELETE_ERROR);
        }
        // 3.删除source_relation_dep信息
        deleteSourceRelationDep(source_id);

    }

    /**
     * 导入数据源
     */
    //public void upLoadFileSDO() {
    //    String ip = "";
    //    String port = "";
    //    String userId = "";
    //    StringBuffer temp = new StringBuffer();
    //    //消息提示
    //    try {
    //        //使用Apache文件上传组件处理文件上传步骤：
    //        //1、创建一个DiskFileItemFactory工厂
    //        DiskFileItemFactory factory = new DiskFileItemFactory();
    //        //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
    //        //设置缓冲区的大小为100KB，如果不指定，那么缓冲区的大小默认是10KB
    //        factory.setSizeThreshold(1024 * 100);
    //        //2、创建一个文件上传解析器
    //        ServletFileUpload upload = new ServletFileUpload(factory);
    //        //解决上传文件名的中文乱码
    //        upload.setHeaderEncoding("UTF-8");
    //        //3、判断提交上来的数据是否是上传表单的数据
    //        if (!ServletFileUpload.isMultipartContent(RequestUtil.getRequest())) {
    //            //按照传统方式获取数据
    //        }
    //        //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
    //        upload.setSizeMax(1024 * 1024 * 100);
    //        //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
    //        List<FileItem> list = upload.parseRequest(RequestUtil.getRequest());
    //        for (FileItem item : list) {
    //            //如果FileItem中封装的是普通输入项的数据
    //            if (item.isFormField()) {
    //                String name = item.getFieldName();
    //                //解决普通输入项的数据的中文乱码问题
    //                String value = item.getString("UTF-8");
    //                if ("upload_ip".equals(name)) {
    //                    ip = value;
    //                }
    //                if ("upload_port".equals(name)) {
    //                    port = value;
    //                }
    //                if ("userId".equals(name)) {
    //                    userId = value;
    //                }
    //            } else {
    //                //如果FileItem中封装的是上传文件,得到上传的文件名称
    //                String name = item.getFieldName();
    //                String value = item.getString("UTF-8");
    //                if ("userId".equals(name)) {
    //                    userId = value;
    //                }
    //                String filename = item.getName();
    //                if (filename == null || filename.trim().equals("")) {
    //                    continue;
    //                }
    //                //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
    //                //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
    //                filename = filename.substring(filename.lastIndexOf(File.separator + File.separator) + 1);
    //                //得到上传文件的扩展名
    //                //如果需要限制上传的文件类型，那么可以通过文件的扩展名来判断上传的文件类型是否合法
    //                //获取item中的上传文件的输入流
    //                InputStream in = item.getInputStream();
    //
    //                //创建一个缓冲区
    //                byte[] buffer = new byte[1];
    //                //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
    //                while ((in.read(buffer)) > 0) {
    //                    temp.append((new String(buffer, "UTF-8")).toString());
    //                }
    //                //关闭输入流
    //                in.close();
    //                //关闭输出流
    //                String strTemp = new String(Base64.decode(temp.toString()).getBytes("UTF-8"), "UTF-8");
    //                objTemp = JSONObject.parseObject(strTemp);
    //            }
    //        }
    //        //入库
    //        ImportMetadata.importData(objTemp, PathUtil.DCL, ip, port, userId, user_collect_id);
    //
    //    } catch (Exception e) {
    //        throw new BusinessException(I18nMessage.getMessage("IndexManager.fileUpLoad.wjscsb") + "：" + e);
    //    }
    //}

}
