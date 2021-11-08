package server_side.middlewares.authorization;

import server_side.database.pojo.AccessRight;

public interface Authorizator {
    <T> AuthorizationResult<T> authorize(String username, AccessRight requiredAccessRight);
}
