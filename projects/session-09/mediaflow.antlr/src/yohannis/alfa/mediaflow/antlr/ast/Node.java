package yohannis.alfa.mediaflow.antlr.ast;

import java.util.ArrayList;
import java.util.List;

abstract class Node extends NamedElement {
    public final List<Port> ports = new ArrayList<>();
}