package org.xmlcml.cml.converters;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.converters.cml.CMLCommon;
import org.xmlcml.cml.converters.registry.AbstractConverterModule;


/**
 * @author pm286
 */
public class CoreConverterModule extends AbstractConverterModule {

	public CoreConverterModule() {
        super();
    }

	public List<MimeType> getMimeTypeList() {
		if (mimeTypeList == null) {
			mimeTypeList = new ArrayList<MimeType>();
			mimeTypeList.add(CMLCommon.FOO_TYPE);
		}
		return mimeTypeList;
	}

	public List<Converter> getConverterList() {
		if (converterList == null) {
			converterList = new ArrayList<Converter>();
			converterList.add(new Foo2CMLConverter());
		}
		return converterList;
	}

	public String getPrefix() {
		return "test";
	}

}
