package net.javaguides.springboot.web.dto;

public class SecretCode {
    private String secret_code;
    public SecretCode(String secret_code) {
        this.secret_code = secret_code;
    }

    public SecretCode(){}

    public String getSecret_code() {
        return secret_code;
    }

    public void setSecret_code(String secret_code) {
        this.secret_code = secret_code;
    }
}
