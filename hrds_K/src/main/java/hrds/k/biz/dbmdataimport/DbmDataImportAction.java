package hrds.k.biz.dbmdataimport;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.FileUploadUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ExcelUtil;
import hrds.k.biz.dbmdataimport.commons.ImportData;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;

@DocClass(desc = "数据对标元管理-标准数据导入类", author = "BY-HLL", createdate = "2020/2/21 0021 下午 05:56")
public class DbmDataImportAction extends BaseAction {

    @Method(desc = "导入标准分类信息",
            logicStep = "导入标准分类信息")
    @Param(name = "pathName", desc = "excel文件的全路径", range = "String")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    @UploadFile
    public void importExcelData(String pathName) {
        //获取excel的Workbook对象
//        pathName = "E:\\hyren\\hyrenserv\\hrsv5\\hrds_K\\src\\test\\java\\hrds\\k\\biz\\dbmdataimport\\upload\\dbm_import_test.xlsx";
        File excelFile = FileUploadUtil.getUploadedFile(pathName);
        if (!excelFile.exists()) {
            throw new BusinessException("excel文件不存在!");
        }
        Workbook workbook;
        try {
            workbook = ExcelUtil.getWorkbookFromExcel(excelFile);
        } catch (IOException e) {
            throw new BusinessException("获取excel数据失败!");
        }
        //导入标准分类信息
        ImportData.importDbmSortInfoData(workbook, getUser());
        //导入代码类信息
//        importDbmCodeTypeInfoData(workbook);
        //导入代码项信息
        //导入标准信息
//        importDbmNormbasicData(workbook);
    }
}
