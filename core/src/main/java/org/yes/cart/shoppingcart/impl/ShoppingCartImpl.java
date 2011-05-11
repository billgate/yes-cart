package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.CartItem;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.ShoppingCart;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Shopping cart default implementation.
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:39:12 PM
 */
public class ShoppingCartImpl implements ShoppingCart, SecurityContext {

    private static final long serialVersionUID =  20110312L;

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartImpl.class);

    private List<CartItemImpl> items = new ArrayList<CartItemImpl>();

    private String currencyCode;

    private String customerName;

    private String latestViewedSkus;

    private Date modifiedDate;

    private Authentication authentication;

    private Integer carrierSlaId;

    private String guid = java.util.UUID.randomUUID().toString();

    private long shopId;

    private String orderMessage;

    private boolean separateBillingAddress;

    private boolean multipleDelivery;

    private String paymentGatewayLabel;


    /**
     * Clean current cart and prepare it to reuse.
     */
    public void clean() {

        guid = java.util.UUID.randomUUID().toString();

        items = new ArrayList<CartItemImpl>();

        orderMessage = null;

        modifiedDate = new Date();

    }

    /**
     * Get selected payment gateway.
     * @return selected payment gateway
     */
    public String getPaymentGatewayLabel() {
        return paymentGatewayLabel;
    }

    /**
     * Set selected payment gateway.
     * @param paymentGatewayLabel   selected payment gateway.
     */
    public void setPaymentGatewayLabel(final String paymentGatewayLabel) {
        this.paymentGatewayLabel = paymentGatewayLabel;
    }

    /**
     * Is order need multiple delivery.
     * @return true if need multiple delivery.
     */
    public boolean isMultipleDelivery() {
        return multipleDelivery;
    }

    /**
     * Set multiple delivery for order.
     * @param multipleDelivery multiple delivery for order.
     */
    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }

    /**
     * Is billing address different from shipping adress.
     * @return true is billing and shipping address are different.
     */
    public boolean isSeparateBillingAddress() {
        return separateBillingAddress;
    }

    /**
     * Set billilnd address different from shipping address flag.
     * @param separateBillingAddress flag.
     */
    public void setSeparateBillingAddress(final boolean separateBillingAddress) {
        this.separateBillingAddress = separateBillingAddress;
    }

    /**
     * Get order message.
     * @return order message
     */
    public String getOrderMessage() {
        return orderMessage;
    }

    /**
     * Set order message.
     * @param orderMessage order message.
     */
    public void setOrderMessage(final String orderMessage) {
        this.orderMessage = orderMessage;
    }

    /**
     * Get current shop id
     * @return current shop id.
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * Set current shop id.
     * @param shopId current shop id.
     */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /**
     * Get shopping cart guid.
     * @return shopping cart guid.
     */
    public String getGuid() {
        return guid;
    }


    /**
     * Get carrier shipping SLA.
     * @return carries sla id.
     */
    public Integer getCarrierSlaId() {
        return carrierSlaId;
    }

    /**
     * Set carrier shipping SLA.
     * @param carrierSlaId selected sla id.
     */
    public void setCarrierSlaId(final Integer carrierSlaId) {
        this.carrierSlaId = carrierSlaId;
    }

    /**
     * {@inheritDoc}
     */
    public List<CartItem> getCartItemList() {
        final List<CartItem> immutableItems = new ArrayList<CartItem>(getItems().size());
        for (CartItem item : getItems()) {
            immutableItems.add(new ImmutableCartItemImpl(item));
        }
        return Collections.unmodifiableList(immutableItems);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {

        final int skuIndex = indexOf(sku);
        if (skuIndex != -1) {
            getItems().get(skuIndex).addQuantity(quantity);
            return false;
        }

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setProductSkuCode(sku.getCode());
        newItem.setQuantity(quantity);
        getItems().add(newItem);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean setProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setProductSkuCode(sku.getCode());
        newItem.setQuantity(quantity);

        final int skuIndex = indexOf(sku);
        if (skuIndex != -1) {
            getItems().set(skuIndex, newItem);
        } else {
            getItems().add(newItem);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param productSku
     */
    public boolean removeCartItem(ProductSkuDTO productSku) {
        final int skuIndex = indexOf(productSku);
        if (skuIndex != -1) {
            getItems().remove(skuIndex);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeCartItemQuantity(final ProductSkuDTO productSku, final BigDecimal quantity) {
        final int skuIndex = indexOf(productSku);
        if (skuIndex != -1) {
            try {
                getItems().get(skuIndex).removeQuantity(quantity);
            } catch (final CartItemRequiresDeletion cartItemRequiresDeletion) {
                getItems().remove(skuIndex);
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean setProductSkuPrice(final String skuCode, final BigDecimal price) {
        final int skuIndex = indexOf(skuCode);
        if (skuIndex != -1) {
            getItems().get(skuIndex).setPrice(price);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int getCartItemsCount() {
        BigDecimal quantity = BigDecimal.ZERO;
        final List<? extends CartItem> items = getItems();
        for (CartItem cartItem : items) {
            quantity = quantity.add(cartItem.getQty());
        }
        return quantity.intValue();
    }



    /**
     * {@inheritDoc}
     */
    public BigDecimal getCartSubTotal(final List<? extends CartItem> items) {

        BigDecimal cartSubTotal = BigDecimal.ZERO;
        if (items != null) {
            for (CartItem cartItem : items) {
                final BigDecimal price = cartItem.getPrice();
                cartSubTotal = cartSubTotal.add(price.multiply(cartItem.getQty()).setScale(Constants.DEFAULT_SCALE));
            }
        }

        return cartSubTotal.setScale(Constants.DEFAULT_SCALE);
    }

    /**
     * @param sku product sku for which to locate position of shopping cart item
     * @return idex of cart item for this sku
     */
    int indexOf(final ProductSkuDTO sku) {
        return indexOf(sku.getCode());
    }

    /**
     * @param  skuCode sku code
     * @return idex of cart item for this sku
     */
    public int indexOf(final String skuCode) {
        for (int index = 0; index < getItems().size(); index++) {
            final CartItem item = getItems().get(index);
            if (item.getProductSkuCode().equals(skuCode)) {
                return index;
            }
        }
        return -1;

    }


    /**
     * Is sku code present in cart
     * @param skuCode product sku code
     * @return true if sku code present in cart
     */
    public boolean contains(final String skuCode) {
        return (indexOf(skuCode) != -1);
    }


    /**
     * @return list of items (testing convenience method)
     */
    List<CartItemImpl> getItems() {
        return items;
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    /**
     * {@inheritDoc}
     */
    public String getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /**
     * {@inheritDoc}
     */
    public void setLatestViewedSkus(final String latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus;
    }

    /**
     * {@inheritDoc}
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    public void setModifiedDate(final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /** {@inheritDoc} */
    public String getCustomerEmail() {
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof User) {
                return ((User) authentication.getPrincipal()).getUsername();
            }
            if (LOG.isWarnEnabled()) {
                LOG.warn(
                        MessageFormat.format(
                                "The {0} in authentication#principal not instance of User ",
                                authentication.getPrincipal()
                        )
                );
            }
        }
        return null;
    }


    /**
     *
     * Get logon state.
     *
     * @return Logon state
     */
    public int getLogonState() {
        if (StringUtils.isBlank(getCustomerEmail())
                   && StringUtils.isNotBlank(getCustomerName())) {
            return ShoppingCart.SESSION_EXPIRED;
        } else if (StringUtils.isNotBlank(getCustomerEmail())
                   && StringUtils.isNotBlank(getCustomerName())) {
            return ShoppingCart.LOGGED_IN;
        }
        return ShoppingCart.NOT_LOGGED;
    }

    /** {@inheritDoc} */
    public Authentication getAuthentication() {        
        return authentication;
    }

    /** {@inheritDoc} */
    public void setAuthentication(final Authentication authentication) {
        this.authentication = authentication;
    }
}