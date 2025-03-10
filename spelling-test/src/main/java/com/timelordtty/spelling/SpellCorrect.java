package com.timelordtty.spelling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class SpellCorrect extends Application {

    private static final String API_KEY = "CsdvxQbBbwYREpS2iy7cukmr";
    private static final String SECRET_KEY = "VzfeFhNb4DzVxXWKW2J2aMJM7uHgeUpg";
    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String CORRECTION_API_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v2/text_correction?access_token=";

    private TextArea inputArea;
    private TextFlow resultFlow;
    private TextArea correctedArea;  // 新增：显示完整校正后文本
    private Label statusLabel;
    private Button checkButton;
    private OkHttpClient httpClient = new OkHttpClient();
    private String cachedAccessToken;

    @Override
    public void start(Stage primaryStage) {
        initializeToken();
        buildUI(primaryStage);
    }

    /**
     * 初始化Token：在应用启动时获取一次Token
     */
    private void initializeToken() {
        new Thread(() -> {
            try {
                cachedAccessToken = getBaiduToken();
                Platform.runLater(() -> {
                    statusLabel.setText("Token初始化成功");
                    checkButton.setDisable(false);
                });
            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("Token初始化失败: " + e.getMessage()));
            }
        }).start();
    }

    private void buildUI(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        inputArea = new TextArea();
        inputArea.setPrefSize(800, 400);
        inputArea.setPromptText("请在此粘贴或输入文章...");

        // 用于展示错误提示的区域
        resultFlow = new TextFlow();
        resultFlow.setPrefSize(800, 200);
        resultFlow.setPadding(new Insets(10));
        ScrollPane resultScroll = new ScrollPane(resultFlow);
        resultScroll.setFitToWidth(true);

        // 新增：用于展示校正后的完整文本的文本框
        correctedArea = new TextArea();
        correctedArea.setPrefSize(800, 150);
        correctedArea.setEditable(false);
        correctedArea.setWrapText(true);

        statusLabel = new Label("就绪");
        checkButton = new Button("校验文本");
        checkButton.setDisable(true);
        checkButton.setOnAction(e -> startCheckProcess());

        root.getChildren().addAll(
                new Label("输入文本："),
                inputArea,
                checkButton,
                new Label("错误提示："),
                resultScroll,
                new Label("校正后文本："),
                correctedArea,
                statusLabel
        );

        Scene scene = new Scene(root, 850, 750);
        stage.setTitle("中文错别字检测工具");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 用户点击“校验文本”按钮后调用此方法，异步进行校验。
     */
    private void startCheckProcess() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            statusLabel.setText("请输入待校验文本");
            return;
        }
        checkButton.setDisable(true);
        statusLabel.setText("校验中...");
        new Thread(() -> {
            try {
                processText(text);
            } catch (IOException e) {
                Platform.runLater(() -> statusLabel.setText("校验失败: " + e.getMessage()));
            } finally {
                Platform.runLater(() -> checkButton.setDisable(false));
            }
        }).start();
    }

    /**
     * 调用百度API进行文本校正。
     */
    private void processText(String text) throws IOException {
        String apiUrl = CORRECTION_API_URL + cachedAccessToken;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(
                mediaType,
                String.format("{\"text\":\"%s\"}", text)
        );
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();
        log.info("请求: " + request);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> statusLabel.setText("网络错误: " + e.getMessage()));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                log.info("响应: " + json);
                Platform.runLater(() -> updateUI(json));
            }
        });
    }

    /**
     * 根据百度API返回的JSON更新UI。<br>
     * 展示错误提示（错误字/词、建议及行列信息），并直接更新校正后文本框内容。
     */
    private void updateUI(String json) {
        resultFlow.getChildren().clear();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode itemNode = root.path("item");
            String originalText = itemNode.path("text").asText();
            String correctedQuery = itemNode.path("correct_query").asText();
            int errorNum = itemNode.path("error_num").asInt(0);

            // 更新校正后完整文本
            correctedArea.setText(correctedQuery);

            // 如果没有错误，则只提示未检测到错误
            if (errorNum == 0) {
                resultFlow.getChildren().add(new Text("未检测到错误。"));
            } else {
                // 从details中取第一条记录
                JsonNode details = itemNode.path("details");
                if (details.isArray() && details.size() > 0) {
                    JsonNode detail = details.get(0);
                    JsonNode fragments = detail.path("vec_fragment");
                    if (fragments.isArray()) {
                        for (JsonNode fragment : fragments) {
                            int begin = fragment.path("begin_pos").asInt();
                            String location = getLineCol(begin, originalText);
                            String oriFrag = fragment.path("ori_frag").asText();
                            String correctFrag = fragment.path("correct_frag").asText();
                            String info = String.format("错误：%s，建议：%s，位置：%s", oriFrag, correctFrag, location);
                            Text infoText = new Text(info + "\n");
                            infoText.setFill(Color.DARKBLUE);
                            resultFlow.getChildren().add(infoText);
                        }
                    }
                }
            }
            statusLabel.setText("校验完成");
        } catch (Exception e) {
            statusLabel.setText("解析错误: " + e.getMessage());
        }
    }

    /**
     * 根据原文和偏移量计算所在的行号和列号（行、列从1开始）。
     */
    private String getLineCol(int offset, String text) {
        String[] lines = text.split("\n", -1);
        int cum = 0;
        for (int i = 0; i < lines.length; i++) {
            int lineLength = lines[i].length();
            if (offset <= cum + lineLength) {
                int col = offset - cum + 1;
                return "第" + (i + 1) + "行，第" + col + "个字";
            }
            cum += lineLength + 1; // 加上换行符
        }
        return "未知位置";
    }

    /**
     * 获取百度API的Token。如果已有缓存则直接返回。
     */
    private String getBaiduToken() throws IOException {
        if (cachedAccessToken != null && !cachedAccessToken.isEmpty()) {
            return cachedAccessToken;
        }
        HttpUrl url = HttpUrl.parse(TOKEN_URL).newBuilder()
                .addQueryParameter("client_id", API_KEY)
                .addQueryParameter("client_secret", SECRET_KEY)
                .addQueryParameter("grant_type", "client_credentials")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Token请求失败: " + response.code());
            }
            byte[] bytes = response.body().bytes();
            JsonNode root = new ObjectMapper().readTree(bytes);
            cachedAccessToken = root.path("access_token").asText();
            if (cachedAccessToken.isEmpty()) {
                throw new IOException("无效的Token响应");
            }
            log.info("获取新Token: {}", cachedAccessToken);
            return cachedAccessToken;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
