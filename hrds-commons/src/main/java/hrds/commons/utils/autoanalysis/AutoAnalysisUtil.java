package hrds.commons.utils.autoanalysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.AxisType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "仪表板工具类", author = "dhw", createdate = "2020/10/20 17:24")
public class AutoAnalysisUtil {

	@Method(desc = "根据仪表板id获取数据仪表板信息表数据", logicStep = "1.根据仪表板id获取数据仪表板信息表数据" +
			"2.获取仪表板边框组件信息表信息" +
			"3.查询关联信息表" +
			"4.查询仪表板标题表与字体表信息" +
			"5.查询仪表板分割线表信息" +
			"6.返回仪表盘信息")
	@Param(name = "dashboard_id", desc = "仪表板id", range = "新建仪表盘的时候生成")
	@Param(name = "db", desc = "连接对象", range = "无限制")
	@Return(desc = "返回仪表盘信息", range = "无限制")
	public static Map<String, Object> getDashboardInfoById(long dashboard_id, DatabaseWrapper db) {
		Map<String, Object> resultMap = new HashMap<>();
		// 1.根据仪表板id获取数据仪表板信息表数据
		Map<String, Object> dashboardInfo = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_dashboard_info.TableName + " WHERE dashboard_id=?",
				dashboard_id);
		resultMap.put("auto_dashboard_info", dashboardInfo);
		// 2.获取仪表板边框组件信息表信息
		List<Auto_frame_info> frameInfoList = SqlOperator.queryList(db, Auto_frame_info.class,
				"SELECT * FROM " + Auto_frame_info.TableName + " WHERE dashboard_id = ?", dashboard_id);
		List<Map<String, Object>> dashboardList = new ArrayList<>();
		if (!frameInfoList.isEmpty()) {
			for (Auto_frame_info auto_frame_info : frameInfoList) {
				Map<String, Object> frameMap = new HashMap<>();
				setGridLayout(frameMap, auto_frame_info.getX_axis_coord(), auto_frame_info.getY_axis_coord(),
						auto_frame_info.getLength(), auto_frame_info.getWidth(),
						auto_frame_info.getFrame_id(), auto_frame_info.getFrame_id());
				frameMap.put("label", "2");
				dashboardList.add(frameMap);
			}
			resultMap.put("frameInfo", frameInfoList);
		}
		// 3.查询关联信息表
		List<Auto_asso_info> autoAssoInfoList = SqlOperator.queryList(db, Auto_asso_info.class,
				"SELECT * FROM " + Auto_asso_info.TableName + " WHERE dashboard_id = ?", dashboard_id);
		List<Auto_comp_sum> autoCompSumList = new ArrayList<>();
		if (!autoAssoInfoList.isEmpty()) {
			for (Auto_asso_info auto_asso_info : autoAssoInfoList) {
				Map<String, Object> componentMap =
						getVisualComponentInfoById(auto_asso_info.getComponent_id(), Dbo.db());
				Auto_comp_sum auto_comp_sum = JsonUtil.toObjectSafety(
						JsonUtil.toJson(componentMap.get("compSum")), Auto_comp_sum.class)
						.orElseThrow(() -> new BusinessException("转换实体失败"));
				setGridLayout(componentMap, auto_asso_info.getX_axis_coord(), auto_asso_info.getY_axis_coord(),
						auto_asso_info.getLength().longValue(), auto_asso_info.getWidth().longValue(),
						auto_asso_info.getComponent_id(), auto_asso_info.getComponent_id());
				autoCompSumList.add(auto_comp_sum);
				resultMap.put(String.valueOf(auto_asso_info.getComponent_id()), auto_comp_sum.getComponent_buffer());
				dashboardList.add(componentMap);
			}
			resultMap.put("autoCompSums", autoCompSumList);
		}
		// 4.查询仪表板标题表与字体表信息
		List<Map<String, Object>> labelAndFontList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_label_info.TableName + " T1 LEFT JOIN "
						+ Auto_font_info.TableName + " T2 ON T1.label_id = T2.font_corr_id" +
						" AND T2.font_corr_tname = ? WHERE dashboard_id = ?",
				Auto_label_info.TableName, dashboard_id);
		if (!labelAndFontList.isEmpty()) {
			for (Map<String, Object> map : labelAndFontList) {
				Auto_label_info auto_label_info = JsonUtil.toObjectSafety(
						JsonUtil.toJson(map), Auto_label_info.class)
						.orElseThrow(() -> new BusinessException("实体转换失败"));
				Auto_font_info auto_font_info = JsonUtil.toObjectSafety(
						JsonUtil.toJson(map), Auto_font_info.class)
						.orElseThrow(() -> new BusinessException("实体转换失败"));
				map.put("textStyle", auto_font_info);
				Map<String, Object> labelMap = new HashMap<>();
				setGridLayout(labelMap, auto_label_info.getX_axis_coord(), auto_label_info.getY_axis_coord(),
						auto_label_info.getLength().longValue(), auto_label_info.getWidth().longValue(),
						auto_label_info.getLabel_id(), auto_label_info.getLabel_id());
				labelMap.put("label", "0");
				Map<String, Object> contentColorSize = new HashMap<>();
				contentColorSize.put("label_content", auto_label_info.getLabel_content());
				contentColorSize.put("label_color", auto_label_info.getLabel_color());
				contentColorSize.put("label_size", auto_label_info.getLabel_size());
				resultMap.put(auto_label_info.getLabel_id().toString(), contentColorSize);
				dashboardList.add(labelMap);
			}
			resultMap.put("labelAndFont", labelAndFontList);
		}
		// 5.查询仪表板分割线表信息
		List<Auto_line_info> autoLineInfoList = SqlOperator.queryList(db, Auto_line_info.class,
				"SELECT * FROM " + Auto_line_info.TableName + " WHERE dashboard_id = ?", dashboard_id);
		if (!autoLineInfoList.isEmpty()) {
			for (Auto_line_info auto_line_info : autoLineInfoList) {
				Map<String, Object> lineMap = new HashMap<>();
				setGridLayout(lineMap, auto_line_info.getX_axis_coord(), auto_line_info.getY_axis_coord(),
						auto_line_info.getLine_length(), auto_line_info.getLine_weight(),
						auto_line_info.getLine_id(), auto_line_info.getLine_id());
				lineMap.put("label", "1");
				JSONObject contentColorType = new JSONObject();
				contentColorType.put("line_color", auto_line_info.getLine_color());
				contentColorType.put("line_type", auto_line_info.getLine_type());
				resultMap.put(auto_line_info.getLine_id().toString(), contentColorType);
				dashboardList.add(lineMap);
			}
			resultMap.put("autoLineInfo", autoLineInfoList);
		}
		resultMap.put("layout", dashboardList);
		// 6.返回仪表盘信息
		return resultMap;
	}

	@Method(desc = "根据可视化组件ID查看可视化组件信息", logicStep = "1.查询组件汇总表" +
			"2.根据组件id查询组件条件表" +
			"3.根据组件id查询组件分组表" +
			"4.根据组件id查询组件数据汇总信息表" +
			"5.根据组件id查询组件横纵纵轴信息表 字段显示类型show_type使用IsFlag代码项 0:x轴，1:y轴" +
			"6.根据组件id查询图表标题字体属性信息表" +
			"7.根据组件id查询x,y轴标签字体属性信息表" +
			"8.根据组件id查询x/y轴配置信息表" +
			"9.根据组件id查询x/y轴标签配置信息表" +
			"10.根据组件id查询x/y轴线配置信息表" +
			"11.根据组件id查询二维表样式信息表" +
			"12.根据组件id查询图表配置信息表" +
			"13.根据组件id查询文本标签信息表" +
			"14.根据组件id查询图例信息表" +
			"15.获取组件查询结果" +
			"16.获取图表结果" +
			"17.获取列信息" +
			"18.返回根据可视化组件ID查看可视化组件信息")
	@Param(name = "component_id", desc = "组件ID", range = "创建组件时生成")
	@Param(name = "db", desc = "连接对象", range = "无限制")
	@Return(desc = "返回根据可视化组件ID查看可视化组件信息", range = "无限制")
	public static Map<String, Object> getVisualComponentInfoById(long component_id, DatabaseWrapper db) {
		Map<String, Object> resultMap = new HashMap<>();
		// 1.查询组件汇总表
		Auto_comp_sum auto_comp_sum = SqlOperator.queryOneObject(db, Auto_comp_sum.class,
				"SELECT * FROM " + Auto_comp_sum.TableName + " WHERE component_id = ?",
				component_id)
				.orElseThrow(() -> new BusinessException("sql查询错误或者映射实体失败"));
		resultMap.put("compSum", auto_comp_sum);
		// 2.根据组件id查询组件条件表
		List<Map<String, Object>> compCondList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_comp_cond.TableName + " WHERE component_id = ?"
				, component_id);
		resultMap.put("compCond", JSONArray.parseArray(JSON.toJSONString(compCondList)));
		// 3.根据组件id查询组件分组表
		List<Map<String, Object>> compGroupList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_comp_group.TableName + " WHERE component_id = ?",
				component_id);
		resultMap.put("compGroup", JSONArray.parseArray(JSON.toJSONString(compGroupList)));
		// 4.根据组件id查询组件数据汇总信息表
		List<Map<String, Object>> compDataSumList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_comp_data_sum.TableName + " WHERE component_id = ?",
				component_id);
		resultMap.put("compDataSum", compDataSumList);
		// 5.根据组件id查询组件横纵纵轴信息表 字段显示类型show_type使用IsFlag代码项 0:x轴，1:y轴
		List<Map<String, Object>> xAxisColList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_axis_col_info.TableName
						+ " WHERE component_id = ? AND show_type = ?",
				component_id, AxisType.XAxis.getCode());
		resultMap.put("xAxisCol", JSONArray.parseArray(JSON.toJSONString(xAxisColList)));
		List<Map<String, Object>> yAxisColList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_axis_col_info.TableName
						+ " WHERE component_id = ? AND show_type = ?",
				component_id, AxisType.YAxis.getCode());
		resultMap.put("yAxisCol", JSONArray.parseArray(JSON.toJSONString(yAxisColList)));
		// 6.根据组件id查询图表标题字体属性信息表
		Map<String, Object> fontInfoMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_font_info.TableName + " WHERE font_corr_id = ?",
				component_id);
		resultMap.put("titleFontInfo", JSONArray.parseObject(JSON.toJSONString(fontInfoMap)));
		// 7.根据组件id查询x,y轴标签字体属性信息表,因为x/y轴字体是一样的,保存的时候是以x轴编号保存所以这里以x轴编号查询
		Map<String, Object> xFontInfoMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_font_info.TableName
						+ " WHERE font_corr_id IN (SELECT axis_id FROM " + Auto_axis_info.TableName
						+ " WHERE component_id = ? AND axis_type = ?)",
				component_id, AxisType.XAxis.getCode());
		resultMap.put("axisFontInfo", JSONArray.parseObject(JSON.toJSONString(xFontInfoMap)));
		// 8.根据组件id查询x/y轴配置信息表
		List<Map<String, Object>> xAxisInfoList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_axis_info.TableName + " WHERE component_id = ? AND axis_type = ?",
				component_id, AxisType.XAxis.getCode());
		resultMap.put("xAxisInfo", JSONArray.parseArray(JSON.toJSONString(xAxisInfoList)));
		List<Map<String, Object>> yAxisInfoList = SqlOperator.queryList(db,
				"SELECT * FROM " + Auto_axis_info.TableName + " WHERE component_id = ? AND axis_type = ?",
				component_id, AxisType.YAxis.getCode());
		resultMap.put("yAxisInfo", JSONArray.parseArray(JSON.toJSONString(yAxisInfoList)));
		// 9.根据组件id查询x/y轴标签配置信息表
		Map<String, Object> xAxislabelMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_axislabel_info.TableName
						+ " WHERE axis_id IN (SELECT axis_id FROM " + Auto_axis_info.TableName
						+ " WHERE component_id = ? AND axis_type = ?)",
				component_id, AxisType.XAxis.getCode());
		resultMap.put("xAxisLabel", JSONArray.parseObject(JSON.toJSONString(xAxislabelMap)));
		Map<String, Object> yAxislabelMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_axislabel_info.TableName
						+ " WHERE axis_id IN (SELECT axis_id FROM " + Auto_axis_info.TableName
						+ " WHERE component_id = ? AND axis_type = ?)",
				component_id, AxisType.YAxis.getCode());
		resultMap.put("yAxisLabel", JSONArray.parseObject(JSON.toJSONString(yAxislabelMap)));
		// 10.根据组件id查询x/y轴线配置信息表
		Map<String, Object> xAxislineMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_axisline_info.TableName
						+ " WHERE axis_id IN (SELECT axis_id FROM " + Auto_axis_info.TableName
						+ " WHERE component_id = ? AND axis_type = ?)",
				component_id, AxisType.XAxis.getCode());
		resultMap.put("xAxisLine", JSONArray.parseObject(JSON.toJSONString(xAxislineMap)));
		Map<String, Object> yAxislineMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_axisline_info.TableName
						+ " WHERE axis_id IN (SELECT axis_id FROM " + Auto_axis_info.TableName
						+ " WHERE component_id = ? AND axis_type = ?)",
				component_id, AxisType.YAxis.getCode());
		resultMap.put("yAxisLine", JSONArray.parseObject(JSON.toJSONString(yAxislineMap)));
		// 11.根据组件id查询二维表样式信息表
		Map<String, Object> tableInfoMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_table_info.TableName + " WHERE component_id = ?",
				component_id);
		resultMap.put("twoDimensionalTable", JSONArray.parseObject(JSON.toJSONString(tableInfoMap)));
		// 12.根据组件id查询图表配置信息表
		Map<String, Object> chartsconfigMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_chartsconfig.TableName + " WHERE component_id = ?",
				component_id);
		resultMap.put("chartsconfig", JSONArray.parseObject(JSON.toJSONString(chartsconfigMap)));
		// 13.根据组件id查询文本标签信息表
		Map<String, Object> textLabelMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_label.TableName + " WHERE label_corr_id = ?", component_id);
		resultMap.put("textLabel", JSONArray.parseObject(JSON.toJSONString(textLabelMap)));
		// 14.根据组件id查询图例信息表
		Map<String, Object> legendMap = SqlOperator.queryOneObject(db,
				"SELECT * FROM " + Auto_legend_info.TableName + " WHERE component_id = ?", component_id);
		resultMap.put("legendInfo", JSONArray.parseObject(JSON.toJSONString(legendMap)));
		// 返回根据可视化组件ID查看可视化组件信息
		return resultMap;
	}

	@Method(desc = "设置仪表盘网格布局参数", logicStep = "1.设置仪表盘网格布局参数")
	@Param(name = "labelMap", desc = "封装仪表盘网格布局参数集合", range = "无限制")
	@Param(name = "x", desc = "标识栅格元素位于第几列", range = "需为自然数")
	@Param(name = "y", desc = "标识栅格元素位于第几行", range = "需为自然数")
	@Param(name = "w", desc = "标识栅格元素的初始宽度", range = "需为自然数")
	@Param(name = "h", desc = "标识栅格元素的初始高度", range = "需为自然数")
	@Param(name = "i", desc = "栅格中元素的ID", range = "需为自然数")
	@Param(name = "type", desc = "对应组件ID", range = "新增对应组件时生成")
	private static void setGridLayout(Map<String, Object> map, Integer x, Integer y,
	                                  Long w, Long h, Object i, Long id) {
		// 1.设置仪表盘网格布局参数
		map.put("x", x);
		map.put("y", y);
		map.put("w", w);
		map.put("h", h);
		map.put("i", i);
		map.put("type", String.valueOf(id));
		map.put("static", false);
	}
}
