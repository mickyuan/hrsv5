package hrds.h.biz.spark.function;

import hrds.commons.exception.AppSystemException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FunctionsReader {

    private static final Logger logger = LogManager.getLogger(FunctionsReader.class);
    private static final File funcProperty = new File("conf/function.properties");
    private final List<Function> functions = new ArrayList<>();

    public FunctionsReader() {

        try {
            initFunctions();
        } catch (IOException e) {
            throw new AppSystemException("从配置文件中获取function信息失败：", e);
        }
        validateClass(functions);//确保配置的function类都存在
    }

    private void initFunctions() throws IOException {

        if (!funcProperty.exists()) {
            logger.debug("配置文件都没有，没有函数需要注册！");
            return;
        }

        // TODO readLines needs to add character encoding to UTF-8 after Hadoop 3.0
        List<String> readLines = FileUtils.readLines(funcProperty, "utf-8");

        for (String funcStr : readLines) {
            //不为空，且不是注释
            if (StringUtils.isEmpty(funcStr) || StringUtils.startsWith(funcStr, "#")) {
                continue;
            }
            String[] funcArray = StringUtils.split(funcStr.trim());
            if (funcArray.length != 3) {
                throw new AppSystemException("错误行：" + funcStr);
            }
            functions.add(new Function(funcArray));
        }
    }

    private void validateClass(List<Function> functions) {

        for (Function function : functions) {
            String className = function.getClassName();
            try {
                ClassUtils.getClass(className);
            } catch (ClassNotFoundException e) {
                throw new AppSystemException("类不存在：" + className);
            }
        }
    }

    public List<Function> getFunctions() {

        return functions;
    }

    public Iterator<Function> iterator() {

        return functions.iterator();
    }

    public static void main(String[] args) {

        try {
            FunctionsReader fr = new FunctionsReader();
            List<Function> functions2 = fr.getFunctions();
            System.out.println(functions2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
