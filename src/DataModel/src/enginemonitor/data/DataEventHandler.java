package enginemonitor.data;

import java.util.ArrayList;

public class DataEventHandler {
	
	private ArrayList<IDataListener> _listListeners = new ArrayList<IDataListener>();
	
	public void addListenner(IDataListener listener){
		_listListeners.add(listener);
	}
	
	public void removeListener(IDataListener listener){
		_listListeners.remove(listener);
	}
	
	void sendEventDataChanging(String param){
		for(IDataListener listener : _listListeners){
			listener.onDataChanged(this, param);
		}
	}

}
