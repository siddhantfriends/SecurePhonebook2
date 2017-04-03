package uk.ac.northumbria.securephonebook.models;



public class Group {

    private int id;             // id as mentioned in the database
    private String groupName;   // group name

    /**
     * Constructor for Group.
     * @param id - id of the group as mentioned in the database.
     * @param groupName - Name of the group.
     */
    public Group(int id, String groupName) {
        this.id = id;
        this.groupName = groupName;
    }

    /**
     * The id of the group as mentioned in the database.
     * @return - Returns the id of the group.
     */
    public int getId() {
        return id;
    }

    /**
     * The name of the group.
     * @return - Returns the name of the group.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Matches the id in String to that of int in the given model.
     * @param idText - ID in the text format.
     * @return - true if the id match else false.
     */
    public boolean hasId(String idText) {
        return (this.id == Integer.parseInt(idText));
    }

    @Override
    public String toString() {
        return groupName;
    }
}
