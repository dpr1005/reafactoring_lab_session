/*   This file is part of lanSimulation.
 *
 *   lanSimulation is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   lanSimulation is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with lanSimulation; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *   Copyright original Java version: 2004 Bart Du Bois, Serge Demeyer
 *   Copyright C++ version: 2006 Matthias Rieger, Bart Van Rompaey
 */
package lanSimulation;

import lanSimulation.internals.*;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.*;

/**
 * A <em>Network</em> represents the basic data stucture for simulating a Local
 * Area Network (LAN). The LAN network architecture is a token ring, implying
 * that packahes will be passed from one node to another, until they reached
 * their destination, or until they travelled the whole token ring.
 */
public class Network {
	/**
	 * Holds a pointer to myself. Used to verify whether I am properly initialized.
	 */
	private final Network initPtr_;
	/**
	 * Holds a pointer to some "first" node in the token ring. Used to ensure that
	 * various printing operations return expected behaviour.
	 */
	private Node firstNode_;
	/**
	 * Maps the names of workstations on the actual workstations. Used to initiate
	 * the requests for the network.
	 */
	private final Hashtable<String, Node> workstations_;

	/**
	 * Construct a <em>Network</em> suitable for holding #size Workstations.
	 * <p>
	 * <strong>Postcondition:</strong>(result.isInitialized()) & (!
	 * result.consistentNetwork());
	 * </p>
	 */
	public Network(int size) {
		assert size > 0;
		initPtr_ = this;
		firstNode_ = null;
		workstations_ = new Hashtable<>(size, 1.0f);
		assert isInitialized();
		assert !consistentNetwork();
	}

	/**
	 * Return a <em>Network</em> that may serve as starting point for various
	 * experiments. Currently, the network looks as follows.
	 * 
	 * <pre>
	 Workstation Filip [Workstation] -> Node -> Workstation Hans [Workstation]
	 -> Printer Andy [Printer] -> ...
	 * </pre>
	 * <p>
	 * <strong>Postcondition:</strong>result.isInitialized() &
	 * result.consistentNetwork();
	 * </p>
	 */
	public static Network DefaultExample() {
		Network network = new Network(2);

		Node wsFilip = new Workstation("Filip");
		Node n1 = new Node("n1");
		Node wsHans = new Workstation("Hans");
		Node prAndy = new Printer("Andy");

		wsFilip.nextNode_ = n1;
		n1.nextNode_ = wsHans;
		wsHans.nextNode_ = prAndy;
		prAndy.nextNode_ = wsFilip;

		network.workstations_.put(wsFilip.name_, wsFilip);
		network.workstations_.put(wsHans.name_, wsHans);
		network.firstNode_ = wsFilip;

		assert network.isInitialized();
		assert network.consistentNetwork();
		return network;
	}

	/**
	 * Answer whether #receiver is properly initialized.
	 */
	public boolean isInitialized() {
		return (initPtr_ == this);
	}

	/**
	 * Answer whether #receiver contains a workstation with the given name.
	 * <p>
	 * <strong>Precondition:</strong>this.isInitialized();
	 * </p>
	 */
	public boolean hasWorkstation(String ws) {
		Node n;

		assert isInitialized();
		n = workstations_.get(ws);
		if (n == null) {
			return false;
		} else {
			return n instanceof Workstation;
		}
	};

	/**
	 * Answer whether #receiver is a consistent token ring network. A consistent
	 * token ring network - contains at least one workstation and one printer - is
	 * circular - all registered workstations are on the token ring - all
	 * workstations on the token ring are registered.
	 * <p>
	 * <strong>Precondition:</strong>this.isInitialized();
	 * </p>
	 */
	public boolean consistentNetwork() {
		Node currentNode;
		int printersFound = 0, workstationsFound = 0;
		Hashtable<String, Node> encountered = new Hashtable<>(workstations_.size() * 2, 1.0f);

		if (notCircular(workstations_.isEmpty(), false)) return false;

		if (notCircular(firstNode_ == null, false)) return false;

		if (checkWorkstations()) return false;

		currentNode = firstNode_;
		while (!encountered.containsKey(currentNode.name_)) {
			encountered.put(currentNode.name_, currentNode);
			if (currentNode instanceof Workstation) {
				workstationsFound++;
			}

			if (currentNode instanceof Printer) {
				printersFound++;
			}

			currentNode = currentNode.nextNode_;
		}

		if (notCircular(currentNode != firstNode_, false)) return false;

		if (notCircular(printersFound == 0, false)) return false;

		return workstationsFound == workstations_.size();
	}

