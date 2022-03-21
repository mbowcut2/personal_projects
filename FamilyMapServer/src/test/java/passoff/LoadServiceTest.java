package passoff;

import com.google.gson.stream.JsonReader;
import dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import model.*;
//import passoffrequest.LoadRequest;
import com.google.gson.*;
import request.ClearRequest;
import request.LoadRequest;
import result.LoadResult;
import result.PersonResult;
import service.ClearService;
import service.LoadService;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LoadServiceTest {

    Event [] events = new Event[0];
    Person [] persons = new Person[0];
    User [] users = new User[0];
    Gson gson = new Gson();
    LoadRequest request;
    LoadResult result;
    ClearService clear = new ClearService();
    LoadService service;


    @BeforeEach
    public void setup() throws FileNotFoundException {

        JsonReader jsonReader = new JsonReader(new FileReader("passoffFiles/LoadData.json"));

        request = gson.fromJson(jsonReader, LoadRequest.class);



    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        clear.process(new ClearRequest());

    }

    @Test
    public void loadPass() throws DataAccessException {

        service = new LoadService();
        result = service.process(request);
        Assertions.assertTrue(result.isSuccess());


    }

    @Test
    public void loadFail() throws DataAccessException {

        service = new LoadService();
        result = service.process(new LoadRequest(null, null, null));
        Assertions.assertFalse(result.isSuccess());
    }
}
