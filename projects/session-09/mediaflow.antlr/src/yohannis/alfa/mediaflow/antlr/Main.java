package yohannis.alfa.mediaflow.antlr;

import yohannis.alfa.mediaflow.antlr.ast.*;
import yohannis.alfa.mediaflow.antlr.generated.*;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.Trees;


public class Main {
    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromFileName("input/test.mediaflow");
        MediaFlowLexer lexer = new MediaFlowLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MediaFlowParser parser = new MediaFlowParser(tokens);

        ParseTree tree = parser.model();

        System.out.println("Parse tree:");
        printTree(tree, parser, 0);

        AstBuilder builder = new AstBuilder();
        Graph graph = (Graph) builder.visit(tree);

        System.out.println("Parsed graph: " + graph.name);
        System.out.println("Nodes: " + graph.nodes.size());
        System.out.println("Edges: " + graph.edges.size());
    }

    private static void printTree(ParseTree node, Parser parser, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + Trees.getNodeText(node, parser));
        for (int i = 0; i < node.getChildCount(); i++) {
            printTree(node.getChild(i), parser, depth + 1);
        }
    }
}
