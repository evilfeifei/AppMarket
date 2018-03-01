package com.huiyun.vrxf.been;

import java.io.Serializable;
import java.util.List;

public class CategoryFirst implements Serializable {

	private String id;
	private String name;
	List<CategorySecond> category;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CategorySecond> getCategory() {
		return category;
	}

	public void setCategory(List<CategorySecond> category) {
		this.category = category;
	}
}
