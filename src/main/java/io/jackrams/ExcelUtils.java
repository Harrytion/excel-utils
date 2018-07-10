package io.jackrams;

import io.jackrams.annotations.TypeEnum;
import io.jackrams.annotations.ViewType;
import io.jackrams.domain.ExportFieldDomain;
import io.jackrams.domain.ImportFieldDomain;
import io.jackrams.processors.ExportFieldProcessor;
import io.jackrams.processors.ImportFieldProcessor;
import io.jackrams.row.CellDataType;
import io.jackrams.row.ExcelRowReader;
import io.jackrams.xlsx.XlsxReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ExcelUtils {
  private static final String XML_ENCODING = "UTF-8";


  private static transient Log log = LogFactory.getLog(ExcelUtils.class);

 // private static ReentrantLock exportLock = new ReentrantLock();

 // private static ReentrantLock importLock = new ReentrantLock();


  public static<T> List<T> importExcel(Class<T> tClass,File file) throws Exception{
    List<T> dataList= null;
  //  importLock.lock();
    String fileName = file.getName();
    synchronized (fileName.intern()) {
      try {
        // String name = file.getName();
        dataList = importExcel(tClass, file, fileName.endsWith(".xls"));
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        //  importLock.unlock();
      }
    }
    return dataList;
  }
  //
  public static<T> List<T> importExcel(Class<T> tClass,File file,boolean xls) throws Exception{
    if(null==tClass) throw new RuntimeException("Class  is Null");
    List<T> dataList = new LinkedList<>();
    Workbook workbook = null;
    List<ImportFieldDomain> domains = new ArrayList<>();
    if(xls) {
      new ImportFieldProcessor().doFieldAnnotation(tClass,domains);
      FileInputStream inputStream = new FileInputStream(file);;
      workbook = new HSSFWorkbook(inputStream);

      int index = 0;

      Sheet sheet = null;
      do {
        try {
          sheet = workbook.getSheetAt(index++);
        } catch (Exception e) {
          break;
        }
        List<T> dataListFromSheet = getDataListFromSheet(tClass, sheet, domains);
        dataList.addAll(dataListFromSheet);
      } while (null != sheet);

      workbook.close();
      inputStream.close();
    }else {
      dataList = new XlsxReader<T>(tClass).processAllSheets(file);
    }
    return dataList;
  }

  private static<T> List<T> getDataListFromSheet(Class<T> tClass,Sheet sheet,List<ImportFieldDomain> domainList){

    List<T> dataList = new LinkedList<>();
    try {
      int lastRowNum = sheet.getLastRowNum();
      if(lastRowNum<0) return new ArrayList<>();
      int startIndex=indexMap(sheet,domainList);
      for(int index=startIndex+1;index<=lastRowNum;index++){
        Row row = sheet.getRow(index);
        if(skipNullRow(row,index)!=index) continue;
        dataList.add(getDataFromRow(tClass,row,domainList));
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return dataList;
  }

  private static <T> T getDataFromRow(Class<T> tClass,Row row,List<ImportFieldDomain> domainList) throws Exception{


    T t =tClass.newInstance();
    for(ImportFieldDomain domain : domainList){
      if(null==domain.getIndex()) continue;
       TypeEnum type = domain.getType();
      Field field = domain.getField();
      int index = domain.getIndex();
      Cell cell = row.getCell(index);
      field.set(t,getValue(type,getCellValue(cell)));
    }
    return t;
  }

  private static int  indexMap(Sheet sheet,List<ImportFieldDomain> domainList){
    int lastRowNum = sheet.getLastRowNum();
    if(lastRowNum<0) throw new RuntimeException("size=0");
    int startIndex=0;
    Set<Integer> indexSet = new HashSet<>();
    Set<String>  titleSet = new HashSet<>();
  //  boolean flag=false;
    for (int rowIndex=0;rowIndex<lastRowNum;rowIndex++){
        for (ImportFieldDomain domain : domainList){
          Row row =sheet.getRow(rowIndex);
          if(skipNullRow(row,rowIndex)!=rowIndex) continue;
          if(null==row) continue;
          short lastCellNum = row.getLastCellNum();
       //   boolean[] flags= new boolean[lastCellNum-1];
          for(short cellIndex=0;cellIndex<=lastCellNum;cellIndex++){
            Cell cell = row.getCell(cellIndex);
            Set<String> titles = domain.getTitles();
            String cellValue = getCellValue(cell);
            if(titles.contains(cellValue)) {
              if(titleSet.addAll(titles)) {
                if (indexSet.add((int) cellIndex)){
                  domain.setIndex((int) cellIndex);
                }else{
                  domain.setIndex(null);
                }
                //      flags[cellIndex-1]=true;
                startIndex = rowIndex;
              }
            }
           // if (checkFlags(flags)) break;
          }
        }
    }
    return startIndex;
  }

  private static boolean checkFlags(boolean[] flags){
    for (boolean flag : flags)
      if(!flag) return false;
    return true;
  }

  private static int skipNullRow(Row row,int rowIndex){
    if(row==null) return rowIndex+1;
    short lastCellNum = row.getLastCellNum();
    for (int cellIndex=1;cellIndex<=lastCellNum;cellIndex++){
      Cell cell = row.getCell(cellIndex);
      String cellValue = getCellValue(cell);
      if(!cellValue.equalsIgnoreCase("")){
        return rowIndex;
      }
    }
    return rowIndex+1;
  }

  public static Object getValue(TypeEnum type,String strVal){
    switch (type){
      case String:
        return strVal;
      case Byte:
        try {
          return Byte.valueOf(strVal);

        }catch (Exception e){
          return 0;
        }
        //break;
      case Long:
        try {
          return Long.valueOf(strVal);
        }catch (Exception e){
          return 0;
        }

      case Float:
        try {
          return Float.valueOf(strVal);

        }catch (Exception e){
          return 0;
        }

      case Short:
        try {
          return Short.valueOf(strVal);

        }catch (Exception e){
          return 0;
        }

      case Double:
        try {
          return Double.valueOf(strVal);
        }catch (Exception e){
          return 0;
        }

      case Boolean:
        try {
          return Boolean.valueOf(strVal);

        }catch (Exception e){
          return null;
        }

      case Integer:
        try {
          if(strVal.contains(".")) {
            strVal = strVal.split("\\.")[0];
          }
          return Integer.valueOf(strVal);
        }catch (Exception e){
          return 0;
        }

      case Character:
        return strVal.charAt(0);

      case ByteArray:
        return new byte[10];

      default:
          return null;
    }
  }

  private static String getCellValue(Cell cell){
    if(cell == null){
      return "";
    }
    //把数字当成String来读，避免出现1读成1.0的情况
    if(cell.getCellTypeEnum() == CellType.NUMERIC && !isDate(cell)){
      cell.setCellType(CellType.STRING);
    }
    //判断数据的类型
    switch (cell.getCellTypeEnum()){
      case NUMERIC: //数字
        if(isDate(cell)){
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          return sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue()));
        }

        return String.valueOf(cell.getNumericCellValue());
      case STRING: //字符串
        return String.valueOf(cell.getStringCellValue());
      case BOOLEAN: //Boolean
        return String.valueOf(cell.getBooleanCellValue());
      case FORMULA: //公式
        return String.valueOf(cell.getCellFormula());
      case BLANK: //空值
        return "";
      case ERROR: //故障
        return  "非法字符";
      default:
        return  "未知类型";
    }
  }




  private static boolean isDate(Cell cell){
    return  DateUtil.isCellDateFormatted(cell);
   // return false;
  }



  //导出数据

  public  static<T> void  exportExcel(Class<T> tClass,Collection<T> data,OutputStream stream,
                    File templateFile,Map<String, XSSFCellStyle> cellStyles,String sheetName){
    if(null==tClass) return;
   // exportLock.lock();
    synchronized (templateFile.getName().intern()) {
      if (log.isInfoEnabled()) {
        log.info("Start Excel Export");
      }
      List<ExportFieldDomain> exportFieldDomains = new ArrayList<>();
      try {
        new ExportFieldProcessor().doFieldAnnotation(tClass, exportFieldDomains);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {

      }

      XSSFWorkbook wb = new XSSFWorkbook();
      if (cellStyles == null || cellStyles.isEmpty()) {
        cellStyles = createStyles(wb);
      }
      try {
        XSSFSheet sheet = wb.createSheet(sheetName);


        //name of the zip entry holding sheet data, e.g. /xl/worksheets/sheet1.xml
        String sheetRef = sheet.getPackagePart().getPartName().getName();

        //save the template
        FileOutputStream os = new FileOutputStream(templateFile);
        wb.write(os);
        os.close();

        //Step 2. Generate XML file.
        File tmp = File.createTempFile("sheet", ".xml");
        Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), XML_ENCODING);
        generateData(fw, cellStyles, data, exportFieldDomains);

        //Step 3. Substitute the template entry with the generated data
        //  FileOutputStream out = new FileOutputStream("big-grid.xlsx");
        fw.close();
        substitute(templateFile, tmp, sheetRef.substring(1), stream);
        stream.close();
      } catch (Exception e) {
        log.error("Stop Excel Export，Something Error", e);

      } finally {
        // exportLock.unlock();
      }
    }
      log.info("export excel success ");
  }












  private  static void  generateData(Writer out, Map<String, XSSFCellStyle> styles, Collection<?> data, List<ExportFieldDomain> domains) throws Exception {

    Calendar calendar = Calendar.getInstance();

    SpreadsheetWriter sw = new SpreadsheetWriter(out);
    sw.beginSheet();

    //insert header row
    sw.insertRow(0);
    int styleIndex = styles.get("header").getIndex();
    for(int i=0;i<domains.size();i++){
      ExportFieldDomain domain = domains.get(i);
      String title = domain.getTitle();
      sw.createCell(i, title, styleIndex);
    }

    sw.endRow();

    int rownum=0;
    Object[] args=new Object[]{};
    for (Object object : data) {
      rownum++;
      sw.insertRow(rownum);
      for (int i = 0; i <domains.size(); i++) {
        ExportFieldDomain domain = domains.get(i);
        Field field = domain.getField();
        Object value = field.get(object);
            //method.invoke(t, args);
        TypeEnum type= domain.getType();
        ViewType viewType=	domain.getViewType();
        if(value!=null){
          if(TypeEnum.Boolean==type){
            Boolean boolValue=(Boolean) value;
            if(boolValue)
            {
              sw.createCell(i,"是" );
            }else{
              sw.createCell(i, "否");
            }

          }else if(viewType==ViewType.Money){
            try{
              double doubleValue = Double.parseDouble(value.toString());


              sw.createCell(i, doubleValue, styles.get("currency").getIndex());
            }catch (Exception e) {
              sw.createCell(i, value.toString());
            }
          }
          else if(TypeEnum.Date==type){
            Date date=(Date) value;
            calendar.setTime(date);
            sw.createCell(i, calendar, styles.get("date").getIndex());

          }else if(TypeEnum.Double==type||TypeEnum.Integer==type||TypeEnum.Float==type){
            if(viewType==ViewType.Percent){

              sw.createCell(i, Double.parseDouble(value.toString()), styles.get("percent").getIndex());
            }else{
              sw.createCell(i,Double.parseDouble( value.toString()));
            }
          }else if(type==TypeEnum.ByteArray || viewType==ViewType.Image){
            sw.createCell(i, " ");
          }else{
            sw.createCell(i, value.toString());
          }

        }
        //当为空时
        else{
          sw.createCell(i, " ");
        }

      }

      sw.endRow();

    }
    sw.endSheet();
  }





  private static   Map<String, XSSFCellStyle> createStyles(XSSFWorkbook wb){
    Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
    XSSFDataFormat fmt = wb.createDataFormat();

    XSSFCellStyle style1 = wb.createCellStyle();
    style1.setAlignment(HorizontalAlignment.RIGHT);
    style1.setDataFormat(fmt.getFormat("0.0%"));
    styles.put("percent", style1);

    XSSFCellStyle style2 = wb.createCellStyle();
    style2.setAlignment(HorizontalAlignment.CENTER);
    style2.setDataFormat(fmt.getFormat("0.0X"));
    styles.put("coeff", style2);

    XSSFCellStyle style3 = wb.createCellStyle();
    style3.setAlignment(HorizontalAlignment.RIGHT);
    style3.setDataFormat(fmt.getFormat("￥#,##0.00"));
    styles.put("currency", style3);

    XSSFCellStyle style4 = wb.createCellStyle();
    style4.setAlignment(HorizontalAlignment.RIGHT);
    style4.setDataFormat(fmt.getFormat("yyyy-mm-dd"));
    styles.put("date", style4);

    XSSFCellStyle style5 = wb.createCellStyle();
    XSSFFont headerFont = wb.createFont();
    headerFont.setBold(true);
    style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    style5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style5.setFont(headerFont);
    styles.put("header", style5);

    return styles;
  }


  private static void substitute(File zipfile, File tmpfile, String entry, OutputStream out) throws IOException {
    ZipFile zip = ZipHelper.openZipFile(zipfile);
    try {
      ZipOutputStream zos = new ZipOutputStream(out);

      Enumeration<? extends ZipEntry> en = zip.entries();
      while (en.hasMoreElements()) {
        ZipEntry ze = en.nextElement();
        if(!ze.getName().equals(entry)){
          zos.putNextEntry(new ZipEntry(ze.getName()));
          InputStream is = zip.getInputStream(ze);
          copyStream(is, zos);
          is.close();
        }
      }
      zos.putNextEntry(new ZipEntry(entry));
      InputStream is = new FileInputStream(tmpfile);
      copyStream(is, zos);
      is.close();

      zos.close();
    } finally {
      zip.close();
    }
  }

  private static void copyStream(InputStream in, OutputStream out) throws IOException {
    byte[] chunk = new byte[1024];
    int count;
    while ((count = in.read(chunk)) >=0 ) {
      out.write(chunk,0,count);
    }
  }

  /**
   *
   *
   *
   */
  public static class SpreadsheetWriter {
    private final Writer _out;
    private int _rownum;

    public SpreadsheetWriter(Writer out){
      _out = out;
    }

    public void beginSheet() throws IOException {
      _out.write("<?xml version=\"1.0\" encoding=\""+XML_ENCODING+"\"?>" +
          "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" );
      _out.write("<sheetData>\n");
    }

    public void endSheet() throws IOException {
      _out.write("</sheetData>");
      _out.write("</worksheet>");
    }

    /**
     * Insert a new row
     *
     * @param rownum 0-based row number
     */
    public void insertRow(int rownum) throws IOException {
      _out.write("<row r=\""+(rownum+1)+"\">\n");
      this._rownum = rownum;
    }

    /**
     * Insert row end marker
     */
    public void endRow() throws IOException {
      _out.write("</row>\n");
    }

    public void createCell(int columnIndex, String value, int styleIndex) throws IOException {
      String ref = new CellReference(_rownum, columnIndex).formatAsString();
      _out.write("<c r=\""+ref+"\" t=\"inlineStr\"");
      if(styleIndex != -1) _out.write(" s=\""+styleIndex+"\"");
      _out.write(">");
      _out.write("<is><t>"+value+"</t></is>");
      _out.write("</c>");
    }

    public void createCell(int columnIndex, String value) throws IOException {
      createCell(columnIndex, value, -1);
    }

    public void createCell(int columnIndex, double value, int styleIndex) throws IOException {
      String ref = new CellReference(_rownum, columnIndex).formatAsString();
      _out.write("<c r=\""+ref+"\" t=\"n\"");
      if(styleIndex != -1) _out.write(" s=\""+styleIndex+"\"");
      _out.write(">");
      _out.write("<v>"+value+"</v>");
      _out.write("</c>");
    }

    public void createCell(int columnIndex, double value) throws IOException {
      createCell(columnIndex, value, -1);
    }

    public void createCell(int columnIndex, Calendar value, int styleIndex) throws IOException {
      createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
    }
  }

  private ExcelUtils(){
    throw new RuntimeException("不好意思，不允许实例化我");
  }




}
