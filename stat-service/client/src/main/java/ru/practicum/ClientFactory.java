package ru.practicum;

public class ClientFactory {
    public static StatClientImpl getDefaultClient(String url) {
        return new StatClientImpl(url);
    }
}
