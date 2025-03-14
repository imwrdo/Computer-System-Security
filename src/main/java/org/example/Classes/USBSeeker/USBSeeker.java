package org.example.Classes.USBSeeker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class USBSeeker {

    private List<String> forWin = new LinkedList<>();
    private void CreateWinList() {
        try {
            Process process = Runtime.getRuntime().exec("wmic logicaldisk get caption,DriveType");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("2")) {
                    forWin.add(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean CheckIfUSB(File root) throws RuntimeException {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                if(forWin.isEmpty()) {
                    CreateWinList();
                }
                if(root.exists() && root.canRead())
                    for (String s : forWin) {
                        if (s.contains(root.getAbsolutePath().substring(
                                0, root.getAbsolutePath().length() - 1))) {
                            return true;
                        }
                    }
                return false;
            } else if (os.contains("mac")) {
                File volumesDir = new File("/Volumes");
                if (volumesDir.exists() && volumesDir.canRead()) {
                    return root.getAbsolutePath().startsWith("/Volumes");
                }
            } else if (os.contains("nux") || os.contains("nix")) {
                File mediaDir = new File("/media");
                if (mediaDir.exists() && mediaDir.isDirectory()) {
                    return root.getAbsolutePath().startsWith("/media") ||
                            root.getAbsolutePath().startsWith("/mnt");
                }
            }
            return false;
    }

    private List<File> FindUSBs() {
        File[] roots = File.listRoots();

        List<File> ret = new LinkedList<>();

        for(File root : roots) {
            if(CheckIfUSB(root))
                ret.add(root);
        }

        return ret;
    }

    public String GetKeyPath(String keyFileName) {
        List<File> pendrives = FindUSBs();
        for(File root : pendrives) {
            if(Files.exists(Path.of(root.getAbsolutePath().concat(keyFileName))))
                return root.getAbsolutePath() + keyFileName;
        }
        return "None";
    }
}
