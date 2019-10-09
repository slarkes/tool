package org.zhong.main;

import com.youzan.cloud.open.sdk.common.exception.SDKException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Index extends Application {

    private IndexController indexController = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL location = getClass().getClassLoader().getResource("index.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("有赞云API工具");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        indexController=fxmlLoader.getController();

        try {
            indexController.initUI();
            indexController.initApi();
        } catch (SDKException e) {
            indexController.showMessage("初始化失败, 请重启再试");
        }
    }
}
