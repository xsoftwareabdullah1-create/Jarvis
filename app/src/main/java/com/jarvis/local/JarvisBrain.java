package com.jarvis.local;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class JarvisBrain {
    private final MemoryStore memory;
    private final AndroidTools tools;
    private LocalBrainEngine localModel;
    private final CodingAgent coding;
    public JarvisBrain(Context context) {
        memory = new MemoryStore(context);
        tools = new AndroidTools(context);
        coding = new CodingAgent(context);
        localModel = new LocalModelGateway(context); // Plug an embedded GGUF/llama.cpp engine here later.
    }

    public String think(String input) {
        String raw = input == null ? "" : input.trim();
        String c = raw.toLowerCase(Locale.US);
        if (raw.isEmpty()) return "Awaiting your instruction.";

        String codingResult = coding.handle(raw);
        if (codingResult != null) return codingResult;

        if (localModel != null && localModel.isLoaded()) {
            try {
                String generated = localModel.generate(raw);
                if (generated != null && !generated.trim().isEmpty()) return generated.trim();
            } catch (Throwable ignored) {}
        }

        String action = tools.execute(raw);
        if (action != null) return action;

        if (c.equals("who are you") || c.contains("your name"))
            return "I am JARVIS, your local personal AI assistant. My core can operate without an AI API.";

        if (c.contains("time"))
            return "The time is " + new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date()) + ".";

        if (c.contains("date"))
            return "Today is " + new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(new Date()) + ".";

        if (c.startsWith("remember ")) {
            String value = raw.substring(9).trim();
            memory.remember(value);
            return "Consider it remembered.";
        }
        if (c.contains("what do you remember") || c.equals("recall")) {
            String value = memory.recall();
            return value.isEmpty() ? "My memory is currently empty." : "You asked me to remember: " + value;
        }
        if (c.equals("forget everything") || c.equals("clear memory")) {
            memory.clear();
            return "Memory cleared.";
        }
        if (c.contains("build an app") || c.contains("make an app") || c.contains("create an app")) {
            return "App Builder mode is ready. I can turn your instruction into a project plan; the full local coding model can be connected to the same brain without changing this interface.";
        }
        if (c.contains("system status") || c.equals("status"))
            return "All core systems are online. Local brain, memory, Android tools and voice interface are standing by.";

        if (c.contains("hello") || c.contains("hi jarvis"))
            return "Good evening, sir. Systems are online. How may I assist you?";

        return "I heard you, sir. My local command brain does not yet have a dedicated tool for that request.";
    }
}
