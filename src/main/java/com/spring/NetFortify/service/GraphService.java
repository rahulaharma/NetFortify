package com.spring.NetFortify.service;

import com.spring.NetFortify.model.Graph;
import com.spring.NetFortify.model.SimulationResult;
import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
public class GraphService {
    private Graph graph;
    private Graph origionalGraph;// to reset simulation

    private final SimpMessagingTemplate simpMessagingTemplate; // a class for sending websocket messages from server to client its .convertAndSend() will create a message object and that will be sent to the messagebroker

    public GraphService(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    public void loadGraph(MultipartFile file) throws IOException {
        origionalGraph=new Graph();
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){
            String line;
            while((line= reader.readLine())!=null){
                String[] parts=line.trim().split("\\s+");  // \\S+ sequence of one or more spaces as a seperator
                if(parts.length>=2){
                    origionalGraph.addEdge(parts[0],parts[1]);
                }
            }
        }
        resetGraph();
    }

    private void resetGraph() {
        //deep copy of original graph to run simulations on...
        this.graph=new Graph();
        origionalGraph.getNodes().forEach((id,node)->this.graph.addNode(id));
        origionalGraph.getAdjacencyList().forEach((id,neighbours)->{
            this.graph.getAdjacencyList().get(id).addAll(neighbours);
        });
    }

    @Async
    public void runSimulation(String strategy) {
        resetGraph(); // after running the simulation one time .If the graph is already loaded and I want to run the simulation other time then we have to reset the graph to origional graph
        // reseting the graph at the start is important as runSimulation is async .Suppose two threads are running this function parellely and thread B starts when thread A partially distroyed graph Thread B will run simulations on this partially distroyed graph
        int initialNodeCount=graph.getNodeCount();
        if(initialNodeCount==0) return;
        List<String> nodesToRemove;
        switch(strategy.toLowerCase()){
            case "high-degree":
                nodesToRemove=getNodesByDegree();
                break;
            case "random":
            default:
                nodesToRemove=new ArrayList<>(graph.getNodes().keySet());
                Collections.shuffle(nodesToRemove);
                break;
        }
        int removedCount=0;
        //intial state before any removal
        broadcastMetrics(0,initialNodeCount,"STARTED");

        for(String nodeId: nodesToRemove){
            if(graph.getNodes().containsKey(nodeId)){
                graph.removeNode(nodeId);
                removedCount++;
                //broadcasting the metrics after each removal for real time feeling
                broadcastMetrics(removedCount,initialNodeCount,"RUNNING");
            }
        }
        
        //final state
        broadcastMetrics(removedCount,initialNodeCount,"COMPLETED");
    }

    private void broadcastMetrics(int removedCount, int initialNodeCount, String status) {
        Map<String,Object> metrics=calculateMetrics();
        double percentageRemoved=initialNodeCount>0 ? (double) removedCount/initialNodeCount*100 :0;

        SimulationResult result=new SimulationResult(
                removedCount,
                percentageRemoved,
                (int) metrics.get("lccSize"),
                (int) metrics.get("numComp"),
                status
        );

        // sending to websocket topic
        simpMessagingTemplate.convertAndSend("/topic/metrics",result);

        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private Map<String, Object> calculateMetrics(){
        Map<String,Object> metrics =new HashMap<>();
        Set<String> visited=new HashSet<>();
        int largestConnectedComponentSize=0;
        int numberOfComponents=0;
        for(String nodeId:graph.getAdjacencyList().keySet()){
            if(!visited.contains(nodeId)){
                numberOfComponents++;
                int currComponentSize=0;
                Queue<String> queue =new LinkedList<>();
                queue.add(nodeId);
                visited.add(nodeId);

                while(!queue.isEmpty()){
                    String currentNode=queue.poll();
                    currComponentSize++;
                    List<String> neighbours=graph.getAdjacencyList().get(currentNode);
                    if(neighbours!=null){
                        for(String neighbour:neighbours){
                            if(!visited.contains(neighbour)){
                                visited.add(neighbour);
                                queue.add(neighbour);
                            }
                        }
                    }
                }
                if(currComponentSize>largestConnectedComponentSize){
                    largestConnectedComponentSize=currComponentSize;
                }
            }
        }
        metrics.put("lccSize",largestConnectedComponentSize);
        metrics.put("numComp",numberOfComponents);
        return metrics;
    }
    private List<String> getNodesByDegree(){
        return graph.getAdjacencyList().entrySet().stream(). // entrySet() converts the map into a java Set of key value pairs
                // stream will convert that Set to java stream .A stream is a sequence of elements which can be processed in a pipeline
                sorted((e1,e2)->Integer.compare(e2.getValue().size(),e1.getValue().size())).
                map(Map.Entry::getKey). // extracts each element' key only
                collect(Collectors.toList()); // it collects all the String id's from the stream and make a list of them
    }

}


// file handling
// streams: byte stream for images,videos
// character stream: for characters
// java.io packege
// the stream class
// stream->directly linked to our io divices
//System.in-> in -> reference variable of type inputstream

// byte stream: inputstream and outputstream
// char stream: reader and writer

// these are abstract class which will have read and write
// Io Exception:corupt file,file not found etc
// in inputstream we can also add characters as they are byte data
// Input stream reader converts byte data to character data

// we don't have to close inputstreamreader because try catch will close that
// isr.read() its reads single character doesn't return the character itself but its unicode value