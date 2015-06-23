package org.zywx.wbpalmstar.plugin.citylistview.view;

public class SortModel {

	private String name; // 显示的数据
	private String sortLetters; // 显示数据拼音的首字母
	private String pinyin;
	private boolean isHiddenLine;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public boolean isHiddenLine() {
		return isHiddenLine;
	}

	public void setHiddenLine(boolean isHiddenLine) {
		this.isHiddenLine = isHiddenLine;
	}
	
	
}
