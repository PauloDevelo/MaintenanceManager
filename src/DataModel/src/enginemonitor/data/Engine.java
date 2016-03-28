package enginemonitor.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

public class Engine extends DataEventHandler implements Cloneable{
	private static Logger _logger = Logger.getLogger(Engine.class);
	
	private String _brand = "";

	private String _type = "";
	private float _age = 0;
	private GregorianCalendar _installationDate = new GregorianCalendar();
	private final SimpleDateFormat _df = new SimpleDateFormat("dd/MM/yyyy");

	private String _portName = "";


	private boolean _hasBeenUpdated = false;
	

	public Engine(Element engineElem){
		_brand = engineElem.getAttributeValue("brand");
		_type = engineElem.getAttributeValue("type");
		_age = Float.parseFloat(engineElem.getChild("Age").getText());
		try {
			_installationDate.setTime(_df.parse(engineElem.getAttributeValue("installation")));
		} catch (ParseException e) {
			_logger.error("Erreur de parsing de date", e);
		}
		if(engineElem.getChild("PortCOM") != null){
			_portName = engineElem.getChild("PortCOM").getText();
		}
	}
	
	public Engine(){
	
	}

	public String get_brand() {
		return _brand;
	}

	public String get_type() {
		return _type;
	}

	public float get_age() {
		return _age;
	}
	
	public GregorianCalendar get_installation(){
		return _installationDate;
	}
	
	public synchronized String get_portName() {
		return _portName;
	}
	
	public void set_brand(String brand) {
		if(_brand.compareTo(brand) != 0){
			_brand = brand;
			sendEventDataChanging("brand");
		}
	}

	public void set_type(String type) {
		if(_type.compareTo(type) != 0){
			_type = type;
			sendEventDataChanging("type");
		}
	}
	
	public void set_installation(GregorianCalendar install){
		if(!install.equals(_installationDate)){
			_installationDate = install;
			sendEventDataChanging("installation");
		}
	}

	public synchronized void set_age(float age) {
		if(_age != age){
			_age = age;
			sendEventDataChanging("age");
		}
	}
	
	public void set_portName(String portName) {
		if(_portName.compareTo(portName) != 0){
			_portName = portName;
			sendEventDataChanging("portName");
		}
	}
	
	public boolean is_hasBeenUpdated() {
		return _hasBeenUpdated;
	}

	public synchronized void set_hasBeenUpdated(boolean hasBeenUpdated) {
		if(_hasBeenUpdated != hasBeenUpdated){
			_hasBeenUpdated = hasBeenUpdated;
			sendEventDataChanging("hasBeenUpdated");
		}
	}
	
	public Element getElement(){
		Element engineElement = new Element("Engine");
		
		Attribute brandAttr = new Attribute("brand", _brand);
		Attribute typeAttr = new Attribute("type", _type);
		Attribute installationAttr = new Attribute("installation", _df.format(_installationDate.getTime()));
		engineElement.setAttribute(brandAttr);
		engineElement.setAttribute(typeAttr);
		engineElement.setAttribute(installationAttr);
		
		Element age = new Element("Age");
		age.setText(Float.toString(_age));
		engineElement.addContent(age);
		
		Element portNameElem = new Element("PortCOM");
		portNameElem.setText(_portName);
		engineElement.addContent(portNameElem);
		
		return engineElement;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
