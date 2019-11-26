package hrds.a.biz.datastore;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.StorageType;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_layer_added;
import hrds.commons.entity.Data_store_layer_attr;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.lang.reflect.Type;
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
                    "4.循环新增保存数据存储附加信息" +
                    "5.循环新增保存数据存储层配置属性信息")
    @Param(name = "dataStoreLayer", desc = "数据存储层配置表实体对象", range = "取值范围", isBean = true)
    @Param(name = "dataStoreLayerAdded", desc = "数据存储附加信息表实体对象", range = "取值范围", isBean = true)
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层信息属性信息集合", range = "key,value类型的json字符串," +
            "storage_property_key，storage_property_val,dsla_remark代表key，对应的值为value")
    @Param(name = "dsla_storelayer", desc = "配置附加属性信息数组", range = "使用代码项（StoreLayerAdded）")
    public void addDataStore(Data_store_layer dataStoreLayer, Data_store_layer_added dataStoreLayerAdded,
                             String dataStoreLayerAttr, String[] dsla_storelayer) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查数据存储配置字段合法性
        checkDataStorageField(dataStoreLayer);
        // 3.新增保存数据存储配置信息
        String dsl_id = PrimayKeyGener.getNextId();
        dataStoreLayer.setDsl_id(dsl_id);
        dataStoreLayer.add(Dbo.db());
        // 4.循环新增保存数据存储附加信息
        dataStoreLayerAdded.setDsl_id(dsl_id);
        addDataStoreLayerAdded(dataStoreLayerAdded, dsla_storelayer);
        // 5.循环新增保存数据存储层配置属性信息
        addDataStorageLayerAttr(dataStoreLayerAttr, dsl_id);
    }

    @Method(desc = "检查数据存储层配置字段合法性",
            logicStep = " 1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查配置属性名称是否为空" +
                    "3.检查存储类型是否合法")
    @Param(name = "dataStoreLayer", desc = "数据存储层配置表实体对象", range = "取值范围", isBean = true)
    private void checkDataStorageField(Data_store_layer dataStoreLayer) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查配置属性名称是否为空
        if (StringUtil.isBlank(dataStoreLayer.getDsl_name())) {
            throw new BusinessException("配置属性名称不能为空！");
        }
        // 3.检查存储类型是否合法
        StorageType.ofEnumByCode(dataStoreLayer.getStore_type());
    }

    @Method(desc = "新增保存数据存储附加信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查配置附加属性信息合法性" +
                    "3.新增保存数据存储附加信息")
    @Param(name = "dataStoreLayerAdded", desc = "数据存储附加信息表实体对象", range = "取值范围", isBean = true)
    @Param(name = "dsla_storelayer", desc = "配置附加属性信息数组", range = "使用代码项（StoreLayerAdded）")
    private void addDataStoreLayerAdded(Data_store_layer_added dataStoreLayerAdded, String[] dsla_storelayer) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查配置附加属性信息合法性
        checkDataStoreLayerAddedField(dataStoreLayerAdded);
        // 3.新增保存数据存储附加信息
        for (String dslaStorelayer : dsla_storelayer) {
            dataStoreLayerAdded.setDslad_id(PrimayKeyGener.getNextId());
            dataStoreLayerAdded.setDsla_storelayer(dslaStorelayer);
            dataStoreLayerAdded.add(Dbo.db());
        }
    }

    @Method(desc = "检查数据存储附加信息字段合法性",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查存储层配置ID是否为空" +
                    "3.检查配置附加属性信息是否为空")
    @Param(name = "dataStoreLayerAdded", desc = "数据存储附加信息表实体对象", range = "取值范围", isBean = true)
    private void checkDataStoreLayerAddedField(Data_store_layer_added dataStoreLayerAdded) {
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
                    "5.循环新增保存数据存储层配置属性信息")
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层信息属性信息集合", range = "key,value类型的json字符串")
    private void addDataStorageLayerAttr(String dataStoreLayerAttr, String dsl_id) {
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
            data_store_layer_attr.setStorage_property_key(layerAttr.get("storage_property_key"));
            data_store_layer_attr.setStorage_property_val(layerAttr.get("storage_property_val"));
            data_store_layer_attr.setDsla_remark(layerAttr.get("dsla_remark"));
            // 4.检查数据存储层配置属性字段合法性
            checkDataStoreLayerAttrField(data_store_layer_attr);
            // 5.循环新增保存数据存储层配置属性信息
            data_store_layer_attr.add(Dbo.db());
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

    @Method(desc = "编辑保存数据存储层信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.检查数据存储层配置字段合法性" +
                    "3.更新数据存储配置信息" +
                    "4.更新保存前先删除原来的数据存储附加信息" +
                    "5.更新数据存储附加信息" +
                    "6.更新保存前先删除数据存储层配置属性信息" +
                    "7.更新数据存储层配置属性信息")
    @Param(name = "dataStoreLayer", desc = "数据存储层配置表实体对象", range = "取值范围", isBean = true)
    @Param(name = "dataStoreLayerAdded", desc = "数据存储附加信息表实体对象", range = "取值范围", isBean = true)
    @Param(name = "dsl_id", desc = "数据存储层配置表主键", range = "新增数据存储层时生成")
    @Param(name = "dataStoreLayerAttr", desc = "数据存储层信息属性信息集合", range = "key,value类型的json字符串")
    @Param(name = "dsla_storelayer", desc = "配置附加属性信息数组", range = "使用代码项（StoreLayerAdded）")
    public void updateDataStore(Data_store_layer dataStoreLayer, Data_store_layer_added dataStoreLayerAdded,
                                long dsl_id, String dataStoreLayerAttr, String[] dsla_storelayer) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.检查数据存储配置字段合法性
        checkDataStorageField(dataStoreLayer);
        // 3.更新数据存储配置信息
        dataStoreLayer.update(Dbo.db());
        // 4.更新保存前先删除原来的数据存储附加信息
        deleteDataStoreLayerAdded(dataStoreLayerAdded.getDsl_id());
        // 5.更新数据存储附加信息
        addDataStoreLayerAdded(dataStoreLayerAdded, dsla_storelayer);
        // 6.更新保存前先删除数据存储层配置属性信息
        deleteDataStoreLayerAttr(dsl_id);
        // 7.更新数据存储层配置属性信息
        addDataStorageLayerAttr(dataStoreLayerAttr, String.valueOf(dsl_id));
    }

    @Method(desc = "删除数据存储层信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层配置信息" +
                    "3.删除数据存储附加信息" +
                    "4.删除数据存储层配置属性信息")
    @Param(name = "dsl_id", desc = "存储层配置ID.数据存储层配置表主键", range = "新增存储层配置信息时生成")
    public void deleteDataStore(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层配置信息
        DboExecute.deletesOrThrow("删除data_store_layer表信息失败，dsl_id=" + dsl_id,
                "delete from " + Data_store_layer.TableName + " where dsl_id=?", dsl_id);
        // 3.删除数据存储附加信息
        deleteDataStoreLayerAdded(dsl_id);
        // 4.删除数据存储层配置属性信息
        deleteDataStoreLayerAttr(dsl_id);
    }

    @Method(desc = "删除数据存储层配置属性信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储层配置属性信息,不确定删除几条数据")
    @Param(name = "dsl_id", desc = "存储层配置ID.数据存储层配置表主键", range = "新增存储层配置信息时生成")
    private void deleteDataStoreLayerAttr(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储层配置属性信息,不确定删除几条数据
        Dbo.execute("delete from " + Data_store_layer_attr.TableName + " where dsl_id=?", dsl_id);
    }

    @Method(desc = "删除数据存储附加信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.删除数据存储附加信息")
    @Param(name = "dsl_id", desc = "存储层配置ID.数据存储层配置表主键", range = "新增存储层配置信息时生成")
    private void deleteDataStoreLayerAdded(long dsl_id) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.删除数据存储附加信息
        Dbo.execute("delete from " + Data_store_layer_added.TableName + " where dsl_id=?", dsl_id);
    }

    @Method(desc = "关联查询数据存储层信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.查询所有数据存储层配置信息" +
                    "3.关联查询获取数据存储层配置与数据存储附加信息" +
                    "4.关联查询获取数据存储层配置与数据存储层配置属性信息" +
                    "5.构建存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合并封装数据" +
                    "6.返回存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合")
    @Return(desc = "返回关联查询数据存储层信息", range = "无限制")
    public Map<String, Object> searchDataStore() {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.查询所有数据存储层配置信息
        List<Map<String, Object>> storeLayer = Dbo.queryList("select * from " + Data_store_layer.TableName);
        // 3.关联查询获取数据存储层配置与数据存储附加信息
        List<Map<String, Object>> layerAndAdded = Dbo.queryList("select t1.dsl_id,t2.* from "
                + Data_store_layer.TableName + " t1 left join " + Data_store_layer_added.TableName +
                " t2 on t1.dsl_id=t2.dsl_id");
        // 4.关联查询获取数据存储层配置与数据存储层配置属性信息
        List<Map<String, Object>> layerAndAttr = Dbo.queryList("select t1.dsl_id,t2.* from "
                + Data_store_layer.TableName + " t1 left join " + Data_store_layer_attr.TableName +
                " t2 on t1.dsl_id=t2.dsl_id");
        // 5.构建存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合并封装数据
        Map<String, Object> dataStore = new HashMap<>();
        dataStore.put("storeLayer", storeLayer);
        dataStore.put("layerAndAdded", layerAndAdded);
        dataStore.put("layerAndAttr", layerAndAttr);
        // 6.返回存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合
        return dataStore;
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
        List<Map<String, Object>> layerAndAdded = Dbo.queryList("select t1.dsl_id,t2.* from "
                + Data_store_layer.TableName + " t1 left join " + Data_store_layer_added.TableName +
                " t2 on t1.dsl_id=t2.dsl_id where t1.dsl_id=?", dsl_id);
        // 4.根据权限数据存储层配置ID关联查询获取数据存储层配置与数据存储层配置属性信息
        List<Map<String, Object>> layerAndAttr = Dbo.queryList("select t1.dsl_id,t2.* from "
                + Data_store_layer.TableName + " t1 left join " + Data_store_layer_attr.TableName +
                " t2 on t1.dsl_id=t2.dsl_id where t1.dsl_id=?", dsl_id);
        // 5.封装数据存储附件信息、数据存储层配置属性数据
        storeLayer.put("layerAndAdded", layerAndAdded);
        storeLayer.put("layerAndAttr", layerAndAttr);
        // 6.返回存放数据存储层配置信息、数据存储附加信息、数据存储层配置属性信息的集合
        return storeLayer;
    }

}
