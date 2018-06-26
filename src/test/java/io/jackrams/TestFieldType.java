package io.jackrams;

import io.jackrams.processors.ExportFieldProcessor;
import org.junit.Test;

import java.lang.reflect.Field;

public class TestFieldType {
  @Test
  public void test1(){
    Class<TestBean> testBeanClass = TestBean.class;
    Field[] declaredFields = testBeanClass.getDeclaredFields();
    ExportFieldProcessor exportFieldProcessor = new ExportFieldProcessor();
    for (Field field : declaredFields){
   //   System.out.println(exportFieldProcessor.getFieldType(field));
    }
  }

}
