package nus.edu.sg.workshop14.controller;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import nus.edu.sg.workshop14.error.ResourceNotFoundException;
import nus.edu.sg.workshop14.model.Contact;
import nus.edu.sg.workshop14.service.ContactsRedis;

@Controller
public class ContactController {
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    ContactsRedis contactsRepository;

    @PostMapping(path = "/contact", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String createContact(@ModelAttribute Contact contact, Model model)
            throws MalformedURLException {
        logger.info("Name: {}", contact.getName());
        logger.info("Email: {}", contact.getEmail());
        logger.info("Phone: {}", contact.getPhone());

        Contact persistToRedisContact = new Contact(
            contact.getName(),
            contact.getEmail(),
            contact.getPhone()
        );

        contactsRepository.save(persistToRedisContact);

        model.addAttribute("contact", persistToRedisContact);

        // URI location = URI.create("/contact/" + contact.getId());

        // return ResponseEntity.created(location).body("Contact Created: " +
        // contact.getId() + " access at: " + location);
        // return ResponseEntity.created(location).body("redirect:/addressbook");
        return "result";

    }

    @GetMapping(path = "/contact/{id}")
    public String readContact(@PathVariable(name = "id", required = true) String id, Model model) {
        // read the environment variable set in the spring application
        try {
            Contact retrievedContact = contactsRepository.findById(id);
            model.addAttribute("contact", retrievedContact);
            return "contact";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user id: %s not found", id), e);
        }
    }

    @PostMapping(path = "/contact/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public String updateContact(@PathVariable(name = "id", required = true) String id, @ModelAttribute Contact contact,
            Model model) {
        logger.info("ID: {}", contact.getId());
        logger.info("Name: {}", contact.getName());
        logger.info("Email: {}", contact.getEmail());
        logger.info("Phone: {}", contact.getPhone());

        try {
            contactsRepository.update(contact);
            return "contact";
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user id: %s not found", id), e);
        }
    }
}
