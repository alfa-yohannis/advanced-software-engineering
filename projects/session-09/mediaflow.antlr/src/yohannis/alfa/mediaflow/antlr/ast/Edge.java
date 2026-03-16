package yohannis.alfa.mediaflow.antlr.ast;

public class Edge extends NamedElement {
    public String sourceName; // temporary unresolved text
    public String targetName; // temporary unresolved text
    public Port source;       // resolved later
    public Port target;       // resolved later
}