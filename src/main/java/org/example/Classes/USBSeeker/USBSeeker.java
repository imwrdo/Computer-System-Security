package org.example.Classes.USBSeeker;

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
                    try {
                        String canonicalPath = root.getCanonicalPath();
                        System.out.println(canonicalPath);
                        System.out.println(volumesDir);
                        for (File volume : volumesDir.listFiles()) {
                            System.out.println(volume);
                            if (!volume.toString().equals("Macintosh HD")
                                    || !volume.toString().equals("Recovery")
                                    || !volume.toString().equals("/")) {
                                return true;
                            }
                        }
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
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

    public List<File> FindUSBs() {
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println("OS name: " + os);

        File[] roots = File.listRoots();
        System.out.println("Roots in find USBs:" + Arrays.toString(roots));

        List<File> ret = new LinkedList<>();
        if(os.contains("mac")){
            System.out.println("Mac OS detected");
            File volumesDir = new File("/Volumes");
            if(volumesDir.exists() && volumesDir.canRead()) {
                System.out.println("Volumes dir exists: " + volumesDir);
                for(File volume : Objects.requireNonNull(volumesDir.listFiles())) {
                    if(!volume.toString().equals("/Volumes/Macintosh HD")
                            && !volume.toString().equals("/Volumes/Recovery")
                            && !volume.toString().equals("/")) {
                        System.out.println("Add USB: " + volume);
                        ret.add(volume);
                        System.out.println("Ret after addition: " + ret);
                    }
                }
            }
        }else{
            System.out.println("Not Mac OS");
            for(File root : roots) {
                if(CheckIfUSB(root)) {
                    System.out.println("USB: " + root);
                    ret.add(root);
                }
            }
        }

        return ret;
    }

    public String GetKeyPath(String keyFileName) {
        System.out.println("Key file name: " + keyFileName);
        List<File> pendrives = FindUSBs();
        for(File root : pendrives) {
            if(Files.exists(Path.of(root.getAbsolutePath().concat(keyFileName)))){
                System.out.println("Key found: " + root.getAbsolutePath() + keyFileName);
                return root.getAbsolutePath() + keyFileName;
            }
            if(OSNAME.contains("mac")
                    && Files.exists(root.toPath().resolve("privateKey"))) {
                System.out.println("Key found: " + root.getAbsolutePath() +"/"+ keyFileName);
                return root.getAbsolutePath() +"/"+ keyFileName;
            }

        }
        return "None";
    }
}
