/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.endpoint;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoPaymentGatewayInfo;

import java.util.List;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 17:55
 */
@Controller
@RequestMapping("/payment")
public interface PaymentGatewaysEndpointController {


    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/gateways/all/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getPaymentGateways(@PathVariable("lang") String lang) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/gateways/shop/{code}/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(@PathVariable("lang") String lang, @PathVariable("code") String shopCode) throws Exception;

    @Secured({"ROLE_SMADMIN","ROLE_SMSHOPADMIN","ROLE_SMWAREHOUSEADMIN","ROLE_SMCALLCENTER","ROLE_SMMARKETINGADMIN","ROLE_SMSHIPPINGADMIN"})
    @RequestMapping(value = "/gateways/shop/allowed/{lang}", method = RequestMethod.GET,  produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoPaymentGatewayInfo> getAllowedPaymentGatewaysForShops(@PathVariable("lang") String lang) throws Exception;

}