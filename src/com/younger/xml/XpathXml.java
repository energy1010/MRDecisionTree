package com.younger.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XpathXml {
	
	private static final Logger log = LoggerFactory.getLogger(XpathXml.class);
	
	 // 把xml写入文件
    public static void writeXML(String filename,Document xmldoc) {
    	  try {
              File file = new File(filename);
              if (file.exists()){
              	log.debug("file "+filename +" exist , delete it ----");
              	file.delete();
              }
                  file.createNewFile();
              FileWriter fw = new FileWriter(file);
              OutputFormat format = OutputFormat.createPrettyPrint(); // 美化格式
              XMLWriter output = new XMLWriter(fw, format);
              output.write(xmldoc);
              output.close();
          } catch (IOException e) {
              System.out.println(e.getMessage());

          }

    }
}
