package com.yupi.yuoj.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class UploadPathUtils {

    private UploadPathUtils() {
    }

    public static Path getBackendRoot() {
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if ("backend".equalsIgnoreCase(userDir.getFileName().toString()) && Files.exists(userDir.resolve("pom.xml"))) {
            return userDir;
        }
        Path backendDir = userDir.resolve("backend").toAbsolutePath().normalize();
        if (Files.exists(backendDir.resolve("pom.xml"))) {
            return backendDir;
        }
        return userDir;
    }

    public static Path getWorkspaceRoot() {
        Path backendRoot = getBackendRoot();
        Path parent = backendRoot.getParent();
        return parent == null ? backendRoot : parent;
    }

    public static Path getUploadRoot() {
        return getBackendRoot().resolve("uploads").toAbsolutePath().normalize();
    }
}
