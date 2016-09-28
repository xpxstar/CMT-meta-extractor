package iscas.xpx.devops.extract;

import java.io.FileNotFoundException;
import java.io.IOException;

import iscas.xpx.devops.meta.entity.Meta;

public interface MetaExtract {
	
	
	/**
	 * @param dir the path of a recipe
	 * @return the metadata in form of json
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	Meta extract(String dir) throws FileNotFoundException, IOException;
}
