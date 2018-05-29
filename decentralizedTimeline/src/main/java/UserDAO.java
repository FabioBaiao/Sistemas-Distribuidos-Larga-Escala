import org.mapdb.DB;
import org.mapdb.DBMaker;

public class UserDAO {

    private static final DB db = DBMaker.fileDB("decentralized-timeline.db").fileMmapEnable().make();;
    private static final Map<String, User> userMap;


}
