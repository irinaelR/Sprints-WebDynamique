package framework.utilities;

import java.io.*;
import jakarta.servlet.http.Part;

public class FormFile {
    private final String fileName;
    private final byte[] fileBytes;

    // Constructor
    public FormFile(Part part) throws IOException {
        this.fileName = extractFileName(part);
        this.fileBytes = readBytes(part.getInputStream());
    }

    // Getter for fileName
    public String getFileName() {
        return fileName;
    }

    // Getter for fileBytes
    public byte[] getFileBytes() {
        return fileBytes;
    }

    // Method to copy the file to the specified directory
    public void copyTo(String dirPath, String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            fileName = this.fileName;
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileBytes);
        }
    }

    // Utility method to extract the file name from the Part header
    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }

    // Utility method to read bytes from InputStream
    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        return buffer.toByteArray();
    }
}
