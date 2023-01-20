package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import guru.qa.model.FileJson;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class DocTest {
    ClassLoader cl = DocTest.class.getClassLoader();

    @Test
    void docTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("example/ZIP.zip");
                ZipInputStream zis = new ZipInputStream(resource)
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("example/ZIP.zip/test-file-xlsx1.pdf")) {
                    PDF contentPDF = new PDF(zis);
                    assertThat(contentPDF.text).contains("1 Den");
                } else if (entry.getName().contains("example/ZIP.zip/test-file-xlsx3.xlsx")) {
                    XLS contentXLS = new XLS(zis);
                    assertThat(contentXLS.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue()).contains("Den");
                } else if (entry.getName().contains("example/ZIP.zip/test-file-xlsx2.csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> contentCSV = reader.readAll();
                    assertThat(contentCSV.get(0)[1]).contains("Den");
                }
            }
        }
    }



    @Test
    void jsonTest() throws Exception {
        Gson gson = new Gson();
        try (
                InputStream resource = cl.getResourceAsStream("example/file.json");
                InputStreamReader reader = new InputStreamReader(resource)
        ) {
            FileJson fileJson = gson.fromJson(reader, FileJson.class);
            assertThat(fileJson.name).isEqualTo("Pass Passed");
            assertThat(fileJson.id).isEqualTo("007");
            assertThat(fileJson.age).isEqualTo(39);
            assertThat(fileJson.married).isTrue();
            assertThat(fileJson.address.street).isEqualTo("33, las St");
            assertThat(fileJson.address.city).isEqualTo("London");
            assertThat(fileJson.address.country).isEqualTo("UK");
        }

    }
}
