package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

@DocClass(desc = "中文转换为英文的工具类", author = "博彦科技", createdate = "2020/4/8 0008 下午 04:51")
public class PinyinUtil {

    //格式化配置
    private HanyuPinyinOutputFormat hanyuPinyinOutputFormat;

    public enum Type {
        //转换后的拼音全为大写
        UPPERCASE(HanyuPinyinCaseType.UPPERCASE),
        //转换后的拼音全为小写
        LOWERCASE(HanyuPinyinCaseType.LOWERCASE);

        private HanyuPinyinCaseType caseType;

        //大小写设置
        Type(HanyuPinyinCaseType caseType) {
            this.caseType = caseType;
        }

        //大小写设置,默认为大写
        protected HanyuPinyinCaseType getType() {
            if (null == caseType) {
                caseType = HanyuPinyinCaseType.UPPERCASE;
            }
            return caseType;
        }
    }

    //中文转换为英文的工具类
    public PinyinUtil() {
        hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
        //设置转换类型，大写、小写等
        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        //设置音调类型
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    @Method(desc = "将中文转换为拼音，默认大写,如果不是中文，则不做处理.",
            logicStep = "将中文转换为拼音，默认大写,如果不是中文，则不做处理.")
    @Param(name = "chinese", desc = "中文字符串", range = "String类型")
    @Return(desc = "中文转换成英文后的字符串", range = "中文转换成英文后的字符串")
    public String toPinYin(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(chinese, Type.UPPERCASE);
    }

    @Method(desc = "将中文转换为拼音，默认大写,如果不是中文，则不做处理.",
            logicStep = "将中文转换为拼音，默认大写,如果不是中文，则不做处理.")
    @Param(name = "chinese", desc = "中文字符串", range = "String类型")
    @Param(name = "type", desc = "转换类型(UPPERCASE:大写,LOWERCASE:小写)", range = "String类型")
    @Return(desc = "中文转换成英文后的字符串", range = "中文转换成英文后的字符串")
    public String toPinYin(String chinese, Type type) throws BadHanyuPinyinOutputFormatCombination {
        //如果待装换字符为空,直接返回""
        if (null == chinese || chinese.trim().length() == 0)
            return "";
        //设置转换后的大小写
        hanyuPinyinOutputFormat.setCaseType(type.getType());
        //初始化返回结果
        StringBuilder py = new StringBuilder();
        String[] t;
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if ((int) c <= 128)
                py.append(c);
            else {
                t = PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
                if (t == null)
                    py.append(c);
                else {
                    py.append(t[0]);
                }
            }
        }
        return py.toString().replaceAll("\\W", "").trim();
    }

    @Method(desc = "将中文字符串转换为拼音，并获取每个中文字符的首字符.",
            logicStep = "将中文字符串转换为拼音，并获取每个中文字符的首字符.")
    @Param(name = "chinese", desc = "中文字符串", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public String toFixPinYin(String chinese) {
        return toFixPinYin(chinese, 1, Type.UPPERCASE);
    }

    @Method(desc = "将中文字符串转换为拼音，并获取每个中文字符前number个字母.",
            logicStep = "将中文字符串转换为拼音，并获取每个中文字符前number个字母")
    @Param(name = "chinese", desc = "中文字符串", range = "String类型")
    @Param(name = "number", desc = "要获取的自定义个数的字符", range = "int类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public String toFixPinYin(String chinese, int number) {
        return toFixPinYin(chinese, number, Type.UPPERCASE);
    }

    @Method(desc = "将中文字符串转换为拼音，并获取每个中文字符前number个字母.",
            logicStep = "将中文字符串转换为拼音，并获取每个中文字符前number个字母")
    @Param(name = "chinese", desc = "中文字符串", range = "String类型")
    @Param(name = "number", desc = "要获取的自定义个数的字符", range = "int类型")
    @Param(name = "type", desc = "转换类型(UPPERCASE:大写,LOWERCASE:小写)", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public String toFixPinYin(String chinese, int number, Type type) {
        if (null == chinese || chinese.trim().length() == 0 || 0 == number) {
            return "";
        }

        hanyuPinyinOutputFormat.setCaseType(type.getType());

        StringBuilder pybf = new StringBuilder();
        char[] arr = chinese.toCharArray();
        for (char c : arr) {
            if (c > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
                    if (temp != null) {
                        for (int j = 0; j < number; j++) {
                            //如果转换后字符长度小于或等于指定长度时，跳过，不再取下一位
                            if (temp[0].length() <= j) {
                                continue;
                            }
                            pybf.append(temp[0].charAt(j));
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(c);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }
}
