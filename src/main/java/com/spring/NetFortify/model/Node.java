package com.spring.NetFortify.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="id")
public class Node {
    private final String id;
}
