package com.ogangi.barcode.writer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jmtt on 8/24/17.
 */

public class Barcode {
    @Nullable private String altText;
    private String format;
    private String message;
    private String messageEncoding;



    public Barcode(String format, String message, String messageEncoding, @Nullable String altText) {
        this.altText = altText;
        this.format = format;
        this.message = message;
        this.messageEncoding = messageEncoding;
    }

    public Barcode(String format, String message, @Nullable String altText) {
        this.altText = altText;
        this.format = format;
        this.message = message;
        this.messageEncoding = "iso-8859-1";
    }

    public Barcode(JSONObject jsonBarcode) throws JSONException{
        this.altText = jsonBarcode.getString("altText");
        this.format = jsonBarcode.getString("format");
        this.message = jsonBarcode.getString("message");
        this.messageEncoding = jsonBarcode.getString("messageEncoding");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barcode)) return false;

        Barcode barcode = (Barcode) o;

        if (getAltText() != null ? !getAltText().equals(barcode.getAltText()) : barcode.getAltText() != null)
            return false;
        if (!getFormat().equals(barcode.getFormat())) return false;
        if (!getMessage().equals(barcode.getMessage())) return false;
        return getMessageEncoding().equals(barcode.getMessageEncoding());

    }

    @Override
    public int hashCode() {
        int result = getAltText() != null ? getAltText().hashCode() : 0;
        result = 31 * result + getFormat().hashCode();
        result = 31 * result + getMessage().hashCode();
        result = 31 * result + getMessageEncoding().hashCode();
        return result;
    }



    @Nullable
    public String getAltText() {
        return altText;
    }

    public void setAltText(@NonNull String altText) {
        this.altText = altText;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public void setMessageEncoding(String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

    @Override
    public String toString() {
        return "Barcode{" +
                "altText='" + altText + '\'' +
                ", format='" + format + '\'' +
                ", message='" + message + '\'' +
                ", messageEncoding='" + messageEncoding + '\'' +
                '}';
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("altText", altText);
            jsonObject.put("format", format);
            jsonObject.put("message", message);
            jsonObject.put("messageEncoding", messageEncoding);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject.toString();
    }
}
