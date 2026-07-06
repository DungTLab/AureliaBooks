package com.mycompany.aureliabooks.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model class for parsing Google User Info.
 * @author DungLT
 */
public class GooglePojo implements Serializable {
    @SerializedName("sub")
    private String id; // Google ID
    private String email;
    @SerializedName("email_verified")
    private boolean emailVerified;
    private String name; // Họ và tên đầy đủ
    @SerializedName("given_name")
    private String givenName;
    @SerializedName("family_name")
    private String familyName;
    private String picture; // Link ảnh đại diện Google

    public GooglePojo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
