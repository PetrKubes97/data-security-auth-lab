package server_side.database.pojo;


// These are hardcoded, but it would be super easy in our case to replace them with string, in case the roles are
// dynamically added. The rest of role structure is saved in the db.
public enum RoleName {
    TECHNICIAN,
    ADMIN,
    USER,
    POWER_USER
}
