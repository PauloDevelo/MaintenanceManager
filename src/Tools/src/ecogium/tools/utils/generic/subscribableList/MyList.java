package ecogium.tools.utils.generic.subscribableList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Classe générique permettant de gérer une liste d'objet et de s'inscrire sur l'ajout et la suppression d'objet
 * @author ptorruella
 *
 * @param <T>
 */
public class MyList<T extends IObject> {
	
	protected HashMap<Long, T> _listObj = new HashMap<Long, T>();
	
	private HashSet<IListListenner<T>> _listListListenner = new HashSet<IListListenner<T>>();
	
	public void register(IListListenner<T> listenner){
		_listListListenner.add(listenner);
	}
	
	public void updateList(List<T> newList){
		HashMap<Long, T> listDeleted = (HashMap<Long, T>)_listObj.clone();
		HashMap<Long, T> listAdded = new HashMap<Long, T>();
		
		for(T newObj : newList){
			
			if(_listObj.containsKey(newObj.getId())){
				T obj = _listObj.get(newObj.getId());
				obj.update(newObj);
				listDeleted.remove(newObj.getId());
			}
			else{
				_listObj.put(newObj.getId(), newObj);
				listAdded.put(newObj.getId(), newObj);
			}
		}
		
		for(T obj : listDeleted.values()){
			_listObj.remove(obj.getId());
		}
		
		if(listAdded.size() != 0 || listDeleted.size() != 0){
			fireListChanged(listAdded, listDeleted);
		}
	}

	private void fireListChanged(HashMap<Long, T> listAddedEvt, HashMap<Long, T> listDeletedEvt) {
		for(IListListenner<T> listenner : _listListListenner){
			listenner.onListChanged(Collections.unmodifiableCollection(listAddedEvt.values()), Collections.unmodifiableCollection(listDeletedEvt.values()));
		}
	}
	
	public Collection<T> getUnmodifiableList(){
		return Collections.unmodifiableCollection(_listObj.values());
	}

}
