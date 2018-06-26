package io.jackrams.domain;

import io.jackrams.annotations.TypeEnum;
import io.jackrams.annotations.ViewType;

import java.lang.reflect.Field;

public class ExportFieldDomain {
  private String fieldName;
  private TypeEnum type;
  private String title;
  private Field field;
  private String format;
  private String value;
  private ViewType viewType;

  //---------------


  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ViewType getViewType() {
    return viewType;
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
  }

}
