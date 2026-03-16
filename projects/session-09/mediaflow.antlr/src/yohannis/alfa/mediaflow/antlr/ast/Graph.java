package yohannis.alfa.mediaflow.antlr.ast;

import java.util.*;

public class Graph extends NamedElement {
    public final List<Node> nodes = new ArrayList<>();
    public final List<Edge> edges = new ArrayList<>();
}