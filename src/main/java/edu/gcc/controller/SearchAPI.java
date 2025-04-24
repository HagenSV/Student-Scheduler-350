package edu.gcc.controller;

import edu.gcc.AuthenticatedUserUtil;
import edu.gcc.Course;
import edu.gcc.Search;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class SearchAPI {

    //Search returning a list of courses
    @PostMapping("/api/v1/search")
    public List<Course> search(@RequestBody SearchQuery query) {
        // Assuming 'search' is an object that contains the search criteria
        // You can process the search criteria and return the results
        // For now, we will just return a success message
        //System.out.println("Searching for: " + query.getQuery());
        String user = AuthenticatedUserUtil.getAuthenticatedUser();
        Search search = new Search(query.query(), user, false);
        search.search();
        return search.getResult();
    }

    public record SearchQuery(String query){}
}
