package ua.wyverno;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Folder with wem-files: ");
            String folderWithWemFilesStr = reader.readLine();
            System.out.print("Folder dump scene: ");
            String folderDumpSceneStr = reader.readLine();

            Path folderWithWemFiles = Paths.get(folderWithWemFilesStr);
            Path folderDumpScene = Paths.get(folderDumpSceneStr);

            if (folderWithWemFiles.toFile().exists() && folderDumpScene.toFile().exists()) {
                System.out.println("Start remove HASH from wem-files");
                WemHashRemoverAndCollectorVisitor wemHashRemoverAndCollectorVisitor = new WemHashRemoverAndCollectorVisitor();
                Files.walkFileTree(folderWithWemFiles, wemHashRemoverAndCollectorVisitor);
                System.out.println("The Finish remove HASH from wem-files");


                DumpSceneFilesVisitor dumpSceneFiles = new DumpSceneFilesVisitor();
                Files.walkFileTree(folderDumpScene, dumpSceneFiles);

                List<Path> filesDubWem = wemHashRemoverAndCollectorVisitor.getFilesWem();
                List<Path> filesOggFromTree = dumpSceneFiles.getOggFiles();

                Path treeFolder = Paths.get("./tree-scene");

                if (treeFolder.toFile().exists() || treeFolder.toFile().mkdirs()) {
                    System.out.println("Creating Tree Scene...");
                    FileUtils.cleanDirectory(treeFolder.toFile());
                    filesDubWem.forEach(wemPath -> {
                        String wemFileNameWithoutExt = wemPath.toFile().getName().replaceFirst("\\.wem$", "");
                        System.out.println("Try create tree for KEY: " + wemFileNameWithoutExt);
                        Path oggPath = filesOggFromTree.stream()
                                .filter(oggFile -> {
                                    String oggFileNameWithoutExt = oggFile.toFile().getName().replaceFirst("\\.ogg$", "");
                                    return wemFileNameWithoutExt.equals(oggFileNameWithoutExt);
                                }).findFirst()
                                .orElse(null);

                        if (oggPath != null) {
                            if (treeFolder.toFile().exists() || treeFolder.toFile().mkdirs()) {
                                try {
                                    Path oggCutsceneFolder = oggPath.getParent();
                                    Path oggChapterFolder = oggCutsceneFolder.getParent();
                                    Path jsonOgg = oggCutsceneFolder.resolve(wemFileNameWithoutExt + ".json");

                                    Path sceneTreeFolder = treeFolder.resolve(oggChapterFolder.getFileName()).resolve(oggCutsceneFolder.getFileName());

                                    if (sceneTreeFolder.toFile().exists() || sceneTreeFolder.toFile().mkdirs()) {
                                        Path sceneTreeWem = sceneTreeFolder.resolve(wemPath.toFile().getName());
                                        Path sceneTreeJSON = sceneTreeFolder.resolve(jsonOgg.toFile().getName());

                                        FileUtils.copyFile(wemPath.toFile(), sceneTreeWem.toFile());
                                        FileUtils.copyFile(jsonOgg.toFile(), sceneTreeJSON.toFile());
                                        System.out.println("Create folder for key: " + wemFileNameWithoutExt);
                                    } else {
                                        System.err.println("ERROR CAN'T TO CREATE FOLDER FOR KEY!");
                                    }
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            } else {
                                System.err.println("ERROR CAN'T TO CREATE TREE FOLDER!");
                            }
                        } else {
                            System.out.println("WARNING NOT EXISTS IN SCENE TREE -> " + wemFileNameWithoutExt);
                        }
                    });
                } else {
                    System.err.println("ERROR CAN'T TO CREATE TREE FOLDER!");
                }
            } else {
                System.out.println("Folder with wem files or dump scene folder is not exists!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
