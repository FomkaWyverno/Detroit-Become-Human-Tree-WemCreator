package ua.wyverno;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DumpSceneFilesVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> oggFiles = new ArrayList<>();
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(".ogg")) {
            System.out.println("Put to List .ogg file: " + file.toFile().getName());
            oggFiles.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getOggFiles() {
        return oggFiles;
    }
}
