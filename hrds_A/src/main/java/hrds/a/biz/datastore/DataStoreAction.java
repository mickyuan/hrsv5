package hrds.a.biz.datastore;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据存储层配置管理", author = "dhw", createdate = "2019/11/22 11:25")
public class DataStoreAction extends BaseAction {

    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

    @Method(desc = "新增数据存储层、数据存储附加、数据存储层配置属性信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查数据存储层配置字段合法性" +
                    "3.新增保存数据层存储配置信息" +
                    "4.如果附件属性信息不为空，循环新增保存数据存储附加信息" +
                    "5.循环新增保存数据存储层配置属性信息" +
                    "6.判断文件是否存在，存在则上传配置文件")
    @Param(name = "dsl_name", desc = "配置属性名称", range = "无限制")
    @Param(name = "store_type", desc = "存储类型(使用Store_type代码项）", range = "无限制")
    @Param(name = "is_hadoopclient", desc = "is_hadoopclient（使用IsFlag代码项）", range = "无限制")
    @Param(name = "dsl_remark", desc = "数据存储层配置表备注", range = "无限制", nullable = true)
    @Param(name = "dslad_remark", desc = "数据存储附加信息表备注", range = "无限制", nullable = true)
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层信息属性信息集合", range = "key,value类型的json字符串," +
            "storage_property_key，storage_property_val,dsla_remark,is_file,file(只有传文件时需要）代表key，对应的值为value")
    @Param(name = "dsla_storelayer", desc = "配置附加属性信息数组", range = "使用代码项（StoreLayerAdded）", nullable = true)
    @Param(name = "dtcs_id", desc = "数据类型对照主表ID", range = "新增数据类型对照主表时生成")
    @Param(name = "dlcs_id", desc = "数据类型长度对照主表ID", range = "新增数据类型长度对照主表时生成")
    @Param(name = "files", desc = "上传的配置文件", range = "无限制", nullable = true)
    @Param(name = "dsla_remark", desc = "数据存储层配置属性表备注", range = "无限制", nullable = true)
    @UploadFile
    public void addDataStore(String dsl_name, String store_type, String is_hadoopclient, String dsl_remark,
                             String dslad_remark, String dataStoreLayerAttr, String[] dsla_storelayer,
                             long dtcs_id, long dlcs_id, String[] files, String dsla_remark) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        Data_store_layer dataStoreLayer = new Data_store_layer();
        dataStoreLayer.setDsl_name(dsl_name);
        dataStoreLayer.setIs_hadoopclient(is_hadoopclient);
        dataStoreLayer.setDsl_remark(dsl_remark);
        dataStoreLayer.setStore_type(store_type);
        // 2.检查数据存储配置字段合法性
        checkDataStorageField(dataStoreLayer);
        // 3.新增保存数据存储配置信息
        dataStoreLayer.setDsl_id(PrimayKeyGener.getNextId());
        dataStoreLayer.setDlcs_id(dlcs_id);
        dataStoreLayer.setDtcs_id(dtcs_id);
        dataStoreLayer.add(Dbo.db());
        // 4.如果附件属性信息不为空，循环新增保存数据存储附加信息
        if (dsla_storelayer != null && dsla_storelayer.length != 0) {
            addDataStoreLayerAdded(dataStoreLayer.getDsl_id(), dslad_remark, dsla_storelayer);
        }
        // 5.循环新增保存数据存储层配置属性信息
        addDataStorageLayerAttr(dataStoreLayerAttr, dataStoreLayer.getDsl_id());
        // 6.判断文件是否存在，存在则上传配置文件
        if (files != null && files.length != 0) {
            uploadConfFile(files, dsla_remark, dataStoreLayer.getDsl_id());
        }
    }

    @Method(desc = "上传配置文件",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.存在，遍历文件" +
                    "3.循环新增数据存储层配置属性信息")
    @Param(name = "files", desc = "上传的配置文件", range = "无限制", nullable = true)
    @Param(name = "dsla_remark", desc = "数据存储层配置属性表备注", range = "无限制", nullable = true)
    @Param(name = "dsl_id", desc = "数据存储层配置表主键", range = "新增数据存储层时生成")
    private void uploadConfFile(String[] files, String dsla_remark, long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.存在，遍历文件
        Data_store_layer_attr data_store_layer_attr = new Data_store_layer_attr();
        for (String file : files) {
            // 3.循环新增数据存储层配置属性信息
            data_store_layer_attr.setDsla_id(PrimayKeyGener.getNextId());
            data_store_layer_attr.setDsl_id(dsl_id);
            data_store_layer_attr.setStorage_property_key(FileUploadUtil.getOriginalFileName(file));
            data_store_layer_attr.setStorage_property_val(FileUploadUtil.getUploadedFile(file).getPath());
            data_store_layer_attr.setIs_file(IsFlag.Shi.getCode());
            data_store_layer_attr.setDsla_remark(dsla_remark);
            data_store_layer_attr.add(Dbo.db());
        }
    }

    @Method(desc = "检查数据存储层配置字段合法性",
            logicStep = " 1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查配置属性名称是否为空" +
                    "3.检查存储类型是否合法" +
                    "4.检查是否有hadoop客户端字段是否合法")
    @Param(name = "dataStoreLayer", desc = "数据存储层配置表实体对象", range = "取值范围", isBean = true)
    private void checkDataStorageField(Data_store_layer dataStoreLayer) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查配置属性名称是否为空
        if (StringUtil.isBlank(dataStoreLayer.getDsl_name())) {
            throw new BusinessException("配置属性名称不能为空！");
        }
        // 3.检查存储类型是否合法
        Store_type.ofEnumByCode(dataStoreLayer.getStore_type());
        // 4.检查是否有hadoop客户端字段是否合法
        IsFlag.ofEnumByCode(dataStoreLayer.getIs_hadoopclient());
    }

    @Method(desc = "新增保存数据存储附加信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.循环获取配置附加属性dslaStorelayer" +
                    "3.检查配置附加属性信息合法性" +
                    "4.新增保存数据存储附加信息")
    @Param(name = "dsl_id", desc = "数据存储层配置表主键ID", range = "新增数据存储层配置时生成")
    @Param(name = "dslad_remark", desc = "数据存储附加信息表备注", range = "无限制", nullable = true)
    @Param(name = "dsla_storelayer", desc = "配置附加属性信息数组", range = "使用代码项（StoreLayerAdded）", nullable = true)
    private void addDataStoreLayerAdded(long dsl_id, String dslad_remark, String[] dsla_storelayer) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.循环获取配置附加属性dslaStorelayer
        for (String dslaStorelayer : dsla_storelayer) {
            Data_store_layer_added dataStoreLayerAdded = new Data_store_layer_added();
            dataStoreLayerAdded.setDsl_id(dsl_id);
            dataStoreLayerAdded.setDslad_remark(dslad_remark);
            dataStoreLayerAdded.setDslad_id(PrimayKeyGener.getNextId());
            dataStoreLayerAdded.setDsla_storelayer(dslaStorelayer);
            // 3.检查配置附加属性信息合法性
            checkDataStoreLayerAddedField(dataStoreLayerAdded);
            // 4.新增保存数据存储附加信息
            dataStoreLayerAdded.add(Dbo.db());
        }
    }

    @Method(desc = "检查数据存储附加信息字段合法性",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查存储层配置ID是否为空" +
                    "3.检查配置附加属性信息是否为空")
    @Param(name = "dataStoreLayerAdded", desc = "数据存储附加信息表实体对象", range = "取值范围", isBean = true)
    private void checkDataStoreLayerAddedField(Data_store_layer_added dataStoreLayerAdded) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查存储层配置ID是否为空
        if (StringUtil.isBlank(String.valueOf(dataStoreLayerAdded.getDsl_id()))) {
            throw new BusinessException("存储层配置ID不能为空");
        }
        // 3.检查配置附加属性信息是否为空
        if (StringUtil.isBlank(dataStoreLayerAdded.getDsla_storelayer())) {
            throw new BusinessException("配置附加属性信息不能为空");
        }
    }

    @Method(desc = "新增保存数据存储层配置属性信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.获取存放数据存储配置属性的key,value值" +
                    "3.循环获取数据存储配置属性的key,value值" +
                    "4.检查数据存储层配置属性字段合法性" +
                    "5.循环新增保存数据存储层配置属性信息" +
                    "6.判断文件是否存在" +
                    "7.存在，遍历文件" +
                    "8.循环新增数据存储层配置属性信息")
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层信息属性信息集合,(is_file使用代码项（IsFlag）)",
            range = "key,value类型的json字符串")
    @Param(name = "dsl_id", desc = "数据存储层配置表主键ID", range = "新增数据存储层配置时生成")
    private void addDataStorageLayerAttr(String dataStoreLayerAttr, long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.获取存放数据存储配置属性的key,value值
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> layerAttrList = JsonUtil.toObject(dataStoreLayerAttr, type);
        // 3.循环获取数据存储配置属性的key,value值
        Data_store_layer_attr data_store_layer_attr = new Data_store_layer_attr();
        for (Map<String, String> layerAttr : layerAttrList) {
            data_store_layer_attr.setDsla_id(PrimayKeyGener.getNextId());
            data_store_layer_attr.setDsl_id(dsl_id);
            data_store_layer_attr.setDsla_remark(layerAttr.get("dsla_remark"));
            data_store_layer_attr.setIs_file(layerAttr.get("is_file"));
            data_store_layer_attr.setStorage_property_key(layerAttr.get("storage_property_key"));
            data_store_layer_attr.setStorage_property_val(layerAttr.get("storage_property_val"));
            // 4.检查数据存储层配置属性字段合法性
            checkDataStoreLayerAttrField(data_store_layer_attr);
            // 5.循环新增保存数据存储层配置属性信息
            data_store_layer_attr.add(Dbo.db());
        }
    }

    @Method(desc = "新增存储层数据类型对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查数据类型对照主表字段信息是否合法" +
                    "3.判断类型对照名称是否已存在" +
                    "4.获取源表数据类型，目标表数据类型信息" +
                    "5.新增数据类型对照主表信息" +
                    "6.循环新增存储层数据类型对照表信息")
    @Param(name = "typeContrast", desc = "存放以源表数据类型source_type为key，目标数据类型target_type为value的json字符串",
            range = "无限制")
    @Param(name = "type_contrast_sum", desc = "数据类型对照主表", range = "与数据库对应表字段一致", isBean = true)
    @Param(name = "dtc_remark", desc = "数据类型对照表备注", range = "无限制", nullable = true)
    public void addDataTypeContrastInfo(String typeContrast, Type_contrast_sum type_contrast_sum
            , String dtc_remark) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查数据类型对照主表字段信息是否合法
        checkTypeContrastField(type_contrast_sum);
        // 3.判断类型对照名称是否已存在
        if (Dbo.queryNumber("select count(*) from " + Type_contrast_sum.TableName + " where dtcs_name=?",
                type_contrast_sum.getDtcs_name()).orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
            throw new BusinessException("类型对照名称已存在");
        }
        // 4.获取源表数据类型，目标表数据类型信息
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> typeContrastList = JsonUtil.toObject(typeContrast, type);
        if (!typeContrastList.isEmpty()) {
            // 5.新增数据类型对照主表信息
            type_contrast_sum.setDtcs_id(PrimayKeyGener.getNextId());
            type_contrast_sum.add(Dbo.db());
            Type_contrast type_contrast = new Type_contrast();
            for (Map<String, String> map : typeContrastList) {
                // 6.循环新增存储层数据类型对照表信息
                type_contrast.setDtcs_id(type_contrast_sum.getDtcs_id());
                type_contrast.setDtc_id(PrimayKeyGener.getNextId());
                type_contrast.setSource_type(map.get("source_type"));
                type_contrast.setTarget_type(map.get("target_type"));
                type_contrast.setDtc_remark(dtc_remark);
                type_contrast.add(Dbo.db());
            }
        }
    }

    @Method(desc = "添加存储层数据类型长度对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查存储层数据类型长度对照主表与存储层数据类型长度对照表字段信息是否合法" +
                    "3.判断类型对照名称是否已存在" +
                    "4.获取字段类型，字段长度信息" +
                    "5.新增存储层数据类型长度对照主表信息" +
                    "6.循环新增存储层数据类型长度对照表信息")
    @Param(name = "lengthInfo", desc = "以字段类型dlc_type为key，字段长度dlc_length为value的json字符串", range = "无限制")
    @Param(name = "dlc_remark", desc = "长度对照表备注", range = "无限制", nullable = true)
    @Param(name = "length_contrast_sum", desc = "存储层数据类型长度对照表", range = "与数据库对应表字段一致", isBean = true)
    public void addTypeLengthContrastInfo(String lengthInfo, String dlc_remark,
                                          Length_contrast_sum length_contrast_sum) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查存储层数据类型长度对照主表与存储层数据类型长度对照表字段信息是否合法
        checkTypeLengthContrastField(length_contrast_sum);
        // 3.判断类型对照名称是否已存在
        if (Dbo.queryNumber("select count(*) from " + Length_contrast_sum.TableName + " where dlcs_name=?",
                length_contrast_sum.getDlcs_name()).orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
            throw new BusinessException("长度对照名称已存在");
        }
        // 4.获取字段类型，字段长度信息
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> lengthContrastList = JsonUtil.toObject(lengthInfo, type);
        // 5.新增存储层数据类型长度对照主表信息
        length_contrast_sum.setDlcs_id(PrimayKeyGener.getNextId());
        length_contrast_sum.add(Dbo.db());
        // 6.循环新增存储层数据类型长度对照表信息
        Length_contrast length_contrast = new Length_contrast();
        for (Map<String, String> map : lengthContrastList) {
            length_contrast.setDlcs_id(length_contrast_sum.getDlcs_id());
            length_contrast.setDlc_id(PrimayKeyGener.getNextId());
            length_contrast.setDlc_type(map.get("dlc_type"));
            length_contrast.setDlc_length(map.get("dlc_length"));
            length_contrast.setDlc_remark(dlc_remark);
            length_contrast.add(Dbo.db());
        }
    }

    @Method(desc = "更新存储层数据类型对比信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查数据类型对照主表字段信息是否合法" +
                    "3.获取源表数据类型，目标表数据类型信息" +
                    "4.更新数据类型对照主表信息" +
                    "5.更新前先删除旧存储层数据类型对照表信息" +
                    "6.循环更新存储层数据类型对照表信息")
    @Param(name = "typeContrast", desc = "存放以源表数据类型source_type为key，目标数据类型target_type为value的json字符串",
            range = "无限制")
    @Param(name = "type_contrast_sum", desc = "数据类型对照主表", range = "与数据库对应表字段一致", isBean = true)
    @Param(name = "dtc_remark", desc = "数据类型对照表备注", range = "无限制", nullable = true)
    public void updateDataTypeContrastInfo(String typeContrast, Type_contrast_sum type_contrast_sum
            , String dtc_remark) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查数据类型对照主表字段信息是否合法
        checkTypeContrastField(type_contrast_sum);
        // 3.获取源表数据类型，目标表数据类型信息
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> typeContrastList = JsonUtil.toObject(typeContrast, type);
        if (!typeContrastList.isEmpty()) {
            // 4.更新数据类型对照主表信息
            type_contrast_sum.update(Dbo.db());
            Type_contrast type_contrast = new Type_contrast();
            // 5.更新前先删除旧存储层数据类型对照表信息
            deleteTypeContrastInfo(type_contrast_sum.getDtcs_id());
            type_contrast.setDtcs_id(type_contrast_sum.getDtcs_id());
            for (Map<String, String> map : typeContrastList) {
                // 6.循环更新存储层数据类型对照表信息
                type_contrast.setDtc_id(PrimayKeyGener.getNextId());
                type_contrast.setSource_type(map.get("source_type"));
                type_contrast.setTarget_type(map.get("target_type"));
                type_contrast.setDtc_remark(dtc_remark);
                type_contrast.add(Dbo.db());
            }
        }
    }

    @Method(desc = "删除数据存储层类型对照表信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层类型对照表信息")
    @Param(name = "dtcs_id", desc = "类型对照ID", range = "新增数据类型对照主表时生成")
    private void deleteTypeContrastInfo(long dtcs_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层类型对照表信息
        Dbo.execute("delete from " + Type_contrast.TableName + " where dtcs_id=?", dtcs_id);
    }

    @Method(desc = "删除数据类型对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层类型对照主表信息" +
                    "3.删除数据存储层类型对照主表信息")
    @Param(name = "dtcs_id", desc = "类型对照ID", range = "新增数据类型对照主表时生成")
    public void deleteDataTypeContrastInfo(long dtcs_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层类型对照主表信息
        DboExecute.deletesOrThrow("删除数据存储层类型对照主表信息失败，dtcs_id=" + dtcs_id,
                "delete from " + Type_contrast_sum.TableName + " where dtcs_id=?", dtcs_id);
        // 3.删除数据存储层类型对照主表信息
        deleteTypeContrastInfo(dtcs_id);
    }

    @Method(desc = "更新存储层数据类型长度对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查存储层数据类型长度对照主表与存储层数据类型长度对照表字段信息是否合法" +
                    "3.获取字段类型，字段长度信息" +
                    "4.新增存储层数据类型长度对照主表信息" +
                    "5.新增前先删除原存储层数据类型长度对照表信息" +
                    "6.循环新增存储层数据类型长度对照表信息")
    @Param(name = "lengthInfo", desc = "以字段类型dlc_type为key，字段长度dlc_length为value的json字符串", range = "无限制")
    @Param(name = "dlc_remark", desc = "长度对照表备注", range = "无限制", nullable = true)
    @Param(name = "length_contrast_sum", desc = "存储层数据类型长度对照表", range = "与数据库对应表字段一致", isBean = true)
    public void updateTypeLengthContrastInfo(String lengthInfo, String dlc_remark,
                                             Length_contrast_sum length_contrast_sum) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查存储层数据类型长度对照主表与存储层数据类型长度对照表字段信息是否合法
        checkTypeLengthContrastField(length_contrast_sum);
        // 3.获取字段类型，字段长度信息
        Type type = new TypeReference<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> lengthContrastList = JsonUtil.toObject(lengthInfo, type);
        // 4.新增存储层数据类型长度对照主表信息
        length_contrast_sum.update(Dbo.db());
        // 5.新增前先删除原存储层数据类型长度对照表信息
        deleteTypeLengthInfo(length_contrast_sum.getDlcs_id());
        Length_contrast length_contrast = new Length_contrast();
        length_contrast.setDlcs_id(length_contrast_sum.getDlcs_id());
        for (Map<String, String> map : lengthContrastList) {
            // 6.循环新增存储层数据类型长度对照表信息
            length_contrast.setDlc_id(PrimayKeyGener.getNextId());
            length_contrast.setDlc_type(map.get("dlc_type"));
            length_contrast.setDlc_length(map.get("dlc_length"));
            length_contrast.setDlc_remark(dlc_remark);
            length_contrast.add(Dbo.db());
        }
    }

    @Method(desc = "删除数据存储层类型长度对照表信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层类型长度对照表信息")
    @Param(name = "dlcs_id", desc = "存储层类型长度ID", range = "新增数据类型长度对照主表时生成")
    private void deleteTypeLengthInfo(long dlcs_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层类型对照表信息
        Dbo.execute("delete from " + Length_contrast.TableName + " where dlcs_id=?", dlcs_id);
    }

    @Method(desc = "删除数据存储层类型长度对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层类型长度对照主表信息" +
                    "3.删除数据存储层类型长度对照表信息")
    @Param(name = "dlcs_id", desc = "存储层类型长度ID", range = "新增数据类型长度对照主表时生成")
    public void deleteTypeLengthContrastInfo(long dlcs_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层类型长度对照主表信息
        DboExecute.deletesOrThrow("删除数据存储层类型对照主表信息失败，dlcs_id=" + dlcs_id,
                "delete from " + Length_contrast_sum.TableName + " where dlcs_id=?", dlcs_id);
        // 3.删除数据存储层类型长度对照表信息
        deleteTypeLengthInfo(dlcs_id);
    }

    @Method(desc = "检查存储层数据类型长度对照主表与存储层数据类型长度对照表字段信息是否合法",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.判断长度对照名称是否为空")
    @Param(name = "length_contrast_sum", desc = "存储层数据类型长度对照主表", range = "与数据库对应表字段一致",
            isBean = true)
    @Param(name = "length_contrast", desc = "存储层数据类型长度对照表", range = "与数据库对应表字段一致", isBean = true)
    private void checkTypeLengthContrastField(Length_contrast_sum length_contrast_sum) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.判断长度对照名称是否为空
        if (StringUtil.isBlank(length_contrast_sum.getDlcs_name())) {
            throw new BusinessException("长度对照名称不能为空");
        }
    }

    @Method(desc = "检查存储层数据类型对照表与数据类型对照主表字段信息是否合法",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.判断源表数据类型是否为空")
    @Param(name = "type_contrast_sum", desc = "数据类型对照主表", range = "与数据库对应表字段一致", isBean = true)
    private void checkTypeContrastField(Type_contrast_sum type_contrast_sum) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.判断类型对照名称是否为空
        if (StringUtil.isBlank(type_contrast_sum.getDtcs_name())) {
            throw new BusinessException("类型对照名称不能为空");
        }
    }

    @Method(desc = "检查数据存储层配置属性字段合法性",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查存储层配置ID是否为空" +
                    "3.检查属性key是否为空" +
                    "4.检查属性value是否为空")
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层配置属性表实体对象", range = "取值范围", isBean = true)
    private void checkDataStoreLayerAttrField(Data_store_layer_attr dataStoreLayerAttr) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查存储层配置ID是否为空
        if (StringUtil.isBlank(String.valueOf(dataStoreLayerAttr.getDsl_id()))) {
            throw new BusinessException("存储层配置ID不能为空");
        }
        // 3.检查属性key是否为空
        if (StringUtil.isBlank(dataStoreLayerAttr.getStorage_property_key())) {
            throw new BusinessException("属性key不能为空");
        }
        // 4.检查属性value是否为空
        if (StringUtil.isBlank(dataStoreLayerAttr.getStorage_property_val())) {
            throw new BusinessException("属性value不能为空");
        }
    }

    @Method(desc = "更新保存数据存储层信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查数据存储层配置字段合法性" +
                    "3.更新数据存储配置信息" +
                    "4.更新保存前先删除原来的数据存储附加信息" +
                    "5.更新数据存储附加信息" +
                    "6.判断文件是否存在，如果存在则先删除原配置文件，再更新数据存储配置上传文件属性信息" +
                    "6.更新保存前先删除数据存储层配置属性信息" +
                    "7.更新数据存储层配置属性信息")
    @Param(name = "dsl_name", desc = "配置属性名称", range = "无限制")
    @Param(name = "store_type", desc = "存储类型(使用Store_type代码项）", range = "无限制")
    @Param(name = "is_hadoopclient", desc = "is_hadoopclient（使用IsFlag代码项）", range = "无限制")
    @Param(name = "dsl_remark", desc = "数据存储层配置表备注", range = "无限制", nullable = true)
    @Param(name = "dslad_remark", desc = "数据存储附加信息表备注", range = "无限制", nullable = true)
    @Param(name = "dsl_id", desc = "数据存储层配置表主键", range = "新增数据存储层时生成")
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层信息属性信息集合", range = "key,value类型的json字符串")
    @Param(name = "dsla_storelayer", desc = "配置附加属性信息数组", range = "使用代码项（StoreLayerAdded）", nullable = true)
    @Param(name = "dtcs_id", desc = "数据类型对照主表ID", range = "新增数据类型对照主表时生成")
    @Param(name = "dlcs_id", desc = "数据类型长度对照主表ID", range = "新增数据类型长度对照主表时生成")
    @Param(name = "files", desc = "上传的配置文件", range = "无限制", nullable = true)
    @Param(name = "dsla_remark", desc = "数据存储层配置属性表备注", range = "无限制", nullable = true)
    @UploadFile
    public void updateDataStore(String dsl_name, String store_type, String is_hadoopclient, String dsl_remark,
                                String dslad_remark, long dsl_id, String dataStoreLayerAttr, String dsla_remark,
                                String[] dsla_storelayer, long dtcs_id, long dlcs_id, String[] files) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查数据存储配置字段合法性
        Data_store_layer dataStoreLayer = new Data_store_layer();
        dataStoreLayer.setDsl_name(dsl_name);
        dataStoreLayer.setIs_hadoopclient(is_hadoopclient);
        dataStoreLayer.setDsl_remark(dsl_remark);
        dataStoreLayer.setStore_type(store_type);
        dataStoreLayer.setDsl_id(dsl_id);
        dataStoreLayer.setDlcs_id(dlcs_id);
        dataStoreLayer.setDtcs_id(dtcs_id);
        checkDataStorageField(dataStoreLayer);
        // 3.更新数据存储配置信息
        dataStoreLayer.update(Dbo.db());
        // 4.更新保存前先删除原来的数据存储附加信息
        deleteDataStoreLayerAdded(dsl_id);
        // 5.更新数据存储附加信息
        addDataStoreLayerAdded(dsl_id, dslad_remark, dsla_storelayer);
        // 6.判断文件是否存在，如果存在则先删除原配置文件,删除要放在删除数
        if (files != null && files.length != 0) {
            deleteConfFile(dsl_id);
            uploadConfFile(files, dsla_remark, dsl_id);
        }
        // 7.更新保存前先删除数据存储层配置属性信息
        deleteDataStoreLayerAttr(dsl_id);
        // 8.更新数据存储层配置属性信息
        addDataStorageLayerAttr(dataStoreLayerAttr, dsl_id);
    }

    @Method(desc = "删除配置文件",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.根据数据存储层配置表主键获取数据存储层配置属性表" +
                    "3.遍历获取属性value值，判断是否为文件，是则删除")
    @Param(name = "dsl_id", desc = "数据存储层配置表主键", range = "新增数据存储层时生成")
    private void deleteConfFile(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.根据数据存储层配置表主键获取数据存储层配置属性表
        List<Data_store_layer_attr> storeLayerAttrs = Dbo.queryList(Data_store_layer_attr.class,
                "select * from " + Data_store_layer_attr.TableName + " where dsl_id=? and is_file=?",
                dsl_id, IsFlag.Shi.getCode());
        // 3.遍历获取属性value值，判断文件是否存在，是则删除
        for (Data_store_layer_attr storeLayerAttr : storeLayerAttrs) {
            if (new File(storeLayerAttr.getStorage_property_val()).exists()) {
                try {
                    Files.delete(new File(storeLayerAttr.getStorage_property_val()).toPath());
                } catch (IOException e) {
                    throw new BusinessException("删除文件失败！");
                }
            }
        }
    }

    @Method(desc = "删除数据存储层信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层配置信息，,不关心删除几条数据" +
                    "3.删除数据存储附加信息" +
                    "4.删除数据存储层配置属性信息")
    @Param(name = "dsl_id", desc = "存储层配置ID.数据存储层配置表主键", range = "新增存储层配置信息时生成")
    public void deleteDataStore(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层配置信息，不关心删除几条数据
        DboExecute.deletesOrThrow("删除data_store_layer表信息失败，dsl_id=" + dsl_id,
                "delete from " + Data_store_layer.TableName + " where dsl_id=?", dsl_id);
        // 3.删除数据存储附加信息
        deleteDataStoreLayerAdded(dsl_id);
        // 4.删除数据存储层配置属性信息
        deleteDataStoreLayerAttr(dsl_id);
    }

    @Method(desc = "删除数据存储层配置属性信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层配置属性信息,,不关心删除几条数据")
    @Param(name = "dsl_id", desc = "存储层配置ID.数据存储层配置表主键", range = "新增存储层配置信息时生成")
    private void deleteDataStoreLayerAttr(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层配置属性信息,,不关心删除几条数据
        Dbo.execute("delete from " + Data_store_layer_attr.TableName + " where dsl_id=? and is_file=?",
                dsl_id, IsFlag.Fou.getCode());
    }

    @Method(desc = "删除数据存储附加信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储附加信息,不关心删除几条数据")
    @Param(name = "dsl_id", desc = "存储层配置ID.数据存储层配置表主键", range = "新增存储层配置信息时生成")
    private void deleteDataStoreLayerAdded(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储附加信息,不关心删除几条数据
        Dbo.execute("delete from " + Data_store_layer_added.TableName + " where dsl_id=?", dsl_id);
    }

    @Method(desc = "查询数据存储层配置信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.查询所有数据存储层配置信息")
    @Return(desc = "返回关联查询数据存储层信息", range = "无限制")
    public Result searchDataStore() {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.查询所有数据存储层配置信息
        return Dbo.queryResult("select * from " + Data_store_layer.TableName);
    }

    @Method(desc = "根据权限数据存储层配置ID关联查询数据存储层信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.根据权限数据存储层配置ID查询数据存储层配置信息" +
                    "3.关联查询获取数据存储层配置与数据存储附加信息" +
                    "4.根据权限数据存储层配置ID关联查询获取数据存储层配置与数据存储层配置属性信息" +
                    "5.封装数据存储附件信息、数据存储层配置属性数据" +
                    "6.返回存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合")
    @Param(name = "dsl_id", desc = "数据存储层配置表主键ID", range = "新增存储层配置信息时生成")
    @Return(desc = "返回关联查询数据存储层信息", range = "无限制")
    public Map<String, Object> searchDataStoreById(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.根据权限数据存储层配置ID查询数据存储层配置信息
        Map<String, Object> storeLayer = Dbo.queryOneObject("select * from " + Data_store_layer.TableName
                + " where dsl_id=?", dsl_id);
        // 3.根据权限数据存储层配置ID关联查询获取数据存储层配置与数据存储附加信息
        List<Map<String, Object>> layerAndAdded = Dbo.queryList("select * from " +
                Data_store_layer_added.TableName + " where dsl_id=?", dsl_id);
        // 4.根据权限数据存储层配置ID关联查询获取数据存储层配置与数据存储层配置属性信息
        List<Map<String, Object>> layerAndAttr = Dbo.queryList("select * from "
                + Data_store_layer_attr.TableName + " where dsl_id=?", dsl_id);
        // 5.封装数据存储层配置属性数据
        storeLayer.put("layerAndAdded", layerAndAdded);
        storeLayer.put("layerAndAttr", layerAndAttr);
        // 6.返回存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合
        return storeLayer;
    }

    @Method(desc = "查询数据存储层数据类型对照以及长度对照主表信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.查询数据存储层数据类型对照主表信息" +
                    "3.查询数据类型长度对照主表信息" +
                    "4.封装数据存储层数据类型对照以及长度对照主表信息并返回")
    @Return(desc = "返回数据存储层数据类型对照以及长度对照主表信息", range = "无限制")
    public Map<String, Object> searchDataTypeMasterTableInfo() {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.查询数据存储层数据类型对照主表信息
        List<Map<String, Object>> typeContrastSumList = Dbo.queryList("select * from "
                + Type_contrast_sum.TableName);
        // 3.查询数据类型长度对照主表信息
        List<Map<String, Object>> lengthContrastSumList = Dbo.queryList("select * from "
                + Length_contrast_sum.TableName);
        // 4.封装数据存储层数据类型对照以及长度对照主表信息并返回
        Map<String, Object> dataType = new HashMap<>();
        dataType.put("typeContrastSumList", typeContrastSumList);
        dataType.put("lengthContrastSumList", lengthContrastSumList);
        return dataType;
    }

    @Method(desc = "查询存储层数据类型对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.查询数据存储层数据类型对照表信息" +
                    "3.查询数据类型长度对照表信息" +
                    "数据存储层数据类型对照以及长度对照表信息")
    @Param(name = "dtcs_id", desc = "存储层类型长度ID", range = "新增数据类型对照主表时生成", nullable = true)
    @Return(desc = "返回类型以及类型长度对照表信息", range = "无限制")
    public Result searchDataLayerDataTypeInfo(Long dtcs_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.查询数据存储层数据类型对照表信息
        asmSql.clean();
        asmSql.addSql("select t1.*,t2.dtcs_name from " + Type_contrast.TableName +
                " t1 left join " + Type_contrast_sum.TableName + " t2 on t1.dtcs_id=t2.dtcs_id");
        if (dtcs_id != null) {
            asmSql.addSql(" where t1.dtcs_id=?").addParam(dtcs_id);
        }
        return Dbo.queryResult(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "查询存储层数据类型对照信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.查询数据存储层数据类型对照表信息" +
                    "3.查询数据类型长度对照表信息" +
                    "数据存储层数据类型对照以及长度对照表信息")
    @Param(name = "dlcs_id", desc = "存储层类型长度ID", range = "新增数据类型长度对照主表时生成", nullable = true)
    @Return(desc = "返回类型以及类型长度对照表信息", range = "无限制")
    public Result searchDataLayerDataTypeLengthInfo(Long dlcs_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.查询数据类型长度对照表信息
        asmSql.clean();
        asmSql.addSql("select t1.*,t2.dlcs_name from " + Length_contrast.TableName +
                " t1 left join " + Length_contrast_sum.TableName + " t2 on t1.dlcs_id=t2.dlcs_id");
        if (dlcs_id != null) {
            asmSql.addSql(" where t1.dlcs_id=?").addParam(dlcs_id);
        }
        return Dbo.queryResult(asmSql.sql(), asmSql.params());
    }

}
