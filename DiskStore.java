package com.example.family;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiskStore {

    private final Path dataDir;

    public DiskStore(int port) {
        this.dataDir = Path.of("data", String.valueOf(port));
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new RuntimeException("DiskStore init failed", e);
        }
    }

    public void save(int messageId, String message) {
        Path file = dataDir.resolve(messageId + ".txt");
        try {
            Files.writeString(file, message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write message " + messageId, e);
        }
    }

    public String load(int messageId) {
        Path file = dataDir.resolve(messageId + ".txt");
        if (!Files.exists(file)) {
            return null;
        }
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read message " + messageId, e);
        }
    }
}
