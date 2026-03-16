package yohannis.alfa.mediaflow.antlr.ast;

abstract class Transformer extends Node {
    public Backend backend;
    public String command;
    public int replicas;
}