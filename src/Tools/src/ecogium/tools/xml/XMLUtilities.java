package ecogium.tools.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLUtilities {
	
	private final static Logger _logger = Logger.getLogger(XMLUtilities.class);
	
	public static String getValueInElmt(Element ifn, String path) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(String elmtName : listElmt){
			if(elmt != null){
				elmt = elmt.getChild(elmtName);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return "";
			}
		}
		
		if(elmt == null)
		{
			_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
			return "";
		}
		else{
			return elmt.getText();
		}
	}
	
	public static String getValueInElmt(Element ifn, String path, Namespace ns) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(String elmtName : listElmt){
			if(elmt != null){
				Element elmttemp = elmt.getChild(elmtName, ns);
				if(elmttemp == null){
					elmttemp = elmt.getChild(elmtName);
				}
				
				elmt = elmttemp;
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return "";
			}
		}
		
		if(elmt == null)
		{
			_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
			return "";
		}
		else{
			return elmt.getText();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static List getListElem(Element ifn, String path) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(int i = 0; i < listElmt.length - 1; i++){
			String elmtName = listElmt[i];
			if(elmt != null){
				elmt = elmt.getChild(elmtName);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
		}
		
		return elmt == null ? null : elmt.getChildren(listElmt[listElmt.length - 1]);
	}
	
	@SuppressWarnings("rawtypes")
	public static List getListElem(Element ifn, String path, Namespace ns) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(int i = 0; i < listElmt.length - 1; i++){
			String elmtName = listElmt[i];
			if(elmt != null){
				elmt = elmt.getChild(elmtName, ns);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
		}
		
		return elmt == null ? null : elmt.getChildren(listElmt[listElmt.length - 1], ns);
	}

	public static Element getFirstElem(Element ifn, String path) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(int i = 0; i < listElmt.length - 1; i++){
			String elmtName = listElmt[i];
			if(elmt != null){
				elmt = elmt.getChild(elmtName);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
		}
		
		if(elmt != null){
			@SuppressWarnings("rawtypes")
			List listElem = elmt.getChildren(listElmt[listElmt.length - 1]);
			if(listElem == null){
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
			else{
				if(listElem.size() == 0){
					return null;
				}
				else{
					return (Element)listElem.get(0);
				}
			}
		}
		else{
			_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
			return null;
		}
		
		
	}
	
	public static Element getFirstElem(Element ifn, String path, Namespace ns) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(int i = 0; i < listElmt.length - 1; i++){
			String elmtName = listElmt[i];
			if(elmt != null){
				elmt = elmt.getChild(elmtName, ns);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
		}
		
		if(elmt != null){
			@SuppressWarnings("rawtypes")
			List listElem = elmt.getChildren(listElmt[listElmt.length - 1], ns);
			if(listElem == null){
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
			else{
				if(listElem.size() == 0){
					return null;
				}
				else{
					return (Element)listElem.get(0);
				}
			}
		}
		else{
			_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
			return null;
		}
	}
	
	public static Element getLastElem(Element ifn, String path) {
		String[] listElmt = path.split("\\.");
		Element elmt = ifn;
		
		for(int i = 0; i < listElmt.length - 1; i++){
			String elmtName = listElmt[i];
			if(elmt != null){
				elmt = elmt.getChild(elmtName);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
		}
		
		if(elmt != null){
			@SuppressWarnings("rawtypes")
			List listElement = elmt.getChildren(listElmt[listElmt.length - 1]);
			if(listElement == null){
				_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
				return null;
			}
			else{
				if(listElement.size() == 0){
					return null;
				}
				else{
					return (Element)listElement.get(listElement.size() - 1);
				}
			}
		}
		else{
			_logger.error("Noeud inexistant : " + path + " dans " + ifn.getName());
			return null;
		}
		
		
	}
	
	public static void setValueInElmt(Element elementRoot, Namespace ns, String path, String value) {
		String[] listElmt = path.split("\\.");
		Element elmt = elementRoot;
		
		for(String elmtName : listElmt){
			if(elmt != null){
				elmt = elmt.getChild(elmtName, ns);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + elementRoot.getName());
			}
		}
		
		if(elmt != null){
			elmt.setText(value);
		}
		else{
			_logger.error("Noeud inexistant : " + path + " dans " + elementRoot.getName());
		}
	}

	public static void setValueInElmt(Element elementRoot, String path, String value) {
		String[] listElmt = path.split("\\.");
		Element elmt = elementRoot;
		
		for(String elmtName : listElmt){
			if(elmt != null){
				elmt = elmt.getChild(elmtName);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + elementRoot.getName());
			}
		}
		
		if(elmt != null){
			elmt.setText(value);
		}
		else{
			_logger.error("Noeud inexistant : " + path + " dans " + elementRoot.getName());
		}
	}

	public static Element getElement(Element elementRoot, String path) {
		String[] listElmt = path.split("\\.");
		Element elmt = elementRoot;
		
		for(String elmtName : listElmt){
			if(elmt != null){
				elmt = elmt.getChild(elmtName);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + elementRoot.getName());
			}
		}
		
		return elmt;
	}
	
	public static Element getElement(Element elementRoot, String path, Namespace ns) {
		String[] listElmt = path.split("\\.");
		Element elmt = elementRoot;
		
		for(String elmtName : listElmt){
			if(elmt != null){
				elmt = elmt.getChild(elmtName, ns);
			}
			else{
				_logger.error("Noeud inexistant : " + path + " dans " + elementRoot.getName());
			}
		}
		
		return elmt;
	}

	public static void saveDoc(Document doc, File generationDateFile) {
		if(generationDateFile.exists()){
			generationDateFile.delete();
		}
		
		try
		{
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fileOutput = new FileOutputStream(generationDateFile);
			sortie.output(doc, fileOutput);
			
			fileOutput.flush();
			fileOutput.close();
		}
		catch (java.io.IOException e){
			_logger.error("Erreur lors de la création du fichier XML Datex.", e);
		}
	}

}
