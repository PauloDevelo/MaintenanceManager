package enginemonitor.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class FactoryEngineMaintenance {
	
	private static final Logger _logger = Logger.getLogger(FactoryEngineMaintenance.class);
	
	public static EngineMaintenance createModel(File xmlFile){
		//Ici on traite les fichiers xml ...
		SAXBuilder sxb = new SAXBuilder();

		_logger.debug("Parsing du document " + xmlFile.getName());
		Document document = null;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(xmlFile);
			document = sxb.build(inputStream);
			inputStream.close();
		} catch (Exception e) {
			if(inputStream != null)
			{
				try {
					inputStream.close();
				} catch (IOException e1) {
					_logger.error(e1.getMessage(), e1);
				}
			}
			_logger.error(e.getMessage(), e);
			return createEmptyModel();
		}
		
		_logger.debug("Fin du parsing du document " + xmlFile.getName());
		
		//On initialise un nouvel élément racine avec l'élément racine du document.
		Element racine = document.getRootElement();
		return new EngineMaintenance(racine);
	}
	
	public static EngineMaintenance createEmptyModel(){
		return new EngineMaintenance();
	}

}
