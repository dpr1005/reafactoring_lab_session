package lanSimulation.internals;

public class Printer extends Node{

    public Printer(String name) {
        super(name);
    }

    public Printer(String name, Node nextNode) {
        super(name,nextNode);
    }

    /**
     * Prints the name of the printer.
     *
     * @param buf The StringBuffer object that the printOn method will write to.
     */
    public void printOn(StringBuffer buf) {
        buf.append("Printer ");
        buf.append(name_);
        buf.append(" [Printer]");
    }

    /**
     * "Prints the name of the printer, followed by the string [Printer]."
     *
     * The first line of the function is a comment. Comments are ignored by the compiler. They are used to document the
     * code
     *
     * @param buf The StringBuffer to which the HTML will be appended.
     */
    public void printHTMLOn(StringBuffer buf) {
        buf.append("Printer ");
        buf.append(name_);
        buf.append(" [Printer]");
    }

    /**
     * This function prints the name of the printer.
     *
     * @param buf The StringBuffer to which the XML will be written.
     */
    public void printXMLOn(StringBuffer buf) {
        buf.append("<printer>");
        buf.append(name_);
        buf.append("</printer>");
    }
}