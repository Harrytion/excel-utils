package io.jackrams.processors;

import io.jackrams.annotations.TypeEnum;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TypeMap {
  public static final Map<Class<?>,TypeEnum> classTypeEnumMap = new HashMap<>();

  static {
    classTypeEnumMap.put(Integer.class,TypeEnum.Integer);
    classTypeEnumMap.put(int.class,TypeEnum.Integer);
    classTypeEnumMap.put(Short.class,TypeEnum.Short);
    classTypeEnumMap.put(short.class,TypeEnum.Short);
    classTypeEnumMap.put(Byte.class,TypeEnum.Byte);
    classTypeEnumMap.put(byte.class,TypeEnum.Byte);
    classTypeEnumMap.put(Long.class,TypeEnum.Long);
    classTypeEnumMap.put(long.class,TypeEnum.Long);
    classTypeEnumMap.put(Character.class,TypeEnum.Character);
    classTypeEnumMap.put(char.class,TypeEnum.Character);
    classTypeEnumMap.put(Boolean.class,TypeEnum.Boolean);
    classTypeEnumMap.put(boolean.class,TypeEnum.Boolean);
    classTypeEnumMap.put(Double.class,TypeEnum.Double);
    classTypeEnumMap.put(double.class,TypeEnum.Double);
    classTypeEnumMap.put(Byte[].class,TypeEnum.ByteArray);
    classTypeEnumMap.put(byte[].class,TypeEnum.ByteArray);
    classTypeEnumMap.put(Float.class,TypeEnum.Float);
    classTypeEnumMap.put(float.class,TypeEnum.Float);
    classTypeEnumMap.put(Date.class,TypeEnum.Date);
    classTypeEnumMap.put(String.class,TypeEnum.String);
  }
  public static TypeEnum getFieldType(Field field){
    TypeEnum typeEnum = classTypeEnumMap.get(field.getType());
    System.out.println("F:"+field.getName()+"\t"+typeEnum);
    if(!(null== typeEnum)) return typeEnum;
    return TypeEnum.String;
  }
}
