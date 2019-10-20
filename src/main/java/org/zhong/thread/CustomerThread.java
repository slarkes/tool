package org.zhong.thread;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.*;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.*;
import com.youzan.cloud.open.sdk.gen.v3_1_0.api.YouzanScrmCustomerGet;
import com.youzan.cloud.open.sdk.gen.v3_1_0.api.YouzanScrmCustomerSearch;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerGetParams;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerGetResult;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerSearchParams;
import com.youzan.cloud.open.sdk.gen.v3_1_0.model.YouzanScrmCustomerSearchResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanScrmTagRelationGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanScrmTagRelationGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanScrmTagRelationGetResult;
import com.youzan.cloud.open.sdk.gen.v4_0_1.api.YouzanTradesSoldGet;
import com.youzan.cloud.open.sdk.gen.v4_0_1.model.YouzanTradesSoldGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_1.model.YouzanTradesSoldGetResult;
import org.zhong.data.Customer;
import org.zhong.main.IndexController;

import java.io.File;
import java.util.ArrayList;

public class CustomerThread implements Runnable {

    private IndexController indexController;

    private long total;

    private ExcelWriter excelWriter;

    private WriteSheet writeSheet;

    public CustomerThread(IndexController indexController, long total) {
        this.indexController = indexController;
        this.total = total;
    }

    @Override
    public void run() {
        try {
            String fileName = System.getProperty("user.dir") + File.separator +  "客户列表" + System.currentTimeMillis() + ".xlsx";
            // 这里 需要指定写用哪个class去读
            excelWriter = EasyExcel.write(fileName, Customer.class).build();

            // 这里注意 如果同一个sheet只要创建一次
            writeSheet = EasyExcel.writerSheet("客户").build();

            downloadCustomer(indexController.getYouzanScrmCustomerSearchParams(), total);

            indexController.showMessage("下载成功,文件地址为:"+fileName);
        } catch (Exception e) {
            e.printStackTrace();
            indexController.showMessage("下载失败:" + e.getMessage());
        } finally {
            indexController.export_button.setDisable(false);
            indexController.progress_bar.setProgress(0);
            excelWriter.finish();
        }
    }

