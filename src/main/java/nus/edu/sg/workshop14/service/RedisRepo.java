package nus.edu.sg.workshop14.service;

import java.util.Map;

import org.springframework.stereotype.Repository;

import nus.edu.sg.workshop14.model.Contact;

@Repository
public interface RedisRepo {
    public void save(Contact contact);
    public Contact findById(final String contactId);
    public Map<String, Contact> findAll();
    public void delete(String contactId);
}
