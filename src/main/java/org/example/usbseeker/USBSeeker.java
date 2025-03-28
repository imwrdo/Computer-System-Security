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

/**
 * The USBSeeker class is responsible for detecting connected USB drives and searching for specific files
 * within those drives. It is designed to operate across multiple operating systems including Windows,
 * macOS, and Linux.
 *
 * This class provides functionality to:
 * - Identify USB drives based on operating system-specific criteria.
 * - Search connected USB drives for a file with a specific name.
 * - Handle differences in file system structure across platforms.
 */
public class USBSeeker {

    // Operating system detection for cross-platform compatibility
    private final static String OSNAME = System.getProperty("os.name").toLowerCase();

    // Constants for specific OS paths and drive types
    private static final String WINDOWS_DRIVE_TYPE = "2"; // Represents a removable drive on Windows
    private static final String MAC_HD = "Macintosh HD";  // Mac default system volume name
    private static final String MAC_RECOVERY = "Recovery"; // Mac recovery partition name

    // List to hold Windows drive information
    private final List<String> windowsDriveList = new LinkedList<>();

    /**
     * Creates a list of Windows drives by executing a WMIC command.
     * The command returns a list of drives and their types, and only removable drives are added to the list.
     */
    private void createWindowsDriveList() {
        try {
            // Execute command to list logical disks and their types
            Process process = Runtime.getRuntime().exec("wmic logicaldisk get caption,DriveType");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                // Filter out drives that are removable (DriveType = 2)
                reader.lines()
                        .filter(line -> line.contains(WINDOWS_DRIVE_TYPE))
                        .forEach(windowsDriveList::add);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Error handling for failed command execution
        }
    }

    /**
     * Checks whether a given file path represents a pendrive (removable storage) based on the operating system.
     * @param root The file path to check.
     * @return True if the path corresponds to a pendrive, false otherwise.
     */
    private boolean isPendrive(File root) {
        if (OSNAME.contains("win")) {
            if (windowsDriveList.isEmpty()) {
                createWindowsDriveList(); // Build the list of removable drives for Windows
            }
            // For Windows, check if the drive matches one of the removable drives from the list
            return windowsDriveList.stream()
                    .anyMatch(drive -> drive.contains(root.getAbsolutePath()
                            .substring(0, root.getAbsolutePath().length() - 1)));
        } else if (OSNAME.contains("mac")) {
            return isValidMacVolume(root); // For macOS, check if it's a valid external volume
        } else if (OSNAME.contains("nux") || OSNAME.contains("nix")) {
            // For Linux, check if the path is located under /media or /mnt, which are common mount points
            return root.getAbsolutePath().startsWith("/media") || root.getAbsolutePath().startsWith("/mnt");
        }
        return false; // Default case for unsupported OS types
    }

    /**
     * Checks if a given volume on macOS is a valid external volume (not system or recovery partition).
     * @param root The file path to check.
     * @return True if the volume is a valid external volume, false otherwise.
     */
    private boolean isValidMacVolume(File root) {
        File volumesDir = new File("/Volumes");
        if (volumesDir.exists() && volumesDir.canRead()) {
            // Check against known system volumes like "Macintosh HD" and "Recovery"
            return Arrays.stream(Objects.requireNonNull(volumesDir.listFiles()))
                    .anyMatch(volume -> !volume.getName().equals(MAC_HD) && !volume.getName().equals(MAC_RECOVERY) && root.equals(volume));
        }
        return false; // Return false if unable to read the volumes directory
    }

    /**
     * Finds all connected USB drives (pendrives) based on the operating system.
     * @return A list of File objects representing the pendrives found on the system.
     */
    public List<File> findPendrives() {
        File[] roots = File.listRoots(); // Get the root file system locations
        List<File> pendrives = new LinkedList<>();
        if (OSNAME.contains("mac")) {
            // For macOS, check volumes directory
            File volumesDir = new File("/Volumes");
            if (volumesDir.exists() && volumesDir.canRead()) {
                pendrives = Arrays.stream(Objects.requireNonNull(volumesDir.listFiles()))
                        .filter(this::isValidMacVolume) // Filter out non-valid Mac volumes
                        .toList();
            }
        } else {
            // For other operating systems (Windows, Linux), check the root file systems
            for (File root : roots) {
                if (isPendrive(root)) {
                    pendrives.add(root); // Add the pendrive to the list if it matches
                }
            }
        }
        return pendrives; // Return the list of detected pendrives
    }

    /**
     * Searches for the key file on all detected USB drives.
     * @param keyFileName The name of the key file to search for.
     * @return The full path of the key file if found, or "None" if not found.
     */
    public String getKeyPath(String keyFileName) {
        // Iterate through all detected pendrives
        for (File pendrive : findPendrives()) {
            // Construct the path of the potential key file
            Path potentialPath = Path.of(pendrive.getAbsolutePath(), keyFileName);
            if (Files.exists(potentialPath)) {
                return potentialPath.toString(); // Return the path if the file exists
            }
            if (OSNAME.contains("mac")) {
                // Special case for macOS, check for a privateKey file
                Path macPrivateKeyPath = pendrive.toPath().resolve("privateKey");
                if (Files.exists(macPrivateKeyPath)) {
                    return macPrivateKeyPath.toString(); // Return the path for privateKey file
                }
            }
        }
        return "None"; // Return "None" if the key file is not found on any pendrive
    }
}
