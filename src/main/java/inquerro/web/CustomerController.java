package inquerro.web;

import inquerro.model.Customer;
import inquerro.repository.CustomerRepository;
import inquerro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/")
public class CustomerController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final CustomerRepository userRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.userRepository = customerRepository;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Customer> getAllUsers() {
        LOG.info("Getting all users.");
        return userRepository.findAll();

    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public Customer getUser(@PathVariable String userId) {
        LOG.info("Getting user with ID: {}.", userId);
        return userRepository.findById(userId).get();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Customer addNewUsers(@RequestBody Customer user) {
        LOG.info("Saving user.");
        return userRepository.save(user);
    }

    @RequestMapping(value = "/settings/{userId}", method = RequestMethod.GET)
    public Object getAllUserSettings(@PathVariable String userId) {
        Customer user = userRepository.findById(userId).get();
        if (user != null) {
            return user.getUserSettings();
        } else {
            return "User not found.";
        }
    }

    @RequestMapping(value = "/settings/{userId}/{key}", method = RequestMethod.GET)
    public String getUserSetting(@PathVariable String userId, @PathVariable String key) {
        Customer user = userRepository.findById(userId).get();
        if (user != null) {
            return user.getUserSettings().get(key);
        } else {
            return "User not found.";
        }


    }

    @RequestMapping(value = "/settings/{userId}/{key}/{value}", method = RequestMethod.GET)
    public String addUserSetting(@PathVariable String userId, @PathVariable String key, @PathVariable String value) {
        Customer user = userRepository.findById(userId).get();
        if (user != null) {
            user.getUserSettings().put(key, value);
            userRepository.save(user);
            return "Key added";
        } else {
            return "User not found.";
        }
    }

}
