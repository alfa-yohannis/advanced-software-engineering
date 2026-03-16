package yohannis.alfa.mediaflow.antlr.ast;

import java.util.*;

import yohannis.alfa.mediaflow.antlr.generated.*;


public class AstBuilder extends MediaFlowBaseVisitor<Object> {

    private final Map<String, Port> portIndex = new HashMap<>();
    private final List<Edge> unresolvedEdges = new ArrayList<>();

    @Override
    public Graph visitModel(MediaFlowParser.ModelContext ctx) {
        Graph graph = (Graph) visit(ctx.graph());
        resolveEdges();
        return graph;
    }

    @Override
    public Graph visitGraph(MediaFlowParser.GraphContext ctx) {
        Graph g = new Graph();
        g.name = ctx.name.getText();

        for (MediaFlowParser.NodeContext n : ctx.node()) {
            g.nodes.add((Node) visit(n));
        }

        for (MediaFlowParser.EdgeContext e : ctx.edge()) {
            Edge edge = (Edge) visit(e);
            g.edges.add(edge);
        }

        return g;
    }

    @Override
    public Node visitNode(MediaFlowParser.NodeContext ctx) {
        if (ctx.resource() != null) return (Node) visit(ctx.resource());
        if (ctx.scaler() != null) return (Node) visit(ctx.scaler());
        return (Node) visit(ctx.transcoder());
    }

    @Override
    public Resource visitResource(MediaFlowParser.ResourceContext ctx) {
        Resource r = new Resource();
        r.name = ctx.name.getText();
        r.mediaType = MediaType.valueOf(ctx.mediaTypeValue.getText());
        r.uri = unquote(ctx.uri.getText());
        r.external = Boolean.parseBoolean(ctx.externalValue.getText());

        for (MediaFlowParser.PortContext pctx : ctx.port()) {
            Port p = (Port) visit(pctx);
            p.owner = r;
            r.ports.add(p);
            registerPort(p);
        }
        return r;
    }

    @Override
    public Scaler visitScaler(MediaFlowParser.ScalerContext ctx) {
        Scaler s = new Scaler();
        s.name = ctx.name.getText();
        s.backend = Backend.valueOf(ctx.backendValue.getText());
        s.command = unquote(ctx.command.getText());
        s.replicas = Integer.parseInt(ctx.replicas.getText());
        s.width = Integer.parseInt(ctx.width.getText());
        s.height = Integer.parseInt(ctx.height.getText());

        for (MediaFlowParser.PortContext pctx : ctx.port()) {
            Port p = (Port) visit(pctx);
            p.owner = s;
            s.ports.add(p);
            registerPort(p);
        }
        return s;
    }

    @Override
    public Transcoder visitTranscoder(MediaFlowParser.TranscoderContext ctx) {
        Transcoder t = new Transcoder();
        t.name = ctx.name.getText();
        t.backend = Backend.valueOf(ctx.backendValue.getText());
        t.command = unquote(ctx.command.getText());
        t.replicas = Integer.parseInt(ctx.replicas.getText());
        t.videoCodec = unquote(ctx.videoCodec.getText());
        t.audioCodec = unquote(ctx.audioCodec.getText());
        t.container = unquote(ctx.container.getText());
        t.bitrate = Integer.parseInt(ctx.bitrate.getText());

        for (MediaFlowParser.PortContext pctx : ctx.port()) {
            Port p = (Port) visit(pctx);
            p.owner = t;
            t.ports.add(p);
            registerPort(p);
        }
        return t;
    }

    @Override
    public Port visitPort(MediaFlowParser.PortContext ctx) {
        Port p = new Port();
        p.name = ctx.name.getText();
        p.direction = Direction.valueOf(ctx.directionValue.getText());
        return p;
    }

    @Override
    public Edge visitEdge(MediaFlowParser.EdgeContext ctx) {
        Edge e = new Edge();
        e.name = ctx.name.getText();
        e.sourceName = ctx.source.getText();
        e.targetName = ctx.target.getText();
        unresolvedEdges.add(e);
        return e;
    }

    private void registerPort(Port p) {
        if (portIndex.containsKey(p.name)) {
            throw new RuntimeException("Duplicate port name: " + p.name);
        }
        portIndex.put(p.name, p);
    }

    private void resolveEdges() {
        for (Edge e : unresolvedEdges) {
            e.source = portIndex.get(e.sourceName);
            e.target = portIndex.get(e.targetName);

            if (e.source == null) {
                throw new RuntimeException("Unknown source port: " + e.sourceName);
            }
            if (e.target == null) {
                throw new RuntimeException("Unknown target port: " + e.targetName);
            }
        }
    }

    private String unquote(String s) {
        return s.substring(1, s.length() - 1);
    }
}
