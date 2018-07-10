package io.jackrams.beans;

import io.jackrams.annotations.ImportField;

/**
 *  保障兜底
 * @author Jackrams
 *
 */
public class PoorBzddBean {
	
	private String id;

	@ImportField(title="姓名")
	private String bfrName;
	

	@ImportField(title="组名")
	private String bzdxZu;
	

	@ImportField(title="户主姓名")
	private String bzdxHzxm;
	

	@ImportField(title="身份证,身份证号,身份证号码")
	private String peopleId;

	@ImportField(title="保障对象姓名")
	private String bzdxXm;
	

	@ImportField(title="补助金额")
	private Double bzje;
	

	@ImportField(title="发放时间")
	private String ffsj;


	private String createTime;
	

	private String creator;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBfrName() {
		return bfrName;
	}
	public void setBfrName(String bfrName) {
		this.bfrName = bfrName;
	}
	public String getBzdxZu() {
		return bzdxZu;
	}
	public void setBzdxZu(String bzdxZu) {
		this.bzdxZu = bzdxZu;
	}
	public String getBzdxHzxm() {
		return bzdxHzxm;
	}
	public void setBzdxHzxm(String bzdxHzxm) {
		this.bzdxHzxm = bzdxHzxm;
	}
	public String getPeopleId() {
		return peopleId;
	}
	public void setPeopleId(String peopleId) {
		this.peopleId = peopleId;
	}
	public String getBzdxXm() {
		return bzdxXm;
	}
	public void setBzdxXm(String bzdxXm) {
		this.bzdxXm = bzdxXm;
	}
	public Double getBzje() {
		return bzje;
	}
	public void setBzje(Double bzje) {
		this.bzje = bzje;
	}
	public String getFfsj() {
		return ffsj;
	}
	public void setFfsj(String ffsj) {
		this.ffsj = ffsj;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
}
