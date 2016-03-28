package enginemonitor.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jdom.Element;

public class Entry extends DataEventHandler implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4215007745841963004L;

	String _type = null;
	
	String _name = null;
	String _dateUTC = null;
	int _age;
	String _remarks = null;
	
	static SimpleDateFormat _format = new SimpleDateFormat("yyyyMMdd");
	
	public Entry(Element elem){
		_type = elem.getAttributeValue("type");
		if(elem.getAttribute("name") != null){
			_name = elem.getAttributeValue("name");
		}
		else{
			_name = "";
		}
				
		Element dateElem = elem.getChild("Date");
		_dateUTC = dateElem.getAttributeValue("date");
		_age = (int)Float.parseFloat(dateElem.getAttributeValue("engine"));
		 
		
		Element remarksElem = elem.getChild("Remark");
		_remarks = remarksElem.getText();
		
	}
	
	public Entry(String type, String name, GregorianCalendar dateUTC, int age){
		_type = type;
		_name = name;
		_dateUTC = _format.format(dateUTC.getTime());
		_age = age;
		_remarks = "";
	}
	
	public Entry(String type, String name, GregorianCalendar dateUTC, int age, String remark){
		this(type, name, dateUTC, age);
		_remarks = remark;
	}

	public String get_type() {
		return _type;
	}
	
	
	public void set_type(String type) {
		if(_type.compareTo(type) != 0){
			_type = type;
			sendEventDataChanging("type");
		}
	}
	
	public String get_name(){
		return _name;
	}

	public void set_name(String name){
		if(_name.compareTo(name) != 0){
			_name = name;
			sendEventDataChanging("name");
		}
	}

	public GregorianCalendar get_dateUTC() throws ParseException  {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.setTime(_format.parse(_dateUTC));
		
		return cal;
	}

	public void set_dateUTC(GregorianCalendar dateUTC) {
		if(!_dateUTC.equals(dateUTC)){
			_dateUTC = _format.format(dateUTC.getTime());
			sendEventDataChanging("dateUTC");
		}
	}

	public int get_age() {
		return _age;
	}

	public void set_age(int age) {
		if(_age != age){
			_age = age;
			sendEventDataChanging("age");
		}
	}

	public String get_remarks() {
		return _remarks;
	}

	public void set_remarks(String remarks) {
		if(_remarks.compareTo(remarks) != 0){
			_remarks = remarks;
			sendEventDataChanging("remarks");
		}
	}
	
	public Element getElement(){
		Element entryElem = new Element("Entry");
		entryElem.setAttribute("type", _type);
		entryElem.setAttribute("name", _name);
		
		Element dateElem = new Element("Date");
		dateElem.setAttribute("date", _dateUTC);
		dateElem.setAttribute("engine", Float.toString(_age));
		
		entryElem.addContent(dateElem);
		
		Element remarksElem = new Element("Remark");
		remarksElem.setText(_remarks);
		entryElem.addContent(remarksElem);
		
		return entryElem;
	}
}
