package com.example.orm_exercise.controllers;

import com.example.orm_exercise.models.Contact;
import com.example.orm_exercise.models.Address;
import com.example.orm_exercise.repositories.ContactRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {

        // IMPORTANT: set the back-reference on each Address
        if (contact.getAddresses() != null) {
            for (Address address : contact.getAddresses()) {
                address.setContact(contact);
            }
        }

        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {

            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());

            // FIX: safely replace the address list
            contact.getAddresses().clear();

            if (updatedContact.getAddresses() != null) {
                for (Address address : updatedContact.getAddresses()) {
                    address.setContact(contact);
                    contact.getAddresses().add(address);
                }
            }

            return contactRepository.save(contact);
        }).orElse(null);
    }


    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable int id) {
        contactRepository.deleteById(id);
    }
}
