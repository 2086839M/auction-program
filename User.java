
public interface User extends java.rmi.Remote {
	//retrieve the name of the user
	public String getName() throws java.rmi.RemoteException;
	
	//retrieve the unique id of the user
	public long getId() throws java.rmi.RemoteException;
	
	//notify the user of the auction result
	public void notifyResult(String result) throws java.rmi.RemoteException;
	
	public void setId(long id) throws java.rmi.RemoteException;

}
