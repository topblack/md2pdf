package com.perkinelmer.pdfutils.model;

public class OutlineItem {
	private String content;
	
	private String slug;
	
	private int lvl;
	
	private int i;
	
	private int seen;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getSeen() {
		return seen;
	}

	public void setSeen(int seen) {
		this.seen = seen;
	}
	
}
