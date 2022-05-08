package net.javaguides.springboot.web.dto;

public class RiskValue {
    private String risk;

    public RiskValue() {
    }

    public RiskValue(String risk) {
        this.risk = risk;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }
}
