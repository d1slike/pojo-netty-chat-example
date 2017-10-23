package ru.disdev;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Connection {

    private StringProperty host = new SimpleStringProperty("127.0.0.1");
    private StringProperty port = new SimpleStringProperty("1234");
    private StringProperty name = new SimpleStringProperty("");

    public String getHost() {
        return host.get();
    }

    public StringProperty hostProperty() {
        return host;
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public String getPort() {
        return port.get();
    }

    public StringProperty portProperty() {
        return port;
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
