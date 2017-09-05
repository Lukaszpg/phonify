package pro.lukasgorny.util;

/**
 * Created by lukaszgo on 2017-05-25.
 */
public class QueryBody {

    public class UserQuery {

    }

    public class UserRoleQuery {
        public final static String FIND_ROLE_BY_EMAIL = "SELECT a.role FROM Role a, User b WHERE b.email=?1 and a.userId = b.id";
    }
}
