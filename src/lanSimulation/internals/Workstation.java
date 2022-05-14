package lanSimulation.internals;


public class Workstation extends Node{

    public Workstation(String name) {
        super(name);
    }

    public Workstation(String name, Node nextNode) {
        super(name, nextNode);
    }

    /**
     * Append the string "Workstation " to the StringBuffer buf, then append the name of the workstation, then append the
     * string " [Workstation]
     *
     * @param buf The StringBuffer to print the information on.
     */
    public void printOn(StringBuffer buf) {
        buf.append("Workstation ");
        buf.append(name_);
        buf.append(" [Workstation]");
    }

    /**
     * Prints the name of the workstation, followed by the string [Workstation].
     *
     * @param buf The StringBuffer to which the HTML is appended.
     */
    public void printHTMLOn(StringBuffer buf) {
        buf.append("Workstation ");
        buf.append(name_);
        buf.append(" [Workstation]");
    }

    /**
     * This function prints the name of the workstation to the given buffer.
     *
     * @param buf The StringBuffer to which the XML will be written.
     */
    public void printXMLOn(StringBuffer buf) {
        buf.append("<workstation>");
        buf.append(name_);
        buf.append("</workstation>");
    }

}