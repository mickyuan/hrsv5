package hrds.agent.job.biz.core.dfstage.fileparser;

import java.io.IOException;
import java.util.List;

/**
 * FileParserInterface
 * date: 2020/4/21 16:42
 * author: zxz
 */
public interface FileParserInterface {

	String parserFile();

	void dealLine(List<String> lineList) throws IOException;

	void stopStream() throws IOException;
}
