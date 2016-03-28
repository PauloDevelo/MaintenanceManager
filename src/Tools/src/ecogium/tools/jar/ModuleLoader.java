package ecogium.tools.jar;

import java.util.Dictionary;
import java.util.Hashtable;
import java.net.URL;
import java.io.File;
import java.util.jar.JarFile;
import java.io.IOException;
import java.util.jar.Manifest;
import java.io.FileFilter;

import org.apache.log4j.Logger;

public class ModuleLoader {
	private final static Logger logger = Logger.getLogger(ModuleLoader.class);
	
	public static Dictionary<URL, String> getModuleClasses(String directory){ 
		Dictionary<URL, String> classes = new Hashtable<URL, String>(); 

		File dir = new File(directory);
		File[] files = new File[0];
		if(dir.exists()){
			files = dir.listFiles(new ModuleFilter()); 
		}
		else{
			logger.fatal("Le répertoire des jar est inexistant : " + dir.getAbsolutePath());
		}
	
		for(File f : files){ 
			JarFile jarFile = null; 

			try { 
				logger.debug("On ouvre le fichier JAR " + f.getName());
				jarFile = new JarFile(f); 

				logger.debug("Récupération du manifest.");
				Manifest manifest = jarFile.getManifest(); 

				if(manifest != null){
					String moduleClassName = manifest.getMainAttributes().getValue("Module-Class"); 
					if(moduleClassName != null){
						logger.debug("Ajout de la classe " + moduleClassName + " pour le jar " + f.getName());
						classes.put(f.toURI().toURL(), moduleClassName); 
					}
					else{
						logger.error("Le manifest du jar " + f.getName() + " ne contient l'attribut Module-Class.");
					}
				}
				else{
					logger.error("Le jar " + f.getName() + " ne contient pas de manifest.");
				}
			} catch (IOException e) { 
				logger.error(e.getMessage(), e);
			} finally { 
				if(jarFile != null){ 
					try { 
						jarFile.close(); 
					} catch (IOException e) { 
						logger.error(e.getMessage(), e);
					} 
				} 
			} 
		} 

		return classes; 
	} 

	private static class ModuleFilter implements FileFilter { 
		public boolean accept(File file) { 
			return file.isFile() && file.getName().toLowerCase().endsWith(".jar"); 
		} 
	} 
}
