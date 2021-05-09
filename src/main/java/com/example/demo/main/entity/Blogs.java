package com.example.demo.main.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedBy;

import com.example.demo.custom.BaseEntity;

/**
 * 博客表
 * @author mcy
 *
 */
@Entity
public class Blogs extends BaseEntity<Integer> {
	private String title;	//标题
	private String text;	//文本内容
	private String TextType;	//文章类型
	private String classify;	//博客分类
	private String blogStatic;	//博客状态
	private SysUser	user;		//博主id
	private boolean open;		//是否公开
	private String suggest;		//建议
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(columnDefinition="LONGTEXT")
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTextType() {
		return TextType;
	}
	public void setTextType(String textType) {
		TextType = textType;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getBlogStatic() {
		return blogStatic;
	}
	public void setBlogStatic(String blogStatic) {
		this.blogStatic = blogStatic;
	}
	@ManyToOne
	@CreatedBy
	public SysUser getUser() {
		return user;
	}
	public void setUser(SysUser user) {
		this.user = user;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public String getSuggest() {
		return suggest;
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	
}