	/**
	 * If printersFound is true, return true, otherwise return false.
	 *
	 * @param printersFound This is the variable that will be returned. It is set to false by default.
	 * @param x the value of the parameter in the method being tested
	 * @return The boolean value of printersFound.
	 */
	private boolean notCircular(boolean printersFound, boolean x) {
		return printersFound;
	}

	/**
	 * Check if the workstations are circular.
	 *
	 * @return A boolean value.
	 */
	private boolean checkWorkstations() {
		Enumeration<Node> iter;
		Node currentNode;
		iter = workstations_.elements();
		while (iter.hasMoreElements()) {
			currentNode = iter.nextElement();
			if (notCircular(!(currentNode instanceof Workstation), true)) return true;

		}
		return false;
	}

	/**
	 * The #receiver is requested to broadcast a message to all nodes. Therefore
	 * #receiver sends a special broadcast packet across the token ring network,
	 * which should be treated by all nodes.
	 * <p>
	 * <strong>Precondition:</strong> consistentNetwork();
	 * </p>
	 * 
	 * @param report Stream that will hold a report about what happened when
	 *               handling the request.
	 * @return Anwer #true when the broadcast operation was succesful and #false
	 *         otherwise
	 */
	public boolean requestBroadcast(Writer report) {
		assert consistentNetwork();

		try {
			report.write("Broadcast Request\n");
		} catch (IOException ignored) {

		}

		Node currentNode = firstNode_;
		Packet packet = new Packet("BROADCAST", firstNode_.name_, firstNode_.name_);

		List<String> actions = new ArrayList<>();
		actions.add("' accepts broadcase packet.\n");
		actions.add("' passes packet on.\n");

		do {
			currentNode = send(report, currentNode, actions);
		} while (!atDestination(currentNode, packet));

		try {
			report.write(">>> Broadcast travelled whole token ring.\n\n");
		} catch (IOException ignored) {

		}

		return true;
	}

	/**
	 * If the packet's destination is the same as the current node's name, then we're at the destination.
	 *
	 * @param currentNode The node that the packet is currently at.
	 * @param packet The packet that is being sent.
	 * @return The boolean value of whether the packet has reached its destination.
	 */
	private boolean atDestination(Node currentNode, Packet packet) {
		return packet.destination_.equals(currentNode.name_);
	}

	/**
	 * If the packet's origin is the same as the current node's name, then return true.
	 *
	 * @param currentNode The node that the packet is currently at.
	 * @param packet the packet that is being sent
	 * @return The packet is being returned to the origin node.
	 */
	private boolean atOrigin(Node currentNode, Packet packet) {
		return packet.origin_.equals(currentNode.name_);
	}

	/**
	 * The #receiver is requested by #workstation to print #document on #printer.
	 * Therefore, #receiver sends a packet across the token ring network, until
	 * either (1) #printer is reached or (2) the packet travelled complete token
	 * ring.
	 * <p>
	 * <strong>Precondition:</strong> consistentNetwork() &
	 * hasWorkstation(workstation);
	 * </p>
	 * 
	 * @param workstation Name of the workstation requesting the service.
	 * @param document    Contents that should be printed on the printer.
	 * @param printer     Name of the printer that should receive the document.
	 * @param report      Stream that will hold a report about what happened when
	 *                    handling the request.
	 * @return Anwer #true when the print operation was succesful and #false
	 *         otherwise
	 */
	public boolean requestWorkstationPrintsDocument(String workstation, String document, String printer,
			Writer report) {

		assert consistentNetwork() & hasWorkstation(workstation);

		try {
			report.write("'");
			report.write(workstation);
			report.write("' requests printing of '");
			report.write(document);
			report.write("' on '");
			report.write(printer);
			report.write("' ...\n");
		} catch (IOException exc) {
			// just ignore
		}

		boolean result = false;
		Node currentNode;
		Packet packet = new Packet(document, workstation, printer);

		currentNode = workstations_.get(workstation);

		ArrayList<String> actions = new ArrayList<>();
		actions.add("' passes packet on.\n");

		do {
			currentNode = send(report, currentNode, actions);
		} while ((!atDestination(currentNode, packet)) & (!atOrigin(currentNode, packet)));

		if (atDestination(currentNode, packet)) {
			result = packet.printDocument(currentNode, report, this);
		} else {
			try {
				report.write(">>> Destinition not found, print job cancelled.\n\n");
				report.flush();
			} catch (IOException ignored) {

			}

		}

		return result;
	}

