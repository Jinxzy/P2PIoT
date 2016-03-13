package release;

import milestone4.Node;

public class Release {
	public static void main(String[] args) throws InterruptedException {
		int num_nodes = 20, multiplier = 2, bootstrap_port = 50000;
		String bootstrap_host = "localhost";


		Node[] nodes = new Node[num_nodes];
		final Node root = new Node(bootstrap_host, bootstrap_port);
		root.join();
		nodes[0] = root;
		root.findResponsibleNode();
		Thread.sleep(6000);
		for(int i = 1; i < num_nodes; i++)
		{
			int port = bootstrap_port + i;
			nodes[i] = new Node(bootstrap_host, port);
			nodes[i].join(root.getIp(), root.getPort());
		}
		//root.findResponsibleNode();
		
		for(int i = 0; i < num_nodes; i++) {
			System.out.println("-----------------------");
			System.out.println("NodeID: " + nodes[i].getID());
			nodes[i].printPhotonInfo();
			System.out.println("-----------------------");
		}
	}
}
