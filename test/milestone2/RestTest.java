package milestone2;

import java.util.Timer;
import java.util.TimerTask;

import milestone2.Node;

public class RestTest {
	public static void main(String[] args) {
		int num_nodes = 10, multiplier = 2, bootstrap_port = 50001;
		String bootstrap_host = "localhost";


		Node[] nodes = new Node[num_nodes];
		final Node root = new Node(bootstrap_host, bootstrap_port);
		root.join();
		nodes[0] = root;
		for(int i = 1; i < num_nodes; i++)
		{
			int port = bootstrap_port + i;
			nodes[i] = new Node(bootstrap_host, port);
			nodes[i].join(root.getIp(), root.getPort());
		}
	}
}
