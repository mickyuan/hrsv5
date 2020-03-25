package hrds.k.biz.dbm.dataimport;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.FileUploadUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ExcelUtil;
import hrds.k.biz.dbm.dataimport.commons.ImportData;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;

@DocClass(desc = "数据对标元管理-标准数据导入类", author = "BY-HLL", createdate = "2020/2/21 0021 下午 05:56")
public class DbmDataImportAction extends BaseAction {

    @Method(desc = "批量导入标准元数据信息",
            logicStep = "批量导入标准元数据信息")
    @Param(name = "pathName", desc = "excel文件的全路径", range = "String")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    @UploadFile
    public void importExcelData(String pathName) {
        //获取文件对象
        File excelFile = FileUploadUtil.getUploadedFile(pathName);
        if (!excelFile.exists()) {
            throw new BusinessException("excel文件不存在!");
        }
        //获取excel的Workbook对象
        Workbook workbook;
        try {
            workbook = ExcelUtil.getWorkbookFromExcel(excelFile);
        } catch (IOException e) {
            throw new BusinessException("获取excel数据失败!");
        }
        //导入标准分类信息
        ImportData.importDbmSortInfoData(workbook, getUser());
        //导入代码类信息
        ImportData.importDbmCodeTypeInfoData(workbook, getUser());
        //导入代码项信息
        ImportData.importDbmCodeItemInfoData(workbook);
        //导入标准信息
        ImportData.importDbmNormbasicData(workbook, getUser());
    }
}
