package io.jackrams.beans;

import io.jackrams.annotations.ExportField;
import io.jackrams.annotations.ImportField;

public class TestBean {
  @ExportField(title = "身份证")
  @ImportField(title = "身份证",titles = "身份证号")
  private String peopleId;
  @ExportField(title = "姓名")
  @ImportField(title = "姓名,名字")
  private String name;
  @ExportField(title = "工号")
  @ImportField(title = "工号,员工编号")
  private String empNo;
  @ExportField(title = "年龄")
  @ImportField(title = "年龄")
  private int age;
  @ExportField(title = "薪水")
  @ImportField(titles = "工资,薪水")
  private Double sarly;

  public String getPeopleId() {
    return peopleId;
  }

  public void setPeopleId(String peopleId) {
    this.peopleId = peopleId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmpNo() {
    return empNo;
  }

  public void setEmpNo(String empNo) {
    this.empNo = empNo;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Double getSarly() {
    return sarly;
  }

  public void setSarly(Double sarly) {
    this.sarly = sarly;
  }

  
}
