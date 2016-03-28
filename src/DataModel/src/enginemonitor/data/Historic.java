package enginemonitor.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdom.Element;

public class Historic extends DataEventHandler implements IDataListener {
	
	private ArrayList<Entry> _histo = new ArrayList<Entry>();

	public Historic(Element histoElem){
		List listEntry = histoElem.getChildren("Entry");
		
		for(Object obj : listEntry){
			Entry entry = new Entry((Element)obj);
			_histo.add(entry);
		}
	}
	
	public Historic() {
	}

	public ArrayList<Entry> getChronoEntryList(){
		ArrayList<Entry> chronoListEntry = new ArrayList<Entry>(_histo);
		
		Collections.sort(chronoListEntry, new Comparator<Entry>(){
			@Override
			public int compare(Entry arg0, Entry arg1) {
				return arg0._dateUTC.compareTo(arg1._dateUTC);
			}
			
		});
		
		return chronoListEntry;
	}
	
	public ArrayList<Entry> getChronoEntryList(String type){
		ArrayList<Entry> chronoListEntry = new ArrayList<Entry>();
		for(Entry entry : _histo){
			if(entry.get_type().compareTo(type) == 0){
				chronoListEntry.add(entry);
			}
		}
		
		Collections.sort(chronoListEntry, new Comparator<Entry>(){
			@Override
			public int compare(Entry arg0, Entry arg1) {
				return arg0._dateUTC.compareTo(arg1._dateUTC);
			}
			
		});
		
		return chronoListEntry;
	}
	
	public boolean addEntry(Entry entry){
		entry.addListenner(this);
		boolean retour = _histo.add(entry);
		sendEventDataChanging("histo");
		return retour;
	}
	
	public boolean removeEntry(Entry entry){
		entry.removeListener(this);
		boolean retour = _histo.remove(entry);
		sendEventDataChanging("histo");
		return retour;
	}
	
	
	public Element getElement(){
		Element histoElm = new Element("Historic");
		
		for(Entry entry : _histo){
			histoElm.addContent(entry.getElement());
		}
		
		return histoElm;
	}

	@Override
	public void onDataChanged(Object source, String param) {
		sendEventDataChanging("histo");
	}
}
