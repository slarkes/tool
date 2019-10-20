package org.zhong.main;

import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v3_1_0.api.YouzanScrmCustomerSearch;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerSearchParams;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerSearchResult;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.zhong.thread.CustomerThread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IndexController {

    public DatePicker created_at_start;
    public Button export_button;
    public DatePicker created_at_end;
    public TextArea progress_field;
    public ProgressBar progress_bar;
    public ChoiceBox choice_member;
    public Button quick_export;
    public ChoiceBox choice_quick;


    private String clientId = "c24d5dc6c81de9a1b8";

    private String clientSecret = "8d3376938c7cac6ff10ddaf2123b1cd0";

    private String ktdId = "41508817";

    public DefaultYZClient yzClient = null;

    public Token token = null;

    public void exportCustomer(ActionEvent actionEvent) {
        export_button.setDisable(true);
        showMessage("正在导出客户列表...");
        showMessage("获取客户总数...");
        long total=getTotalCustomers();
        showMessage("总客户数为:" + total);
        if (total > 10000) {
            showMessage("客户总数超过10000条, 不能全部导出, 请修改查询条件再试");
            export_button.setDisable(false);
            return ;
        }
        showMessage("开始下载客户数据...");

        Thread thread =new Thread(new CustomerThread(this, total));
        thread.start();

    }

    public void quickExport(ActionEvent actionEvent) {
        System.out.println(System.getProperty("user.dir"));
    }


    public void showMessage(String message) {
        progress_field.appendText(message);
        progress_field.appendText("\r\n");
    }

    public void initUI() {
        choice_member.getItems().addAll("不限", "非会员", "会员");
        choice_member.getSelectionModel().selectFirst();

        choice_quick.getItems().addAll("全部会员", "上月会员", "本月会员");
        choice_quick.getSelectionModel().selectFirst();
    }

    public void initApi() throws SDKException {
        DefaultYZClient yzClient = new DefaultYZClient();
        TokenParameter tokenParameter = TokenParameter.self()
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .grantId(this.ktdId)
                .build();
        OAuthToken oAuthToken  = yzClient.getOAuthToken(tokenParameter);

        this.token=new Token(oAuthToken.getAccessToken());
        this.yzClient=yzClient;
        System.out.println(token.getAccessToken());
        showMessage("API初始化成功!");
    }

    private long getTotalCustomers(){
        YouzanScrmCustomerSearch youzanScrmCustomerSearch = new YouzanScrmCustomerSearch();
        //创建参数对象,并设置参数

        try {
            YouzanScrmCustomerSearchParams youzanScrmCustomerSearchParams = getYouzanScrmCustomerSearchParams();
            youzanScrmCustomerSearch.setAPIParams(youzanScrmCustomerSearchParams);
            YouzanScrmCustomerSearchResult result = yzClient.invoke(youzanScrmCustomerSearch, token, YouzanScrmCustomerSearchResult.class);
            return result.getData().getTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public YouzanScrmCustomerSearchParams getYouzanScrmCustomerSearchParams() throws ParseException {
        YouzanScrmCustomerSearchParams youzanScrmCustomerSearchParams = new YouzanScrmCustomerSearchParams();
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
        if (null != created_at_start.getValue()) {
            Date date = format.parse(created_at_start.getValue().toString());
            //日期转时间戳（毫秒）
            long time=date.getTime();
//            System.out.println("开始时间:" + time);
            youzanScrmCustomerSearchParams.setCreatedAtStart(time/1000);
        }

        if (null != created_at_end.getValue()) {
            Date date = format.parse(created_at_end.getValue().toString());
            //日期转时间戳（毫秒）
            long time=date.getTime();
//            System.out.println("结束时间:" + time);
            youzanScrmCustomerSearchParams.setCreatedAtEnd(time/1000);
        }

        if (0 < choice_member.getSelectionModel().getSelectedIndex()) {
            youzanScrmCustomerSearchParams.setIsMember((short) (choice_member.getSelectionModel().getSelectedIndex()-1));
        }
        return youzanScrmCustomerSearchParams;
    }


}
