package org.xmlcml.cml.converters.cml;


import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.converters.AbstractConverterModule;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.MimeType;
import org.xmlcml.cml.converters.MimeType.ObjectType;

/**
 * @author Sam Adams
 */
public class CMLModule extends AbstractConverterModule {

	public static final MimeType XYZ_TYPE = new MimeType("chemical/x-xyz", ObjectType.TEXT, "xyz");
	private static final String PREFIX = "xyz";
	
    public CMLModule(){
    }
    
    public String getPrefix() {
    	return PREFIX;
    }

	public List<Converter> getConverterList() {
		if (converterList == null) {
			converterList = new ArrayList<Converter>();
			converterList.add(new CML2CMLLiteConverter());
		}
		return converterList;
	}

	public List<MimeType> getMimeTypeList() {
		if (mimeTypeList == null) {
			mimeTypeList = new ArrayList<MimeType>();
			mimeTypeList.add(CMLCommon.CML_TYPE);
		}
		return mimeTypeList;
	}
	
	
}
