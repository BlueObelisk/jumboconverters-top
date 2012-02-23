package org.xmlcml.cml.converters;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.cml.CMLCommon;
import org.xmlcml.cml.element.CMLCml;

/** dummy class
 * 
 * @author pm286
 *
 */
public class Foo2CMLConverter extends AbstractConverter {
	private static final Logger LOG = Logger.getLogger(Foo2CMLConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
	/**
	 * converts a Foo string to CML. returns cml:cml
	 * 
	 */
	public Element convertToXML(String[] lines) {
		String line = (lines == null || lines.length == 0) ? "foo" : lines[0];
		CMLCml cml = new CMLCml();
		cml.setTitle(line);
		return cml;
	}

	public MimeType getInputType() {
		return CMLCommon.FOO_TYPE;
	}
	
	public MimeType getOutputType() {
		return CMLCommon.CML_TYPE;
	}
	
	public String getDescription() {
		return "A stub test class";
	}

}
