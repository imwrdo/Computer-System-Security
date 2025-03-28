package org.example.usbseeker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class USBSeeker {
    private final static String OSNAME = System.getProperty("os.name").toLowerCase();
    private static final String WINDOWS_DRIVE_TYPE = "2";
    private static final String MAC_HD = "Macintosh HD";
    private static final String MAC_RECOVERY = "Recovery";

    private final List<String> windowsDriveList = new LinkedList<>();
    private void createWindowsDriveList() {
        try {
            Process process = Runtime.getRuntime().exec("wmic logicaldisk get caption,DriveType");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines()
                        .filter(line -> line.contains(WINDOWS_DRIVE_TYPE))
                        .forEach(windowsDriveList::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isPendrive(File root) {
        if (OSNAME.contains("win")) {
            if (windowsDriveList.isEmpty()) {
                createWindowsDriveList();
            }
            return windowsDriveList.stream()
                    .anyMatch(drive -> drive.contains(root.getAbsolutePath()
                            .substring(0, root.getAbsolutePath().length() - 1)));
        } else if (OSNAME.contains("mac")) {
            return isValidMacVolume(root);
        } else if (OSNAME.contains("nux") || OSNAME.contains("nix")) {
            return root.getAbsolutePath().startsWith("/media") || root.getAbsolutePath().startsWith("/mnt");
        }
        return false;
    }


    private boolean isValidMacVolume(File root) {
        File volumesDir = new File("/Volumes");
        if (volumesDir.exists() && volumesDir.canRead()) {
            return Arrays.stream(Objects.requireNonNull(volumesDir.listFiles()))
                    .anyMatch(volume -> !volume.getName().equals(MAC_HD) && !volume.getName().equals(MAC_RECOVERY) && root.equals(volume));
        }
        return false;
    }


    public List<File> findPendrives() {
        File[] roots = File.listRoots();
        List<File> pendrives = new LinkedList<>();
        if (OSNAME.contains("mac")) {
            File volumesDir = new File("/Volumes");
            if (volumesDir.exists() && volumesDir.canRead()) {
                pendrives = Arrays.stream(Objects.requireNonNull(volumesDir.listFiles()))
                        .filter(this::isValidMacVolume)
                        .toList();
            }
        } else {
            for (File root : roots) {
                if (isPendrive(root)) {
                    pendrives.add(root);
                }
            }
        }
        return pendrives;
    }


    public String getKeyPath(String keyFileName) {
        for (File pendrive : findPendrives()) {
            Path potentialPath = Path.of(pendrive.getAbsolutePath(), keyFileName);
            if (Files.exists(potentialPath)) {
                return potentialPath.toString();
            }
            if (OSNAME.contains("mac")) {
                Path macPrivateKeyPath = pendrive.toPath().resolve("privateKey");
                if (Files.exists(macPrivateKeyPath)) {
                    return macPrivateKeyPath.toString();
                }
            }
        }
        return "None";
    }
}
