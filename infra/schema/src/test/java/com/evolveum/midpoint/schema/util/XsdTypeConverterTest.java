/**
 * 
 */
package com.evolveum.midpoint.schema.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.namespace.QName;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.evolveum.midpoint.prism.util.PrismTestUtil;
import com.evolveum.midpoint.prism.xml.XmlTypeConverter;
import com.evolveum.midpoint.prism.xml.XsdTypeMapper;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.util.DOMUtil;
import com.evolveum.midpoint.util.JAXBUtil;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.xml.ns._public.common.common_1.AccountShadowType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.Objects;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ProtectedStringType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.UserType;

/**
 * @author Radovan Semancik
 *
 */
public class XsdTypeConverterTest {

	// TODO: convert this test to create a Protected string structure in prism and then check it in the DOM view 
	
	private static final String FOO_NAMESPACE = "http://foo.com/";
	private static final QName FOO_QNAME = new QName(FOO_NAMESPACE,"foo");
	private static final QName BAR_QNAME = new QName(FOO_NAMESPACE,"bar");

	
	@Test(enabled=false)
	public void testConvertFromProtectedString() throws SchemaException {
		Document document = DOMUtil.parseDocument(
				"<password xmlns=\""+FOO_NAMESPACE+"\" "+
				"xmlns:c=\"http://midpoint.evolveum.com/xml/ns/public/common/common-1.xsd\" "+
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
				"xsi:type=\"c:ProtectedStringType\">"+
				"<c:clearValue>3lizab3th</c:clearValue></password>");
		Element element = DOMUtil.getFirstChildElement(document);
		
		Object value = XmlTypeConverter.toJavaValue(element);
		
		System.out.println("XML -> ProtectedStringType: "+value);
		assertNotNull(value);
		assertTrue(value instanceof ProtectedStringType);
		assertEquals("3lizab3th",((ProtectedStringType)value).getClearValue());
	}
	
	@Test(enabled=false)
	public void testConvertToProtectedString() throws JAXBException, SchemaException {
		ProtectedStringType ps = new ProtectedStringType();
		ps.setClearValue("abra kadabra");
		Document doc = DOMUtil.getDocument();
		
		Object xsdElement = XmlTypeConverter.toXsdElement(ps, FOO_QNAME, doc, true);
		
		System.out.println("ProtectedStringType -> XML");
		System.out.println(xsdElement);
		
		assertTrue(xsdElement instanceof JAXBElement);
		Object value = ((JAXBElement)xsdElement).getValue();
		
		System.out.println(value);
		assertTrue(value instanceof ProtectedStringType);
		assertEquals("abra kadabra",((ProtectedStringType)value).getClearValue());
	}
	
	@Test(enabled=false)
	public void testAccountMarshall() throws JAXBException, SchemaException {
		System.out.println("===[ testAccountMarshall ]===");
		JAXBElement<AccountShadowType> jaxbElement = PrismTestUtil.unmarshalElement(new File("src/test/resources/converter/account-jack.xml"), AccountShadowType.class);
		System.out.println("Object: "+jaxbElement.getValue());
		AccountShadowType shadow = jaxbElement.getValue();
		
		ProtectedStringType ps = new ProtectedStringType();
		ps.setClearValue("foo");
		JAXBElement<ProtectedStringType> pse = new JAXBElement<ProtectedStringType>(FOO_QNAME,ProtectedStringType.class,ps);
		shadow.getAttributes().getAny().add(pse);
		
		shadow.getAttributes().getAny().add(XmlTypeConverter.toXsdElement(42, BAR_QNAME, null, true));
		
		Document doc = DOMUtil.getDocument();
		JAXBElement<AccountShadowType> accountElement = new JAXBElement<AccountShadowType>(ObjectTypes.ACCOUNT.getQName(),AccountShadowType.class,shadow);
		PrismTestUtil.marshalElementToDom(accountElement, doc);
		
		System.out.println("marshalled shadow: "+DOMUtil.serializeDOMToString(doc));
		Element rootElement = DOMUtil.getFirstChildElement(doc);
		System.out.println("root element: "+rootElement);
		
		Element attrElement = (Element) rootElement.getElementsByTagNameNS(SchemaConstants.NS_C,"attributes").item(0);
		System.out.println("attrElement element: "+attrElement);
		
		Element fooElement = (Element) attrElement.getElementsByTagNameNS(FOO_QNAME.getNamespaceURI(), FOO_QNAME.getLocalPart()).item(0);
		System.out.println("fooElement element: "+fooElement);
		Element clearValue = DOMUtil.getFirstChildElement(fooElement);
		assertEquals("foo",clearValue.getTextContent());
		
		Element barElement = (Element) attrElement.getElementsByTagNameNS(BAR_QNAME.getNamespaceURI(), BAR_QNAME.getLocalPart()).item(0);
		System.out.println("barElement element: "+barElement);
		assertEquals(DOMUtil.XSD_INTEGER,DOMUtil.resolveXsiType(barElement));
		assertEquals("42",barElement.getTextContent());
	}
	
}
