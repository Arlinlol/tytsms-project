package generator;

public class UserField {
	private String name;// 生成模板字段的名字，和POJO中对于
	private String title;// 生成模板中表格字段名称，和POJO的中@Title对应
	private String type;// 模板格式，根据格式确定生成页面中的input类型

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
