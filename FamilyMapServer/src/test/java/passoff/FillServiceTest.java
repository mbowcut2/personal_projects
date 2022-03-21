package passoff;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dao.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions; //TODO: what's the deal with this?
import request.ClearRequest;
import request.FillRequest;
import request.LoadRequest;
import result.FillResult;
import service.ClearService;
import service.FillService;
import service.LoadService;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class FillServiceTest {

    Gson gson = new Gson();
    LoadService s = new LoadService();

    FillRequest request;
    FillService service = new FillService();
    FillResult result;

    ClearService clear = new ClearService();



    @BeforeEach
    public void setup() throws FileNotFoundException, DataAccessException {

        JsonReader jsonReader = new JsonReader(new FileReader("passoffFiles/LoadData.json"));

        LoadRequest r = gson.fromJson(jsonReader, LoadRequest.class);
        s.process(r);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        clear.process(new ClearRequest());

    }

    @Test
    public void fillPass() throws FileNotFoundException, DataAccessException {

        request = new FillRequest("sheila", 5);

        result = service.process(request);

        Assertions.assertTrue(result.isSuccess());


    }

    @Test
    public void fillFail() throws FileNotFoundException, DataAccessException {

        request = new FillRequest("badUsername", 5);

        result = service.process(request);

        Assertions.assertFalse(result.isSuccess());

    }
}
