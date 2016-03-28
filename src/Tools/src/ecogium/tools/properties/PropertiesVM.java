package ecogium.tools.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesVM {
	private static final Logger _logger = Logger.getLogger(PropertiesVM.class);
	private static final String _attrLastModified = "LastModified";

	private static final Hashtable<File, PropertiesVM> _hashTableProperties = new Hashtable<File, PropertiesVM>();
	public static boolean DEBUG;

	private final Properties _prop;
	private final File _file;

	/**
	 * Cette m�thode permet de r�cup�rer ou cr�er l'objet PropertiesVM.
	 * @param fileLocation Chemin vers le fichier de propri�t�s.
	 * @return
	 */
	public static PropertiesVM getPropertiesVM(String fileLocation){
		File fileSearched = new File(fileLocation);
		PropertiesVM newPropertiesVM = null;

		synchronized (_hashTableProperties) {
			for(File file : _hashTableProperties.keySet()){
				if(file.getAbsolutePath().equals(fileSearched.getAbsolutePath())){
					return _hashTableProperties.get(file);
				}
			}

			newPropertiesVM = new PropertiesVM(fileLocation);
			_hashTableProperties.put(new File(fileLocation), newPropertiesVM);
		}

		return newPropertiesVM;
	}

	private PropertiesVM(String fileLocation)
	{
		_prop = new java.util.Properties();
		_file = new File(fileLocation);
		try 
		{	
			FileInputStream fileInputStream = new FileInputStream(_file);
			_prop.load(fileInputStream);
			_prop.setProperty(_attrLastModified, Long.toString(_file.lastModified()));
		} 
		catch (FileNotFoundException e) 
		{
			_logger.fatal(e.getMessage(), e);
		} 
		catch (IOException e) 
		{
			_logger.fatal(e.getMessage(), e);
		}
		
	}

	/**
	 * Permet de r�cup�rer la valeur d'une propri�t�. Cette m�thode v�rifie 
	 * la date de derni�re mise � jour du fichier de propri�t� avant de retourner la valeur.
	 * @param key Nom de la propri�t�
	 * @return La valeur de la propri�t�
	 */
	public String getValue(String key)
	{
		checkLastModified();

		if(_prop == null)
		{
			_logger.error("Aucune propri�t� n'a pu �tre charg�e pour le fichier " + _file.getAbsolutePath());
			return "";
		}
		else
		{
			return _prop.getProperty(key, "").trim();
		}
	}

	public int getValueInt(String key, int defaultValue){
		checkLastModified();

		if(_prop == null)
		{
			_logger.error("Aucune propri�t� n'a pu �tre charg�e.");
			return defaultValue;
		}
		else
		{
			try{
				return Integer.parseInt(getValue(key));
			}
			catch(NumberFormatException e){
				_logger.error("Impossible de parser l'entier " + getValue(key) + " de l'attribut " + key + " pour le fichier " + _file.getAbsolutePath(), e);
				return defaultValue;
			}
		}
	}
	
	private void checkLastModified()
	{
		if(!DEBUG && _prop != null){
			return;
		}
		
		synchronized (_prop) {
			//Si la date du fichier des propri�t�s a chang�, on recharge le fichier ...
			if(!_prop.getProperty(_attrLastModified).equals(Long.toString(_file.lastModified())))
			{
				try 
				{
					_prop.clear();
					FileInputStream fileInputStream = new FileInputStream(_file);
					_prop.load(fileInputStream);
					_prop.setProperty(_attrLastModified, Long.toString(_file.lastModified()));
				} 
				catch (FileNotFoundException e) 
				{
					_logger.fatal(e.getStackTrace());
				} 
				catch (IOException e) 
				{
					_logger.fatal(e.getStackTrace());
				}
			}
		}
	}

	public ArrayList<String> getKeys() {
		checkLastModified();

		if(_prop != null)
		{
			ArrayList<String> keys = new ArrayList<String>(_prop.size());

			Enumeration<Object> enumKeys = _prop.keys();
			while(enumKeys.hasMoreElements()){
				String key = (String) enumKeys.nextElement();
				keys.add(key);
			}

			return keys;
		}
		else{
			return new ArrayList<String>(0);
		}
	}

	public boolean containsKey(String string) {
		return _prop.containsKey(string);
	}

	public float getValueFloat(String key, float defaultValue) {
		checkLastModified();

		if(_prop == null)
		{
			_logger.error("Aucune propri�t� n'a pu �tre charg�e.");
			return defaultValue;
		}
		else
		{
			try{
				return Float.parseFloat(getValue(key));
			}
			catch(NumberFormatException e){
				_logger.error("Impossible de parser le flottant " + getValue(key) + " de l'attribut " + key + " pour le fichier " + _file.getAbsolutePath(), e);
				return defaultValue;
			}
		}
	}

}
