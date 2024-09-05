package ua.wyverno;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class WemHashRemoverAndCollectorVisitor extends SimpleFileVisitor<Path> {

    private final List<Path> filesWem = new ArrayList<>();
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(".wem")) {
            System.out.println("Wem-file: " + file);
            String fileName = file.toFile().getName();
            String renameFile = fileName.replaceFirst("_50DB02DB\\.wem$", ".wem");
            File newFileName = new File(file.getParent().toFile(), renameFile);

            if (!file.toFile().equals(newFileName)) {
                if (file.toFile().renameTo(newFileName)) {
                    System.out.println("Remove HASH file to -> " + newFileName);

                    this.filesWem.add(newFileName.toPath());
                } else {
                    System.out.println("Fail rename file: " + file + " to -> " + newFileName);
                }
            } else {
                System.out.println("File is exists! -> " + file);
                this.filesWem.add(file);
            }

        }
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getFilesWem() {
        return filesWem;
    }
}
