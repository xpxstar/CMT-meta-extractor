package iscas.xpx.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipFileUtils {
	public static void main(String[] args) throws Exception {
        try {  
               unzip("f:/javacode/java.zip","f:/ruby/xyz");  
           } catch (Exception e) {  
               // TODO Auto-generated catch block  
               e.printStackTrace();  
           }  
    }
    
    /**
     * @description 读取zip压缩文件 测试
     * @param file
     * @throws Exception void
     * @author xpxstar@gmail.com
     * 2016年4月18日 上午11:25:40
     */
    public static StringBuffer readZipFile(String file) throws Exception {  
           ZipFile zf = new ZipFile(file);  
           InputStream in = new BufferedInputStream(new FileInputStream(file));  
           ZipInputStream zin = new ZipInputStream(in);  
           ZipEntry ze;  
           StringBuffer result = new StringBuffer();
           while ((ze = zin.getNextEntry()) != null) {  
               if (!ze.isDirectory()){  
                   System.err.println("file - " + ze.getName() + " : "  
                           + ze.getSize() + " bytes");  
                   long size = ze.getSize();  
                   if (size > 0) {  
                       BufferedReader br = new BufferedReader(  
                               new InputStreamReader(zf.getInputStream(ze)));  
                       String line="";  
                       while ((line = br.readLine()) != null) {  
                           result.append(line);
                       }  
                       br.close();  
                   }  
               }  
           }  
           zin.closeEntry();  
           zin.close();
           zf.close();
           return result;
       }
    /**
     *解压缩zip文件
     *
     */
    	
    public static void unzip(String sourceFile,String targetDic) {
   	try {
   		ZipInputStream Zin=new ZipInputStream(new FileInputStream(sourceFile));//输入源zip路径
   		BufferedInputStream Bin=new BufferedInputStream(Zin);
   		File Fout=null;
   		ZipEntry entry;
   		try {
   			while((entry = Zin.getNextEntry())!=null ){
   				if (!entry.isDirectory()){
	   				Fout=new File(targetDic,entry.getName());
	   				if(!Fout.exists()){
	   					(new File(Fout.getParent())).mkdirs();
	   				}
	   				FileOutputStream out=new FileOutputStream(Fout);
	   				BufferedOutputStream Bout=new BufferedOutputStream(out);
	   				int b;
	   				while((b=Bin.read())!=-1){
	   					Bout.write(b);
	   				}
	   				Bout.close();
	   				out.close();
   				}
   			}
   			Bin.close();
   			Zin.close();
   			
   		} catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
   	} catch (FileNotFoundException e) {
   		// TODO Auto-generated catch block
   		e.printStackTrace();
   	}
   }
}

