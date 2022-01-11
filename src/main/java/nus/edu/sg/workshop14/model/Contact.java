package nus.edu.sg.workshop14.model;

import java.io.Serializable;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Contact implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String id;
    private SecureRandom random = new SecureRandom();
    private static final int ID_LENGTH = 8;
    private static final Logger logger = LoggerFactory.getLogger(Contact.class);

    public Contact() {
        id = generateId(ID_LENGTH);
        logger.info("ID: {}", id);
    }

    public Contact(String name, String email, String phone) {
        id = generateId(ID_LENGTH);
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Contact(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    private synchronized String generateId(int length) {
        int num = random.nextInt(0x1000000);
        String hexFormat = String.format("%%0%sx", length);
        return String.format(hexFormat, num);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
