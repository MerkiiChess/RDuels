package ru.merkii.rduels.config.messages;

import com.bivashy.configuration.ConfigurationHolder;
import com.bivashy.configuration.holder.ConfigurationSectionHolder;
import ru.merkii.rduels.util.ColorUtil;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MessageConfiguration implements ConfigurationHolder {

    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, List<String>> messagesList = new HashMap<>();
    private HashMap<String, MessageConfiguration> subMessages = new HashMap<>();

    public MessageConfiguration(ConfigurationSectionHolder section) {
        for (String key : section.keys()) {
            if (section.isSection(key)) {
                addSubMessages(key, section);
                continue;
            }
            addMessage(key, section.getString(key));
            addMessagesList(key, section.getList(key));
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, String.format("Message with key %s not found", key));
    }

    public List<String> getMessages(String key) {
        return this.messagesList.getOrDefault(key, Collections.singletonList(String.format("Message with key %s not found", key)));
    }

    private void addSubMessages(String key, ConfigurationSectionHolder section) {
        subMessages.put(key, new MessageConfiguration(section.section(key)));
    }

    private void addMessage(String key, String message) {
        messages.put(key, color(message));
    }

    private void addMessagesList(String key, List<String> messages) {
        messagesList.put(key, ColorUtil.color(messages));
    }

    private String color(String text) {
        if (text == null)
            throw new IllegalArgumentException("Cannot color null text: " + null);
        return ColorUtil.color(text);
    }
}