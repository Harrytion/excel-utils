package io.jackrams.processors;

import io.jackrams.annotations.ImportField;
import io.jackrams.annotations.TypeEnum;
import io.jackrams.annotations.ViewType;
import io.jackrams.domain.ImportFieldDomain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.apache.commons.lang3.StringUtils.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.jackrams.processors.TypeMap.*;
public class ImportFieldProcessor  {

  private transient Log log = LogFactory.getLog(getClass());


  public void doFieldAnnotation(Class clazz, List<ImportFieldDomain> fieldDomains) throws Exception{
    log.info("start parse Field annotation ");
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields){
      if(field.isAnnotationPresent(ImportField.class)){
        field.setAccessible(true);
        ImportField importField = field.getAnnotation(ImportField.class);
        ImportFieldDomain domain = getImportFieldDomainWithField(field, importField);
        fieldDomains.add(domain);
      }

    }

    log.info("parse field annotation ends");
  }

  private ImportFieldDomain getImportFieldDomainWithField(Field field, ImportField importField){
    ImportFieldDomain domain =new ImportFieldDomain();
    Set<String> titles = new HashSet<>();
    domain.setField(field);

    String format = importField.format();
    if(isNotEmpty(format)) domain.setFormat(format);

    String title = importField.title();
    if(isNotEmpty(title)) {
         titles.addAll(Arrays.asList(title.split(",")));
    }


    TypeEnum type = getFieldType(field);

    domain.setType(type);

    String strTitles = importField.titles();

    if(isNotEmpty(strTitles)){
      titles.addAll(Arrays.asList(strTitles.split(",")));
    }

    domain.setTitles(titles);
    domain.setTitle(title);

    ViewType viewType = importField.viewType();
    domain.setViewType(viewType);

    String value = importField.value();
    domain.setValue(value);

    return domain;
  }





}
