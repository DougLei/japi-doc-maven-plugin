package com.douglei.api.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 构建api文件数据到zip
 * @author DougLei
 */
public class ApiZipBuilder extends ApiDocBuilder{

	@Override
	protected void build_() throws Exception {
		byte[] b = new byte[1024];
		short len;
		ZipEntry entryIn, entryOut;
		try(ZipInputStream zipIn = new ZipInputStream(getClass().getClassLoader().getResourceAsStream(apiDocTemplateFileName));
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(new File(path + name + "-" + version + ".zip")))){
			while((entryIn=zipIn.getNextEntry()) != null) {
				if(entryIn.isDirectory()) {
					zipOut.putNextEntry(entryIn);
				}else {
					entryOut = new ZipEntry(entryIn.getName());
					zipOut.putNextEntry(entryOut);
					while((len=(short) zipIn.read(b)) != -1) {
						zipOut.write(b, 0, len);
					}
				}
				zipOut.closeEntry();
				zipIn.closeEntry();
			}
			
			zipOut.putNextEntry(new ZipEntry("js"+File.separatorChar+"api.data.js"));
			writeApiData(zipOut);
			zipOut.closeEntry();
		}
	}
}
