package com.example.family;

import family.NodeInfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NodeRegistry {

    private final Set<NodeInfo> nodes = ConcurrentHashMap.newKeySet();

    private final ConcurrentHashMap<Integer, Set<NodeInfo>> messageLocations =
            new ConcurrentHashMap<>();

    public void add(NodeInfo node) {
        nodes.add(node);
    }

    public void addAll(Collection<NodeInfo> others) {
        nodes.addAll(others);
    }

    public List<NodeInfo> snapshot() {
        return List.copyOf(nodes);
    }

    public void remove(NodeInfo node) {
        nodes.remove(node);
        for (Set<NodeInfo> holders : messageLocations.values()) {
            holders.remove(node);
        }
    }

    public void registerMessage(int messageId, NodeInfo node) {
        messageLocations
                .computeIfAbsent(messageId, k -> ConcurrentHashMap.newKeySet())
                .add(node);
    }

    public Set<NodeInfo> getMessageLocations(int messageId) {
        return messageLocations.getOrDefault(messageId, Set.of());
    }
}