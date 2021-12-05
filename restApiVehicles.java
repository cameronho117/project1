package hellorestapi.hellorestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.nio.file.Paths;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

import static org.apache.commons.io.FileUtils.writeStringToFile;

public class restApiVehicles {

    /**
     * take in vehicle object and write it to a local txt file.
     * always appends to file
     * if 5 POST requests are made, the local file will contain 5 vehicles in JSON
     *
     * @param newVehicle
     * @return
     * @throws IOException
     */

    // New Fun
    @RequestMapping(value = "/addVehicle", method = RequestMethod.POST)
    public Vehicle addVehicle(@RequestBody Vehicle newVehicle) throws IOException {
        //objectMapper provides functionality for reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();
        //create a FileWrite to write to inventory.txt and APPEND mode is true
        FileWriter output = new FileWriter("./files/inventory.txt", true);
        //serialize greeting object to JSON and write it to file
        mapper.writeValue(output, newVehicle);
        //Append a new line character to the file
        //The above FileWriter ("output") is automatically closed by the mapper.
        writeStringToFile(new File("./files/inventory.txt"),
                System.lineSeparator(), //newline String
                CharEncoding.UTF_8, //encoding type
                true);

        return newVehicle;
    }

    /**
     * take given id and find the vehicles that has the matching id
     * It will iterate the local file line-by-line, check if the id matches, and if there is a match
     * return the vehicle object.
     *
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getVehicle/{id}", method = RequestMethod.GET)
    public Vehicle getVehicle(@PathVariable("id") int id) throws IOException {
        FileReader fr = new FileReader("./files/inventory.txt");
        BufferedReader br = new BufferedReader(fr);

        ObjectMapper mapper = new ObjectMapper();

        while (br.readLine() != null) {
            Vehicle localVeh = mapper.readValue(br.readLine(), Vehicle.class);
            if (localVeh.getId() == id) {
                return localVeh;
            }
        }
        return null;
    }

    /**
     * Iterate the local file line-by-line
     * Check if the current line’s vehicle’s id matches the vehicle id that is passed in
     * If there is a match, update the current line with the vehicle that was passed in
     *
     * @param newVehicle
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateVehicle", method = RequestMethod.PUT)
    public Vehicle updateVehicle(@RequestBody Vehicle newVehicle) throws IOException {
//        Vehicle localVeh = ObjectMapper.readValue(new File("src/test/resources/json_car.json"), Vehicle.class);

        Scanner sc = new Scanner(new File("D://input.txt"));
        StringBuffer buffer = new StringBuffer();

        ObjectMapper mapper = new ObjectMapper();
//        mapper.value
        while (sc.hasNextLine()) {
            Vehicle localVeh = mapper.readValue(sc.nextLine(), Vehicle.class);
            if (localVeh.getId() == newVehicle.getId()) {
                sc.nextLine().replaceAll(newVehicle.toString(), System.lineSeparator());
                return localVeh;
            }
        }
        return null;
    }

    /**
     * takes the given id and deletes from the local file.
     * It will iterate the local file line-by-line to check if the given id exists, then delete
     *
     * @param id
     * @return ResponseEntity<String>
     * @throws IOException
     */
    @RequestMapping(value = "/deleteVehicle/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteVehicle(@PathVariable("id") int id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Vehicle> vehicleList = new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get("books.json").toFile(), Vehicle[].class)));
        for (Vehicle tempVeh : vehicleList) {
//            String message = FileUtils.readFileToString(new File("./files/inventory.txt"), StandardCharsets.UTF_8.name());
//            Vehicle vehicle = mapper.readValue(message, Vehicle.class);
            if (tempVeh.getId() == id) {
                FileUtils.writeStringToFile(new File("./files/inventory.txt"), "", StandardCharsets.UTF_8.name());
            }
            return new ResponseEntity("Deleted", HttpStatus.OK);
        }
        return new ResponseEntity("Not Found", HttpStatus.BAD_REQUEST);
    }

    /**
     * return the most recent 10 vehicles (as a list) that were added to
     * the inventory.
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getLatestVehicles", method = RequestMethod.GET)
    public List<Vehicle> getLatestVehicles() throws IOException {
        List<Vehicle> returnArray = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            // convert JSON array to list of Vehicle
            List<Vehicle> vehicleList = new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get("books.json").toFile(), Vehicle[].class)));

            // Add 10 elements
            for (int i = 0; i < 10; i++) {
                returnArray.add(vehicleList.get(vehicleList.size() - i));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnArray;
    }
}

