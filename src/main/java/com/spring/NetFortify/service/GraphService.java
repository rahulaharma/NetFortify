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

    private final SimpMessagingTemplate simpMessagingTemplate;

    public GraphService(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate=simpMessagingTemplate;
    }

    public void loadGraph(MultipartFile file) throws IOException {
        origionalGraph=new Graph();
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){
            String line;
            while((line= reader.readLine())!=null){
                String[] parts=line.trim().split("\\s+");
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
        resetGraph();
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
        broadcastMetrics(0,initialNodeCount,"RUNNING");

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
                (int) metrics.get("numComponents"),
                status
        );

        // sending to websocket topic
        simpMessagingTemplate.convertAndSend("/topic/metrics",result);

        try{
            Thread.sleep(100);
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
        return graph.getAdjacencyList().entrySet().stream().
                sorted((e1,e2)->Integer.compare(e2.getValue().size(),e1.getValue().size())).
                map(Map.Entry::getKey).
                collect(Collectors.toList());
    }

}
