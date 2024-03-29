package nus.edu.sg.workshop14.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import nus.edu.sg.workshop14.error.ResourceNotFoundException;
import nus.edu.sg.workshop14.model.Contact;

@Service
public class ContactsRedis implements RedisRepo {
    private static final Logger logger = LoggerFactory.getLogger(ContactsRedis.class);
    private static final String CONTACT_CACHE = "CONTACTS";
    private static final String CONTACT_IDS = "CONTACT_IDS";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(final Contact contact) {
        logger.info("Saving to ID: {}", contact.getId());
        Long existingId = redisTemplate.opsForList().indexOf(CONTACT_IDS, contact.getId());
        if (existingId == null) {
            redisTemplate.opsForList().leftPush(CONTACT_IDS, contact.getId());
        }
        redisTemplate.opsForHash().put(CONTACT_CACHE, contact.getId(), contact);
    }

    @Override
    public Contact findById(final String contactId) {
        Contact retrievedContact = (Contact) redisTemplate.opsForHash().get(CONTACT_CACHE, contactId);
        // logger.info(String.valueOf(retrievedContact));
        if (retrievedContact == null) {
            throw new ResourceNotFoundException();
        }
        return retrievedContact;
    }

    @Override
    public Map<String, Contact> findAll() {
        // entries returns Map<Object, Object> so need to convert to Map<String, Contact>
        return redisTemplate.opsForHash().entries(CONTACT_CACHE).entrySet().stream()
            .filter(entry -> entry.getKey() instanceof String && entry.getValue() instanceof Contact)
            .collect(Collectors.toMap(e -> (String) e.getKey(), e -> (Contact) e.getValue()));
    }

    public List<Contact> findRange(int startIdx, int batchSize) {
        int endIdx = startIdx + batchSize - 1;
        if (batchSize == 0) {
            // return empty list
            return new ArrayList<>();
        }
        List<Object> contactIds = redisTemplate.opsForList().range(CONTACT_IDS, startIdx, endIdx);
        if (contactIds != null) {
            return redisTemplate.opsForHash().multiGet(CONTACT_CACHE, contactIds).stream()
                .filter(Contact.class::isInstance)
                .map(Contact.class::cast)
                .toList();
        }
        return new ArrayList<>();
    }

    @Override
    public void delete(String contactId) {
        redisTemplate.opsForHash().delete(CONTACT_CACHE, contactId);
    }

    public List<Contact> getContacts() {
        return redisTemplate.opsForHash().values(CONTACT_CACHE).stream()
            .filter(Contact.class::isInstance)
            .map(Contact.class::cast)
            .toList();
    }

    public void update(Contact contact) {
        Contact retrivedContact = findById(contact.getId());
        if (retrivedContact != null) {
            retrivedContact.setName(contact.getName());
            retrivedContact.setEmail(contact.getEmail());
            retrivedContact.setPhone(contact.getPhone());
            save(retrivedContact);
        } else {
            throw new ResourceNotFoundException();
        }
    }
}
