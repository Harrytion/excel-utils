package io.jackrams;

import io.jackrams.beans.PoorBzddBean;
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
    for (int i=0;i<10000000 ;i++){
      TestBean testBean =new TestBean();
      testBean.setAge((int)(Math.abs(Math.random()*100)));
      testBean.setEmpNo(i+"");
      testBean.setPeopleId("44564546463664"+i);
      testBean.setName("Name:"+i);
      testBean.setSarly(Math.random()*10000);
      testBean.setDate("1992-21-37");
      testBeans.add(testBean);
    }
    long start = System.currentTimeMillis();

    ExcelUtils.exportExcel(TestBean.class,testBeans,new FileOutputStream(new File("d:/tests.xlsx")),new File("temp.xlsx"),new HashMap<String, XSSFCellStyle>(),"test");

    long end = System.currentTimeMillis();

    long useTime = ((end-start)/1000);
    System.out.println(useTime);
  }
  @Test
  public void testImport() throws Exception{
    long start = System.currentTimeMillis();
    List<TestBean> testBeans = ExcelUtils.importExcel(TestBean.class,new File("d:/tests.xlsx"));

    long end = System.currentTimeMillis();

    long useTime = ((end-start)/1000);
    System.out.println(useTime);
//    for (TestBean testBean : testBeans){
//      System.out.print("Name="+testBean.getName());
//      System.out.print("\tPeopleId="+testBean.getPeopleId());
//      System.out.print("\tAge="+testBean.getAge());
//      System.out.print("\tSarly="+testBean.getSarly());
//      System.out.print("\tEmpNo="+testBean.getEmpNo());
//      System.out.print("\tDate="+testBean.getDate());
//      System.out.println();
//    }
    System.out.println(testBeans.size());
  }
  @Test
  public void testDateImport() throws Exception{
    List<PoorBzddBean> poorBzddBeans = ExcelUtils.importExcel(PoorBzddBean.class, new File("C:\\Users\\Jackrams\\Desktop\\data\\保障兜底模版.xls"));
    for (PoorBzddBean bean :poorBzddBeans){
      System.out.println(bean.getBfrName());
    //  System.out.println(bean.getPeopleId());
      System.out.println(bean.getFfsj());
    }


  }


}
