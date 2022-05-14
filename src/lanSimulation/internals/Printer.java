package lanSimulation.internals;

public class Printer extends Node{

    public Printer(String name) {
        super(name);
    }

    public Printer(String name, Node nextNode) {
        super(name,nextNode);
    }

    public void printOn(StringBuffer buf) {
        buf.append("Printer ");
        buf.append(name_);
        buf.append(" [Printer]");
    }

    public void printHTMLOn(StringBuffer buf) {
        buf.append("Printer ");
        buf.append(name_);
        buf.append(" [Printer]");
    }

    public void printXMLOn(StringBuffer buf) {
        buf.append("<printer>");
        buf.append(name_);
        buf.append("</printer>");
    }
}