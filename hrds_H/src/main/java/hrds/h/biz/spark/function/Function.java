package hrds.h.biz.spark.function;

public class Function {

	private String name;
	private String className;
	private String dataType;

	public Function(String name, String className, String dataType) {

		this.name = name;
		this.className = className;
		this.dataType = dataType;
	}

	public Function(String[] funcArray) {

		this(funcArray[0], funcArray[1], funcArray[2]);
	}

	/** 
	 * name. 
	 * 
	 * @return  the name 
	 * @since   JDK 1.7
	 */
	public String getName() {

		return name;
	}

	/** 
	 * name. 
	 * 
	 * @param   name    the name to set 
	 * @since   JDK 1.7 
	 */
	public void setName(String name) {

		this.name = name;
	}

	/** 
	 * className. 
	 * 
	 * @return  the className 
	 * @since   JDK 1.7
	 */
	public String getClassName() {

		return className;
	}

	/** 
	 * className. 
	 * 
	 * @param   className    the className to set 
	 * @since   JDK 1.7 
	 */
	public void setClassName(String className) {

		this.className = className;
	}

	/** 
	 * dateType. 
	 * 
	 * @return  the dateType 
	 * @since   JDK 1.7
	 */
	public String getDateType() {

		return dataType;
	}

	/** 
	 * dateType. 
	 * 
	 * @param   dateType    the dateType to set 
	 * @since   JDK 1.7 
	 */
	public void setDateType(String dateType) {

		this.dataType = dateType;
	}

	/** 
	 * TODO 简单描述该方法的实现功能（可选）. 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return "Function [name=" + name + ", className=" + className + ", dataType=" + dataType + "]";
	}

}
