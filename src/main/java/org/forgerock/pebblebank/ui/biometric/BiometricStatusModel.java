package org.forgerock.pebblebank.ui.biometric;

import java.io.Serializable;

public class BiometricStatusModel implements Serializable {
    private String status = "";
    private Boolean success = null;

    public BiometricStatusModel(String status, Boolean success) {
        this.status = status;
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
