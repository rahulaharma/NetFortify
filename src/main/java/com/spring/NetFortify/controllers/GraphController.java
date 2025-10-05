package com.spring.NetFortify.controllers;

import com.spring.NetFortify.model.Graph;
import com.spring.NetFortify.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService){
        this.graphService=graphService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadGraph(@RequestParam("file") MultipartFile file){// @RequestParm(name) : look for a paremeter named "name" in the HTTP request  Multipartfile is the object to store the uploaded file
        //Normal File object could be used to interect if the file was on servers filesystem
        try{
            graphService.loadGraph(file);
            return ResponseEntity.ok("Graph loaded successfully! Nodes:"+graphService.getGraph().getNodeCount());
        }
        catch (IOException e){
            return ResponseEntity.status(500).body("Failed to load graph: "+e.getMessage());
        }
    }

    @PostMapping("/simulate")
    public ResponseEntity<String> startSimulation(@RequestParam("strategy") String strategy){
        if(graphService.getGraph()==null){
            return ResponseEntity.badRequest().body("No graph loaded... Please upload graph first.");
        }
        graphService.runSimulation(strategy);
        return ResponseEntity.ok("Simulation started with strategy: "+strategy);
    }
}
