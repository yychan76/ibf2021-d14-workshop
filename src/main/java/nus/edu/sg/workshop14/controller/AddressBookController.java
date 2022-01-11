package nus.edu.sg.workshop14.controller;

import java.util.List;

// import third party library for logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import nus.edu.sg.workshop14.model.Contact;
import nus.edu.sg.workshop14.service.ContactsRedis;

@Controller
public class AddressBookController {
    private static final Logger logger = LoggerFactory.getLogger(AddressBookController.class);

    @Autowired
    ContactsRedis contactsRepository;

    @GetMapping("/addcontact")
    public String showAddContactForm(Model model) {
        Contact contact = new Contact();
        model.addAttribute("contact", contact);
        return "form";
    }

    @GetMapping("/")
    public String showAddressBook(Model model) {
        List<Contact> contactsList = contactsRepository.getContacts();
        model.addAttribute("contactsList", contactsList);
        return "addressbook";
    }

}
