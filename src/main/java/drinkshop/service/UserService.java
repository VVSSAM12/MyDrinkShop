package drinkshop.service;

import drinkshop.domain.User;
import drinkshop.repository.Repository;

import java.util.List;

public class UserService {
    private final Repository<Integer, User> repository;

    public UserService(Repository<Integer, User> repository) {
        this.repository = repository;
    }
    
    public List<User> findAll() {
        return repository.findAll();
    }
    
    public void save(User user) {
        repository.save(user);
    }
    
    public boolean login(String username, String password) {
        //
        return true;
    }
}
