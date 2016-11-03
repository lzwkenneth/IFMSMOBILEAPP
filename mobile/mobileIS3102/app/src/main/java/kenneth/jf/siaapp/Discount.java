package kenneth.jf.siaapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by User on 1/11/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Discount {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("retailerName")
    private String retailerName;
    @JsonProperty("QRCode")
    private String QRCode;
    @JsonProperty("discountMessage")
    private String discountMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getDiscountMessage() {
        return discountMessage;
    }

    public void setDiscountMessage(String discountMessage) {
        this.discountMessage = discountMessage;
    }
}


