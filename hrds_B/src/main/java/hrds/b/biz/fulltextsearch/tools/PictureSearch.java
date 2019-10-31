package hrds.b.biz.fulltextsearch.tools;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import hrds.commons.exception.BusinessException;

/**
 * <p>类名: PictureSeach </p>
 * <p>类说明: 图片搜索类</p>
 * @author BY-HLL
 * @date 2019/10/10 0010 上午 09:51
 * @since JDK1.8
 */
public class PictureSearch {

	/**
	 * <p>方法名: pictureSearchResult</p>
	 * <p>方法说明: 以图搜图</p>
	 *
	 * @param imageAddress 含义： 以图搜图上传图片地址
	 * @return search 含义：以图搜图搜索返回结果集
	 */
	public Result pictureSearchResult(String imageAddress) {
		Result search = new Result();
		if (!StringUtil.isBlank(imageAddress)) {
			throw new BusinessException("以图搜图的方法未实现！");
		}
		return search;
	}

}
