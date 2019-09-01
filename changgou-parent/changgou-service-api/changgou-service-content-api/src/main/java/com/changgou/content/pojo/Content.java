package com.changgou.content.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;

/****
 * @Author:shenkunlin
 * @Description:Content构建
 * @Date 2019/6/14 19:13
 *****/
@ApiModel(description = "Content",value = "Content")
@Table(name="tb_content")
public class Content implements Serializable{

	@ApiModelProperty(value = "",required = false)
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;//

	@ApiModelProperty(value = "内容类目ID",required = false)
    @Column(name = "category_id")
	private Long categoryId;//内容类目ID

	@ApiModelProperty(value = "内容标题",required = false)
    @Column(name = "title")
	private String title;//内容标题

	@ApiModelProperty(value = "链接",required = false)
    @Column(name = "url")
	private String url;//链接

	@ApiModelProperty(value = "图片绝对路径",required = false)
    @Column(name = "pic")
	private String pic;//图片绝对路径

	@ApiModelProperty(value = "状态,0无效，1有效",required = false)
    @Column(name = "status")
	private String status;//状态,0无效，1有效

	@ApiModelProperty(value = "排序",required = false)
    @Column(name = "sort_order")
	private Integer sortOrder;//排序



	//get方法
	public Long getId() {
		return id;
	}

	//set方法
	public void setId(Long id) {
		this.id = id;
	}
	//get方法
	public Long getCategoryId() {
		return categoryId;
	}

	//set方法
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	//get方法
	public String getTitle() {
		return title;
	}

	//set方法
	public void setTitle(String title) {
		this.title = title;
	}
	//get方法
	public String getUrl() {
		return url;
	}

	//set方法
	public void setUrl(String url) {
		this.url = url;
	}
	//get方法
	public String getPic() {
		return pic;
	}

	//set方法
	public void setPic(String pic) {
		this.pic = pic;
	}
	//get方法
	public String getStatus() {
		return status;
	}

	//set方法
	public void setStatus(String status) {
		this.status = status;
	}
	//get方法
	public Integer getSortOrder() {
		return sortOrder;
	}

	//set方法
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}


}
