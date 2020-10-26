package hrds.h.biz.spark.initialize;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SparkConfLoader {
    private static final Logger logger = LogManager.getLogger();

    static Map<String, String> load(File configFile) throws IOException {
        final Map<String, String> configMap = new HashMap<>();
        FileUtils.readLines(configFile, StandardCharsets.UTF_8)
                .stream()
                .filter(line -> !line.trim().isEmpty())//去除空行
                .filter(line -> !line.startsWith("#"))//去除注释行
                .map(a -> a.split("="))
                .filter(confArr -> confArr.length == 2)
                .forEach(a -> configMap.put(a[0].trim(), a[1].trim()));
        return configMap;
    }

    static void setConf(SparkSession.Builder builder) {
        try {
            SparkConfLoader
                    .load(new File("resources/spark.conf"))
                    .forEach((k, v) -> {
                        builder.config(k, v);
                        logger.info("  {}:{}", k, v);
                    });
        } catch (IOException e) {
            logger.warn("读取spark配置文件失败，使用 spark 默认配置参数。", e);
        }
    }
}
