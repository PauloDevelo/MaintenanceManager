package ecogium.tools.utils.generic.subscribableList;

import java.util.Collection;


public interface IListListenner<T extends IObject> {
	void onListChanged(Collection<T> valuesAdded,	Collection<T> valuesDeleted);
}
