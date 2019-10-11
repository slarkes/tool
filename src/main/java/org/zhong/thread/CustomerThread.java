package org.zhong.thread;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanUsersWeixinFollowerGet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanUsersWeixinFollowerGetParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanUsersWeixinFollowerGetResult;
import com.youzan.cloud.open.sdk.gen.v3_1_0.api.YouzanScrmCustomerGet;
import com.youzan.cloud.open.sdk.gen.v3_1_0.api.YouzanScrmCustomerSearch;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerGetParams;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerGetResult;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerSearchParams;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerSearchResult;
import org.zhong.data.Customer;
import org.zhong.main.IndexController;

import java.io.File;
import java.util.ArrayList;

public class CustomerThread implements Runnable {

    private IndexController indexController;

    private long total;

    public CustomerThread(IndexController indexController, long total) {
        this.indexController = indexController;
        this.total = total;
    }

    @Override
    public void run() {
        try {
            downloadCustomer(indexController.getYouzanScrmCustomerSearchParams(), total);
        } catch (Exception e) {
            e.printStackTrace();
            indexController.showMessage("下载失败:" + e.getMessage());
        } finally {
            indexController.export_button.setDisable(false);
        }
    }

    private String downloadCustomer(YouzanScrmCustomerSearchParams youzanScrmCustomerSearchParams, long total) throws Exception {
        String fileName = System.getProperty("user.dir") + File.separator +  "客户列表" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去读
        ExcelWriter excelWriter = EasyExcel.write(fileName, Customer.class).build();

        // 这里注意 如果同一个sheet只要创建一次
        WriteSheet writeSheet = EasyExcel.writerSheet("客户").build();

        youzanScrmCustomerSearchParams.setPageSize(20);
        int page = Math.round((float)total/20);

        indexController.showMessage("总页数为:" + page);





        int no = 1;
        ArrayList<Customer> list = null;

        for (int i = 1; i <= page; i++) {
            indexController.showMessage("正在下载第" + i + "页数据");
            youzanScrmCustomerSearchParams.setPage(i);
            YouzanScrmCustomerSearch youzanScrmCustomerSearch = new YouzanScrmCustomerSearch();//客户列表
            youzanScrmCustomerSearch.setAPIParams(youzanScrmCustomerSearchParams);
            YouzanScrmCustomerSearchResult result = indexController.yzClient.invoke(youzanScrmCustomerSearch, indexController.token, YouzanScrmCustomerSearchResult.class);
            if (!result.getSuccess()) {
                throw new Exception("客户列表请求失败:" + result.getMessage());
            }
            System.out.println("第"+i+"页总数为:"+result.getData().getTotal());
            list = new ArrayList<Customer>();
            for (YouzanScrmCustomerSearchResult.YouzanScrmCustomerSearchResultRecordlist record:result.getData().getRecordList()) {
                System.out.println(record.getFansId());
                Customer customer = new Customer(no);
                customer.setWechat_fan_id(record.getFansId());
                customer.setCreate_time(record.getCreatedAt());
                customer.setSex(record.getGender());
                customer.setIs_member(record.getIsMember());

                //获取客户详情
                YouzanScrmCustomerGetParams youzanScrmCustomerGetParams = new YouzanScrmCustomerGetParams();
                YouzanScrmCustomerGetParams.YouzanScrmCustomerGetParamsAccount account = new YouzanScrmCustomerGetParams.YouzanScrmCustomerGetParamsAccount();
                account.setAccountId(String.valueOf(record.getFansId()));
                account.setAccountType("FansID");
                youzanScrmCustomerGetParams.setAccount(account);
                YouzanScrmCustomerGet youzanScrmCustomerGet = new YouzanScrmCustomerGet();//客户详情
                youzanScrmCustomerGet.setAPIParams(youzanScrmCustomerGetParams);
                YouzanScrmCustomerGetResult customerDetail = indexController.yzClient.invoke(youzanScrmCustomerGet, indexController.token, YouzanScrmCustomerGetResult.class);
                if (!customerDetail.getSuccess()) {
                    throw new Exception("客户详情请求失败:" + result.getMessage());
                }

                customer.setCustomer_name(customerDetail.getData().getName());
                customer.setMobile(customerDetail.getData().getMobile());
                customer.setProvince(customerDetail.getData().getContactAddress().getProvince());
                customer.setCity(customerDetail.getData().getContactAddress().getCity());
                customer.setDistrict(customerDetail.getData().getContactAddress().getCounty());
                customer.setAddress(customerDetail.getData().getContactAddress().getAddress());
                customer.setBirthday(customerDetail.getData().getBirthday());

                //获取用户详情
                YouzanUsersWeixinFollowerGetParams youzanUsersWeixinFollowerGetParams = new YouzanUsersWeixinFollowerGetParams();
                youzanUsersWeixinFollowerGetParams.setFansId(record.getFansId());
                YouzanUsersWeixinFollowerGet youzanUsersWeixinFollowerGet = new YouzanUsersWeixinFollowerGet();//用户详情
                youzanUsersWeixinFollowerGet.setAPIParams(youzanUsersWeixinFollowerGetParams);

                YouzanUsersWeixinFollowerGetResult userDetail = indexController.yzClient.invoke(youzanUsersWeixinFollowerGet, indexController.token, YouzanUsersWeixinFollowerGetResult.class);

                if (!customerDetail.getSuccess()) {
                    throw new Exception("用户详情请求失败:" + result.getMessage());
                }
                customer.setCustomer_point(userDetail.getResponse().getUser().getPoints());
                customer.setTotal_success_order_price(Double.valueOf(userDetail.getResponse().getUser().getTradedMoney()));
                customer.setTotal_success_order_num(userDetail.getResponse().getUser().getTradedNum());
                customer.setPct(customer.getTotal_success_order_price()/customer.getTotal_success_order_num());

                list.add(customer);
                no++;
                indexController.progress_bar.setProgress((double) no/total);

            }
            excelWriter.write(list, writeSheet);
        }

        excelWriter.finish();

        return fileName;
    }
}
