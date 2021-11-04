package server_side.database.pojo;

import java.util.HashSet;
import java.util.Set;

public class Role {

    final String name;
    final Set<Role> subRoles;
    final Set<AccessRight> extraAccessRights;

    public Role(String name, Set<Role> subRoles, Set<AccessRight> extraAccessRights) {
        this.name = name;
        this.subRoles = subRoles;
        this.extraAccessRights = extraAccessRights;
    }

    public Set<AccessRight> getAllAccessRights() {
        final HashSet<AccessRight> result = new HashSet<>();
        for (Role subRole : subRoles) {
            result.addAll(subRole.getAllAccessRights());
        }
        result.addAll(extraAccessRights);
        return result;
    }
}
