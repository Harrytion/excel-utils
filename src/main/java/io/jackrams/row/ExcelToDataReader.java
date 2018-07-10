package io.jackrams.row;

import io.jackrams.ExcelUtils;
import io.jackrams.annotations.TypeEnum;
import io.jackrams.domain.ImportFieldDomain;
import io.jackrams.processors.ImportFieldProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExcelToDataReader<T> implements ExcelRowReader {

   private List<T> datas =new ArrayList<>();
   private Class<T> clazz ;
   private List<ImportFieldDomain> importFieldDomains =new ArrayList<>();
   private boolean flag =false;
    @Override
    public void getRows(List<String> rowlist) throws Exception{
        if(!flag){
        for (ImportFieldDomain domain : importFieldDomains){
            for (int index=0;index<rowlist.size();index++){
                if(titleToColum(domain.getTitles(),rowlist.get(index),index)!=-1){
                    domain.setIndex(index);
                }
            }
        }

        for (ImportFieldDomain domain : importFieldDomains) {
            if(domain.getIndex()!=null);
            else {
                flag=false;
                break;
            }

            flag=true;
        }

        }else {
            T t = clazz.newInstance();
            for (ImportFieldDomain fieldDomain : importFieldDomains){
                Field field = fieldDomain.getField();
                TypeEnum type = fieldDomain.getType();
                Integer index = fieldDomain.getIndex();
                if(index!=null&&index!=-1)
                field.set(t,ExcelUtils.getValue(type,rowlist.get(index)));
            }
            datas.add(t);

        }

      //  System.out.print(rowlist);
      //  System.out.println();
    }

    public ExcelToDataReader(Class<T> clazz) throws Exception{
           new ImportFieldProcessor().doFieldAnnotation(clazz,importFieldDomains);
           this.clazz=clazz;

    }

    private int titleToColum(Set<String> titles,String title,int index){
        if(titles.contains(title.trim())) return index;
        else return -1;
    }

    public List<T> getDatas() {
        return datas;
    }
}
