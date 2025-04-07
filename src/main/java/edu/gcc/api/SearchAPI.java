package edu.gcc.api;

import edu.gcc.Course;
import edu.gcc.Search;
import org.springframework.web.bind.annotation.GetMapping;
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
        Search search = new Search(query.getQuery());
        search.search();
        return search.getResult();
    }

    public static class SearchQuery {
        private String query;

        // Getter and Setter
        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }
}
