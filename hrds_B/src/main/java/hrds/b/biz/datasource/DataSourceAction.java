package hrds.b.biz.datasource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.fileupload.FileItem;
import fd.ng.web.fileupload.disk.DiskFileItemFactory;
import fd.ng.web.fileupload.servlet.ServletFileUpload;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.RequestUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.Base64;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    @UploadFile
    public void uploadFile() throws IOException {
        String ip = "";
        String port = "";
        // 表单中传输的值
        String user_id = "";
        StringBuffer temp = new StringBuffer();
        JSONObject jsonObject = new JSONObject();
        //消息提示
        //使用Apache文件上传组件处理文件上传步骤：
        //1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
        //设置缓冲区的大小为100KB，如果不指定，那么缓冲区的大小默认是10KB
        factory.setSizeThreshold(1024 * 100);
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        //解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");
        //3、判断请求消息中的内容是否是“multipart/form-data”类型，是则返回true，否则返回false
        if (!ServletFileUpload.isMultipartContent(RequestUtil.getRequest())) {
            //按照传统方式获取数据
        }
        //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
        upload.setSizeMax(1024 * 1024 * 100);
        //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
        List<FileItem> list = upload.parseRequest(RequestUtil.getRequest());
        for (FileItem item : list) {
            //如果FileItem中封装的是普通输入项的数据
            if (item.isFormField()) {
                String name = item.getFieldName();
                //数据的解决普通输入项的中文乱码问题
                String value = item.getString("UTF-8");
                if ("upload_ip".equals(name)) {
                    ip = value;
                }
                if ("upload_port".equals(name)) {
                    port = value;
                }
                if ("user_id".equals(name)) {
                    user_id = value;
                }
            } else {
                //如果FileItem中封装的是上传文件,得到上传的文件名称
                //表单标签name属性的值
                String name = item.getFieldName();
                String value = item.getString("UTF-8");
                if ("user_id".equals(name)) {
                    user_id = value;
                }
                // 获得文件上传字段中的文件名
                String filename = item.getName();
                if (filename == null || filename.trim().equals("")) {
                    continue;
                }
                //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                filename = filename.substring(filename.lastIndexOf(File.separator + File.separator) + 1);
                //得到上传文件的扩展名
                //如果需要限制上传的文件类型，那么可以通过文件的扩展名来判断上传的文件类型是否合法
                //获取item中的上传文件的输入流
                InputStream in = item.getInputStream();

                //创建一个缓冲区
                byte[] buffer = new byte[1];
                //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                while ((in.read(buffer)) > 0) {
                    temp.append((new String(buffer, "UTF-8")));
                }
                //关闭输入流
                in.close();
                String strTemp = new String(Base64.decode(temp.toString()).getBytes("UTF-8"), "UTF-8");
                jsonObject = JSONObject.parseObject(strTemp);
            }
        }
        //入库
        importDclData(jsonObject, ip, port, user_id, ActionUtil.getUser().getUserId());
    }

    /**
     * 导入贴源层元数据
     *
     * @param objTemp
     * @param port
     * @param ip
     * @param userId
     */
    public void importDclData(JSONObject objTemp, String ip, String port, String
            userId, Long user_collect_id) {

        //数据源data_source
        Data_source data_source = JSONObject.toJavaObject(objTemp.getJSONObject("data_source"), Data_source.class);
        //Agent信息表agent_info
        List<Agent_info> agent_info = (List<Agent_info>) JSONArray.parseArray(objTemp.getJSONArray("agent_info").toJSONString(),
                Agent_info.class);
        //Agent下载信息表Agent_down_info
        List<Agent_down_info> agent_down_info = (List<Agent_down_info>) JSONArray
                .parseArray(objTemp.getJSONArray("agent_down_info").toJSONString(), Agent_down_info.class);
        //采集任务分类表collect_job_classify
        List<Collect_job_classify> collect_job_classify = (List<Collect_job_classify>) JSONArray
                .parseArray(objTemp.getJSONArray("collect_job_classify").toJSONString(), Collect_job_classify.class);
        //ftp采集设置ftp_collect
        List<Ftp_collect> ftp_collect = (List<Ftp_collect>) JSONArray.parseArray(objTemp.getJSONArray("ftp_collect").toJSONString(),
                Ftp_collect.class);
        //ftp已传输表ftp_transfered
        List<Ftp_transfered> ftp_transfered = (List<Ftp_transfered>) JSONArray.parseArray(objTemp.getJSONArray("ftp_transfered").toJSONString(),
                Ftp_transfered.class);
        //ftp目录表ftp_folder
        List<Ftp_folder> ftp_folder = (List<Ftp_folder>) JSONArray.parseArray(objTemp.getJSONArray("ftp_folder").toJSONString(),
                Ftp_folder.class);
        //对象采集设置object_collect
        List<Object_collect> object_collect = (List<Object_collect>) JSONArray.parseArray(objTemp.getJSONArray("object_collect").toJSONString(),
                Object_collect.class);
        //对象采集对应信息object_collect_task
        List<Object_collect_task> object_collect_task = (List<Object_collect_task>) JSONArray
                .parseArray(objTemp.getJSONArray("object_collect_task").toJSONString(), Object_collect_task.class);
        //对象采集存储设置object_storage
        List<Object_storage> object_storage = (List<Object_storage>) JSONArray.parseArray(objTemp.getJSONArray("object_storage").toJSONString(),
                Object_storage.class);
        //对象采集结构信息object_collect_struct
        List<Object_collect_struct> object_collect_struct = (List<Object_collect_struct>) JSONArray
                .parseArray(objTemp.getJSONArray("object_collect_struct").toJSONString(), Object_collect_struct.class);
        //数据库设置database_set
        List<Database_set> database_set = (List<Database_set>) JSONArray.parseArray(objTemp.getJSONArray("database_set").toJSONString(),
                Database_set.class);
        //文件系统设置file_collect_set
        List<File_collect_set> file_collect_set = (List<File_collect_set>) JSONArray
                .parseArray(objTemp.getJSONArray("file_collect_set").toJSONString(), File_collect_set.class);
        //文件源设置file_source
        List<File_source> file_source = (List<File_source>) JSONArray.parseArray(objTemp.getJSONArray("file_source").toJSONString(),
                File_source.class);
        //卸数作业参数表collect_frequency
        List<Collect_frequency> collect_frequency = (List<Collect_frequency>) JSONArray
                .parseArray(objTemp.getJSONArray("collect_frequency").toJSONString(), Collect_frequency.class);
        //压缩作业参数表collect_reduce
        List<Collect_reduce> collect_reduce = (List<Collect_reduce>) JSONArray.parseArray(objTemp.getJSONArray("collect_reduce").toJSONString(),
                Collect_reduce.class);
        //传递作业参数表collect_transfer
        List<Collect_transfer> collect_transfer = (List<Collect_transfer>) JSONArray
                .parseArray(objTemp.getJSONArray("collect_transfer").toJSONString(), Collect_transfer.class);
        //清洗作业参数表collect_clean
        List<Collect_clean> collect_clean = (List<Collect_clean>) JSONArray.parseArray(objTemp.getJSONArray("collect_clean").toJSONString(),
                Collect_clean.class);
        //信号文件入库信息signal_file
        List<Signal_file> signal_file = (List<Signal_file>) JSONArray.parseArray(objTemp.getJSONArray("signal_file").toJSONString(),
                Signal_file.class);
        //数据库对应的表table_info
        List<Table_info> table_info = (List<Table_info>) JSONArray.parseArray(objTemp.getJSONArray("table_info").toJSONString(),
                Table_info.class);
        //列合并信息表column_merge
        List<Column_merge> column_merge = (List<Column_merge>) JSONArray.parseArray(objTemp.getJSONArray("column_merge").toJSONString(),
                Column_merge.class);
        //表存储信息table_storage_info
        List<Table_storage_info> table_storage_info = (List<Table_storage_info>) JSONArray
                .parseArray(objTemp.getJSONArray("table_storage_info").toJSONString(), Table_storage_info.class);
        //表清洗参数信息table_clean
        List<Table_clean> table_clean = (List<Table_clean>) JSONArray.parseArray(objTemp.getJSONArray("table_clean").toJSONString(),
                Table_clean.class);
        //表对应的字段table_column
        List<Table_column> table_column = (List<Table_column>) JSONArray.parseArray(objTemp.getJSONArray("table_column").toJSONString(),
                Table_column.class);
        //列清洗参数信息 column_clean
        List<Column_clean> column_clean = (List<Column_clean>) JSONArray.parseArray(objTemp.getJSONArray("column_clean").toJSONString(),
                Column_clean.class);
        //列拆分信息表column_split
        List<Column_split> column_split = (List<Column_split>) JSONArray.parseArray(objTemp.getJSONArray("column_split").toJSONString(),
                Column_split.class);
        /**判断上传文件的数据源名称和已有的名称是否重复*/
        Result result = Dbo.queryResult("select * from data_source where datasource_name = ?",
                data_source.getDatasource_name());
        if (!result.isEmpty()) {
            throw new BusinessException("数据源名称重复");
        }
        //数据源data_source
        data_source.setUser_id(user_collect_id);
        if (data_source.add(Dbo.db()) != 1) {
            throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
        }
        //数据源和部门关系表source_relation_dep
        Result diResult = Dbo.queryResult("select dep_id from department_info where dep_name" +
                " = '第一部门'");
        String dep_id = diResult.getString(0, "dep_id");
        Source_relation_dep source_relation_dep = new Source_relation_dep();
        source_relation_dep.setDep_id(dep_id);
        source_relation_dep.setSource_id(data_source.getSource_id());
        if (1 != source_relation_dep.add(Dbo.db())) {
            throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
        }
        //Agent信息表agent_info
        for (Agent_info agent : agent_info) {
            agent.setUser_id(userId);
            agent.setAgent_ip(ip);
            agent.setAgent_port(port);
            if (1 != agent.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //Agent下载信息表Agent_down_info
        for (Agent_down_info down_info : agent_down_info) {
            down_info.setUser_id(userId);
            down_info.setAgent_ip(ip);
            down_info.setAgent_port(port);
            if (down_info.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //采集任务分类表collect_job_classify
        for (Collect_job_classify classify : collect_job_classify) {
            classify.setUser_id(userId);
            if (classify.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //ftp采集设置ftp_collect
        for (Ftp_collect collect : ftp_collect) {
            if (collect.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //ftp已传输表ftp_transfered
        for (Ftp_transfered transfered : ftp_transfered) {
            if (transfered.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //ftp目录表ftp_folder
        for (Ftp_folder folder : ftp_folder) {
            if (folder.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //对象采集设置object_collect
        for (Object_collect collect : object_collect) {
            if (1 != collect.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //对象采集对应信息object_collect_task
        for (Object_collect_task collect_task : object_collect_task) {
            if (1 != collect_task.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //对象采集存储设置object_storage
        for (Object_storage storage : object_storage) {
            if (1 != storage.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //对象采集结构信息object_collect_struct
        for (Object_collect_struct collect_struct : object_collect_struct) {
            if (1 != collect_struct.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //数据库设置database_set
        for (Database_set set : database_set) {
            if (1 != set.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //文件系统设置file_collect_set
        for (File_collect_set collect_set : file_collect_set) {
            if (1 != collect_set.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //文件源设置file_source
        for (File_source source : file_source) {
            if (1 != source.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //卸数作业参数表collect_frequency
        for (Collect_frequency frequency : collect_frequency) {
            if (1 != frequency.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //压缩作业参数表collect_reduce
        for (Collect_reduce collect : collect_reduce) {
            if (1 != collect.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //传递作业参数表collect_transfer
        for (Collect_transfer transfer : collect_transfer) {
            if (1 != transfer.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //清洗作业参数表collect_clean
        for (Collect_clean clean : collect_clean) {
            if (1 != clean.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //信号文件入库信息signal_file
        for (Signal_file signal_file2 : signal_file) {
            if (1 != signal_file2.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //数据库对应的表table_info
        for (Table_info info : table_info) {
            if (1 != info.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //列合并信息表column_merge
        for (Column_merge merge : column_merge) {
            if (1 != merge.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //表存储信息table_storage_info
        for (Table_storage_info storage_info : table_storage_info) {
            if (1 != storage_info.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //表清洗参数信息table_clean
        for (Table_clean clean : table_clean) {
            if (1 != clean.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //表对应的字段table_column
        for (Table_column column : table_column) {
            if (1 != column.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //列清洗参数信息column_clean
        for (Column_clean clean : column_clean) {
            if (1 != clean.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
        //列拆分信息表column_split
        for (Column_split split : column_split) {
            if (1 != split.add(Dbo.db())) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
    }

}
