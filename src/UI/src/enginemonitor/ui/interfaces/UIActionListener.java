package enginemonitor.ui.interfaces;

//Interface permettant de s'abonner aux demandes d'action provenant des UI.
public interface UIActionListener {
	
	boolean saveModel(String absolutePath);
	boolean exportPDF(String absolutePath);

}
