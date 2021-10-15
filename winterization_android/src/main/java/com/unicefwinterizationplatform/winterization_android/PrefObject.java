package com.unicefwinterizationplatform.winterization_android;

import java.io.Serializable;

/**
 * Created by Tarek on 9/9/15.
 */
public class PrefObject implements Serializable {

private String prefName;
private boolean enabled;
private String baseUrl;
private String port;
private String cbName;
private String username;
private String password;


    public PrefObject(String prefName, boolean enabled, String baseUrl, String port, String cbName, String username, String password) {
        this.prefName = prefName;
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        this.port = port;
        this.cbName = cbName;
        this.username = username;
        this.password = password;
    }

    public PrefObject() {

    }

    public String getPrefName() {
        return prefName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPrefName(String prefName) {
        this.prefName = prefName;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCbName() {
        return cbName;
    }

    public void setCbName(String cbName) {
        this.cbName = cbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
