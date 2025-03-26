package be.umons.sdd.utils;

import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.Scene2D;
import be.umons.sdd.models.StraightSegment2D;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SceneSerializer {
    private static final File SCENES_FILE = buildSceneDirectoryPathFromCurrentDir();

    private static File buildSceneDirectoryPathFromCurrentDir() {
        try {
            // Get the current directory as a real path
            Path currentDir = Paths.get(".").toRealPath();

            // Search for a directory named "src" with a maximum depth of 5
            Optional<Path> srcDirectory = Files.find(
                currentDir, 
                5, 
                (path, attributes) -> Files.isDirectory(path) && "src".equals(path.getFileName().toString())
            ).findFirst();

            if (srcDirectory.isPresent()) {
                // Build the final path by appending "ressources/scenes"
                Path scenePath = srcDirectory.get().resolve("ressources").resolve("scenes");
                return scenePath.toFile();
            } else {
                throw new RuntimeException("The 'src' folder was not found within a search depth of 5.");
            }
        } catch (IOException e) {
            throw new RuntimeException("IO Error while building resource path from current directory: " + e.getMessage(), e);
        }
    }

    public static Scene2D readScene(String fileName) throws IOException {
        File sceneFile = new File(SCENES_FILE, fileName);

        return readScene(sceneFile);
    }

    public static Scene2D readScene(File fileFromRoot) throws IOException {
        if (!fileFromRoot.exists()) {
            throw new IOException("File not found: " + fileFromRoot.getAbsolutePath());
        }

        List<StraightSegment2D> segments = new ArrayList<>();
        int extentX = 0;
        int extentY = 0;
        int expectedCount = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileFromRoot))) {

            // Read header line.
            String header = br.readLine();
            if (header == null) {
                throw new IOException("Empty file: " + fileFromRoot.getAbsolutePath());
            }

            // Parse header: extentX, extentY, number of segments.
            header = header.replace(">", "").trim();
            String[] headerTokens = header.split("\\s+");
            if (headerTokens.length != 3) {
                throw new IOException("Invalid header format. Expected three numbers: a b n.");
            }
            try {
                extentX = Integer.parseInt(headerTokens[0]);
                extentY = Integer.parseInt(headerTokens[1]);
                expectedCount = Integer.parseInt(headerTokens[2]);
            } catch (NumberFormatException e) {
                throw new IOException("Header contains invalid numbers.", e);
            }
            
            // Read each segment line.
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length != 5) {
                    System.err.println("Warning: Invalid format at line " + lineNumber + ". Expected 5 tokens, found " + tokens.length + ". Skipping line.");
                    continue;
                }
                try {
                    double x1 = Double.parseDouble(tokens[0]);
                    double y1 = Double.parseDouble(tokens[1]);
                    double x2 = Double.parseDouble(tokens[2]);
                    double y2 = Double.parseDouble(tokens[3]);

                    String colorName = tokens[4];
                    Color color = ColorParser.getColor(colorName);
                    if (color == null) {
                        throw new IllegalArgumentException("Unknown color: " + colorName);
                    }

                    // Create a StraightSegment (assumed to implement SceneObject).
                    StraightSegment2D segment = new StraightSegment2D(new Point2D(x1, y1), new Point2D(x2, y2), color);
                    segments.add(segment);

                } catch (NumberFormatException e) {
                    System.err.println("Warning: Number format error at line " + lineNumber + ". Skipping line.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: " + e.getMessage() + " at line " + lineNumber + ". Skipping line.");
                }
            }

            br.close();
        }
        
        if (segments.size() != expectedCount) {
            System.err.println("Warning: Expected " + expectedCount + " segments, but read " + segments.size() + ".");
        }
        
        return new Scene2D(segments, extentX, extentY);
    }

    
}