	/**
	 * Send the actions to the report and return the next node.
	 *
	 * @param report a Writer object that will be used to write the report.
	 * @param currentNode the current node in the chain
	 * @param actions a list of actions to send to the server
	 * @return The next node in the list.
	 */
	private Node send(Writer report, Node currentNode, List<String> actions) {
		try {
			for (String action : actions) {
				currentNode.logActionReport(report, action);
			}
			report.flush();
		} catch (IOException ignored) {

		}
		currentNode = currentNode.nextNode_;
		return currentNode;
	}

	/**
	 * "This function writes a report to a Writer object, and it throws an IOException if the Writer object throws an
	 * IOException."
	 *
	 * The above function is a good example of a function that is easy to understand. It's easy to understand because it's
	 * easy to read. The function is easy to read because it's easy to scan. The function is easy to scan because it's easy to
	 * parse. The function is easy to parse because it's easy to tokenize. The function is easy to tokenize because it's easy
	 * to lex. The function is easy to lex because it's easy to lex
	 *
	 * @param report The Writer object to which the report is written.
	 * @param author The author of the book.
	 * @param title The title of the book.
	 * @param status The status of the book.
	 */
	public void printAccounting(Writer report, String author, String title, String status) throws IOException {
		report.write("\tAccounting -- author = '");
		report.write(author);
		report.write("' -- title = '");
		report.write(title);
		report.write("'\n");
		report.write(status);
		report.flush();
	}

	/**
	 * Return a printable representation of #receiver.
	 * <p>
	 * <strong>Precondition:</strong> isInitialized();
	 * </p>
	 */
	public String toString() {
		assert isInitialized();
		StringBuffer buf = new StringBuffer(30 * workstations_.size());
		printOn(buf);
		return buf.toString();
	}

	/**
	 * Write a printable representation of #receiver on the given #buf.
	 * <p>
	 * <strong>Precondition:</strong> isInitialized();
	 * </p>
	 */
	public void printOn(StringBuffer buf) {
		assert isInitialized();
		Node currentNode = firstNode_;
		do {
			currentNode.printOn(buf);
			;
			buf.append(" -> ");
			currentNode = currentNode.nextNode_;
		} while (currentNode != firstNode_);
		buf.append(" ... ");
	}

	/**
	 * Write an HTML representation of #receiver on the given #buf.
	 * <p>
	 * <strong>Precondition:</strong> isInitialized();
	 * </p>
	 */
	public void printHTMLOn(StringBuffer buf) {
		assert isInitialized();

		buf.append("<HTML>\n<HEAD>\n<TITLE>LAN Simulation</TITLE>\n</HEAD>\n<BODY>\n<H1>LAN SIMULATION</H1>");
		Node currentNode = firstNode_;
		buf.append("\n\n<UL>");
		do {
			buf.append("\n\t<LI> ");
			currentNode.printHTMLOn(buf);
			;
			buf.append(" </LI>");
			currentNode = currentNode.nextNode_;
		} while (currentNode != firstNode_);
		buf.append("\n\t<LI>...</LI>\n</UL>\n\n</BODY>\n</HTML>\n");
	}

	/**
	 * Write an XML representation of #receiver on the given #buf.
	 * <p>
	 * <strong>Precondition:</strong> isInitialized();
	 * </p>
	 */
	public void printXMLOn(StringBuffer buf) {
		assert isInitialized();

		Node currentNode = firstNode_;
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<network>");
		do {
			buf.append("\n\t");
			currentNode.printXMLOn(buf);

			currentNode = currentNode.nextNode_;
		} while (currentNode != firstNode_);
		buf.append("\n</network>");
	}

}
