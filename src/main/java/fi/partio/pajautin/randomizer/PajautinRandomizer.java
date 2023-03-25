package fi.partio.pajautin.randomizer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PajautinRandomizer {

    public static final double personalPreferenceWeight = 3.5;
    public static final int numberOfSelections = 10;

    public ArrayList<Workshop> workshops;
    public ArrayList<Participant> participants;
    public Random random;
    public static void main(String[] args) throws IOException {

        new PajautinRandomizer();
    }

    public PajautinRandomizer() throws IOException {
        random=new Random();
        generateWorkshops();
        generateParticipants();

        Workbook workbook = new XSSFWorkbook();
        addParticiapntSheet(workbook);
        addWorkshopSheet(workbook);
        saveWorkbook(workbook,"Johtajatuli_example_data");

        saveToJSON("Johtajatuli_example_data");

    }




    private void generateWorkshops() {

        workshops = new ArrayList<>();

        // workshops
        int[][] bounds = {{5,15},{10,20},{10,30},{15,35}};
        ArrayList<String> ws = readJsonToArrayList("fakeworkshops.json");
        int id = 100;
        for (String wsName : ws) {
            int[] b = bounds[random.nextInt(4)];
            int av = random.nextInt(7)+1;
            boolean[] avb = new boolean[]{ ((av & 1) ==1) , (av & 2) == 2, (av & 4) == 4};
            workshops.add(new Workshop(id,wsName,b[0],b[1],random.nextGaussian(),false,avb,random.nextInt(3)+1));
            id++;
        }

        // speeches

        ArrayList<String> sp = readJsonToArrayList("fakespeeches.json");
        id = 200;
        for (String wsName : sp) {
            boolean[] avb = new boolean[]{false,false,false};
            avb[(int)(((float)(id-200))/(float)sp.size()*3)]=true; // one true for one third of the items
            workshops.add(new Workshop(id,wsName,10,200,random.nextGaussian(),true,avb,random.nextInt(3)+1));
            id++;
        }

    }

    private void generateParticipants() {
        participants = readParticipantsFromCSV("fakenames.csv");
        // Generate "personal preference number" for each workshop and then order list by
        // workshops inherit desirability (unchanged) + personal preference number

        for (Participant participant : participants) {

            for (Workshop ws : workshops)
                ws.setPersonalPreference(random.nextDouble() * personalPreferenceWeight);
            Collections.sort(workshops);


            ArrayList<Integer> preferences = new ArrayList<>();
            for (int i = 0; i < numberOfSelections; i++) {
                preferences.add(workshops.get(i).getId());
                workshops.get(i).addPopularity(numberOfSelections-i);
            }

            participant.setWorkshopPreference(preferences);
            var categoryPrefs = Arrays.asList(1,2,3);
            Collections.shuffle(categoryPrefs);
            participant.setCategoryPreference(categoryPrefs);

            boolean[] avail = new boolean[]{true,true,true};
            for (int i=0;i<2; i++)
                if (random.nextInt(100)>95)
                    avail[random.nextInt(3)]=false;

            participant.setAvailability(avail);
        }

        // Re-order workshops by id
        workshops.sort((o1, o2) -> Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId())));

    }

    private ArrayList<Participant> readParticipantsFromCSV(String filename) {
        var ret = new ArrayList<Participant>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename), Charset.forName("utf-8")))) {
            String line;
            br.readLine(); // discard first line (titles)
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                ret.add(new Participant(values[4],values[0],values[1]));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private ArrayList<String> readJsonToArrayList(String filename) {
        File file = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ArrayList<String> arrayList = objectMapper.readValue(file, ArrayList.class);
            return arrayList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addParticiapntSheet(Workbook book) {
        Sheet sheet = book.createSheet("Participants");
        Row headerRow=sheet.createRow(0);
        for (int i=0; i<3; i++)
            sheet.setColumnWidth(i,6000);

        int hc=0;
        for (String s : new String[]{"id","firstName","lastName","slot_1","slot_2","slot_3","categoryPreferences","","","workshopPreferences"})
            headerRow.createCell(hc++).setCellValue(s);

        int row = 1;
        for (Participant p : participants) {
            Row dataRow = sheet.createRow(row);
            dataRow.createCell(0).setCellValue(p.getId());
            dataRow.createCell(1).setCellValue(p.getFirstName());
            dataRow.createCell(2).setCellValue(p.getLastName());
            dataRow.createCell(3).setCellValue(p.getAvailability()[0] ? 1 : 0);
            dataRow.createCell(4).setCellValue(p.getAvailability()[1] ? 1 : 0);
            dataRow.createCell(5).setCellValue(p.getAvailability()[2] ? 1 : 0);
            dataRow.createCell(6).setCellValue(p.getCategoryPreference().get(0));
            dataRow.createCell(7).setCellValue(p.getCategoryPreference().get(1));
            dataRow.createCell(8).setCellValue(p.getCategoryPreference().get(2));
            int i=9;
            for (Integer wsp : p.getWorkshopPreference())
                dataRow.createCell(i++).setCellValue(wsp);

            row++;
        }
    }

    private void addWorkshopSheet(Workbook book) {
        Sheet sheet = book.createSheet("Workshop");
        Row headerRow=sheet.createRow(0);
        sheet.setColumnWidth(1,9000);
        int hc=0;
        for (String s : new String[]{"id","name","type","min","max","desirability","slot_1","slot_2","slot_3","category","popularity"})
            headerRow.createCell(hc++).setCellValue(s);

        int row = 1;
        for (Workshop ws : workshops) {
            Row dataRow = sheet.createRow(row);
            dataRow.createCell(0).setCellValue(ws.getId());
            dataRow.createCell(1).setCellValue(ws.getName());
            dataRow.createCell(2).setCellValue(ws.isSpeech() ? "speech" : "workshop");
            dataRow.createCell(3).setCellValue(ws.getMinParticipants());
            dataRow.createCell(4).setCellValue(ws.getMaxParticipants());
            dataRow.createCell(5).setCellValue(ws.getDesirability());
            dataRow.createCell(6).setCellValue(ws.getAvailableSlots()[0] ? 1 : 0);
            dataRow.createCell(7).setCellValue(ws.getAvailableSlots()[1] ? 1 : 0);
            dataRow.createCell(8).setCellValue(ws.getAvailableSlots()[2] ? 1 : 0);
            dataRow.createCell(9).setCellValue(ws.getCategory());
            dataRow.createCell(10).setCellValue(ws.getPopularity());

            row++;
        }

    }

    private void saveWorkbook(Workbook book, String filename) throws IOException {
        File f = new File(filename+".xlsx");
        FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
        book.write(fos);
        fos.close();
    }

    private void saveToJSON(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,ArrayList> res = new TreeMap<>();
        res.put("workshops",workshops);
        res.put("participants",participants);
        FileOutputStream fos = new FileOutputStream(new File(filename+".json"));
        mapper.writeValue(fos,res);
        fos.close();
    }

}