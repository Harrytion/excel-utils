package io.jackrams.processors;

import io.jackrams.annotations.ExportField;
import io.jackrams.annotations.TypeEnum;
import io.jackrams.annotations.ViewType;
import io.jackrams.domain.ExportFieldDomain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.List;

import static io.jackrams.processors.TypeMap.*;

public class ExportFieldProcessor {
  private transient Log log = LogFactory.getLog(getClass());

  public void doFieldAnnotation(Class clazz, List<ExportFieldDomain> fieldDomains) throws Exception{
    Field[] declaredFields = clazz.getDeclaredFields();
    for (Field field : declaredFields)
      if (field.isAnnotationPresent(ExportField.class)) {
        field.setAccessible(true);
        ExportField exportField = field.getAnnotation(ExportField.class);
        ExportFieldDomain exportFieldDomain = getExportFieldDomain(field, exportField);
        fieldDomains.add(exportFieldDomain);
      }



  }

  public ExportFieldDomain getExportFieldDomain(Field field,ExportField exportField){
    ExportFieldDomain domain = new ExportFieldDomain();
    domain.setField(field);
    domain.setFieldName(field.getName());
    TypeEnum type = exportField.type();
    if(TypeEnum.Null==type) type=getFieldType(field);
    domain.setType(type);
    String title = exportField.title();
    if(StringUtils.isEmpty(title)) title=field.getName();
    domain.setTitle(title);
    ViewType viewType = exportField.viewType();
    domain.setViewType(viewType);
    String formate = exportField.formate();
    domain.setFormat(formate);
    String value = exportField.value();
    domain.setValue(value);
    return domain;
  }




}
