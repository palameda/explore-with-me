package ru.practicum;

public class ClientFactory {
    public static StatClient getDefaultClient(String url) {
        return new StatClient(url);
    }
}
