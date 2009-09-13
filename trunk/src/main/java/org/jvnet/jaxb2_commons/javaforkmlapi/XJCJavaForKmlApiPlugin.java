package org.jvnet.jaxb2_commons.javaforkmlapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jvnet.jaxb2_commons.javaforkmlapi.addproperjavadoc.AddProperJavaDocumentationForKML;
import org.jvnet.jaxb2_commons.javaforkmlapi.command.Command;
import org.jvnet.jaxb2_commons.javaforkmlapi.convenience.CreateCoordinateClass;
import org.jvnet.jaxb2_commons.javaforkmlapi.convenience.CreateCreateAndAddMethodsForCoordinates;
import org.jvnet.jaxb2_commons.javaforkmlapi.convenience.CreateIconClass;
import org.jvnet.jaxb2_commons.javaforkmlapi.factory.CreateOwnObjectFactory;
import org.jvnet.jaxb2_commons.javaforkmlapi.fluent.CreateConstructors;
import org.jvnet.jaxb2_commons.javaforkmlapi.fluent.FluentPattern;
import org.jvnet.jaxb2_commons.javaforkmlapi.jaxbtools.CreateMarshalAndUnmarshal;
import org.jvnet.jaxb2_commons.javaforkmlapi.xmlrootelement.JaxbPluginXmlRootElement;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CEnumConstant;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XmlString;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.reader.RawTypeSet.Ref;

/**
 * {@link Plugin} that always removes the Type-Suffix (if present) from generated classes.
 * 
 * <pre>
 * 
 * Omit Type:
 *   PlacemarkType -&gt; Placemark
 * 
 * </pre>
 * 
 * @author Florian Bachmann
 */
public class XJCJavaForKmlApiPlugin extends Plugin {
	private static final String LOG4J_LOCATION = "src/main/java/log4j.properties";

	private final Logger LOG = Logger.getLogger(getClass().getName());

	/** Constant for the option string. */
	protected final String OPTION_NAME = "XJavaForKmlApi";

	public final static String PLUGINNAME = "[XJCJavaForKmlApiPlugin]";

	public final static String PACKAGE_KML = "de.micromata.opengis.kml.v_2_2_0";

	public final static String PACKAGE_GX = PACKAGE_KML + ".gx";

	public final static String PACKAGE_ATOM = PACKAGE_KML + ".atom";

	public final static String PACKAGE_XAL = PACKAGE_KML + ".xal";

	private List<Command> mCommand = new ArrayList<Command>();

	@Override
	public void postProcessModel(Model model, ErrorHandler errorHandler) {
		JPackage mainPackage = null;
		CClassInfo classLink = null;
		CEnumLeafInfo altitudeModeKML = null;
		CEnumLeafInfo altitudeModeGX = null;

		for (CClassInfo classInfo : model.beans().values()) {
			if (classInfo.shortName.equals("Kml")) {
				LOG.info("Found KML package: " + classInfo.getOwnerPackage().name());
				mainPackage = classInfo.getOwnerPackage();
				break;
			}
		}

		for (CClassInfo classInfo : model.beans().values()) {
			if (classInfo.shortName.equals("Link") && classInfo.getOwnerPackage().name().equals(mainPackage.name())) {
				classLink = classInfo;
//				for (iterable_type iterable_element : classLink.getProperties()) {
//	        
//        }
				Util.logInfo("Found Link class, prepare creation of Icon class");
				break;
			}
		}
		
		for (CClassInfo classInfo : model.beans().values()) {
			if (classInfo.fullName().equals(PACKAGE_GX + ".TourPrimitive") && !classInfo.isAbstract()) {
				classInfo.setAbstract();
				LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " Found gxTourPrimtive class, set abstract!");
				break;
			}
		}
//		
//		for (CClassInfo classInfo : model.beans().values()) {
//			if (classInfo.fullName().equals(PACKAGE_GX + ".Playlist")) {
//				LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " Found Playlist class!");
//				List<CPropertyInfo> properties = classInfo.getProperties();
//				for (CPropertyInfo cPropertyInfo : properties) {
//					LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " > " + cPropertyInfo.kind(). + " " + cPropertyInfo.getName(true));
//        }
//				break;
//			}
//		}

		for (CEnumLeafInfo classInfo : model.enums().values()) {
			if (classInfo.fullName().equals(PACKAGE_KML + ".AltitudeMode")) {
				altitudeModeKML = classInfo;
				LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " Found AltitudeMode in KML package");
				continue;
			}
			if (classInfo.fullName().equals(PACKAGE_GX + ".AltitudeMode")) {
				altitudeModeGX = classInfo;
				LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " Found AltitudeMode in GX package");
				continue;
			}
			
			
		}
		assert mainPackage != null;
		assert classLink != null;
		assert altitudeModeKML != null;
		assert altitudeModeGX != null;
		CBuiltinLeafInfo doubleValue = CBuiltinLeafInfo.DOUBLE;

		XSSchema schema = model.schemaComponent.getSchema("http://www.opengis.net/kml/2.2");

		LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " -------Icon properties ------------------");
		CClassInfoParent cClassInfoParentLink = classLink.parent();

		CClassInfo iconClass = new CClassInfo(model, cClassInfoParentLink, "Icon", null, new QName("Icon"), null, schema, new CCustomizations());
		iconClass.setBaseClass(classLink.getBaseClass());

		//fill icon class with the properties defined in link class
		List<CPropertyInfo> properties = classLink.getProperties();
		for (CPropertyInfo c : properties) {
			iconClass.addProperty(c);
		}
		
		Collection<CEnumConstant> constants = altitudeModeGX.getConstants();
		for (CEnumConstant cEnumConstant : constants) {
			altitudeModeKML.members.add( new CEnumConstant(cEnumConstant.getName(), null, cEnumConstant.getLexicalValue(), null, null, null));
    }
		
		//model.enums().remove(altitudeModeGX.fullName());
		
