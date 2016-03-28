package enginemonitor.controler;

import java.io.File;

import org.apache.log4j.Logger;

import ecogium.tools.properties.PropertiesVM;
import enginemonitor.data.EngineMaintenance;
import enginemonitor.data.FactoryEngineMaintenance;
import enginemonitor.ui.MainUI;


public class StartingCtrl {

	private static PropertiesVM _prop = PropertiesVM.getPropertiesVM("properties\\EngineMonitor.prop");
	private static Logger _logger = Logger.getLogger(StartingCtrl.class);
	
	private Thread IUThread = null;
	
	private EngineMaintenance _model = null;
	
	public static void main(String[] args) {
		StartingCtrl startCtrl = new StartingCtrl();
		startCtrl.start();
	}

	private void start() {
		if(_prop == null){
			File propertiesFile = new File("properties\\EngineMonitor.prop");
			_logger.error("Impossible de trouver le fichier de propriété " + propertiesFile.getAbsolutePath());
			return;
		}
		else{
			//Ici, on lit le fichier contenant l'historique
			String defaultModelPath = _prop.getValue("DefaultModel");
			
			File xmlFile = new File(defaultModelPath);
			if(xmlFile.exists() && xmlFile.isFile()){
				_model = FactoryEngineMaintenance.createModel(xmlFile);
			}
			else{
				_logger.warn("Le fichier " + xmlFile.getAbsolutePath() + " n'existe pas ou bien n'est pas un fichier.");
				_model = FactoryEngineMaintenance.createEmptyModel();
			}
			_model.setFile(xmlFile);
			
			//On met à jour l'age du moteur
			BTReaderMonEngine btReaderMonEngine = new BTReaderMonEngine(_model.get_engine());
			Thread updateAgeEngineBT = new Thread(btReaderMonEngine);
			//Si la mise à jour de l'age du moteur n'a pas pu être effectuée, on lance la mise à jour en tache de fond 
			//et on continue le lancement de l'application
			if(!_model.get_engine().is_hasBeenUpdated()){
				updateAgeEngineBT.start();
			}
			
			EngineMonControler mainCtrl = new EngineMonControler(_model);
			
			//On lance l'IHM
			Runnable IUTask = new Runnable() {
				
				@Override
				public void run() {
					MainUI mainUI = new MainUI();
					mainUI.addUIActionListener(mainCtrl);
					mainUI.open(_model);
					updateAgeEngineBT.stop();
				}
			};
			
			IUThread = new Thread(IUTask);
			IUThread.start();
			
		}
	}

}
