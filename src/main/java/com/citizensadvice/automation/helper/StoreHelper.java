package com.citizensadvice.automation.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.citizensadvice.automation.model.ElementInfo;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum StoreHelper {
  INSTANCE;
  Logger logger = LoggerFactory.getLogger(getClass());
  private static final String DEFAULT_DIRECTORY_PATH = "elementValues";
  ConcurrentMap<String, Object> elementMapList;
  ConcurrentMap<String, List<ElementInfo>> elementInfoListMap;

  StoreHelper() {
    initMap(getFileList());
  }

  private void initMap(File[] fileList) {
    elementMapList = new ConcurrentHashMap<String, Object>();
    elementInfoListMap = new ConcurrentHashMap<String, List<ElementInfo>>();
    Type elementType = new TypeToken<List<ElementInfo>>() {
    }.getType();
    Gson gson = new Gson();
    List<ElementInfo> elementInfoList = null;

    for (File file : fileList) {
      try {
        elementInfoList = gson
                .fromJson(new FileReader(file), elementType);
        elementInfoListMap.put(file.getName(), elementInfoList);
        elementInfoList.parallelStream()
                .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
      } catch (FileNotFoundException e) {
        logger.warn("{} not found", e);
      }
    }
  }

  private File[] getFileList() {
    URI uri = null;
    try {
      uri = new URI(this.getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_PATH).getFile());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      logger.error(
              "File Directory Is Not Found! file name: {}",
              DEFAULT_DIRECTORY_PATH);
      throw new NullPointerException();
    }
    File[] fileList = new File(uri.getPath())
            .listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".json"));
    try{
      logger.info("json uzantılı dosya sayısı: " + fileList.length);
    }catch (Exception e){
      logger.error(
              "File Directory Is Not Found! Please Check Directory Location. Default Directory Path = {}",
              DEFAULT_DIRECTORY_PATH);
      throw new NullPointerException();
    }
    return fileList;
  }

  public void printAllValues() {
    elementMapList.forEach((key, value) -> logger.info("Key = {} value = {}", key, value));
  }

  public ElementInfo findElementInfoByKey(String key) {

    if(!elementMapList.containsKey(key)){
      Assert.fail(key + " no element found, check again");
    }
    return (ElementInfo) elementMapList.get(key);
  }

  public void addElementInfoByKey(String key, ElementInfo elementInfo){elementMapList.put(key,elementInfo);}

  public boolean containsKey(String key){return elementMapList.containsKey(key);}

  public void saveValue(String key, String value) {
    elementMapList.put(key, value);
  }

  public String getValue(String key) {
    return elementMapList.get(key).toString();
  }

  public void duplicateKeyControl(boolean allElements) {

    List<ElementInfo> elementInfoList = null;
    int elementNumberInFile = 0;
    ConcurrentHashMap<String,List<String>> map = new ConcurrentHashMap<String, List<String>>();
    Set<String> keySet = elementInfoListMap.keySet();
    StringBuilder emptyValues = new StringBuilder();
    for (String fileName : keySet) {

      elementNumberInFile = 0;
      elementInfoList = elementInfoListMap.get(fileName);
      for (ElementInfo elementInfo: elementInfoList){

        elementNumberInFile++;
        String key = elementInfo.getKey();
        String value = elementInfo.getValue();
        String type = elementInfo.getType();
        if (allElements){
          System.out.println("fileName: " + fileName + " elementNumberInFile: " + elementNumberInFile
                  + " elementKey: " + key + " elementValue: " + value
                  + " elementType: " + type);
          }

        if(!key.equals("") && (value.equals("") || type.equals(""))) {
          emptyValues.append("fileName: " + fileName + " elementNumberInFile: " + elementNumberInFile
                  + " elementKey: " + key + " elementValue: " + value
                  + " elementType: " + type + " elementLine: " + (elementNumberInFile * 5 - 3) + "\n");
        }
        if(!map.containsKey(key)){
          List<String> list = new ArrayList<String>();
          list.add(fileName + "!DATA!" + elementNumberInFile);
          map.put(key, list);
        }else {
          List<String> list = map.get(key);
          list.add(fileName + "!DATA!" + elementNumberInFile);
        }
      }
    }
    System.out.println("********************************************************************");
    System.out.println("Empty value or type");
    System.out.println(emptyValues.toString());
    System.out.println("Duplicate key");
     Set<String> elementKeySet = map.keySet();
     for (String elementKey: elementKeySet) {

       List<String> list = map.get(elementKey);
       if(list.size() >= 2) {
         for (String value : list) {

           String[] values = value.split("!DATA!");
           // if one element size is 5 line, element line is (Integer.parseInt(values[1]) * 5 - 3)
           System.out.println("*Duplicate Key* " + " fileName: " + values[0] + " elementKey: " + elementKey
                   + " elementNumberInFile: " + values[1] + " elementLine: " + (Integer.parseInt(values[1]) * 5 - 3));
         }
       }
     }
    System.out.println("********************************************************************");
   }

}