//		altitudeModeKML.javadoc
		Collection<CEnumConstant> constantsKML = altitudeModeKML.getConstants();
		for (CEnumConstant cEnumConstant : constantsKML) {
			LOG.info(XJCJavaForKmlApiPlugin.PLUGINNAME + " altitudeMode property: " + cEnumConstant.getLexicalValue());
    }
		
//		altitudeModeGX.parent.getOwnerPackage().remove(altitudeModeGX.);
		
		
		QName qNameCoordinate = new QName("http://www.opengis.net/kml/2.2", "Coordinate");
		QName qNameLongitude = new QName("PerlPleaseRemoveMeLongitude");
		QName qNameLatitude = new QName("PerlPleaseRemoveMeLatitude");
		QName qNameAltitude = new QName("PerlPleaseRemoveMeAltitude");
		CClassInfo cc4 = new CClassInfo(model, cClassInfoParentLink, "Coordinate", null, qNameCoordinate, null, schema, new CCustomizations());

		//LOG.info(":::::::::::::::::::::::::::::::::: " + doubleValue.getType().fullName());

		CAttributePropertyInfo cAttributeLongitude = new CAttributePropertyInfo("longitude", null, new CCustomizations(), null, qNameLongitude,
		    doubleValue, null, false);
		CAttributePropertyInfo cAttributeLatitude = new CAttributePropertyInfo("latitude", null, null, null, qNameLatitude, doubleValue, null,
		    false);
		CAttributePropertyInfo cAttributeAltitude = new CAttributePropertyInfo("altitude", null, new CCustomizations(), null, qNameAltitude,
		    doubleValue, null, false);

		cc4.addProperty(cAttributeLongitude);
		cc4.addProperty(cAttributeLatitude);
		cc4.addProperty(cAttributeAltitude);
	}



	/**
	 * Returns the option string used to turn on this plugin.
	 * 
	 * @return option string to invoke this plugin
	 */
	@Override
	public String getOptionName() {
		return OPTION_NAME;
	}

	/**
	 * Returns a string specifying how to use this plugin and what it does.
	 * 
	 * @return string containing the plugin usage message
	 */
	@Override
	public String getUsage() {
		return "  -" + OPTION_NAME + " : removes every Type suffix from the generated classes";
	}

	/**
	 * On plugin activation, sets a customized NameConverter to adjust code generation.
	 * 
	 * @param opts options used to invoke XJC
	 * @throws com.sun.tools.xjc.BadCommandLineException if the plugin is invoked with wrong parameters
	 */
	@Override
	public void onActivated(Options opts) throws BadCommandLineException {

		PropertyConfigurator.configure(LOG4J_LOCATION);
		LOG
		    .info(XJCJavaForKmlApiPlugin.PLUGINNAME + " clean up generated names (Legend: T = removed Type, A = removed Abstract, E = removed Enum");
		opts.setNameConverter(new OmitTypeNameConverter(), this);
	}

	/**
	 * Returns true without touching the generated code. All the relevant work is done during name conversion.
	 * 
	 * @param model This object allows access to various generated code.
	 * @param opts options used to invoke XJC
	 * @param errorHandler Errors should be reported to this handler.
	 * @return If the add-on executes successfully, return true. If it detects some errors but those are reported and recovered gracefully,
	 *         return false.
	 */
	@Override
	public boolean run(Outline outline, Options opts, ErrorHandler errorHandler) {
		opts.noFileHeader = true;

		ClazzPool pool = new ClazzPool(outline);

		// mCommand.add(new CreateAnnotationClasses(outline, opts, errorHandler, this));
		mCommand.add(new CleanCode(outline, opts, errorHandler, pool));
		mCommand.add(new CreateIconClass(outline, opts, errorHandler, pool));
		mCommand.add(new CreateCoordinateClass(outline, opts, errorHandler, pool));
		mCommand.add(new JaxbPluginXmlRootElement(outline, opts, errorHandler, pool));
		mCommand.add(new ConvertComplexTypesToSimpleTypes(outline, opts, errorHandler, pool));
		// // mCommand.add(new JaxbPluginXmlRootElement(outline, opts, errorHandler));
		mCommand.add(new CreateEqualsAndHashCode(outline, opts, errorHandler, pool));
		mCommand.add(new CreateConstructors(outline, opts, errorHandler, pool));
		// // mCommand.add(new BuilderPattern(outline, opts, errorHandler));
		mCommand.add(new FluentPattern(outline, opts, errorHandler, pool));
		mCommand.add(new CreateOwnObjectFactory(outline, opts, errorHandler, pool));
		mCommand.add(new CreateMarshalAndUnmarshal(outline, opts, errorHandler, pool));
		mCommand.add(new CreateCreateAndAddMethodsForCoordinates(outline, opts, errorHandler, pool));
		// mCommand.add(new DefaultValuePlugin(outline, opts, errorHandler, pool));
		mCommand.add(new AddProperJavaDocumentationForKML(outline, opts, errorHandler, pool));

		for (Command command : mCommand) {
			command.execute();
		}

		return true;
	}

}