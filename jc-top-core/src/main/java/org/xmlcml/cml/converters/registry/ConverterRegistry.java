package org.xmlcml.cml.converters.registry;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.converters.Converter;
import org.xmlcml.cml.converters.MimeType;

/**
 * @author Sam Adams
 */
public class ConverterRegistry {
	private final static Logger LOG = Logger.getLogger(ConverterRegistry.class);

    public static final String META_INF_JUMBO = "META-INF/jumbo-converters";

    /** create singleton registry
     */
    private static ConverterRegistry CONVERTER_REGISTRY = null;
    
    protected ClassLoader classLoader;
    protected Map<TypePair, List<Converter>> converterMap = null;
    protected List<Converter> converterList = null;
    protected Set<MimeType> typeSet = null;
    protected Map<String, Set<MimeType>> typesBySuffixMap = null;

    public static synchronized ConverterRegistry getDefaultConverterRegistry() {
    	if (CONVERTER_REGISTRY == null) {
    		CONVERTER_REGISTRY = new ConverterRegistry(ConverterRegistry.class.getClassLoader());
//    		CONVERTER_REGISTRY.populateAndRegister();
    	}
    	return CONVERTER_REGISTRY;
    }
    
    public ConverterRegistry(ClassLoader classLoader) {
    	this.classLoader = classLoader;
    	populateAndRegister();
    }

    public ConverterRegistry(Class clazz) {
    	this(clazz.getClassLoader());
    }

	public void populateAndRegister() {
		createConvertersList();
        registerConverters();
	}

	public void createConvertersList() {
		if (converterList == null) {
	        converterList = new ArrayList<Converter>();
 	        LOG.trace("new ArrayList:"+this.getClass());
			try {
	            Enumeration<URL> e = classLoader.getResources(META_INF_JUMBO);
	            List<URL> urlList = Collections.list(e);
	            for (URL url : urlList) {
	            	LOG.trace(url);
	                InputStream is = url.openStream();
	                try {
	                    List<String> lineList = IOUtils.readLines(is);
	                    registerModuleClasses(lineList);
	                } finally {
	                    IOUtils.closeQuietly(is);
	                }
	            }
	        } catch (Exception e) {
	            LOG.error("Error loading converter files");
	            e.printStackTrace();
	        }
		}
	}

	private void registerModuleClasses(List<String> lineList) {
		for (String line : lineList) {
		    line = stripComments(line);
		    String convertersName = line.trim();
		    if (convertersName.length() > 0) {
		        try {
		        	LOG.trace("Meta-inf Name: "+convertersName+" classLoader "+classLoader);
		            Class<?> clazz = Class.forName(convertersName);
		            AbstractConverterModule converterModule = (AbstractConverterModule) clazz.newInstance();
		            List<Converter> newConverterList = converterModule.getConverterList();
		            for (Converter newConverter : newConverterList) {
		            	LOG.trace(newConverter);
		            }
		            converterList.addAll(newConverterList);
		        } catch (Exception ex) {
		            LOG.error("Error loading converter: "+ex);
		            ex.printStackTrace();
		        }
		    }
		}
	}
	
	private void registerConverters() {
//		createConvertersList();
        for (Converter converter : converterList) {
        	register(converter);
    		register(converter.getInputType());
    		register(converter.getOutputType());
        }
	}

	private String stripComments(String line) {
		int comment = line.indexOf('#');
		if (comment >= 0) {
		    line = line.substring(0, comment);
		}
		return line.trim();
	}
    
    public Map<TypePair, List<Converter>> getMap() {
    	return converterMap;
    }


    public List<Converter> findConverters(String intype, String outtype) {
    	ensureConverterMap();
        TypePair t = new TypePair(intype, outtype);
        List<Converter> converterList = converterMap.get(t);
        return converterList;
    }

    private void ensureConverterMap() {
    	if (converterMap == null) {
    		converterMap = new HashMap<TypePair, List<Converter>>();
    	}
	}

	public Converter findSingleConverter(String intype, String outtype) {
    	List<Converter> converterList = findConverters(intype, outtype);
    	return (converterList == null || converterList.size() != 1) ? null : converterList.get(0);
    }

    public List<Converter> findConverters(MimeType intype, MimeType outtype) {
    	return findConverters(intype.toString(), outtype.toString());
    }


    public List<Converter> getConverterList() {
        return converterList;
    }

    private synchronized void register(Converter converter) {
    	ensureConverterMap();
        MimeType intype = converter.getInputType();
        MimeType outtype = converter.getOutputType();
        if (intype != null && outtype != null) {
	        TypePair t = new TypePair(intype.toString(), outtype.toString());
	        List<Converter> list = converterMap.get(t);
	        if (list == null) {
	        	list = new ArrayList<Converter>();
	        	converterMap.put(t,  list);
	        }
	        list.add(converter);
        } else {
        	LOG.info("NULL types for "+converter.getClass()+" ("+intype+", "+outtype+")");
        }
    }

    private synchronized void register(MimeType type) {
    	ensureTypeSet();
    	ensureTypesBySuffixMap();
    	typeSet.add(type);
    	List<String> extensions = type.getExtensions();
    	for (String extension : extensions) {
    		Set<MimeType> types = typesBySuffixMap.get(extension);
    		if (types == null) {
    			types = new HashSet<MimeType>();
    			typesBySuffixMap.put(extension, types);
    		}
//    		System.out.println("TYPE "+types);
    		types.add(type);
    	}
    }
    
	private void ensureTypeSet() {
		if (typeSet == null) {
			typeSet = new HashSet<MimeType>();
		}
	}

	public Set<MimeType> getTypes(String suffix) {
		ensureTypesBySuffixMap();
		return typesBySuffixMap.get(suffix);
	}
    
	public MimeType getSingleTypeFromSuffix(String suffix) {
		ensureTypesBySuffixMap();
		Set<MimeType> types = typesBySuffixMap.get(suffix);
		return (types != null && types.size() == 1) ? (MimeType) types.toArray()[0] : null;
	}
	
	private void ensureTypesBySuffixMap() {
		if (typesBySuffixMap == null) {
			typesBySuffixMap = new  HashMap<String, Set<MimeType>>();
		}
	}

	public MimeType getSingleTypeFromFilename(String filename) {
		int idx = filename.lastIndexOf(".");
		return (idx == -1) ? null : getSingleTypeFromSuffix(filename.substring(idx+1));
	}
	
	public String getSingleMimeTypeFromFilename(String filename) {
		MimeType type = getSingleTypeFromFilename(filename);
		return (type == null) ? null : type.getMimeType();
	}

	public void add(Converter converter) {
		createConvertersList();
		converterList.add(converter);
	}

    
}
