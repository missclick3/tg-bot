package com.example.finalbot;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController  {

    FileChooser fileChooser = new FileChooser();
    @FXML
    private TextArea bodyContent;

    @FXML
    private Button confButton;

    @FXML
    private Button fileButton;

    @FXML
    private TextArea linkContent;

    @FXML
    private Button photoButton;

    @FXML
    private CheckBox poleConfButton;

    @FXML
    private TextArea poleContent;

    @FXML
    private TextArea titleContent;

    File file;

    //Парсит текст по энтерам
    String[] parseText(String text) {
        return text.split("\n");
    }
    //Выбирает файл для фото
    @FXML
    public void chooseFileMethod() {
         file = fileChooser.showOpenDialog(new Stage());
    }
    //Выполняет отправку сообщений
    public void handleMessage(MessageInfo message, boolean hasPhoto) throws TelegramApiException {
        String text = message.title +  "\n\n" + message.body;
        String[] parsedMessage = parseText(text);
        TestBot bot = new TestBot(new DefaultBotOptions());
        String newMessage = "";
        int count = 0;
        int counterOfMessages = 1;
        //Действия если нет фото
        if (!hasPhoto) {
            //Если есть Данные в опросе
            if (!(message.poll.equals(""))){
                String[] parsedPoll = parseText(message.poll);
                bot.execute(SendPoll.builder()
                        .chatId("-1001648062483")
                        .question(message.title)
                        .options(Arrays.asList(parsedPoll)).build());
            }
            //Если данных в опросе нет
            else {
                while (newMessage.length() + parsedMessage[count].length() <= 4096 && count < parsedMessage.length - 1) {
                    newMessage = newMessage + "\n" + parsedMessage[count];
                    count++;
                    if (newMessage.length() + parsedMessage[count].length() > 4096) {

                        try {
                            bot.execute(SendMessage.builder()
                                    .chatId("-1001648062483")
                                    .text("[Part " + counterOfMessages + "]\n" + newMessage)
                                    .build());
                            counterOfMessages++;
                        }
                        catch (TelegramApiException e) {
                            System.err.println(e.getMessage());
                        }
                        newMessage = "";
                    }
                }
                try {
                    if (counterOfMessages == 1) {
                        bot.execute(SendMessage.builder()
                                .chatId("-1001648062483")
                                .text(newMessage + "\n" + parsedMessage[parsedMessage.length - 1] + "\n\n@Agile_new")
                                .build());
                    }
                    else {
                        bot.execute(SendMessage.builder()
                                .chatId("-1001648062483")
                                .text("[Part " + counterOfMessages + "]\n" + newMessage + "\n" + parsedMessage[parsedMessage.length - 1] + "\n\n@Agile_new")
                                .build());
                    }

                }
                catch (TelegramApiException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        else {
            while (newMessage.length() + parsedMessage[count].length() <= 1024 && count < parsedMessage.length - 1) {
                newMessage = newMessage + "\n" + parsedMessage[count];
                count++;
                if (newMessage.length() + parsedMessage[count].length() > 1024) {
                    break;
                }
            }
            if (count == parsedMessage.length - 1) {
                try {
                    bot.execute(SendPhoto.builder()
                            .chatId("-1001648062483")
                            .caption(newMessage + "\n" + parsedMessage[parsedMessage.length - 1] + "\n\n@Agile_new")
                            .photo(new InputFile(message.photo))
                            .build());
                }
                catch (TelegramApiException e) {
                    System.err.println(e.getMessage());
                }
            }
            else {
                try {
                    bot.execute(SendPhoto.builder()
                            .chatId("-1001648062483")
                            .caption("[Part " + counterOfMessages + "]\n" + newMessage + "\n" + parsedMessage[count - 1] + "\n")
                            .photo(new InputFile(message.photo))
                            .build());
                    counterOfMessages++;
                }
                catch (TelegramApiException e) {
                    System.err.println(e.getMessage());
                }
                newMessage = "";
                while (newMessage.length() + parsedMessage[count].length() <= 4096 && count < parsedMessage.length - 1) {
                    newMessage = newMessage + "\n" + parsedMessage[count];
                    count++;
                    if (newMessage.length() + parsedMessage[count].length() > 4096) {
                        try {
                            bot.execute(SendMessage.builder()
                                    .chatId("-1001648062483")
                                    .text("[Part " + counterOfMessages + "]\n" + newMessage)
                                    .build());
                            counterOfMessages++;
                        }
                        catch (TelegramApiException e) {
                            System.err.println(e.getMessage());
                        }
                        newMessage = "";
                    }
                }
                try {
                    bot.execute(SendMessage.builder()
                            .chatId("-1001648062483")
                            .text("[Part " + counterOfMessages + "]\n" + newMessage + "\n" + parsedMessage[parsedMessage.length - 1] + "\n\n@Agile_new")
                            .build());
                }
                catch (TelegramApiException e) {
                    System.err.println(e.getMessage());
                }

            }


        }
    }
    @FXML
    void onClickConfButton(ActionEvent event) throws TelegramApiException{
        MessageInfo message = new MessageInfo();
        message.setTitle(titleContent.getText());
        message.setBody(bodyContent.getText());
        message.setLink(linkContent.getText());
        message.setPoll(poleContent.getText());
        message.setPhoto(file);
        if (message.photo == null) {
            handleMessage(message, false);
        }
        else {
            handleMessage(message, true);
        }
        titleContent.clear();
        bodyContent.clear();
        poleContent.clear();
        file = null;

    }

    @FXML
    void onClickPoleButton(ActionEvent event) {

    }

}
class TestBot extends DefaultAbsSender {

    public TestBot (DefaultBotOptions options) {
        super(options);
    }
    public String getBotToken () {
        return "5707667703:AAFV9SKlm8GQDUhhmEvgcPpeS5B5smpV3_c";
    }

}
class MessageInfo {
    String title = "";
    String body = "";
    String link = "";
    File file = null;
    File photo = null;
    String poll = "";

    public void setTitle (String str) {
        this.title = str;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPoll(String poll) {
        this.poll = poll;
    }

    public void setPhoto(File file) {
        this.photo = file;
    }
}
