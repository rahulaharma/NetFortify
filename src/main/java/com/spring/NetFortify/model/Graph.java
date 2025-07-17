package com.spring.NetFortify.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Graph {
    private final Map<String,Node> nodes=new ConcurrentHashMap<>();
    private final Map<String, List<String>> adjacencyList=new ConcurrentHashMap<>();

    public void addNode(String id){
        nodes.putIfAbsent(id,new Node(id));
        adjacencyList.putIfAbsent(id,new CopyOnWriteArrayList<>());
    }

    public void addEdge(String sourceId,String destId){
        addNode(sourceId);
        addNode(destId);

        //Assuming a undirected graph as of now
        adjacencyList.get(sourceId).add(destId);
        adjacencyList.get(destId).add(sourceId);
    }

    public int getNodeCount(){
        return nodes.size();
    }

    public void removeNode(String nodeId){
        nodes.remove(nodeId);
        adjacencyList.remove(nodeId);

        // removing all the edges pointing to the removed node
        adjacencyList.values().forEach(neighbours->neighbours.remove(nodeId));
    }

}
