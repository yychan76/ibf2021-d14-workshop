package nus.edu.sg.workshop14.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import nus.edu.sg.workshop14.error.ResourceNotFoundException;
import nus.edu.sg.workshop14.model.Contact;



public class Contacts {
    private String dataDir;
    private static final Logger logger = LoggerFactory.getLogger(Contacts.class);

    public Contacts(String dataDir) {
        logger.info("Initializing Contacts with dataDir: {}", dataDir);
        this.dataDir = dataDir;
    }

    public String getDataDir() {
        return this.dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public Path getContactFile(Contact contact) {
        String filename = contact.getId().toLowerCase();
        return Paths.get(dataDir, filename);
    }

    public Path getContactFile(String id) {
        return Paths.get(dataDir, id);
    }

    public void save(Contact contact) {
        List<String> contactInfo = new ArrayList<>(Arrays.asList(contact.getName(), contact.getEmail(), contact.getPhone()));
        try {
            Path contactFile = getContactFile(contact);
            Files.write(contactFile, contactInfo, StandardCharsets.UTF_8);
            logger.info("Contact info written to: {}", contactFile.toAbsolutePath());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void read(String id, Model model) {
        Path contactFile = getContactFile(id);
        if (contactFile.toFile().exists()) {
            List<String> contactData;
            try {
                contactData = Files.readAllLines(contactFile);
                Contact contact = new Contact(id, contactData.get(0), contactData.get(1), contactData.get(2));
                model.addAttribute("contact", contact);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public Optional<Contact> read(String contactFileName) {
        Path contactFile = Paths.get(dataDir, contactFileName);
        if (contactFile.toFile().exists()) {
            try {
                List<String> contactData = Files.readAllLines(contactFile);
                Contact contact = new Contact(contactFileName, contactData.get(0), contactData.get(1), contactData.get(2));
                return Optional.ofNullable(contact);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public void update(Contact contact) {
        Path contactFile = getContactFile(contact);
        if (contactFile.toFile().exists()) {
            save(contact);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public Set<String> getContactFileNames() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dataDir))) {
            Set<String> contactFileNames = stream
                                        .filter(file -> !Files.isDirectory(file))
                                        .map(Path::getFileName)
                                        .map(Path::toString)
                                        .collect(Collectors.toSet());

            logger.info(contactFileNames.toString());
            return contactFileNames;
        }
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        try {
            for (String contactFileName : getContactFileNames()) {
                Optional<Contact> opt = read(contactFileName);
                if (opt.isPresent()) {
                    contacts.add(opt.get());
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return contacts;
    }

}
