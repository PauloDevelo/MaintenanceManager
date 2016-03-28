package enginemonitor.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ecogium.tools.properties.PropertiesVM;

public class EngineMaintenance implements IDataListener {
	private static Logger _logger = Logger.getLogger(EngineMaintenance.class);
	
	private static PropertiesVM _prop = PropertiesVM.getPropertiesVM("properties\\EngineMonitor.prop");
	
	private Engine _engine = null;
	private HashMap<String, Task> _listTask = new HashMap<String, Task>();
	private Historic _histo = null;
	
	private boolean _hasBeenSaved = true;
	private File _file;
	
	
	public EngineMaintenance(Element engineMaintenanceElm){
		_engine = new Engine(engineMaintenanceElm.getChild("Engine"));
		_engine.addListenner(this);
		_histo = new Historic(engineMaintenanceElm.getChild("Historic"));
		_histo.addListenner(this);
		
		List listTaskElem = engineMaintenanceElm.getChild("ListTask").getChildren("Task");
		for(Object obj : listTaskElem){
			Task task = new Task((Element)obj, _histo, _engine);
			_listTask.put(task.get_id(), task);
			task.addListenner(this);
		}
		
		for(Entry entry : _histo.getChronoEntryList()){
			if(entry.get_type().startsWith("Task")){
				if(_listTask.get(entry.get_type().substring(5)) == null){
					_logger.warn("Cette entrée n'a pas de tache associée : " + entry.get_type());
				}
			}
			entry.addListenner(this);
		}
	}

	public EngineMaintenance() {
		_engine = new Engine();
		_histo = new Historic();
		
		_hasBeenSaved = false;
	}

	public Engine get_engine() {
		return _engine;
	}

	public Historic get_histo() {
		return _histo;
	}
	
	
	public Collection<Task> getArrayListTask(){
	ArrayList<Task> collTask = new ArrayList<Task>(_listTask.values());
		Collections.sort(collTask, new Comparator<Task>() {

			@Override
			public int compare(Task task0, Task task1) {
				if(task0.getLevel() > task1.getLevel()){
					return -1;
				}
				else if(task0.getLevel() < task1.getLevel()){
					return 1;
				}
				else if(task0.get_engineHours() != -1 && task1.get_engineHours() != -1){
					if(task0.howManyEngineHours() < task1.howManyEngineHours()){
						return -1;
					}
					else if(task0.howManyEngineHours() > task1.howManyEngineHours()){
						return 1;
					}
					else{
						if(task0.whenTodoIt().before(task1.whenTodoIt())){
							return -1;
						}
						else if(task0.whenTodoIt().after(task1.whenTodoIt())){
							return 1;
						}
						else{
							return 0;
						}
					}
				}
				else{
					if(task0.whenTodoIt().before(task1.whenTodoIt())){
						return -1;
					}
					else if(task0.whenTodoIt().after(task1.whenTodoIt())){
						return 1;
					}
					else{
						return 0;
					}
				}
			}
		});
		return collTask;
	}
	
	public void addTask(Task task){
		_listTask.put(task.get_id(), task);
		_hasBeenSaved = false;
		
		task.addListenner(this);
	}
	
	public boolean removeTask(Task taskToRemove){
		if(_listTask.containsKey(taskToRemove.get_id())){
			taskToRemove.removeListener(this);
			_listTask.remove(taskToRemove.get_id());
			_hasBeenSaved = false;
			return true;
		}
		else{
			return false;
		}
	}
	
	public Element getElement(){
		Element engMaintenanceElm = new Element("EngineMaintenance");
		engMaintenanceElm.setAttribute("version", "1.0");
		
		engMaintenanceElm.addContent(_engine.getElement());
		
		Element listTaskElm = new Element("ListTask");
		for(Task task : _listTask.values()){
			listTaskElm.addContent(task.getElement());
		}
		engMaintenanceElm.addContent(listTaskElm);
		
		engMaintenanceElm.addContent(_histo.getElement());
		
		return engMaintenanceElm;
	}

	public void removeTask(String name) {
		
		for(Task task : _listTask.values()){
			if(task.get_name().compareTo(name) == 0){
				task.removeListener(this);
				for(Entry entry : task.getHistoric()){
					_histo.removeEntry(entry);
				}
				_listTask.remove(task.get_id());
				_hasBeenSaved = false;
				break;
			}
		}	
	}

	public Task getTask(String name) {
		for(Task task : _listTask.values()){
			if(task.get_name().compareTo(name) == 0){
				return task;
			}
		}
		return null;
	}

	@Override
	public void onDataChanged(Object source, String param) {
		_hasBeenSaved = false;
	}

	public boolean hasBeenSaved() {
		return _hasBeenSaved;
	}

	public void setFile(File xmlFile) {
		_file = xmlFile;
	}
	
	public void save(){
		if(saveInXMLFile(_file)){
			_hasBeenSaved = true;
		}
		
	}
	
	public boolean saveInXMLFile(File xmlFile){
		Document doc = new Document();
		//Création de la racine du document XML, de ses attributs et de son noeud tpeg_message_set.
		
		Element racine = getElement();
		doc.addContent(racine);
		
		Map instructions = new HashMap();
		
		String xslURL = _prop.getValue("XSLURL");
		instructions.put("href", xslURL);
		instructions.put("type", "text/xsl");
		ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet", instructions);
		doc.getContent().add(0, pi);
		
		
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		FileOutputStream fileOutput;
		try {
			fileOutput = new FileOutputStream(xmlFile);
			sortie.output(doc, fileOutput);
			
			fileOutput.flush();
			fileOutput.close();
			
			return true;
		} catch (IOException e) {
			_logger .error(e);
			return false;
		}
		
	}
}
