package enginemonitor.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jdom.Element;

public class Task extends DataEventHandler implements Cloneable{
	private static Logger _logger = Logger.getLogger(Task.class);
	private static int _countTask = 0;
	
	private String _id = null;
	private String _name = null;
	private String _description = null;
	
	/**
	 * Période en mois après lequel il faut effectuer la tâche
	 */
	private short _month = 0;
	/**
	 * Nombre d'heure moteur après lesquelles il faut exécuter la tache.
	 */
	private int _engineHours = 0;
	
	private Historic _histo = null;
	private Engine _engine = null;
	
	private Task(Historic histo, Engine engine){
		_countTask++;
		_histo = histo;
		_engine  = engine;
	}
	
	public Task(Element taskElem, Historic histo, Engine engine){
		this(histo, engine);
		
		_id = taskElem.getAttributeValue("Id");
		checkId();
		_name = taskElem.getChildText("Name");
		_description = taskElem.getChildText("Description");
		if(_description == null){
			_description = "";
		}
		
		Element periodElm = taskElem.getChild("Period");
		_month = Short.parseShort(periodElm.getChildText("Month"));
		_engineHours = Integer.parseInt(periodElm.getChildText("EngineHour"));
		
	}
	
	/**
	 * Cette fonction permet d'assurer que la variable static permettant de générer les identifiants
	 * est bien correcte ...
	 */
	private void checkId() {
		String numPartStr = _id.substring(5);
		int numPart = Integer.parseInt(numPartStr);
		if(_countTask < numPart){
			_countTask = numPart + 1;
		}
	}

	public Task(String name, String desc, short month, int engineHours, Historic histo, Engine engine){
		this(histo, engine);
		
		_id = "Task_" + Integer.toString(_countTask);
		_histo = histo;
		_name = name;
		_description = desc;
		_month = month;
		_engineHours = engineHours;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String name) {
		if(_name.compareTo(name) != 0){
			_name = name;
			
			for(Entry myEntry : getHistoric()){
				myEntry.set_name(name);
			}
			
			sendEventDataChanging("name");
		}
	}

	public String get_description() {
		return _description;
	}

	public void set_description(String description) {
		if(_description.compareTo(description) != 0){
			this._description = description;
			sendEventDataChanging("description");
		}
	}

	public short get_month() {
		return _month;
	}

	public void set_month(short month) {
		if(_month != month){
			_month = month;
			sendEventDataChanging("month");
		}
	}

	public int get_engineHours() {
		return _engineHours;
	}

	public void set_engineHours(int engineHours) {
		if(_engineHours != engineHours){
			_engineHours = engineHours;
			sendEventDataChanging("engineHours");
		}
	}
	
	String get_id() {
		return this._id;
	}
	
	public Element getElement(){
		Element taskElem = new Element("Task");
		taskElem.setAttribute("Id", _id);
		
		Element nameElement = new Element("Name");
		nameElement.setText(_name);
		taskElem.addContent(nameElement);
		
		Element descElement = new Element("Description");
		descElement.setText(_description);
		taskElem.addContent(descElement);
		
		Element periodElm = new Element("Period");
		Element monthElm = new Element("Month");
		monthElm.setText(Short.toString(_month));
		periodElm.addContent(monthElm);
		Element engineHourElm = new Element("EngineHour");
		engineHourElm.setText(Integer.toString(_engineHours));
		periodElm.addContent(engineHourElm);
		taskElem.addContent(periodElm);
		
		return taskElem;
	}
	
	public Entry getLastEntry(){
		ArrayList<Entry> histo = _histo.getChronoEntryList("Task_"+get_id());
		
		if(histo.isEmpty()){
			return null;
		}
		else{
			return histo.get(histo.size() - 1);
		}
	}

	
	public int howManyEngineHours() throws IllegalStateException{
		if(get_engineHours() == -1){
			throw new IllegalStateException("Cette tâche n'est pas a effectué en fonction des heures moteur.");
		}
		return  get_engineHours() + getLastEngineHour() - (int)(_engine.get_age() + (float)0.5);
	}
	
	/**
	 * Indique quand effectuer cette tache
	 * @return
	 */
	public GregorianCalendar whenTodoIt(){
		GregorianCalendar lastDate = getLastDate();
		long lastTime = lastDate.getTimeInMillis();
		
		long nextTime = lastTime + (long)((float)get_month() * (float)30.5 * (float)24 * (float)60 * (float)60 * (float)1000);
		
		GregorianCalendar gregCal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		gregCal.setTime(new Date(nextTime));
		return gregCal;
	}
	
	/**
	 * Permet de récupérer l'age du moteur à la dernière exécution. Si la tache n'a jamais été effectuée, on retourne 0 (moteur neuf).
	 * @return
	 */
	private int getLastEngineHour(){
		Entry lastEntry = getLastEntry();
		if(lastEntry != null)
			return lastEntry.get_age();
		else{
			return 0;
		}
	}
	
	/**
	 * Permet d'obtenir la date de la dernière exécution de la tâche. Si la tache n'a jamais été effectuée, on retourne la date d'installation
	 * du moteur.
	 * @return Une date UTC
	 */
	private GregorianCalendar getLastDate(){
		Entry lastEntry = getLastEntry();
		if(lastEntry != null)
			try {
				return lastEntry.get_dateUTC();
			} catch (ParseException e) {
				_logger.error("erreur de parsing de date", e);
				return null;
			}
		else{
			return _engine.get_installation();
		}
	}

	public ArrayList<Entry> getHistoric() {
		return _histo.getChronoEntryList("Task_" + get_id());
	}
	
	public synchronized void addEntry(GregorianCalendar cal, int ageMoteur, String description){
		Entry entry = new Entry("Task_"+get_id(), get_name(), cal, ageMoteur, description);
		_histo.addEntry(entry);
		sendEventDataChanging("histo");
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Engine getEngine() {
		return _engine;
	}

	public synchronized boolean removeEntry(Entry selectedEntry) {
		if(_histo.removeEntry(selectedEntry)){
			sendEventDataChanging("histo");
			return true;
		}
		else{
			return false;
		}
	}

	public void addEntry(Entry entry) {
		entry.set_type("Task_"+get_id());
		_histo.addEntry(entry);
		sendEventDataChanging("histo");
	}

	
	public int getLevel() {
		if(get_engineHours() != -1){
			if(howManyEngineHours() <= 0 || whenTodoIt().getTimeInMillis() <= System.currentTimeMillis()){
				return 3;
			}
			else if(howManyEngineHours() < (int)((float)get_engineHours()/(float)10 + (float)0.5) ||
					whenTodoIt().getTimeInMillis() - System.currentTimeMillis() <= (float)get_month()*(float)30.5*(float)24 * (float)360000.5){
				return 2;
			}
			else{
				return 1;
			}
		}
		else{
			if(whenTodoIt().getTimeInMillis() <= System.currentTimeMillis()){
				return 3;
			}
			else if(whenTodoIt().getTimeInMillis() - System.currentTimeMillis() <= (float)get_month()*(float)30.5*(float)24 * (float)360000.5){
				return 2;
			}
			else{
				return 1;
			}
		}
	}
}