    private void downloadCustomer(YouzanScrmCustomerSearchParams youzanScrmCustomerSearchParams, long total) throws Exception {

        youzanScrmCustomerSearchParams.setPageSize(40);
        int page = (int) Math.ceil((float) total/40);

        indexController.showMessage("总页数为:" + page);


        //有赞api
        //获取客户详情
        YouzanScrmCustomerGet youzanScrmCustomerGet = new YouzanScrmCustomerGet();//客户详情
        YouzanScrmCustomerGetParams youzanScrmCustomerGetParams = new YouzanScrmCustomerGetParams();
        YouzanScrmCustomerGetParams.YouzanScrmCustomerGetParamsAccount account = new YouzanScrmCustomerGetParams.YouzanScrmCustomerGetParamsAccount();


        //获取用户详情
        YouzanUsersWeixinFollowerGetParams youzanUsersWeixinFollowerGetParams = new YouzanUsersWeixinFollowerGetParams();
        YouzanUsersWeixinFollowerGet youzanUsersWeixinFollowerGet = new YouzanUsersWeixinFollowerGet();//用户详情

        //客户标签
        YouzanScrmTagRelationGet youzanScrmTagRelationGet = new YouzanScrmTagRelationGet();
        YouzanScrmTagRelationGetParams youzanScrmTagRelationGetParams = new YouzanScrmTagRelationGetParams();

        //客户订单
        YouzanTradesSoldGet youzanTradesSoldGet = new YouzanTradesSoldGet();
        YouzanTradesSoldGetParams youzanTradesSoldGetParams = new YouzanTradesSoldGetParams();

        //客户退款
        YouzanTradeRefundSearch youzanTradeRefundSearch = new YouzanTradeRefundSearch();
        YouzanTradeRefundSearchParams youzanTradeRefundSearchParams = new YouzanTradeRefundSearchParams();

        //客户储值卡
        YouzanCardvoucherValuecardInfoQuery youzanCardvoucherValuecardInfoQuery = new YouzanCardvoucherValuecardInfoQuery();
        YouzanCardvoucherValuecardInfoQueryParams youzanCardvoucherValuecardInfoQueryParams = new YouzanCardvoucherValuecardInfoQueryParams();

        //客户权益卡
        YouzanScrmCustomerCardList youzanScrmCustomerCardList = new YouzanScrmCustomerCardList();
        YouzanScrmCustomerCardListParams youzanScrmCustomerCardListParams = new YouzanScrmCustomerCardListParams();

        //会员卡详情
        YouzanScrmCardGet youzanScrmCardGet = new YouzanScrmCardGet();
        YouzanScrmCardGetParams youzanScrmCardGetParams = new YouzanScrmCardGetParams();

        int no = 1;
        ArrayList<Customer> list = null;

        for (int i = 1; i <= page; i++) {
            indexController.showMessage("正在下载第" + i +'/'+page+ "页数据");
            youzanScrmCustomerSearchParams.setPage(i);
            YouzanScrmCustomerSearch youzanScrmCustomerSearch = new YouzanScrmCustomerSearch();//客户列表
            youzanScrmCustomerSearch.setAPIParams(youzanScrmCustomerSearchParams);
            YouzanScrmCustomerSearchResult result = indexController.yzClient.invoke(youzanScrmCustomerSearch, indexController.token, YouzanScrmCustomerSearchResult.class);
            if (!result.getSuccess()) {
                throw new Exception("客户列表请求失败:" + result.getMessage());
            }
//            System.out.println("第"+i+"页总数为:"+result.getData().getTotal());
            list = new ArrayList<Customer>();

            for (YouzanScrmCustomerSearchResult.YouzanScrmCustomerSearchResultRecordlist record:result.getData().getRecordList()) {
//                System.out.println(record.getFansId());
                Customer customer = new Customer(no);
                customer.setWechat_fan_id(record.getFansId());
                customer.setCreate_time(record.getCreatedAt());
                customer.setSex(record.getGender());
                customer.setIs_member(record.getIsMember());

                if (null != record.getFansId()) {
                    //获取客户详情
                    account.setAccountId(String.valueOf(record.getFansId()));
                    account.setAccountType("FansID");
                    youzanScrmCustomerGetParams.setAccount(account);
                    youzanScrmCustomerGet.setAPIParams(youzanScrmCustomerGetParams);
                    try {
                        YouzanScrmCustomerGetResult customerDetail = indexController.yzClient.invoke(youzanScrmCustomerGet, indexController.token, YouzanScrmCustomerGetResult.class);
                        if (customerDetail.getSuccess()) {
                            customer.setCustomer_name(customerDetail.getData().getName());
                            customer.setMobile(customerDetail.getData().getMobile());
                            customer.setProvince(customerDetail.getData().getContactAddress().getProvince());
                            customer.setCity(customerDetail.getData().getContactAddress().getCity());
                            customer.setDistrict(customerDetail.getData().getContactAddress().getCounty());
                            customer.setAddress(customerDetail.getData().getContactAddress().getAddress());
                            customer.setBirthday(customerDetail.getData().getBirthday());
                        }
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }

                    //获取用户详情
                    youzanUsersWeixinFollowerGetParams.setFansId(record.getFansId());
                    youzanUsersWeixinFollowerGet.setAPIParams(youzanUsersWeixinFollowerGetParams);

                    try {
                        YouzanUsersWeixinFollowerGetResult userDetail = indexController.yzClient.invoke(youzanUsersWeixinFollowerGet, indexController.token, YouzanUsersWeixinFollowerGetResult.class);
                        if (null != userDetail.getResponse()) {
                            customer.setCustomer_point(userDetail.getResponse().getUser().getPoints());
                            customer.setTotal_success_order_price(Double.valueOf(userDetail.getResponse().getUser().getTradedMoney()));
                            customer.setTotal_success_order_num(userDetail.getResponse().getUser().getTradedNum());
                            customer.setPct(0==customer.getTotal_success_order_num()?0:customer.getTotal_success_order_price()/customer.getTotal_success_order_num());
                            if ("".equals(customer.getProvince())) {
                                customer.setProvince(userDetail.getResponse().getUser().getProvince());
                            }
                            if ("".equals(customer.getCity())) {
                                customer.setCity(userDetail.getResponse().getUser().getCity());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //客户标签
                    youzanScrmTagRelationGet.setAPIParams(youzanScrmTagRelationGetParams);

                    try {
                        YouzanScrmTagRelationGetResult customerTag = indexController.yzClient.invoke(youzanScrmTagRelationGet, indexController.token, YouzanScrmTagRelationGetResult.class);
//                    System.out.println("客户标签"+customerTag.getMessage()+customerTag.getCode());
                        String tagName ="无";
                        if (customerTag.getSuccess()) {
                            for (YouzanScrmTagRelationGetResult.YouzanScrmTagRelationGetResultData tag:customerTag.getData()) {
                                if ("无".equals(tagName)) {
                                    tagName = tag.getTagName();
                                }
                                else {
                                    tagName = tagName+"/"+tag.getTagName();
                                }
                            }
                        }
                        customer.setCustomer_tag(tagName);
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }

                    //客户订单
                    youzanTradesSoldGetParams.setFansId(record.getFansId());
                    youzanTradesSoldGet.setAPIParams(youzanTradesSoldGetParams);

                    try {
                        YouzanTradesSoldGetResult customerOrder = indexController.yzClient.invoke(youzanTradesSoldGet, indexController.token, YouzanTradesSoldGetResult.class);
                        if (customerOrder.getSuccess()) {
                            for (int order_index = 0; order_index < customerOrder.getData().getFullOrderInfoList().size(); order_index++) {
                                YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfolist order = customerOrder.getData().getFullOrderInfoList().get(order_index);
                                if (order_index == 0) {
                                    customer.setLast_order_time(order.getFullOrderInfo().getOrderInfo().getCreated());
                                    //如果有订单则拿第一个订单地址作为地址
                                    customer.setProvince(order.getFullOrderInfo().getAddressInfo().getDeliveryProvince());
                                    customer.setCity(order.getFullOrderInfo().getAddressInfo().getDeliveryCity());
                                    customer.setDistrict(order.getFullOrderInfo().getAddressInfo().getDeliveryDistrict());
                                    customer.setAddress(order.getFullOrderInfo().getAddressInfo().getDeliveryAddress());
                                }

                                if ("WAIT_SELLER_SEND_GOODS".equals(order.getFullOrderInfo().getOrderInfo().getStatus()) ||
                                        "WAIT_BUYER_CONFIRM_GOODS".equals(order.getFullOrderInfo().getOrderInfo().getStatus()) ||
                                        "TRADE_SUCCESS".equals(order.getFullOrderInfo().getOrderInfo().getStatus())) {
                                    customer.setLast_pay_time(order.getFullOrderInfo().getOrderInfo().getPayTime());
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("请客订单请求报错:" +record.getFansId());
                        e.printStackTrace();
                    }


                    //客户权益卡
                    youzanScrmCustomerCardListParams.setFansId(record.getFansId());
                    youzanScrmCustomerCardListParams.setPage(1);
                    youzanScrmCustomerCardList.setAPIParams(youzanScrmCustomerCardListParams);
                    try {
                        YouzanScrmCustomerCardListResult cardListResult = indexController.yzClient.invoke(youzanScrmCustomerCardList, indexController.token, YouzanScrmCustomerCardListResult.class);

                        String cardName ="无";
                        if (cardListResult.getSuccess()) {
                            for (YouzanScrmCustomerCardListResult.YouzanScrmCustomerCardListResultItems cardItem:cardListResult.getData().getItems()) {
                                //会员卡详情
                                youzanScrmCardGetParams.setCardAlias(cardItem.getCardAlias());
                                youzanScrmCardGet.setAPIParams(youzanScrmCardGetParams);
                                YouzanScrmCardGetResult cardDetail = indexController.yzClient.invoke(youzanScrmCardGet, indexController.token, YouzanScrmCardGetResult.class);
                                if (cardDetail.getSuccess()) {
                                    if ("无".equals(cardName)) {
                                        cardName = cardDetail.getData().getName();
                                    }
                                    else {
                                        cardName = cardName + "/" + cardDetail.getData().getName();
                                    }
                                }
                            }
                        }
                        customer.setCustomer_power(cardName);
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }

                    if (null != record.getYzUid()) {

                        //客户退款
                        youzanTradeRefundSearchParams.setBuyerId(record.getYzUid());
                        youzanTradeRefundSearchParams.setPageSize(50);
                        youzanTradeRefundSearchParams.setStatus("SUCCESS");
                        youzanTradeRefundSearch.setAPIParams(youzanTradeRefundSearchParams);

                        try {
                            YouzanTradeRefundSearchResult refundOrder = indexController.yzClient.invoke(youzanTradeRefundSearch, indexController.token, YouzanTradeRefundSearchResult.class);
                            if (refundOrder.getSuccess()) {
                                customer.setTotal_refund_order_num(refundOrder.getData().getTotal());
                                double refundPrice=0.00;
                                for (YouzanTradeRefundSearchResult.YouzanTradeRefundSearchResultRefunds refund:refundOrder.getData().getRefunds()) {
                                    refundPrice+=Double.parseDouble(refund.getRefundFee());
                                }
                                customer.setTotal_refund_order_price(refundPrice);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //客户储值卡
                        youzanCardvoucherValuecardInfoQueryParams.setBuyerId(record.getYzUid());
                        youzanCardvoucherValuecardInfoQuery.setAPIParams(youzanCardvoucherValuecardInfoQueryParams);

                        try {
                            YouzanCardvoucherValuecardInfoQueryResult balanceResult = indexController.yzClient.invoke(youzanCardvoucherValuecardInfoQuery, indexController.token, YouzanCardvoucherValuecardInfoQueryResult.class);
                            customer.setCustomer_balance(0.00);
//                        System.out.println("客户储值卡"+balanceResult.getMessage()+balanceResult.getCode());
                            if (balanceResult.getSuccess()) {
                                double balancePrice=0.00;
                                for (YouzanCardvoucherValuecardInfoQueryResult.YouzanCardvoucherValuecardInfoQueryResultData balance:balanceResult.getData()) {
                                    balancePrice+=(double) balance.getDenomination()/1000;
                                }
                                customer.setCustomer_balance(balancePrice);
                            }
                        } catch (SDKException e) {
                            e.printStackTrace();
                        }

                    }
                }

                list.add(customer);
                no++;
                indexController.progress_bar.setProgress((double) no/total);

            }
            excelWriter.write(list, writeSheet);
        }

    }
}
