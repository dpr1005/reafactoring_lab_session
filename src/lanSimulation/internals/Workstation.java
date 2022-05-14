package lanSimulation.internals;


public class Workstation extends Node{		
		
		public Workstation(String name) {
			super(name);
		}
		
		public Workstation(String name, Node nextNode) {
			super(name, nextNode);
		}
		
		public void printOn(StringBuffer buf) {
			buf.append("Workstation ");
			buf.append(name_);
			buf.append(" [Workstation]");
		}
		
		public void printHTMLOn(StringBuffer buf) {
			buf.append("Workstation ");
			buf.append(name_);
			buf.append(" [Workstation]");
		}
		
		public void printXMLOn(StringBuffer buf) {
			buf.append("<workstation>");
			buf.append(name_);
			buf.append("</workstation>");
		}
		
	}