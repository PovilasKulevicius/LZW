package UI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import main.models.Decode;
import main.models.Encode;
import main.models.LzwUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MainUI implements Initializable {

    // Inputs
    @FXML
    private TextField dictionarySize;
    @FXML
    private ComboBox dictionaryTypeSelector;
    @FXML
    private TabPane rootPane;
    @FXML
    private TextField compressorFilePathText;
    @FXML
    private TextField decompressorFilePathText;

    // Outputs for compression
    @FXML
    private Text compressorFile;
    @FXML
    private Text compressorEncoded;
    @FXML
    private Text compressorComparison;

    // Outputs for decompression
    @FXML
    private Text decompressorFile;
    @FXML
    private Text decompressorDecoded;
    @FXML
    private Text decompressorComparison;

    private Timeline timer; //Runs in the background
    private URL compressionFilePathUrl;
    private URL decompressionFilePathUrl;
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hideFields();
        dictionarySize.setText("");
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Clear",
                        "Continue"

                );
        dictionaryTypeSelector.setItems(options);

        timer = new Timeline(new KeyFrame(Duration.seconds(0.02), event -> {
            if(dictionarySize.getText().equals("0")) {
                dictionaryTypeSelector.setDisable(true);
            } else dictionaryTypeSelector.setDisable(false);
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }


    public void selectFileForCompression(ActionEvent actionEvent) {
        try {
            File selectedFile = choosePath();
            compressorFile.setText(String.valueOf(selectedFile.length()));
            compressorFile.setVisible(true);
            compressionFilePathUrl = Paths.get(selectedFile.toURI()).toUri().toURL();
            compressorFilePathText.setText(compressionFilePathUrl.getPath().substring(1).replace("%20", " "));
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (Exception e)
        {

        }
    }

    public void compressFile(ActionEvent actionEvent) throws URISyntaxException {
        if(compressorFilePathText.getText().isEmpty() ||
                dictionarySize.getText().isEmpty() ||
                (dictionaryTypeSelector.getValue() == null && !dictionarySize.getText().equals("0")))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please select a file and parameters before compressing");
            alert.showAndWait();
            return;
        }
        LzwUtils.DictionaryMode mode = getMode();
        Encode encoder = new Encode(compressionFilePathUrl, Integer.parseInt(dictionarySize.getText()), mode);
        encoder.encode();

        long encodedFileSize = encoder.getOutputFilesize();
        compressorEncoded.setText(String.valueOf(encodedFileSize));
        compressorEncoded.setVisible(true);

        long fileSize = Long.parseLong(compressorFile.getText());
        compressorComparison.setText(String.valueOf(df.format((float)fileSize/encodedFileSize)));
        compressorComparison.setVisible(true);

    }

    private LzwUtils.DictionaryMode getMode() {
        if(dictionarySize.getText().equals("0")) return LzwUtils.DictionaryMode.Infinite;

        String val = (String) dictionaryTypeSelector.getValue();
        if(val.equals("Clear")) return LzwUtils.DictionaryMode.Clear;
        else return LzwUtils.DictionaryMode.Continue;
    }

    public void selectFileForDecompression(ActionEvent actionEvent) {
        try {
            File selectedFile = choosePath();
            decompressorFile.setVisible(true);
            decompressorFile.setText(String.valueOf(selectedFile.length()));
            decompressionFilePathUrl = Paths.get(selectedFile.toURI()).toUri().toURL();
            decompressorFilePathText.setText(decompressionFilePathUrl.getPath().substring(1).replace("%20", " "));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (Exception e)
        {

        }
    }

    public void decompressFile(ActionEvent actionEvent) throws URISyntaxException, MalformedURLException {
        if(decompressorFilePathText.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please select a file to decompress");
            alert.showAndWait();
            return;
        }

        Decode decoder = new Decode();
        URL decompressedFileOutputUrl =new URL(decompressionFilePathUrl.toString().replace(".Encoded", ".decoded"));
        decoder.decode(decompressionFilePathUrl, decompressedFileOutputUrl);

        long decodedFileSize = decoder.getOutputFilesize();
        decompressorDecoded.setText(String.valueOf(decodedFileSize));
        decompressorDecoded.setVisible(true);

        long fileSize = Long.parseLong(decompressorFile.getText());
        decompressorComparison.setText(String.valueOf(df.format((float)fileSize/decodedFileSize)));
        decompressorComparison.setVisible(true);

    }

    private File choosePath()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose file");
        File defaultDir = new File(System.getProperty("user.dir"));
        fc.setInitialDirectory(defaultDir);
        File selectedFile = fc.showOpenDialog(rootPane.getScene().getWindow());
        System.out.println(selectedFile.getPath());
        return selectedFile;
    }


    private void hideFields() {
        compressorFile.setVisible(false);
        compressorEncoded.setVisible(false);
        compressorComparison.setVisible(false);

        decompressorFile.setVisible(false);
        decompressorDecoded.setVisible(false);
        decompressorComparison.setVisible(false);
    }
}
