package com.jarvis.local;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/** Safe app-private coding workspace. It never writes outside JARVIS/files. */
public final class CodingAgent {
    private final File root;
    public CodingAgent(Context context) {
        root = new File(context.getFilesDir(), "JARVIS_WORKSPACE");
        if (!root.exists()) root.mkdirs();
    }

    public String handle(String command) {
        String c = command.trim();
        String lower = c.toLowerCase();
        try {
            if (lower.startsWith("create folder ")) {
                String name = safePath(c.substring(14).trim());
                File f = new File(root, name);
                return f.mkdirs() || f.isDirectory() ? "Folder created: " + name : "Folder already exists: " + name;
            }
            if (lower.startsWith("create file ")) {
                String path = safePath(c.substring(12).trim());
                File f = new File(root, path);
                File parent = f.getParentFile();
                if (parent != null) parent.mkdirs();
                if (f.createNewFile()) return "File created: " + path;
                return "File already exists: " + path;
            }
            if (lower.startsWith("write file ")) {
                int sep = c.indexOf(" :: ");
                if (sep < 0) return "Use: write file path.ext :: your code";
                String path = safePath(c.substring(11, sep).trim());
                String content = c.substring(sep + 4);
                File f = new File(root, path);
                File parent = f.getParentFile();
                if (parent != null) parent.mkdirs();
                try (FileOutputStream out = new FileOutputStream(f, false)) {
                    out.write(content.getBytes(StandardCharsets.UTF_8));
                }
                return "Code written to: " + path;
            }
            if (lower.equals("list workspace") || lower.equals("open workspace")) {
                return list(root, "");
            }
        } catch (Exception e) {
            return "Coding agent error: " + e.getMessage();
        }
        return null;
    }

    private String safePath(String value) throws Exception {
        if (value.isEmpty() || value.contains("..") || value.startsWith("/")) throw new Exception("invalid workspace path");
        return value.replace('\\', '/');
    }

    private String list(File dir, String prefix) {
        File[] items = dir.listFiles();
        if (items == null || items.length == 0) return "Workspace is empty.";
        StringBuilder b = new StringBuilder("Workspace:\n");
        for (File f : items) b.append(prefix).append(f.isDirectory() ? "[DIR] " : "[FILE] ").append(f.getName()).append('\n');
        return b.toString().trim();
    }
}
