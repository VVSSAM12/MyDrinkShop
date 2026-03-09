package drinkshop.repository.file;

import drinkshop.domain.User;
import drinkshop.repository.Repository;

import java.util.List;


public class FileUserRepo extends FileAbstractRepository<Integer, User> {
    public FileUserRepo(String fileName) {
        super(fileName);
        loadFromFile();
    }

    @Override
    protected User extractEntity(String line) {
        return null;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return "";
    }


    @Override
    protected Integer getId(User entity) {
        return 0;
    }
}
