package com.spring.NetFortify.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Graph {
    private final Map<String,Node> nodes=new ConcurrentHashMap<>();
    private final Map<String, List<String>> adjacencyList=new ConcurrentHashMap<>(); // concurrent hashmap is used for thread safty . Multiple threads can read and write the data simountionasly from a normal hashmap leading to data inconsistency

    public void addNode(String id){
        nodes.putIfAbsent(id,new Node(id));
        adjacencyList.putIfAbsent(id,new CopyOnWriteArrayList<>());  // it is different from normal arraylist as it is thread safe
        // the reader thread will work on a perticular snapshot of the list
        // the writer will create a copy of the origional list and extra element it and make it the new list but reader reading the previous list
    }

    public void addEdge(String sourceId,String destId){
        addNode(sourceId);
        addNode(destId);

        //Assuming an undirected graph as of now
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
