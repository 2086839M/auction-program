import java.rmi.Naming;

public class AuctionServer {
	public AuctionServer() {
		try {
			Auction auction = new AuctionImpl();
			Naming.rebind("rmi://localhost/CalculatorService", auction);
		} catch (Exception e) {
			System.out.println("Server Error: " + e);
		}
	}
	
	public static void main(String args[]) {
		new AuctionServer();
		System.out.println("Server running");
	}

}
