package uk.ac.northumbria.securephonebook.models;

/**
 * Created by Siddhant on 30/03/2017.
 */

public class Contact {

    private String firstName;   // First name of the contact
    private String lastName;    // Last name of the contact
    private String email;       // Email of the contact
    private String number;      // Number of the contact

    /**
     * Constructor for creating a Contact.
     * @param firstName - First name of the contact.
     * @param lastName - Last name of the contact.
     * @param email - Email of the contact.
     * @param number - Number of the contact.
     */
    public Contact(String firstName, String lastName, String email, String number) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.number = number;
    }

    /**
     * Returns the First Name of the contact.
     * @return - First name of the contact.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the Last Name of the contact.
     * @return - Last name of the contact.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the email of the contact.
     * @return - Email of the contact.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the number of the contact.
     * @return - Number of the contact.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Overriding toString to display custom text in the ListView.
     * @return - Returns custom formatted text with first name, last name and email fields.
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + "\n" + email;
    }

}
