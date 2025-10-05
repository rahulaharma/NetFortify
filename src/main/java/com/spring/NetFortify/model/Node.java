package com.spring.NetFortify.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data // this annotation like a bundler for many other anotations such as @Getter @Setter @RequiredArgsConstructor( A constructor for only final attributes)  a usefull to string method and even @EqualsHashCode
@EqualsAndHashCode(of="id")  // this anotation defines equals() and hashcode() methods
// two Node objects will be considered equals if only their id match
// two Nodes are equal then there hashcode should be same. Defining hashcode is important for using the class HashSet as this class will have only unique values. So if two nodes will have same hashcode then only one will be added in hashset
public class Node {
    private final String id;
}
