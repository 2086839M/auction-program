import java.rmi.RemoteException;

public class UserImpl extends java.rmi.server.UnicastRemoteObject implements User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private long userId;
	
	//constructor
	public UserImpl(String name) throws java.rmi.RemoteException {
		super();
		this.name = name;
	}

	//getter and setter methods
	public String getName() throws java.rmi.RemoteException {
		return name;
	}

	public long getId() throws java.rmi.RemoteException {
		return userId;
	}

	//when an item's auction ends, notify the user with relevant results
	public void notifyResult(String result) throws java.rmi.RemoteException {
		System.out.println(result);
	}

	public void setId(long id) throws RemoteException {
		this.userId = id;
		
	}

	
}
