package io.jackrams;

import io.jackrams.beans.TestBean;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestExcelUtlis {
  @Test
  public void testExport() throws Exception{
    List<TestBean> testBeans = new ArrayList<>();
    for (int i=0;i<10000;i++){
      TestBean testBean =new TestBean();
      testBean.setAge((int)(Math.abs(Math.random()*100)));
      testBean.setEmpNo(i+"");
      testBean.setPeopleId("44564546463664"+i);
      testBean.setName("Name:"+i);
      testBean.setSarly(Math.random()*10000);
      testBeans.add(testBean);
    }

    ExcelUtils.exportExcel(TestBean.class,testBeans,new FileOutputStream(new File("d:/tests.xlsx")),new File("temp.xlsx"),new HashMap<String, XSSFCellStyle>(),"test");

  }
  @Test
  public void testImport() throws Exception{
    List<TestBean> testBeans = ExcelUtils.importExcel(TestBean.class,new File("d:/tests.xlsx"));
    for (TestBean testBean : testBeans){
      System.out.print("Name="+testBean.getName());
      System.out.print("\tPeopleId="+testBean.getPeopleId());
      System.out.print("\tAge="+testBean.getAge());
      System.out.print("\tSarly="+testBean.getSarly());
      System.out.print("\tEmpNo="+testBean.getEmpNo());
      System.out.println();
    }
    System.out.println(testBeans.size());
  }



}
